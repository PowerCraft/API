package powercraft.api.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class PC_WrapperInventory implements IInventory {

	private final ItemStack[] inventoryContents;
	
	public PC_WrapperInventory(ItemStack[] inventoryContents) {
		this.inventoryContents = inventoryContents;
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
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return this.inventoryContents[i];
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		//
	}

	@Override
	public String getInventoryName() {
		return "wrapper";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 0;
	}

	@Override
	public void markDirty() {
		//
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return false;
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
		return false;
	}

}
