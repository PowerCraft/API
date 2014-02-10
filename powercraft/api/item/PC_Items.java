package powercraft.api.item;

import java.util.ArrayList;
import java.util.List;

import powercraft.api.PC_Api;
import powercraft.api.PC_ImmutableList;
import powercraft.api.PC_Logger;
import powercraft.api.PC_Security;

public final class PC_Items {

	private static boolean done;
	private static List<PC_Item> items = new ArrayList<PC_Item>();
	private static List<PC_Item> immutableItems = new PC_ImmutableList<PC_Item>(items);
	
	static void addItem(PC_Item item) {
		if(done){
			PC_Logger.severe("A item want to register while startup is done");
		}else{
			PC_Logger.info("ADD: %s", item);
			items.add(item);
		}
	}
	
	public static List<PC_Item> getItems(){
		return immutableItems;
	}
	
	public static void construct(){
		PC_Security.allowedCaller("PC_Blocks.construct()", PC_Api.class);
		if(!done){
			done = true;
			for(PC_Item item:items){
				PC_Logger.info("CONSTRUCT: %s", item);
				item.construct();
			}
		}
	}
	
	private PC_Items(){
		throw new InstantiationError();
	}
	
}
