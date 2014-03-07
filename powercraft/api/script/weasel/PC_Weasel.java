package powercraft.api.script.weasel;


public final class PC_Weasel {
	
	private static PC_WeaselModule weaselModule;
	
	@SuppressWarnings("hiding")
	public static void register(PC_WeaselModule weaselModule){
		PC_Weasel.weaselModule = weaselModule;
	}

	public static boolean isWeaselPresent(){
		return weaselModule!=null;
	}
	
	public static PC_WeaselClassSave createClassSave(){
		if(weaselModule==null)
			return null;
		return weaselModule.createClassSave();
	}
	
	public static PC_WeaselEngine createEngine(PC_WeaselClassSave classSave, int memSize, PC_IWeaselNativeHandler handler){
		if(weaselModule==null)
			return null;
		return weaselModule.createEngine(classSave, memSize, handler);
	}
	
	public static PC_WeaselEngine loadEngine(PC_WeaselClassSave classSave, byte[] data, PC_IWeaselNativeHandler handler){
		if(weaselModule==null)
			return null;
		return weaselModule.loadEngine(classSave, data, handler);
	}
	
	private PC_Weasel(){
		throw new InstantiationError();
	}
	
}
