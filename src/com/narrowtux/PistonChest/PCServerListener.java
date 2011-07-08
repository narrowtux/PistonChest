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
		String name = plugin.getDescription().getName();
		if(name.equals("Lockette")||name.equals("LWC")){
			if(name.equals("Lockette")){
				PCMain.lockette = true;
			}
			if(name.equals("LWC")){
				PCMain.lwc = true;
			}
			PCMain.log.log(Level.INFO, "[PistonChest] "+plugin.getDescription().getFullName()+" found!");
		}
	}
	
	public void onPluginDisable(PluginDisableEvent event){
		Plugin plugin = event.getPlugin();
		String name = plugin.getDescription().getName();
		if(name.equals("Lockette")){
			PCMain.lockette = false;
		}
		if(name.equals("LWC")){
			PCMain.lwc = false;
		}
	}
}
