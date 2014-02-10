package powercraft.api.gres.slot;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import powercraft.api.inventory.PC_IInventory;


public class PC_Slot extends Slot {

	private ItemStack backgroundStack;
	private boolean renderGrayWhenEmpty;


	public PC_Slot(IInventory inventory, int slotIndex) {

		super(inventory, slotIndex, 0, 0);
		slotNumber = -1;
	}


	@Override
	public boolean isItemValid(ItemStack itemStack) {

		return inventory.isItemValidForSlot(getSlotIndex(), itemStack);
	}


	@Override
	public int getSlotStackLimit() {

		if (inventory instanceof PC_IInventory) {
			return ((PC_IInventory) inventory).getSlotStackLimit(getSlotIndex());
		}
		return super.getSlotStackLimit();
	}


	@Override
	public boolean canTakeStack(EntityPlayer entityPlayer) {

		if (inventory instanceof PC_IInventory) {
			return ((PC_IInventory) inventory).canTakeStack(getSlotIndex(), entityPlayer);
		}
		return super.canTakeStack(entityPlayer);
	}

	public boolean canDragIntoSlot() {
		return true;
	}
	
	public void setBackgroundStack(ItemStack backgroundStack) {

		this.backgroundStack = backgroundStack;
	}


	public ItemStack getBackgroundStack() {

		return backgroundStack;
	}


	public void setRenderGrayWhenEmpty(boolean renderGrayWhenEmpty) {

		this.renderGrayWhenEmpty = renderGrayWhenEmpty;
	}


	public boolean renderGrayWhenEmpty() {

		return renderGrayWhenEmpty;
	}

	public int[] getAppliedSides() {
		if(inventory instanceof PC_IInventory){
			return ((PC_IInventory)inventory).getAppliedSides(getSlotIndex());
		}
		return null;
	}
	
}
