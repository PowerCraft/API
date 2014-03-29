package powercraft.api.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Field;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Field.Flag;
import powercraft.api.inventory.PC_ISidedInventory;
import powercraft.api.inventory.PC_InventoryUtils;


public class PC_TileEntityWithInventory extends PC_TileEntity implements PC_ISidedInventory {

	private final String name;
	@PC_Field
	protected ItemStack[] inventoryContents;
	@PC_Field(flags={Flag.SAVE, Flag.SYNC})
	private int side2IdMaper[] = {-1, -1, -1, -1, -1, -1};
	private final Group groups[];
	
	public static class Group{
		
		public boolean input;
		
		public int slotIds[];
		
		public Group(boolean input, int ...slotIds){
			this.input = input;
			this.slotIds = slotIds;
		}
		
	}
	
	public PC_TileEntityWithInventory(String name, int size, Group... groups){
		this.name = name;
		this.inventoryContents = new ItemStack[size];
		this.groups = groups;
	}
	
	@Override
	public int getSizeInventory() {
		return this.inventoryContents.length;
	}
	
	@Override
	public ItemStack getStackInSlot(int i) {
		return this.inventoryContents[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (this.inventoryContents[i] != null) {
			ItemStack itemstack;
			if (this.inventoryContents[i].stackSize <= j) {
				itemstack = this.inventoryContents[i];
				this.inventoryContents[i] = null;
				markDirty();
				return itemstack;
			} 
			itemstack = this.inventoryContents[i].splitStack(j);
			if (this.inventoryContents[i].stackSize == 0) {
				this.inventoryContents[i] = null;
			}
			markDirty();
			return itemstack;
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (this.inventoryContents[i] != null) {
			ItemStack itemstack = this.inventoryContents[i];
			this.inventoryContents[i] = null;
			return itemstack;
		} 
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		this.inventoryContents[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
		markDirty();
	}

	public void moveOrStore(int i, ItemStack itemstack){
		ItemStack itemStack2 = itemstack;
		int[] sides = getAppliedSides(i);
		if(sides!=null){
			List<PC_Direction> sideList = new ArrayList<PC_Direction>(sides.length);
			for(int j=0; j<sides.length; j++){
				sideList.add(PC_Direction.fromSide(sides[j]));
			}
			while(!sideList.isEmpty() && itemStack2!=null){
				PC_Direction side = sideList.remove((int)(Math.random()*sideList.size()));
				itemStack2 = PC_InventoryUtils.tryToStore(this.worldObj, this.xCoord+side.offsetX, this.yCoord+side.offsetY, this.zCoord+side.offsetZ, side.getOpposite(), itemstack);
			}
		}
		if(itemStack2!=null){
			setInventorySlotContents(i, itemStack2);
		}
	}
	
	@Override
	public String getInventoryName() {
		return this.name;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void openInventory() {
		//
	}

	@Override
	public void closeInventory() {
		//
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		int groupID = getIDForSide(side);
		if(groupID==-1){
			return new int[0];
		}
		return this.groups[groupID].slotIds;
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int side) {
		return isSlotCompatibleWithSide(i, side, true);
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int side) {
		return isSlotCompatibleWithSide(i, side, false);
	}

	@Override
	public int getSlotStackLimit(int i) {
		return getInventoryStackLimit();
	}

	@Override
	public boolean canTakeStack(int i, EntityPlayer entityPlayer) {
		return true;
	}
	
	@Override
	public boolean canDropStack(int i) {
		return true;
	}
	
	@Override
	public void onTick(World world) {
		PC_InventoryUtils.onTick(this, this.worldObj);
	}
	
	@Override
	public int[] getAppliedGroups(int i) {
		List<Integer> sides = new ArrayList<Integer>();
		for(int j=0; j<this.side2IdMaper.length; j++){
			int groupID = this.side2IdMaper[j];
			if(groupID!=-1){
				Integer gid = Integer.valueOf(groupID);
				if(!sides.contains(gid)){
					int[] slotIds = this.groups[groupID].slotIds;
					for(int k=0; k<slotIds.length; k++){
						if(slotIds[k] == i){
							sides.add(gid);
							break;
						}
					}
				}
			}
		}
		if(sides.isEmpty())
			return null;
		int[] a = new int[sides.size()];
		for(int j=0; j<a.length; j++){
			a[j] = sides.get(j).intValue();
		}
		Arrays.sort(a);
		return a;
	}
	
	@Override
	public int[] getAppliedSides(int i) {
		List<Integer> sides = new ArrayList<Integer>();
		for(int j=0; j<this.side2IdMaper.length; j++){
			int groupID = this.side2IdMaper[j];
			if(groupID!=-1 && !this.groups[groupID].input){
				int[] slotIds = this.groups[groupID].slotIds;
				for(int k=0; k<slotIds.length; k++){
					if(slotIds[k] == i){
						sides.add(Integer.valueOf(j));
						break;
					}
				}
			}
		}
		if(sides.isEmpty())
			return null;
		int[] a = new int[sides.size()];
		for(int j=0; j<a.length; j++){
			a[j] = PC_Utils.getSidePosition(this.worldObj, this.xCoord, this.yCoord, this.zCoord, sides.get(j).intValue()).ordinal();
		}
		Arrays.sort(a);
		return a;
	}
	
	public int getIDForSide(int side){
		return getIDForSide(PC_Direction.fromSide(side));
	}
	
	public int getIDForSide(PC_Direction side){
		PC_Direction rside = PC_Utils.getSidePositionInv(this.worldObj, this.xCoord, this.yCoord, this.zCoord, side);
		return this.side2IdMaper[rside.ordinal()];
	}
	
	public boolean isSlotCompatibleWithSide(int i, int side, boolean insert){
		int groupID = getIDForSide(side);
		if(groupID==-1)
			return false;
		if(this.groups[groupID].input!=insert)
			return false;
		int[] slotsForSide = this.groups[groupID].slotIds;
		for(int j=0; j<slotsForSide.length; j++){
			if(slotsForSide[j]==i)
				return true;
		}
		return false;
	}
	
	@Override
	public void onBreak() {
		super.onBreak();
		PC_InventoryUtils.dropInventoryContent(this, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public int getGroupCount() {
		return this.groups.length;
	}

	@Override
	public void setSideGroup(int i, int j) {
		this.side2IdMaper[i] = j;
		NBTTagCompound tagCompound = new NBTTagCompound();
		tagCompound.setInteger("type", 1);
		tagCompound.setInteger("i", i);
		tagCompound.setInteger("j", j);
		sendInternMessage(tagCompound);
	}
	
	@Override
	public void onInternMessage(EntityPlayer player, NBTTagCompound nbtTagCompound) {
		if(nbtTagCompound.getInteger("type")==1){
			this.side2IdMaper[nbtTagCompound.getInteger("i")] = nbtTagCompound.getInteger("j");
			sync();
			markDirty();
		}else{
			super.onInternMessage(player, nbtTagCompound);
		}
	}

	@Override
	public int getSideGroup(int i) {
		return this.side2IdMaper[i];
	}

	@Override
	public IIcon getFrontIcon() {
		return ((PC_AbstractBlockBase)getBlockType()).getIcon(this.worldObj, this.xCoord, this.yCoord, this.zCoord, PC_Direction.NORTH);
	}

	@Override
	public boolean canBeDragged(int i) {
		return true;
	}
	
}
