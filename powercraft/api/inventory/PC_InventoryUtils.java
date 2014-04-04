package powercraft.api.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec3;
import powercraft.api.PC_Vec3I;
import powercraft.api.item.PC_IItem;

public class PC_InventoryUtils {

	public static void loadInventoryFromNBT(IInventory inventory, NBTTagCompound nbtTagCompound, String key) {

		NBTTagList list = nbtTagCompound.getTagList(key, 10);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound nbtTagCompound2 = list.getCompoundTagAt(i);
			inventory.setInventorySlotContents(nbtTagCompound2.getInteger("slot"), ItemStack.loadItemStackFromNBT(nbtTagCompound2));
		}
	}


	public static void saveInventoryToNBT(IInventory inventory, NBTTagCompound nbtTagCompound, String key) {

		NBTTagList list = new NBTTagList();
		int size = inventory.getSizeInventory();
		for (int i = 0; i < size; i++) {
			ItemStack itemStack = inventory.getStackInSlot(i);
			if (itemStack != null) {
				NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
				nbtTagCompound2.setInteger("slot", i);
				itemStack.writeToNBT(nbtTagCompound2);
				list.appendTag(nbtTagCompound2);
			}
		}
		nbtTagCompound.setTag(key, list);
	}

	public static int getSlotStackLimit(IInventory inventory, int i){
		if(inventory instanceof PC_IInventory){
			return ((PC_IInventory)inventory).getSlotStackLimit(i);
		}
		return inventory.getInventoryStackLimit();
	}

	public static void onTick(IInventory inventory, World world) {
		int size = inventory.getSizeInventory();
		for(int i=0; i<size; i++){
			ItemStack itemStack = inventory.getStackInSlot(i);
			if(itemStack!=null){
				Item item = itemStack.getItem();
				if(item instanceof PC_IItem){
					((PC_IItem)item).onTick(itemStack, world, inventory, i);
				}
			}
		}
	}
	
	public static IInventory getInventoryFrom(Object obj) {
		if(obj instanceof PC_IInventoryProvider){
			return ((PC_IInventoryProvider)obj).getInventory();
		}else if(obj instanceof IInventory){
			return (IInventory)obj;
		}else if(obj instanceof EntityPlayer){
			return ((EntityPlayer)obj).inventory;
		}else if(obj instanceof EntityLiving){
			return new PC_WrapperInventory(((EntityLiving)obj).getLastActiveItems());
		}
		return null;
	}

	public static ItemStack tryToStore(World world, int x, int y, int z, PC_Direction to, ItemStack itemstack) {
		IInventory inventory = getInventoryAt(world, x, y, z);
		if(inventory!=null){
			if(storeItemStackToInventoryFrom(inventory, itemstack, to))
				return null;
		}
		return itemstack;
	}
	
	public static IInventory getBlockInventoryAt(IBlockAccess world, int x, int y, int z) {
		IInventory inv = getInventoryFrom(PC_Utils.getTileEntity(world, x, y, z));
		if(inv != null){
			Block block = PC_Utils.getBlock(world, x, y, z);
			final Block[] chests = {Blocks.chest, Blocks.trapped_chest};
			for(Block chest:chests){
				if(block==chest){
					if (PC_Utils.getBlock(world, x-1, y, z) == chest) {
						inv = new InventoryLargeChest("Large chest", (IInventory) PC_Utils.getTileEntity(world, x-1, y, z), inv);
					}else if (PC_Utils.getBlock(world, x+1, y, z) == chest) {
						inv = new InventoryLargeChest("Large chest", inv, (IInventory) PC_Utils.getTileEntity(world, x+1, y, z));
					}else if (PC_Utils.getBlock(world, x, y, z-1) == chest) {
						inv = new InventoryLargeChest("Large chest", (IInventory) PC_Utils.getTileEntity(world, x, y, z-1), inv);
					}else if (PC_Utils.getBlock(world, x, y, z+1) == chest) {
						inv = new InventoryLargeChest("Large chest", inv, (IInventory) PC_Utils.getTileEntity(world, x, y, z+1));
					}
				}
			}
		}
		return inv;
	}
	
	private static class EntitySelector implements IEntitySelector{

		public EntitySelector() {
			
		}

		@Override
		public boolean isEntityApplicable(Entity entity) {
			return getInventoryFrom(entity)!=null;
		}
		
	}
	
	public static IInventory getEntityInventoryAt(World world, int x, int y, int z){
		List<?> list = world.selectEntitiesWithinAABB(
				Object.class,
				AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1)
						.expand(0.6D, 0.6D, 0.6D), new EntitySelector());

		if (list.size() >= 1) {
			return getInventoryFrom(list.get(0));
		}

		return null;
	}
	
	public static IInventory getInventoryAt(World world, int x, int y, int z) {
		IInventory invAt = getBlockInventoryAt(world, x, y, z);

		if (invAt != null) {
			return invAt;
		}

		return getEntityInventoryAt(world, x, y, z);
	}
	
	public static IInventory getInventoryAt(World world, PC_Vec3I pos) {
		return getInventoryAt(world, pos.x, pos.y, pos.z);
	}
	
	public static int[] getInvIndexesForSide(IInventory inv, PC_Direction side){
		if(side==null)
			return null;
		int sideID = side.ordinal();
		if(inv instanceof ISidedInventory && sideID>=0){
			return ((ISidedInventory) inv).getAccessibleSlotsFromSide(sideID);
		}
		return null;
	}
	
	public static int[] makeIndexList(int start, int end){
		int[] indexes = new int[end-start];
		for(int i=0; i<indexes.length; i++){
			indexes[i] = i+start;
		}
		return indexes;
	}
	
	public static int getFirstEmptySlot(IInventory inv, ItemStack itemstack){
		return getFirstEmptySlot(inv, itemstack, (int[])null);
	}
	
	public static int getFirstEmptySlot(IInventory inv, ItemStack itemstack, PC_Direction side){
		return getFirstEmptySlot(inv, itemstack, getInvIndexesForSide(inv, side));
	}
	
	public static int getFirstEmptySlot(IInventory inv, ItemStack itemstack, int[] indexes){
		if(indexes==null){
			int size = inv.getSizeInventory();
			for (int i = 0; i < size; i++) {
				if (inv.getStackInSlot(i) == null && inv.isItemValidForSlot(i, itemstack) && getSlotStackLimit(inv, i)>0) {
					return i;
				}
			}
		}else{
			for (int j = 0; j < indexes.length; j++) {
				int i=indexes[j];
				if (inv.getStackInSlot(i) == null && inv.isItemValidForSlot(i, itemstack) && getSlotStackLimit(inv, i)>0) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public static int getSlotWithPlaceFor(IInventory inv, ItemStack itemstack){
		return getSlotWithPlaceFor(inv, itemstack, (int[])null);
	}
	
	public static int getSlotWithPlaceFor(IInventory inv, ItemStack itemstack, PC_Direction side){
		return getSlotWithPlaceFor(inv, itemstack, getInvIndexesForSide(inv, side));
	}
	
	public static int getSlotWithPlaceFor(IInventory inv, ItemStack itemstack, int[] indexes){
		if(indexes==null){
			int size = inv.getSizeInventory();
			for (int i = 0; i < size; i++) {
				ItemStack slot = inv.getStackInSlot(i);
				if (slot != null) {
					if(slot.isItemEqual(itemstack) && slot.getMaxStackSize()>slot.stackSize && getSlotStackLimit(inv, i)>slot.stackSize){
						if(inv.isItemValidForSlot(i, itemstack))
							return i;
					}
				}
			}
		}else{
			for (int j = 0; j < indexes.length; j++) {
				int i=indexes[j];
				ItemStack slot = inv.getStackInSlot(i);
				if (slot != null) {
					if(slot.isItemEqual(itemstack) && slot.getMaxStackSize()>slot.stackSize && getSlotStackLimit(inv, i)>slot.stackSize){
						if(inv.isItemValidForSlot(i, itemstack))
							return i;
					}
				}
			}
		}
		return getFirstEmptySlot(inv, itemstack, indexes);
	}
	
	public static boolean storeItemStackToInventoryFrom(IInventory inv, ItemStack itemstack){
		return storeItemStackToInventoryFrom(inv, itemstack, (int[])null);
	}
	
	public static boolean storeItemStackToInventoryFrom(IInventory inv, ItemStack itemstack, PC_Direction side){
		return storeItemStackToInventoryFrom(inv, itemstack, getInvIndexesForSide(inv, side));
	}
	
	public static boolean storeItemStackToInventoryFrom(IInventory inv, ItemStack itemstack, int[] indexes){
		while(itemstack.stackSize>0){
			int slot = getSlotWithPlaceFor(inv, itemstack, indexes);
			if(slot<0)
				break;
			storeItemStackToSlot(inv, itemstack, slot);
		}
		return itemstack.stackSize==0;
	}
	
	public static boolean storeItemStackToSlot(IInventory inv, ItemStack itemstack, int i){
		ItemStack slot = inv.getStackInSlot(i);
		if (slot == null) {
			int store = getSlotStackLimit(inv, i);
			if(store>itemstack.getMaxStackSize()){
				store = itemstack.getMaxStackSize();
			}
			if(store>itemstack.stackSize){
				store = itemstack.stackSize;
			}
			slot = itemstack.copy();
			slot.stackSize = store;
			itemstack.stackSize -= store;
		}else{
			if(slot.isItemEqual(itemstack)){
				int store = getSlotStackLimit(inv, i);
				if(store>slot.getMaxStackSize()){
					store = slot.getMaxStackSize();
				}
				store -= slot.stackSize;
				if(store>0){
					if(store>itemstack.stackSize){
						store = itemstack.stackSize;
					}
					itemstack.stackSize -= store;
					slot.stackSize += store;
				}
			}
		}
		inv.setInventorySlotContents(i, slot);
		return itemstack.stackSize==0;
	}
	
	public static int getInventorySpaceFor(IInventory inv, ItemStack itemstack){
		return getInventorySpaceFor(inv, itemstack, (int[])null);
	}
	
	public static int getInventorySpaceFor(IInventory inv, ItemStack itemstack, PC_Direction side){
		return getInventorySpaceFor(inv, itemstack, getInvIndexesForSide(inv, side));
	}
	
	public static int getInventorySpaceFor(IInventory inv, ItemStack itemstack, int[] indexes){
		int space=0;
		if(indexes==null){
			int size = inv.getSizeInventory();
			for (int i = 0; i < size; i++) {
				ItemStack slot = inv.getStackInSlot(i);
				int slotStackLimit = getSlotStackLimit(inv, i);
				if(itemstack==null){
					if (slot == null) {
						space += slotStackLimit;
					}
				}else{
					if(slotStackLimit>itemstack.getMaxStackSize()){
						slotStackLimit = itemstack.getMaxStackSize();
					}
					if (slot != null) {
						if(slot.isItemEqual(itemstack) && slotStackLimit>slot.stackSize){
							if(inv.isItemValidForSlot(i, itemstack)){
								space += slotStackLimit-slot.stackSize;
							}
						}
					}else{
						space += slotStackLimit;
					}
				}
			}
		}else{
			for (int j = 0; j < indexes.length; j++) {
				int i=indexes[j];
				ItemStack slot = inv.getStackInSlot(i);
				int slotStackLimit = getSlotStackLimit(inv, i);
				if(itemstack==null){
					if (slot == null) {
						space += slotStackLimit;
					}
				}else{
					if(slotStackLimit>itemstack.getMaxStackSize()){
						slotStackLimit = itemstack.getMaxStackSize();
					}
					if (slot != null) {
						if(slot.isItemEqual(itemstack) && slotStackLimit>slot.stackSize){
							if(inv.isItemValidForSlot(i, itemstack)){
								space += slotStackLimit-slot.stackSize;
							}
						}
					}else{
						space += slotStackLimit;
					}
				}
			}
		}
		return space;
	}
	
	public static int getInventoryCountOf(IInventory inv, ItemStack itemstack){
		return getInventoryCountOf(inv, itemstack, (int[])null);
	}
	
	public static int getInventoryCountOf(IInventory inv, ItemStack itemstack, PC_Direction side){
		return getInventoryCountOf(inv, itemstack, getInvIndexesForSide(inv, side));
	}
	
	public static int getInventoryCountOf(IInventory inv, ItemStack itemstack, int[] indexes){
		int count=0;
		if(indexes==null){
			int size = inv.getSizeInventory();
			for (int i = 0; i < size; i++) {
				ItemStack slot = inv.getStackInSlot(i);
				if (slot != null) {
					if(itemstack==null){
						count += slot.stackSize;
					}else{
						if(slot.isItemEqual(itemstack)){
							count += slot.stackSize;
						}
					}
				}
			}	
		}else{
			for (int j = 0; j < indexes.length; j++) {
				int i=indexes[j];
				ItemStack slot = inv.getStackInSlot(i);
				if (slot != null) {
					if(itemstack==null){
						count += slot.stackSize;
					}else{
						if(slot.isItemEqual(itemstack)){
							count += slot.stackSize;
						}
					}
				}
			}	
		}
		return count;
	}
	
	public static int getInventoryFreeSlots(IInventory inv){
		return getInventoryFreeSlots(inv, (int[])null);
	}
	
	public static int getInventoryFreeSlots(IInventory inv, PC_Direction side){
		return getInventoryFreeSlots(inv, getInvIndexesForSide(inv, side));
	}
	
	public static int getInventoryFreeSlots(IInventory inv, int[] indexes){
		int freeSlots=0;
		if(indexes==null){
			int size = inv.getSizeInventory();
			for (int i = 0; i < size; i++) {
				ItemStack slot = inv.getStackInSlot(i);
				if (slot == null) {
					freeSlots++;
				}
			}
		}else{
			for (int j = 0; j < indexes.length; j++) {
				int i=indexes[j];
				ItemStack slot = inv.getStackInSlot(i);
				if (slot == null) {
					freeSlots++;
				}
			}
		}
		return freeSlots;
	}
	
	public static int getInventoryFullSlots(IInventory inv){
		return getInventoryFullSlots(inv, (int[])null);
	}
	
	public static int getInventoryFullSlots(IInventory inv, PC_Direction side){
		return getInventoryFullSlots(inv, getInvIndexesForSide(inv, side));
	}
	
	public static int getInventoryFullSlots(IInventory inv, int[] indexes){
		int fullSlots=0;
		if(indexes==null){
			int size = inv.getSizeInventory();
			for (int i = 0; i < size; i++) {
				ItemStack slot = inv.getStackInSlot(i);
				if (slot != null) {
					fullSlots++;
				}
			}
		}else{
			for (int j = 0; j < indexes.length; j++) {
				int i=indexes[j];
				ItemStack slot = inv.getStackInSlot(i);
				if (slot != null) {
					fullSlots++;
				}
			}
		}
		return fullSlots;
	}
	
	public static void moveStacks(IInventory from, IInventory to){
		moveStacks(from, (int[])null, to, (int[])null);
	}
	
	public static void moveStacks(IInventory from, PC_Direction fromSide, IInventory to, PC_Direction toSide) {
		moveStacks(from, getInvIndexesForSide(from, fromSide), to, toSide);
	}
	
	public static void moveStacks(IInventory from, int[] indexes, IInventory to, PC_Direction toSide) {
		moveStacks(from, indexes, to, getInvIndexesForSide(to, toSide));
	}
	
	public static void moveStacks(IInventory from, PC_Direction fromSide, IInventory to, int[] indexes) {
		moveStacks(from, getInvIndexesForSide(from, fromSide), to, indexes);
	}
	
	public static void moveStacks(IInventory from, int[] fromIndexes, IInventory to, int[] toIndexes) {
		if(fromIndexes==null){
			int size = from.getSizeInventory();
			for (int i = 0; i < size; i++) {
				if (from.getStackInSlot(i) != null) {
					
					storeItemStackToInventoryFrom(to, from.getStackInSlot(i), toIndexes);
	
					if (from.getStackInSlot(i) != null && from.getStackInSlot(i).stackSize <= 0) {
						from.setInventorySlotContents(i, null);
					}
				}
			}
		}else{
			for (int j = 0; j < fromIndexes.length; j++) {
				int i=fromIndexes[j];
				if (from.getStackInSlot(i) != null) {
					
					storeItemStackToInventoryFrom(to, from.getStackInSlot(i), toIndexes);
	
					if (from.getStackInSlot(i) != null && from.getStackInSlot(i).stackSize <= 0) {
						from.setInventorySlotContents(i, null);
					}
				}
			}
		}
	}
	
	public static ItemStack[] groupStacks(ItemStack[] input) {
		List<ItemStack> list = stacksToList(input);
		groupStacks(list);
		return stacksToArray(list);
	}

	public static void groupStacks(List<ItemStack> input) {
		if (input == null) {
			return;
		}

		for (ItemStack st1 : input) {
			if (st1 != null) {
				for (ItemStack st2 : input) {
					if (st2 != null && st2.isItemEqual(st1)) {
						int movedToFirst = Math.min(st2.stackSize, st1.getMaxStackSize()
								- st1.stackSize);

						if (movedToFirst <= 0) {
							break;
						}

						st1.stackSize += movedToFirst;
						st2.stackSize -= movedToFirst;
					}
				}
			}
		}

		ArrayList<ItemStack> copy = new ArrayList<ItemStack>(input);

		for (int i = copy.size() - 1; i >= 0; i--) {
			if (copy.get(i) == null || copy.get(i).stackSize <= 0) {
				input.remove(i);
			}
		}
	}

	public static List<ItemStack> stacksToList(ItemStack[] stacks) {
		ArrayList<ItemStack> myList = new ArrayList<ItemStack>();
		Collections.addAll(myList, stacks);
		return myList;
	}

	public static ItemStack[] stacksToArray(List<ItemStack> stacks) {
		return stacks.toArray(new ItemStack[stacks.size()]);
	}

	public static void dropInventoryContent(IInventory inventory, World world, double x, double y, double z) {
		if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
			int size = inventory.getSizeInventory();
			for (int i = 0; i < size; i++) {
				if(inventory instanceof PC_IInventory){
					if(!((PC_IInventory)inventory).canDropStack(i))
						continue;
				}
				ItemStack itemStack = inventory.getStackInSlot(i);
				if (itemStack != null) {
					inventory.setInventorySlotContents(i, null);
					PC_Utils.spawnItem(world, x, y, z, itemStack);
				}
			}
		}
	}
	
	public static int useFuel(IInventory inv, World world, PC_Vec3 pos) {
		return useFuel(inv, (int[])null, world, pos);
	}
	
	public static int useFuel(IInventory inv, PC_Direction side, World world, PC_Vec3 pos) {
		return useFuel(inv, getInvIndexesForSide(inv, side), world, pos);
	}
	
	public static int useFuel(IInventory inv, int[] indexes, World world, PC_Vec3 pos) {
		if(indexes==null){
			int size = inv.getSizeInventory();
			for (int i = 0; i < size; i++) {
				ItemStack is = inv.getStackInSlot(i);
				int fuel = PC_Utils.getBurnTime(is);
				if (fuel > 0) {
					inv.decrStackSize(i, 1);
					ItemStack container = is.getItem().getContainerItem(is);
					if (container != null) {
						storeItemStackToInventoryFrom(inv, container, indexes);
						if (container.stackSize > 0) {
							PC_Utils.spawnItem(world, pos, container);
						}
					}
					return fuel;
				}
			}
		}else{
			for (int j = 0; j < indexes.length; j++) {
				int i=indexes[j];
				ItemStack is = inv.getStackInSlot(i);
				int fuel = PC_Utils.getBurnTime(is);
				if (fuel > 0) {
					inv.decrStackSize(i, 1);
					ItemStack container = is.getItem().getContainerItem(is);
					if (container != null) {
						storeItemStackToInventoryFrom(inv, container, indexes);
						if (container.stackSize > 0) {
							PC_Utils.spawnItem(world, pos, container);
						}
					}
					return fuel;
				}
			}
		}
		return 0;
	}
	
}
