package powercraft.api.village;

import java.util.ArrayList;
import java.util.List;

import powercraft.api.PC_Api;
import powercraft.api.PC_ImmutableList;
import powercraft.api.PC_Logger;
import powercraft.api.PC_Utils;
import powercraft.api.reflect.PC_Security;


public final class PC_Buildings {

	private static boolean done;
	private static List<PC_Building> buildings = new ArrayList<PC_Building>();
	private static List<PC_Building> immutableBuildings = new PC_ImmutableList<PC_Building>(buildings);
	
	static void addBuilding(PC_Building building) {
		if(done){
			PC_Logger.severe("A building want to register while startup is done");
		}else{
			PC_Logger.info("Building-ADD: %s", building);
			buildings.add(building);
		}
	}
	
	public static List<PC_Building> getBlocks(){
		return immutableBuildings;
	}

	public static void construct(){
		PC_Security.allowedCaller("PC_Buildings.construct()", PC_Api.class);
		if(!done){
			done = true;
			for(PC_Building building:buildings){
				PC_Logger.info("CONSTRUCT: %s", building);
				building.construct();
			}
		}
	}
	
	private PC_Buildings(){
		PC_Utils.staticClassConstructor();
	}
	
}
