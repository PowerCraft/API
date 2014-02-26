package powercraft.api.gres.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class PC_SlotPhantom extends PC_Slot {

	public PC_SlotPhantom(IInventory inventory, int slotIndex) {
		super(inventory, slotIndex);
	}

	@Override
	public int getSlotStackLimit() {
		return 0;
	}

	@Override
	public ItemStack decrStackSize(int v) {
		super.putStack(null);
		return null;
	}

	@Override
	public void putStack(ItemStack itemStack) {
		if(itemStack!=null){
			itemStack = itemStack.copy();
			itemStack.stackSize=0;
		}
		super.putStack(itemStack);
	}
	
}
