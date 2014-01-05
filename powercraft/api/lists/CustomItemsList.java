package powercraft.api.lists;

import powercraft.api.item.PC_Item;

/**
 * @author James
 * What the custom items added are
 */
 // TODO same things like in CustomBlocksList
public class CustomItemsList {

	private short itemsAmount = 0;
	
	/**
	 * The blocks list
	 */
	public PC_Item[] items;
	
	/**
	 * Adds an item to the items list
	 * @param item The item to be added
	 */
	public void addItem(PC_Item item){
		PC_Item[] tmp = new PC_Item[this.itemsAmount + 1];
		for(short i = 0; i < this.itemsAmount + 1; i++){
			tmp[i] = this.items[i];
		}
		tmp[this.itemsAmount] = item;
		this.items = tmp;
	}
}
