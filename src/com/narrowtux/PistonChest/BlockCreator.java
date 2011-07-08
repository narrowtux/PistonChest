package com.narrowtux.PistonChest;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class BlockCreator implements Runnable {
	private Block block;
	private ItemStack stack;
	
	public BlockCreator(Block b, ItemStack s){
		block = b;
		stack = s;
	}
	
	@Override
	public void run() {
		block.setType(stack.getType());
		block.setData((byte)stack.getDurability());
	}
}
