package com.narrowtux.PistonChest;

import java.io.File;

import com.narrowtux.narrowtuxlib.utils.FlatFileReader;


public class Configuration {
	private boolean protectChests = true;
	private boolean useFirstItem = true;
	private boolean spawnVehicles = true;
	private boolean dropItems = true;
	private boolean buildBlocks = true;
	private FlatFileReader reader;
	public Configuration(){
		reader = new FlatFileReader(new File(PCMain.instance.getDataFolder(), "PistonChest.cfg"), false);
		load();
		reader.write();
	}
	private void load() {
		protectChests = reader.getBoolean("protectchests", true);
		//useFirstItem = reader.getBoolean("usefirstitem", true);
		spawnVehicles = reader.getBoolean("spawnvehicles", true);
		dropItems = reader.getBoolean("dropitems", true);
		buildBlocks = reader.getBoolean("buildblocks", true);
	}
	
	public boolean hasChestProtection(){
		return protectChests;
	}
	
	public boolean usesFirstItem(){
		return useFirstItem;
	}
	
	public boolean spawnsVehicles(){
		return spawnVehicles;
	}
	
	public boolean dropsItems(){
		return dropItems;
	}
	
	public boolean buildsBlocks(){
		return buildBlocks;
	}
}
