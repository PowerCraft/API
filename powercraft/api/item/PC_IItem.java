package powercraft.api.item;

import powercraft.api.PC_Module;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface PC_IItem {

	public void onTick(ItemStack itemStack, World world, IInventory inventory, int slot);
	
	public int getBurnTime(ItemStack fuel);

	public void construct();
	
	public PC_Module getModule();
	
	public String getRegisterName();
	
	public String getTextureFolderName();

	public String[] getOreNames();

	public void initRecipes();
	
}
