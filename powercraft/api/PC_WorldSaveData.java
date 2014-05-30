package powercraft.api;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.WorldSavedData;


public abstract class PC_WorldSaveData extends WorldSavedData {
	
	private static List<PC_WorldSaveData> datas = new ArrayList<PC_WorldSaveData>();
	
	public PC_WorldSaveData(String name) {
		super(name);
	}
	
	public void cleanup(){
		//
	}
	
	static void onServerStopping(){
		for(PC_WorldSaveData data:datas){
			data.cleanup();
		}
		datas.clear();
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends PC_WorldSaveData> T loadOrCreate(String name, Class<T> c){
		T t = (T) PC_Utils.mcs().worldServerForDimension(0).mapStorage.loadData(c, name);
		if(t==null){
			try{
				t = c.getConstructor(String.class).newInstance(name);
			}catch(Exception e){
				PC_Logger.severe("Can't create %s", name);
			}
			if(t!=null)
				PC_Utils.mcs().worldServerForDimension(0).mapStorage.setData(name, t);
		}
		return t;
	}
	
}
