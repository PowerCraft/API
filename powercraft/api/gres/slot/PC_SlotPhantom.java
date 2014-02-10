package powercraft.api.gres.slot;

import net.minecraft.inventory.IInventory;

public class PC_SlotPhantom extends PC_Slot {

	public PC_SlotPhantom(IInventory inventory, int slotIndex) {
		super(inventory, slotIndex);
	}

	@Override
	public int getSlotStackLimit() {
		return 0;
	}
	
}
