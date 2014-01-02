package powercraft.api.item;

import powercraft.api.PowerCraft;

/**
 * @author James
 * Powercraft's item
 */
public class Item extends net.minecraft.item.Item{
		
	@SuppressWarnings("javadoc")
	public Item(int par1) {
		super(par1);
	}

	// MASSIVE todo
	
	/**
	 * The name of the item
	 */
	public String itemName = "";
	
	/**
	 * Call this, after the block is COMPLETE.
	 */
	public final void register(){
		PowerCraft.pc.itemsList.addItem(this);
	}
}
