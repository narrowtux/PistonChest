package com.narrowtux.PistonChest;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.yi.acru.bukkit.Lockette.Lockette;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;

public class PCMain extends JavaPlugin {
	public static Logger log = Bukkit.getServer().getLogger();
	public static PCMain instance;
	private PCBlockListener blockListener = new PCBlockListener();
	private PCServerListener serverListener = new PCServerListener();
	public static boolean lockette = false;
	public static boolean lwc = false;
	
	@Override
	public void onDisable() {
		sendDescription("disabled");
	}

	@Override
	public void onEnable() {
		instance = this;
		registerEvents();
		fakePluginEnables();
		sendDescription("enabled");
	}
	
	private void fakePluginEnables() {
		String plugins[] = {"Lockette", "LWC"};
		for(String name:plugins){
			Plugin plugin = getServer().getPluginManager().getPlugin(name);
			if(plugin!=null)
			{
				serverListener.onPluginEnable(new PluginEnableEvent(plugin));
			}
		}
	}

	private void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Type.REDSTONE_CHANGE, blockListener, Priority.Normal, this);
		pm.registerEvent(Type.PLUGIN_ENABLE, serverListener, Priority.Normal, this);
		pm.registerEvent(Type.PLUGIN_DISABLE, serverListener, Priority.Normal, this);
	}

	public void sendDescription(String startup){
		PluginDescriptionFile pdf = getDescription();
		String authors = "";
		for(String name: pdf.getAuthors()){
			if(authors.length()>0){
				authors+=", ";
			}
			authors+=name;
		}
		log.log(Level.INFO, "["+pdf.getName()+"] v"+pdf.getVersion()+" by ["+authors+"] "+startup+".");
	}
	
	public static boolean isChestPublic(Block chest){
		boolean flag = true;
		if(lockette){
			flag = !Lockette.isProtected(chest)&&flag;
		}
		if(lwc){
			Protection prot = LWC.getInstance().findProtection(chest);
			flag = prot==null&&flag;
		}
		return flag;
	}

}
