package powercraft.api.lists;

import java.util.HashMap;

import powercraft.api.item.PC_Item;

/**
 * @author James
 * What the custom items added are
 */
 // TODO same things like in CustomBlocksList
public class CustomItemsList {

	/**
	 * The blocks list
	 */
	public HashMap<String, PC_Item> items;
	
	/**
	 * Adds an item to the items list
	 * @param item The item to be added
	 */
	public void addItem(PC_Item item){
		this.items.put(item.itemName, item);
	}
}
