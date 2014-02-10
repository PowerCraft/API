package powercraft.api.item;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface PC_IItem {

	public void onTick(ItemStack itemStack, World world, IInventory inventory, int slot);
	
	public int getBurnTime(ItemStack fuel);
	
}
