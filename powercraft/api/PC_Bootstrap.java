package powercraft.api;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

final class PC_Bootstrap {

	private static boolean loaded;
	
	static void prepare() {
		try{
			if(FMLCommonHandler.instance().getSide()==Side.CLIENT){
				Class.forName("powercraft.api.PC_ClientUtils").newInstance();
				Class.forName("powercraft.api.PC_ClientRegistry").newInstance();
			}else{
				new PC_Utils();
				new PC_Registry();
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
