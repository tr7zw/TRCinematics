package dev.tr7zw.cinematics;

import java.util.ListIterator;
import java.util.function.Consumer;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.github.paperspigot.Title;

public class CinematicUpdater implements Runnable {

	@Override
	public void run() {
		ListIterator<CinematicInstance> iterator = TRCinematics.instance.getCinematicInstances().listIterator();
		while(iterator.hasNext()) {
			try {
				CinematicInstance inst = iterator.next();
				int position = inst.getCinematicPosition().incrementAndGet();
				int length = inst.getCompiledPath().size();
				if(position >= length) {
					iterator.remove();
					inst.stopCinematic();
					continue;
				}
				if(position == 0) { // Cinematic start
					inst.getPlayers().forEach(p -> {
						p.setAllowFlight(true);
				        p.setFlying(true);
				        p.setGameMode(GameMode.SPECTATOR);
				        p.setSpectatorTarget(inst.getTrackingEntity());
					});
				}
				Location nextPos = inst.getCompiledPath().get(position);
				Title title = inst.getCinematicData().getTitles().get(position);
				Consumer<CinematicInstance> customTask = inst.getCinematicData().getCustomFunctions().get(position);
				if(customTask != null)customTask.accept(inst);
				inst.getTrackingEntity().teleport(nextPos);
				inst.getPlayers().forEach(p -> {
					p.setSpectatorTarget(inst.getTrackingEntity());

					if(title != null) {
						p.sendTitle(title);
					}
				});
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

}
