package com.narrowtux.PistonChest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.ContainerBlock;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PCBlockListener extends BlockListener {

	private Block blockBeforeChest = null;
	@Override
	public void onBlockRedstoneChange(BlockRedstoneEvent event){
		List<Block> pistons = getTriggeredPistons(event);
		for(Block piston:pistons){
			Block chest = getChestInLine(piston);
			if(chest!=null&&!blockBeforeChest.equals(piston)){
				ItemStack stack = new ItemStack(blockBeforeChest.getType(), 1, blockBeforeChest.getData());
				Inventory inv = ((ContainerBlock)chest.getState()).getInventory();
				if(chest.getType().equals(Material.FURNACE)||chest.getType().equals(Material.BURNING_FURNACE)){
					if(handleFurnace(inv, stack)){
						blockBeforeChest.setType(Material.AIR);
					}
				} else {
					if(handleNormalInventory(inv, stack)){
						blockBeforeChest.setType(Material.AIR);
					}
				}
			}
		}
	}

	public List<Block> getTriggeredPistons(BlockRedstoneEvent event){
		List<Block> result = new ArrayList<Block>();
		//TODO: Implement all types of possible triggers
		if(event.getNewCurrent()>0){
			BlockFace faces[] = {
					BlockFace.NORTH,
					BlockFace.EAST,
					BlockFace.SOUTH,
					BlockFace.WEST,
					BlockFace.UP,
					BlockFace.SELF,
					BlockFace.DOWN,
			};
			for(BlockFace face:faces){
				Block piston = event.getBlock().getFace(face);
				if(piston.getType().equals(Material.PISTON_BASE)){
					result.add(piston);
				}
				if(piston.getType().toString().contains("DIODE")&&piston.getFace(face).getType().equals(Material.PISTON_BASE)){
					result.add(piston.getFace(face));
				}
			}
		}
		return result;
	}
	
	public Block getChestInLine(Block piston) {
		BlockFace face = getPistonFace(piston.getData());
		Block chest = piston;
		for(int i = 0;i<12;i++){
			blockBeforeChest = chest;
			chest = chest.getFace(face);
			if(isNotAcceptedType(chest.getType())){
				return null;
			}
			if(isAcceptedType(chest.getType())){
				return chest;
			}
		}
		return null;
	}
	
	public boolean isNotAcceptedType(Material type) {
		if(type.equals(Material.OBSIDIAN)
				||type.equals(Material.BEDROCK)
				||type.equals(Material.NOTE_BLOCK)
				||type.equals(Material.AIR)
				||type.equals(Material.REDSTONE_WIRE)
				||type.equals(Material.REDSTONE_TORCH_OFF)
				||type.equals(Material.REDSTONE_TORCH_ON)
				||type.equals(Material.DIODE_BLOCK_OFF)
				||type.equals(Material.DIODE_BLOCK_ON)
				||type.equals(Material.RED_ROSE)
				||type.equals(Material.YELLOW_FLOWER)
				||type.equals(Material.RED_MUSHROOM)
				||type.equals(Material.BROWN_MUSHROOM)
				||type.equals(Material.SAPLING)
				||type.equals(Material.SIGN)
				||type.equals(Material.STONE_BUTTON)
				||type.equals(Material.LEVER)
				||type.equals(Material.LADDER)
				||type.equals(Material.WOODEN_DOOR)
				||type.equals(Material.IRON_DOOR_BLOCK)
				||type.equals(Material.TORCH)){
			return true;
		}
		return false;
	}

	public boolean isAcceptedType(Material type) {
		if(type.equals(Material.CHEST)||type.equals(Material.DISPENSER)||type.equals(Material.FURNACE)||type.equals(Material.BURNING_FURNACE)){
			return true;
		} else {
			return false;
		}
	}

	public BlockFace getPistonFace(byte data){
		BlockFace face = null;
		switch(data){
		case 0:
			face = BlockFace.DOWN;
			break;
		case 1:
			face = BlockFace.UP;
			break;
		case 2:
			face = BlockFace.EAST;
			break;
		case 3:
			face = BlockFace.WEST;
			break;
		case 4:
			face = BlockFace.NORTH;
			break;
		case 5:
			face = BlockFace.SOUTH;
			break;
		default:
			face = null;
			break;
		}
		return face;
	}
	
	public boolean handleNormalInventory(Inventory inv, ItemStack stack){
		HashMap<Integer, ItemStack> ret = inv.addItem(stack);
		return ret.size()==0;
	}
	
	public boolean handleFurnace(Inventory inv, ItemStack stack){
		ItemStack burnSlot = inv.getItem(0);
		if(burnSlot.getTypeId()==0||(burnSlot.getType().equals(stack.getType())&&burnSlot.getData()==stack.getData())){
			if(burnSlot.getTypeId()==0){
				inv.setItem(0, stack);
				return true;
			} else if(burnSlot.getAmount()<=64-stack.getAmount()){
				burnSlot.setAmount(burnSlot.getAmount()+stack.getAmount());
				inv.setItem(0, burnSlot);
				return true;
			}
		}
		return false;
	}
}
