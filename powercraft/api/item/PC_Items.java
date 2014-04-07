package powercraft.api.item;

import java.util.ArrayList;
import java.util.List;

import powercraft.api.PC_Api;
import powercraft.api.PC_ImmutableList;
import powercraft.api.PC_Logger;
import powercraft.api.reflect.PC_Security;

public final class PC_Items {

	private static boolean doneConstruct;
	private static boolean doneRecipes;
	private static List<PC_IItem> items = new ArrayList<PC_IItem>();
	private static List<PC_IItem> immutableItems = new PC_ImmutableList<PC_IItem>(items);
	
	static void addItem(PC_IItem item) {
		if(doneConstruct){
			PC_Logger.severe("A item want to register while startup is done");
		}else{
			PC_Logger.info("Item-ADD: %s", item);
			items.add(item);
		}
	}
	
	public static List<PC_IItem> getItems(){
		return immutableItems;
	}
	
	public static void construct(){
		PC_Security.allowedCaller("PC_Items.construct()", PC_Api.class);
		if(!doneConstruct){
			doneConstruct = true;
			for(PC_IItem item:items){
				PC_Logger.info("CONSTRUCT: %s", item);
				item.construct();
			}
		}
	}
	
	public static void initRecipes() {
		PC_Security.allowedCaller("PC_Items.initRecipes()", PC_Api.class);
		if(!doneRecipes && doneConstruct){
			doneRecipes = true;
			for(PC_IItem item:items){
				item.initRecipes();
			}
		}
	}
	
	private PC_Items(){
		throw new InstantiationError();
	}
	
}
