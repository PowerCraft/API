package powercraft.api;

import java.util.ArrayList;
import java.util.List;

import powercraft.api.PC_Api;
import powercraft.api.PC_ImmutableList;
import powercraft.api.PC_Logger;
import powercraft.api.reflect.PC_Security;

public final class PC_Modules {

	private static boolean done;
	private static List<PC_Module> modules = new ArrayList<PC_Module>();
	private static List<PC_Module> immutableModules = new PC_ImmutableList<PC_Module>(modules);
	
	static void addModule(PC_Module module) {
		if(done){
			PC_Logger.severe("A module want to register while startup is done");
		}else{
			PC_Logger.info("Module-ADD: %s", module);
			modules.add(module);
		}
	}
	
	public static List<PC_Module> getModules(){
		return immutableModules;
	}

	public static void construct(){
		PC_Security.allowedCaller("PC_Modules.construct()", PC_Api.class);
		if(!done){
			done = true;
		}
	}
	
	private PC_Modules(){
		throw new InstantiationError();
	}
	
}
