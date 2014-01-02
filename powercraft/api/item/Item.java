package powercraft.api.item;

import powercraft.api.PowerCraft;

/**
 * @author James
 * Powercraft's item
 */
public class Item {
		
	// MASSIVE todo
	
	/**
	 * The name of the item
	 */
	public String name = "";
	
	/**
	 * Call this, after the block is COMPLETE.
	 */
	public final void register(){
		PowerCraft.pc.itemsList.addItem(this);
	}
}
