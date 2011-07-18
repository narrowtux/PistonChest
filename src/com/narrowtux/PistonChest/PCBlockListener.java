package com.narrowtux.PistonChest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.ContainerBlock;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Item;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.PoweredMinecart;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PCBlockListener extends BlockListener {

	private Block blockBeforeChest = null;
	int invSlot = -1;
	@Override
	public void onBlockRedstoneChange(BlockRedstoneEvent event){
		List<Block> pistons = getTriggeredPistons(event);
		for(Block piston:pistons){
			if(piston.getType().equals(Material.PISTON_BASE)){
				Block chest = getChestInLine(piston);
				if(!PCMain.isChestPublic(chest)){
					continue;
				}
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
			if(piston.getType().equals(Material.PISTON_STICKY_BASE)){
				BlockFace face = getPistonFace(piston.getData());
				Block chest = piston.getFace(face).getFace(face);
				if(isAcceptedType(chest.getType())){
					if(!PCMain.isChestPublic(chest)){
						continue;
					}
					Inventory inv = ((ContainerBlock)chest.getState()).getInventory();
					ItemStack stack = null;
					if(chest.getType().equals(Material.CHEST)||chest.getType().equals(Material.DISPENSER)){
						stack = getFirstItem(inv);
					}
					if(isFurnace(chest.getType())){
						invSlot = 2;
						stack = inv.getItem(invSlot);
						if(!stack.getType().isBlock()){
							stack = null;
						}
					}
					if(stack==null){
						continue;
					}
					if(stack.getTypeId()!=0&&stack.getAmount()>0){
						Location pos = piston.getFace(face).getLocation();
						ItemStack toPull = stack.clone();
						toPull.setAmount(1);
						if(pullItem(toPull, pos, face)){
							stack.setAmount(stack.getAmount()-1);
							if(stack.getAmount()==0){
								inv.setItem(invSlot, null);
							}
						}
						break;
					}
				}
			}
		}
	}

	public ItemStack getFirstItem(Inventory inv) {
		for(invSlot = 0; invSlot<inv.getSize();invSlot++){
			ItemStack stack = inv.getItem(invSlot);
			if(stack.getAmount()>0){
				return stack;
			}
		}
		return null;
	}

	public List<Block> getTriggeredPistons(BlockRedstoneEvent event){
		List<Block> result = new ArrayList<Block>();
		if((event.getNewCurrent()>0&&event.getOldCurrent()>0)||(event.getNewCurrent()==0&&event.getOldCurrent()==0)){
			//When redstone didn't really change, 
			return result;
		}
		//TODO: Implement all types of possible triggers
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
			if(event.getNewCurrent()>0){
				if(piston.getType().equals(Material.PISTON_BASE)){
					result.add(piston);
				}
				if(piston.getType().toString().contains("DIODE")&&piston.getFace(face).getType().equals(Material.PISTON_BASE)){
					result.add(piston.getFace(face));
				}
			} else {
				Block current = null;
				if(piston.getType().equals(Material.PISTON_STICKY_BASE)){
					current = piston;
				}
				if(piston.getType().toString().contains("DIODE")&&piston.getFace(face).getType().equals(Material.PISTON_STICKY_BASE)){
					current = piston.getFace(face);
				}
				if(current!=null&&isPistonExtended(current.getData())){
					result.add(current);
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
				||type.equals(Material.TORCH)
				||type.equals(Material.WATER)
				||type.equals(Material.LAVA)
				||type.equals(Material.STATIONARY_LAVA)
				||type.equals(Material.STATIONARY_WATER)
				||type.equals(Material.FIRE)
				||type.equals(Material.PISTON_MOVING_PIECE)
				||type.equals(Material.PISTON_EXTENSION)){
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
		data = (byte) (data & 0x7);
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

	public ItemStack getFirstBlock(Inventory inv){
		for(invSlot = 0; invSlot<inv.getSize();invSlot++){
			ItemStack stack = inv.getItem(invSlot);
			if(stack.getType().isBlock()&&stack.getAmount()>0){
				return stack;
			}
		}
		return null;
	}

	public boolean isFurnace(Material type){
		return type.equals(Material.FURNACE)||type.equals(Material.BURNING_FURNACE);
	}
	
	public boolean isPistonExtended(byte data){
		return (data&0x8)>0;
	}
	
	public boolean pullItem(ItemStack item, Location pos, BlockFace face){
		pos.add(0.5, 0.5, 0.5);
		Configuration config = PCMain.instance.config;
		if(item.getType().isBlock()&&config.buildsBlocks()){
			Block toBuild = pos.getBlock();
			BlockCreator creator = new BlockCreator(toBuild, item);
			Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(PCMain.instance, creator, 2);
			return true;
		}
		//Check if Minecart or Boat
		if(isItemVehicle(item.getType())&&config.spawnsVehicles()){
			spawnVehicle(item.getType(), pos);
			return true;
		}
		if(config.dropsItems()){
			World w = pos.getWorld();
			Item drop = w.dropItem(pos, item);
			Vector v = new Vector(face.getModX(), face.getModY(), face.getModZ());
			v = v.multiply(-0.05);
			drop.setVelocity(v);
			return true;
		}
		return false;
	}
	
	public void spawnVehicle(Material type, Location pos) {
		World w = pos.getWorld();
		switch(type){
		case MINECART:
			w.spawn(pos, Minecart.class);
			break;
		case BOAT:
			w.spawn(pos, Boat.class);
			break;
		case STORAGE_MINECART:
			w.spawn(pos, StorageMinecart.class);
			break;
		case POWERED_MINECART:
			w.spawn(pos, PoweredMinecart.class);
			break;
		}
	}

	public boolean isItemVehicle(Material item){
		if(item.equals(Material.MINECART)||
				item.equals(Material.STORAGE_MINECART)||
				item.equals(Material.POWERED_MINECART)||
				item.equals(Material.BOAT)){
			return true;
		}
		return false;
	}
}
