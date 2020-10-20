package dev.tr7zw.cinematics;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;

public class TRCinematics extends JavaPlugin implements Listener{

    public static TRCinematics instance;
    @Getter
    private List<CinematicInstance> cinematicInstances = new ArrayList<CinematicInstance>();


    @Override
    public void onEnable(){
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, this);
        //Bukkit.getPluginManager().registerEvents(new Testing(), this);
        Bukkit.getScheduler().runTaskTimer(this, new CinematicUpdater(), 1, 1);
    }

    @Override
    public void onDisable(){
        Bukkit.getScheduler().cancelTasks(this);
    	cinematicInstances.forEach(inst -> inst.stopCinematic());
    	cinematicInstances.clear();
    }


    public CinematicInstance playCinematic(TRCinematic cinematic, List<Player> players) {
    	CinematicInstance inst = new CinematicInstance(cinematic, players);
    	inst.prepareCinematic();
    	cinematicInstances.add(inst);
    	return inst;
    }



}
