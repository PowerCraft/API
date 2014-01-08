package powercraft.api.item;

import powercraft.api.block.BlockUtils;
import powercraft.api.block.PC_Block;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @author James
 * Powercraft's version of a minecraft itemstack
 */
public class PC_ItemStack {
	private ItemStack i;
	
	/**
	 * @param id The item ID
	 * @param amount The amount in the itemstack
	 * @param meta The metadata
	 */
	public PC_ItemStack(int id, int amount, int meta){
		this.i = new ItemStack(id, amount, meta);
	}
	
	/**
	 * @param block The block type
	 * @param amount The amount in the itemstack
	 * @param meta The metadata
	 */
	public PC_ItemStack(PC_Block block, int amount, int meta){
		this.i = new ItemStack(block, amount, meta);
	}

	/**
	 * @param item The item type
	 * @param amount The amount in the itemstack
	 * @param meta The metadata
	 */
	public PC_ItemStack(PC_Item item, int amount, int meta){
		this.i = new ItemStack(item, amount, meta);
	}

	/**
	 * @param name The block/item name
	 * @param amount The amount in the itemstack
	 * @param meta The metadata
	 */
	public PC_ItemStack(String name, int amount, int meta){
		Item tmp1 = ItemUtils.getItemFromName(name);
		if(tmp1 != null) this.i = new ItemStack(tmp1, amount, meta);
		else {
			Block tmp2 = BlockUtils.getBlockFromName(name);
			if(tmp2 != null) this.i = new ItemStack(tmp2, amount, meta);
			else return;
		}
	}
	
	/**
	 * @return The item ID of the item stack
	 */
	public int getID(){
		return this.i.itemID;
	}
	
	/**
	 * @return The meta/damage of the item stack items
	 */
	public int getMeta(){
		return this.i.getItemDamage();
	}
	
	/**
	 * @return The amount of items in the itemstack
	 */
	public int getAmount(){
		return this.i.stackSize;
	}
	
	/**
	 * It works just fine and wont be removed
	 * We just dont want anybody using it
	 * @return The itemstack this represents
	 */
	@Deprecated
	public ItemStack getStack(){
		return this.i;
	}
}
