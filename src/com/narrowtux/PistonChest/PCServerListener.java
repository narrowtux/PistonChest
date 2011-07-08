package com.narrowtux.PistonChest;

import java.util.logging.Level;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

public class PCServerListener extends ServerListener {
	@Override
	public void onPluginEnable(PluginEnableEvent event){
		Plugin plugin = event.getPlugin();
		if(plugin.getDescription().getName().equals("Lockette")){
			PCMain.lockette = true;
			PCMain.log.log(Level.INFO, "[PistonChest] "+plugin.getDescription().getFullName()+" found!");
		}
	}
	
	public void onPluginDisable(PluginDisableEvent event){
		Plugin plugin = event.getPlugin();
		if(plugin.getDescription().getName().equals("Lockette")){
			PCMain.lockette = false;
		}
	}
}
