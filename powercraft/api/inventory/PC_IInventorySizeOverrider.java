package powercraft.api.inventory;

import net.minecraft.item.ItemStack;


public interface PC_IInventorySizeOverrider {
	
	public int getMaxStackSize(ItemStack itemStack, int slot);
	
}
