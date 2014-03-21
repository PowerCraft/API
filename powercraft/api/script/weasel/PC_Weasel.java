package powercraft.api.script.weasel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import powercraft.api.PC_Logger;
import powercraft.api.PC_Utils;


public final class PC_Weasel {
	
	private static PC_WeaselModule weaselModule;
	
	@SuppressWarnings("hiding")
	public static void register(PC_WeaselModule weaselModule){
		PC_Weasel.weaselModule = weaselModule;
	}

	public static boolean isWeaselPresent(){
		return weaselModule!=null;
	}
	
	public static PC_WeaselClassSave createClassSave(boolean createDefault){
		if(weaselModule==null)
			return null;
		return weaselModule.createClassSave(createDefault);
	}
	
	public static PC_WeaselEngine createEngine(PC_WeaselClassSave classSave, int memSize, Object handler){
		if(weaselModule==null)
			return null;
		return weaselModule.createEngine(classSave, memSize, handler);
	}
	
	public static PC_WeaselEngine loadEngine(PC_WeaselClassSave classSave, byte[] data, Object handler){
		if(weaselModule==null)
			return null;
		return weaselModule.loadEngine(classSave, data, handler);
	}
	
	private PC_Weasel(){
		throw new InstantiationError();
	}
	
	public static void saveAs(String name, PC_WeaselClassSave classSave){
		if(PC_Utils.isClient()){
			File dir = PC_Utils.getPowerCraftFile("code/weasel", name);
			if(dir.exists())
				PC_Utils.deleteDirectoryOrFile(dir);
			HashMap<String, ? extends PC_WeaselSourceClass> hm = classSave.getSources();
			for(Entry<String, ? extends PC_WeaselSourceClass> e:hm.entrySet()){
				try {
					File file = new File(dir, e.getKey().replace('.', '/')+".txt");
					file.getParentFile().mkdirs();
					FileWriter fw = new FileWriter(file, false);
					fw.write(e.getValue().getSource());
					fw.close();
				} catch (IOException ee) {
					PC_Logger.throwing("PC_Miniscipt", "saveAs(String, String)", ee);
				}
			}
		}
	}
	
	public static boolean exists(String name){
		if(PC_Utils.isClient()){
			return PC_Utils.getPowerCraftFile("code/weasel", name).exists();
		}
		return true;
	}
	
	public static PC_WeaselClassSave loadFrom(String name){
		if(PC_Utils.isClient()){
			File file = PC_Utils.getPowerCraftFile("code/weasel", name);
			if(!file.exists())
				return null;
			PC_WeaselClassSave classSave = PC_Weasel.createClassSave(false);
			for(File c:file.listFiles()){
				loadFromInt(c, null, classSave);
			}
			return classSave;
		}
		return null;
	}
	
	private static void loadFromInt(File file, String name, PC_WeaselClassSave classSave){
		String cName = file.getName();
		if(file.isDirectory()){
			if(name!=null){
				cName = name+"."+cName;
			}
			for(File c:file.listFiles()){
				loadFromInt(c, cName, classSave);
			}
		}else{
			int index = cName.lastIndexOf('.');
			if(index!=-1){
				cName = cName.substring(0, index);
			}
			if(name!=null){
				cName = name+"."+cName;
			}
			String source = null;
			String line;
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				while((line=br.readLine())!=null){
					if(source==null){
						source += line;
					}else{
						source += "\n"+line;
					}
				}
				br.close();
			} catch (IOException e) {
				PC_Logger.throwing("PC_Miniscipt", "loadFrom(String)", e);
			}
			PC_WeaselSourceClass sc = classSave.addClass(name);
			sc.setSource(source);
		}
	}
	
}
