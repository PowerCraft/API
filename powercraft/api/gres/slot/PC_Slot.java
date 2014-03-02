package powercraft.api.gres.slot;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import powercraft.api.inventory.PC_IInventory;
import powercraft.api.inventory.PC_IInventoryBackground;


public class PC_Slot extends Slot {

	private ItemStack backgroundStack;
	private boolean renderGrayWhenEmpty;


	public PC_Slot(IInventory inventory, int slotIndex) {

		super(inventory, slotIndex, 0, 0);
		this.slotNumber = -1;
	}


	@Override
	public boolean isItemValid(ItemStack itemStack) {

		return this.inventory.isItemValidForSlot(getSlotIndex(), itemStack);
	}


	@Override
	public int getSlotStackLimit() {

		if (this.inventory instanceof PC_IInventory) {
			return ((PC_IInventory) this.inventory).getSlotStackLimit(getSlotIndex());
		}
		return super.getSlotStackLimit();
	}


	@Override
	public boolean canTakeStack(EntityPlayer entityPlayer) {

		if (this.inventory instanceof PC_IInventory) {
			return ((PC_IInventory) this.inventory).canTakeStack(getSlotIndex(), entityPlayer);
		}
		return super.canTakeStack(entityPlayer);
	}

	@SuppressWarnings("static-method")
	public boolean canDragIntoSlot() {
		return true;
	}
	
	public void setBackgroundStack(ItemStack backgroundStack) {

		this.backgroundStack = backgroundStack;
	}


	public ItemStack getBackgroundStack() {
		if(this.inventory instanceof PC_IInventoryBackground){
			return ((PC_IInventoryBackground)this.inventory).getBackgroundStack(getSlotIndex());
		}
		return this.backgroundStack;
	}


	public void setRenderGrayWhenEmpty(boolean renderGrayWhenEmpty) {

		this.renderGrayWhenEmpty = renderGrayWhenEmpty;
	}


	public boolean renderGrayWhenEmpty() {
		if(this.inventory instanceof PC_IInventoryBackground){
			return ((PC_IInventoryBackground)this.inventory).renderGrayWhenEmpty(getSlotIndex());
		}
		return this.renderGrayWhenEmpty;
	}

	public int[] getAppliedSides() {
		if(this.inventory instanceof PC_IInventory){
			return ((PC_IInventory)this.inventory).getAppliedGroups(getSlotIndex());
		}
		return null;
	}
	
	@Override
	public ItemStack decrStackSize(int v) {
		ItemStack itemStack = super.decrStackSize(v);
		return itemStack.stackSize==0?null:itemStack;
	}
	
}
