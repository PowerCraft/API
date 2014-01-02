package powercraft.api.lists;

import powercraft.api.item.Item;

/**
 * @author James
 * What the custom items added are
 */
public class CustomItemsList {

	private int itemsAmount = 0;
	
	/**
	 * The blocks list
	 */
	public Item[] items;
	
	/**
	 * Adds an item to the items list
	 * @param item The item to be added
	 */
	public void addItem(Item item){
		Item[] tmp = new Item[this.itemsAmount + 1];
		for(int i = 0; i < this.itemsAmount + 1; i++){
			tmp[i] = this.items[i];
		}
		tmp[this.itemsAmount] = item;
		this.items = tmp;
	}
}
