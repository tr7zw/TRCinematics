package dev.tr7zw.cinematics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CinematicInstance {

	@NonNull
	private TRCinematic cinematicData;
	@NonNull
	private List<Player> players;

	private List<Location> compiledPath = new ArrayList<Location>();
	private AtomicInteger cinematicPosition = new AtomicInteger(-1);
	private Map<Player, Location> originalPlayerLocation = new HashMap<Player, Location>();
	private Map<Player, GameMode> originalPlayerGamemode = new HashMap<Player, GameMode>();
	private ArmorStand trackingEntity;

	protected void prepareCinematic() {
		for (CinematicPath path : cinematicData.getPaths()) {
			compilePath(path);
		}
		getPlayers().forEach(p -> {
			originalPlayerLocation.put(p, p.getLocation());
			originalPlayerGamemode.put(p, p.getGameMode());
		});
		trackingEntity = (ArmorStand) compiledPath.get(0).getWorld().spawnEntity(compiledPath.get(0), EntityType.ARMOR_STAND);
		trackingEntity.setGravity(false);
		trackingEntity.setMarker(true);
	}

	public Stream<Player> getPlayers() {
		return players.stream().filter(p -> p.isOnline());
	}
	
	protected void stopCinematic() {
		getCinematicData().getOnFinished().accept(this);
		if(getCinematicData().isResetPlayers()) {
			getPlayers().forEach(p -> {
				p.teleport(getOriginalPlayerLocation().getOrDefault(p, p.getLocation()));
				p.setGameMode(getOriginalPlayerGamemode().getOrDefault(p, p.getGameMode()));
				if(p.getGameMode() != GameMode.CREATIVE){
	                p.setAllowFlight(false);
	                p.setFlying(false);
	            }
			});
		}
		getTrackingEntity().remove();
	}

	private void compilePath(CinematicPath path) {
		int time = path.getTicks();

		List<Location> locs = path.getPoints();
		if ((locs == null) || (locs.size() <= 1)) {
			throw new Error("Not enough points set!");
		}
		List<Double> diffs = new ArrayList<>();
		List<Integer> travelTimes = new ArrayList<>();

		double totalDiff = 0.0D;
		for (int i = 0; i < locs.size() - 1; i++) {
			Location s = (Location) locs.get(i);
			Location n = (Location) locs.get(i + 1);
			double diff = positionDifference(s, n);
			totalDiff += diff;
			diffs.add(Double.valueOf(diff));
		}
		for (Double d : diffs) {
			travelTimes.add(Integer.valueOf((int) (d / totalDiff * time)));
		}

		World w = locs.get(0).getWorld();
		for (int i = 0; i < locs.size() - 1; i++) {
			Location s = (Location) locs.get(i);
			Location n = (Location) locs.get(i + 1);
			int t = ((Integer) travelTimes.get(i)).intValue();

			double moveX = n.getX() - s.getX();
			double moveY = n.getY() - s.getY();
			double moveZ = n.getZ() - s.getZ();
			float movePitch = n.getPitch() - s.getPitch();

			float yawDiff = Math.abs(n.getYaw() - s.getYaw());
			double c = 0.0D;
			if (yawDiff <= 180.0D) {
				if (s.getYaw() < n.getYaw()) {
					c = yawDiff;
				} else {
					c = -yawDiff;
				}
			} else if (s.getYaw() < n.getYaw()) {
				c = -(360.0D - yawDiff);
			} else {
				c = 360.0D - yawDiff;
			}
			double d = c / t;
			for (int x = 0; x < t; x++) {
				Location l = new Location(w, s.getX() + moveX / t * x, s.getY() + moveY / t * x,
						s.getZ() + (moveZ / t * x), (float) (s.getYaw() + (float) d * (float) x),
						(float) (s.getPitch() + (movePitch / (float) t * (float) x)));
				compiledPath.add(l);
			}
		}
	}

	public double positionDifference(Location cLoc, Location eLoc) {
		double cX = cLoc.getX();
		double cY = cLoc.getY();
		double cZ = cLoc.getZ();

		double eX = eLoc.getX();
		double eY = eLoc.getY();
		double eZ = eLoc.getZ();

		double dX = eX - cX;
		if (dX < 0.0D) {
			dX = -dX;
		}
		double dZ = eZ - cZ;
		if (dZ < 0.0D) {
			dZ = -dZ;
		}
		double dXZ = Math.hypot(dX, dZ);

		double dY = eY - cY;
		if (dY < 0.0D) {
			dY = -dY;
		}
		double dXYZ = Math.hypot(dXZ, dY);

		return dXYZ;
	}

}
