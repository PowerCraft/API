package powercraft_new.api.item.crafting;

import powercraft_new.api.item.PC_ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * @author James
 * Make crafting registry from this
 */
public class CraftingRegistry {
	
	/**
	 * @param itemstack The item stack to have as output
	 * @param params The recipe data
	 */
	@SuppressWarnings("deprecation")
	public static void addRecipe(PC_ItemStack itemstack, Object... params){
		GameRegistry.addRecipe(itemstack.getStack(), params);
	}
	
	/**
	 * @param itemstack The item stack to have as output
	 * @param params The recipe data
	 */
	@SuppressWarnings("deprecation")
	public static void addShapeless(PC_ItemStack itemstack, Object...params){
		GameRegistry.addShapelessRecipe(itemstack.getStack(), params);
	}
}
