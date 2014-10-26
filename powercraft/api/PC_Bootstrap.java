package powercraft.api;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

/**
 * First thing that is invoked after PC_Api
 * @author XOR
 *
 */
public final class PC_Bootstrap {

	/**
	 * has prepare been invoked
	 */
	private static boolean loaded;
	
	/**
	 * initialize Utils and Registry and logger
	 */
	static void prepare() {
		try{
			if(FMLCommonHandler.instance().getSide()==Side.CLIENT){
				Class.forName("powercraft.api.PC_ClientUtils");
				Class.forName("powercraft.api.PC_ClientRegistry");
			}else{
				new PC_Utils();
				new PC_Registry();
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException("Failed to initialize PowerCraft");
		}
		PC_Logger.init(PC_Utils.getPowerCraftFile(null, PC_Module.POWERCRAFT+".log"));
		PC_Debug.setup();
		loaded = true;
	}

	/**
	 * is Utils and Registry and Logger been loaded
	 * @return if all is loaded
	 */
	public static boolean isLoaded(){
		return loaded;
	}
	
	private PC_Bootstrap(){
		PC_Utils.staticClassConstructor();
	}
	
}
