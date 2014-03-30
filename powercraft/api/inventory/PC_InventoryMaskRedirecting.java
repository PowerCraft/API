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

public class PC_InventoryMaskRedirecting extends PC_InventoryMask{
	
	private IInventory inventory;
	public PC_InventoryMaskRedirecting(PC_TileEntity entity, IInventory inventory, int inventoryStart, int inventoryLastIndex, String inventoryName, boolean isGhost, PC_AccessType[] sides){
		super(entity, null, inventoryStart, inventoryLastIndex, inventoryName, isGhost, sides);
		this.inventory = inventory;
	}
	
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		side = getUnrotatedSide(side);
		int[] tmp=null;
		if(this.sides!=null && !this.sides[side].isAccessible()) return tmp;
		if(inventory instanceof ISidedInventory){
			tmp = ((ISidedInventory)inventory).getAccessibleSlotsFromSide(side);
		}
		if(tmp==null){
			tmp=new int[]{};
		}
		return tmp;
	}

	@Override
	public boolean canInsertItem(int slotPos, ItemStack var2, int side) {
		boolean tmp=false;
		if(this.sides!=null && !sides[side].isIn()) return tmp;
		if(inventory instanceof ISidedInventory){
			tmp = ((ISidedInventory) inventory).canInsertItem(inventoryStart + slotPos, var2, side);
		}
		return tmp;
	}

	@Override
	public boolean canExtractItem(int slotPos, ItemStack var2, int side) {
		boolean tmp=false;
		if(this.sides!=null && !sides[side].isOut()) return tmp;
		if(inventory instanceof ISidedInventory){
			tmp = ((ISidedInventory) inventory).canExtractItem(inventoryStart + slotPos, var2, side);
		}
		return tmp;
	}

	@Override
	public String getInventoryName() {
		String tmp=super.getInventoryName();
		if(inventoryName==null && inventory!=null){
			tmp = inventory.getInventoryName();
		}
		return tmp;
	}

	@Override
	public boolean hasCustomInventoryName() {
		boolean tmp=true;
		if(inventory!=null){
			tmp = inventory.hasCustomInventoryName();
		}
		return tmp;
	}

	@Override
	public int getInventoryStackLimit() {
		int tmp=64;
		if(inventory!=null){
			tmp = inventory.getInventoryStackLimit();
		}
		return tmp;
	}

	@Override
	public void markDirty() {
		if(inventory!=null){
			inventory.markDirty();
		}
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		boolean tmp=true;
		if(inventory!=null){
			tmp = inventory.isUseableByPlayer(var1);
		}
		return tmp;
	}

	@Override
	public void openInventory() {
		if(inventory!=null){
			inventory.openInventory();
		}
	}

	@Override
	public void closeInventory() {
		if(inventory!=null){
			inventory.closeInventory();
		}
	}

	@Override
	public boolean isItemValidForSlot(int slotPos, ItemStack var2) {
		boolean tmp=false;
		if(inventory!=null){
			tmp = inventory.isItemValidForSlot(inventoryStart + slotPos, var2);
		}
		return tmp;
	}

	@Override
	public int getSlotStackLimit(int slotPos) {
		int tmp=getInventoryStackLimit();
		if(inventory instanceof PC_IInventory){
			tmp = ((PC_IInventory) inventory).getSlotStackLimit(inventoryStart + slotPos);
		}
		return tmp;
	}

	@Override
	public boolean canTakeStack(int slotPos, EntityPlayer entityPlayer) {
		boolean tmp=false;
		if(inventory instanceof PC_IInventory){
			tmp = ((PC_IInventory) inventory).canTakeStack(inventoryStart + slotPos, entityPlayer);
		}
		return tmp;
	}

	@Override
	public boolean canDropStack(int slotPos) {
		boolean tmp=true;
		if(inventory instanceof PC_IInventory){
			tmp = ((PC_IInventory) inventory).canDropStack(inventoryStart + slotPos);
		}
		return tmp;
	}

	@Override
	public void onTick(World world) {
		if(inventory instanceof PC_IInventory){
			((PC_IInventory)inventory).onTick(world);
		}
	}

	@Override
	public int[] getAppliedGroups(int slotPos) {
		int[] tmp=null;
		if(inventory instanceof PC_IInventory){
			tmp = ((PC_IInventory)inventory).getAppliedGroups(inventoryStart + slotPos);
		}
		return tmp;
	}

	@Override
	public int[] getAppliedSides(int slotPos) {
		int[] tmp=null;
		if(this.tileEntity==null || this.sides==null){
			if(inventory instanceof PC_IInventory){
				tmp = ((PC_IInventory)inventory).getAppliedSides(inventoryStart + slotPos);
			}
		}else{
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


	@Override
	public boolean canBeDragged(int slotPos) {
		boolean tmp=true;
		if(inventory instanceof PC_IInventory){
			tmp = ((PC_IInventory) inventory).canBeDragged(inventoryStart + slotPos);
		}
		return tmp;
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
