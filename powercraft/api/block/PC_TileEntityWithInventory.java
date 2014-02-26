package powercraft.api.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Field;
import powercraft.api.PC_Utils;
import powercraft.api.inventory.PC_ISidedInventory;


public class PC_TileEntityWithInventory extends PC_TileEntity implements PC_ISidedInventory {

	private final String name;
	@PC_Field
	protected ItemStack[] inventoryContents;
	@PC_Field
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
		return inventoryContents.length;
	}
	
	@Override
	public ItemStack getStackInSlot(int i) {
		return inventoryContents[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (inventoryContents[i] != null) {
			ItemStack itemstack;
			if (inventoryContents[i].stackSize <= j) {
				itemstack = this.inventoryContents[i];
				inventoryContents[i] = null;
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
		inventoryContents[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
		markDirty();
	}

	@Override
	public String getInventoryName() {
		return name;
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

	}

	@Override
	public void closeInventory() {

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
		return groups[groupID].slotIds;
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int side) {
		return isSlotCompatibleWithSide(i, side);
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int side) {
		return isSlotCompatibleWithSide(i, side);
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
		PC_Utils.onTick(this, worldObj);
	}
	
	@Override
	public int[] getAppliedSides(int i) {
		List<Integer> sides = new ArrayList<Integer>();
		for(int j=0; j<side2IdMaper.length; j++){
			int groupID = side2IdMaper[j];
			if(groupID!=-1 && !sides.contains(groupID)){
				int[] slotIds = groups[groupID].slotIds;
				for(int k=0; k<slotIds.length; k++){
					if(slotIds[k] == i){
						sides.add(groupID);
						break;
					}
				}
			}
		}
		if(sides.isEmpty())
			return null;
		int[] a = new int[sides.size()];
		for(int j=0; j<a.length; j++){
			a[j] = sides.get(j);
		}
		Arrays.sort(a);
		return a;
	}
	
	public int getIDForSide(int side){
		return getIDForSide(PC_Direction.fromSide(side));
	}
	
	public int getIDForSide(PC_Direction side){
		return side2IdMaper[side.ordinal()];
	}
	
	public boolean isSlotCompatibleWithSide(int i, int side){
		int groupID = getIDForSide(side);
		if(groupID==-1)
			return false;
		int[] slotsForSide = groups[groupID].slotIds;
		for(int j=0; j<slotsForSide.length; j++){
			if(slotsForSide[j]==i)
				return true;
		}
		return false;
	}
	
	@Override
	public void onBreak() {
		super.onBreak();
		PC_Utils.dropInventoryContent(this, worldObj, xCoord, yCoord, zCoord);
	}

	public int getGroupCount() {
		return groups.length;
	}

	public void setSideGroup(int i, int j) {
		side2IdMaper[i] = j;
	}
	
	public int getSideGroup(int i) {
		return side2IdMaper[i];
	}

	@Override
	public IIcon getFrontIcon() {
		return ((PC_AbstractBlockBase)getBlockType()).getIcon(worldObj, xCoord, yCoord, zCoord, PC_Direction.NORTH);
	}
	
}
