package powercraft.api.dimension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import powercraft.api.PC_Api;
import powercraft.api.PC_ImmutableList;
import powercraft.api.PC_Logger;
import powercraft.api.reflect.PC_Security;

public final class PC_Dimensions {

	private static boolean done;
	private static List<PC_Dimension> dimensions = new ArrayList<PC_Dimension>();
	private static List<PC_Dimension> immutableDimensions = new PC_ImmutableList<PC_Dimension>(dimensions);
	private static HashMap<Class<? extends PC_WorldProvider>, PC_Dimension> providers = new HashMap<Class<? extends PC_WorldProvider>, PC_Dimension>();
	
	static void addDimensions(PC_Dimension dimension) {
		if(done){
			PC_Logger.severe("A dimension want to register while startup is done");
		}else{
			PC_Logger.info("Dimension-ADD: %s", dimension);
			dimensions.add(dimension);
			providers.put(dimension.getWorldProvider(), dimension);
		}
	}
	
	public static List<PC_Dimension> getDimensions(){
		return immutableDimensions;
	}

	public static void construct(){
		PC_Security.allowedCaller("PC_Dimensions.construct()", PC_Api.class);
		if(!done){
			done = true;
			for(PC_Dimension dimension:dimensions){
				PC_Logger.info("CONSTRUCT: %s", dimension);
				dimension.construct();
			}
		}
	}
	
	public static PC_Dimension getDimenstionForProvider(Class<? extends PC_WorldProvider> provider){
		return providers.get(provider);
	}
	
	private PC_Dimensions(){
		throw new InstantiationError();
	}
	
}
