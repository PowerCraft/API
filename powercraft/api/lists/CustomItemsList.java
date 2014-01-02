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
	 * The block names list
	 */
	public String[] itemNames;
	
	/**
	 * Adds an item to the items list
	 * @param item The item to be added
	 * @param name The item name to be added
	 */
	public void addItem(Item item, String name){
		Item[] tmp = new Item[this.itemsAmount + 1];
		String[] tmp2 = new String[this.itemsAmount + 1];
		for(int i = 0; i < this.itemsAmount + 1; i++){
			tmp[i] = this.items[i];
			tmp2[i] = this.itemNames[i];
		}
		tmp[this.itemsAmount] = item;
		tmp2[this.itemsAmount] = name;
		this.items = tmp;
		this.itemNames = tmp2;
	}
}
