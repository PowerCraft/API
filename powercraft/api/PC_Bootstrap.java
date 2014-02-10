package powercraft.api;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

class PC_Bootstrap {

	private static boolean loaded;
	
	static void prepare() {
		try{
			if(FMLCommonHandler.instance().getSide()==Side.CLIENT){
				Class.forName("powercraft.api.PC_ClientUtils").newInstance();
			}else{
				Class.forName("powercraft.api.PC_Utils").newInstance();
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException("Failed to initialize PowerCraft");
		}
		PC_Logger.init(PC_Utils.getPowerCraftFile(null, PC_Api.POWERCRAFT+".log"));
		loaded = true;
	}

	public static boolean isLoaded(){
		return loaded;
	}
	
}
