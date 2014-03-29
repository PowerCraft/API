package powercraft.api.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.PC_Logger;
import powercraft.api.PC_ParameterReturn;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec3I;
import powercraft.api.block.PC_TileEntity;

public class PC_InventoryMask implements PC_IInventory {
	
	public enum PC_AccessType{
		NONE(0), IN(1), OUT(2), BOTH(3);
		
		public final int typeValue;
		PC_AccessType(int type){
			typeValue = type;
		}
		
		public boolean isIn(){
			return this==IN || this==BOTH;
		}
		
		public boolean isOut(){
			return this==OUT || this==BOTH;
		}
		
		public boolean isAccessible(){
			return this!=NONE;
		}
		
		public static PC_AccessType fromValue(int num){
			for(PC_AccessType at:values()){
				if(num==at.typeValue) return at;
			}
			return NONE;
		}
	}
	
	protected IBlockAccess world;
	protected PC_Vec3I pos;
	
	protected final int inventoryStart;
	protected final int inventoryLastIndex;
	protected final int inventoryLength;
	protected final String inventoryName;
	protected final PC_AccessType[] sides;
	protected final boolean isGhost;
	
	protected final IInventory inventory;
	protected final PC_TileEntity tileEntity;
	
	public PC_InventoryMask(PC_TileEntity entity, IInventory inventory, int inventoryStart, int inventoryLastIndex, String inventoryName, boolean isGhost, PC_AccessType[] sides){
		this.tileEntity = entity;
		this.inventory = inventory;
		this.inventoryStart = inventoryStart;
		this.inventoryLastIndex = inventoryLastIndex;
		this.inventoryLength = inventoryLastIndex-inventoryStart+1;
		this.inventoryName = inventoryName;
		this.isGhost = isGhost;
		
		if(sides==null){
			this.sides = sides;
		}else{
			if(sides.length!=6)
				PC_Logger.severe("the \"side access type array\" of inventory:%s in type:%s hasn't had a length of 6 (ERROR CAUSED BY DEVs, COMMIT IT)", inventoryName, entity.toString());
			this.sides = new PC_AccessType[6];
			for(int i=0; i<6; i++){
				this.sides[i] = i<sides.length?sides[i]:PC_AccessType.NONE;
			}
		}
		
	}
	
	public boolean isSingleSlot(){
		return inventoryStart==inventoryLastIndex;
	}
	
	protected int getUnrotatedSide(int side){
		if(tileEntity==null)
			return 0;
		return PC_Utils.getSidePositionInv(tileEntity.getWorldObj(), tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, side).ordinal();
	}
	
	public int globalToLocalIndex(int slotPos){
		return slotPos-inventoryStart;
	}
	
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[]{};
	}

	@Override
	public boolean canInsertItem(int slotPos, ItemStack var2, int side) {
		return false;
	}

	@Override
	public boolean canExtractItem(int slotPos, ItemStack var2, int side) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return inventoryLength;
	}

	@Override
	public ItemStack getStackInSlot(int slotPos) {
		ItemStack tmp=null;
		if(slotPos>inventoryLength-1) return tmp;
		if(inventory!=null){
			tmp = inventory.getStackInSlot(inventoryStart + slotPos);
		}
		return tmp;
	}

	@Override
	public ItemStack decrStackSize(int slotPos, int amount) {
		ItemStack tmp=null;
		if(slotPos>inventoryLength-1) return tmp;
		if(inventory!=null){
			tmp = inventory.decrStackSize(inventoryStart + slotPos, amount);
		}
		return tmp;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slotPos) {
		ItemStack tmp=null;
		if(slotPos>inventoryLength-1) return tmp;
		if(inventory!=null){
			tmp = inventory.getStackInSlotOnClosing(inventoryStart + slotPos);
		}
		return tmp;
	}

	@Override
	public void setInventorySlotContents(int slotPos, ItemStack var2) {
		if(slotPos>inventoryLength-1) return;
		if(inventory!=null){
			inventory.setInventorySlotContents(inventoryStart + slotPos, var2);
		}
	}

	@Override
	public String getInventoryName() {
		return inventoryName==null?"":inventoryName;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return inventoryName!=null && !inventoryName.isEmpty();
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		if(tileEntity!=null)
			markDirty();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
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
	public boolean isItemValidForSlot(int slotPos, ItemStack var2) {
		return true;
	}

	@Override
	public int getSlotStackLimit(int slotPos) {
		return getInventoryStackLimit();
	}

	@Override
	public boolean canTakeStack(int slotPos, EntityPlayer entityPlayer) {
		return true;
	}

	@Override
	public boolean canDropStack(int slotPos) {
		return true;
	}

	@Override
	public void onTick(World world) {
		PC_InventoryUtils.onTick(this, world);
	}

	@Override
	public int[] getAppliedGroups(int slotPos) {
		return null;
	}

	@Override
	public int[] getAppliedSides(int slotPos) {
		int[] tmp=null;
		if(slotPos>inventoryLength-1) return tmp;
		if(this.tileEntity!=null && this.sides!=null){
			List<Integer> sides = new ArrayList<Integer>();
			for(int i=0; i<this.sides.length; i++){
				if(this.sides[i].isOut()) sides.add(i);
			}
			if(!sides.isEmpty()){
				tmp = new int[sides.size()];
				getWorldPos();
				for(int j=0; j<tmp.length; j++){
					tmp[j] = PC_Utils.getSidePosition(world, pos.x, pos.y, pos.z, sides.get(j).intValue()).ordinal();
				}
				Arrays.sort(tmp);
			}
		}
		return tmp;
	}
	
	protected void getWorldPos(){
		world = tileEntity.getWorldObj();
		pos = new PC_Vec3I(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
	}

	@Override
	public boolean canBeDragged(int i) {
		return !isGhost;
	}


	/*
	@Override
	public void setSideGroup(int side, int group) {
		if(inventory instanceof PC_ISidedInventory){
			((PC_ISidedInventory) inventory).setSideGroup(side, group);
		}
	}


	@Override
	public int getGroupCount() {
		int tmp=0;
		if(inventory instanceof PC_ISidedInventory){
			tmp = ((PC_ISidedInventory) inventory).getGroupCount();
		}
		return tmp;
	}


	@Override
	public int getSideGroup(int side) {
		int tmp=-1;
		if(inventory instanceof PC_ISidedInventory){
			tmp = ((PC_ISidedInventory) inventory).getSideGroup(side);
		}
		return tmp;
	}


	@Override
	public IIcon getFrontIcon() {
		IIcon tmp = null;
		if(inventory instanceof PC_ISidedInventory){
			tmp = ((PC_ISidedInventory) inventory).getFrontIcon();
		}
		return tmp;
	}*/

}
