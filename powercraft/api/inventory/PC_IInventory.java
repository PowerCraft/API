package powercraft.api.inventory;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;


public interface PC_IInventory extends ISidedInventory {

	public int getSlotStackLimit(int i);

	public boolean canTakeStack(int i, EntityPlayer entityPlayer);

	public boolean canDropStack(int i);
	
	public boolean canBeDragged(int i);

	public void onTick(World world);

	public int[] getAppliedGroups(int i);
	
	public int[] getAppliedSides(int i);
	
}
