package powercraft.api.script.weasel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_Lang;
import powercraft.api.PC_Logger;
import powercraft.api.PC_Utils;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.autoadd.PC_AutoCompleteDisplay;
import powercraft.api.gres.doc.PC_GresDocument;
import powercraft.api.gres.doc.PC_GresDocumentLine;
import powercraft.api.script.PC_DiagnosticTranslater;


public final class PC_Weasel {
	
	public static final PC_DiagnosticTranslater DIAGNOSTIC_TRANSLATER = new PC_DiagnosticTranslater() {
		
		@Override
		public String translate(String message, String[] args, Locale locale) {
			if(args==null){
				return PC_Lang.translate("powercraft.weasel."+message);
			}
			return PC_Lang.translate("powercraft.weasel."+message, Arrays.copyOf(args, args.length, Object[].class));
		}
	};
	
	private static PC_WeaselModule weaselModule;
	
	@SuppressWarnings("hiding")
	public static void register(PC_WeaselModule weaselModule){
		PC_Weasel.weaselModule = weaselModule;
	}

	public static boolean isWeaselPresent(){
		return weaselModule!=null;
	}
	
	public static PC_WeaselContainer createContainer(String deviceName, int memSize){
		if(weaselModule==null)
			return null;
		return weaselModule.createContainer(deviceName, memSize);
	}
	
	private PC_Weasel(){
		throw new InstantiationError();
	}
	
	public static void saveAs(String name, PC_WeaselContainer classSave){
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
	
	/*public static PC_WeaselContainer loadFrom(String name){
		if(PC_Utils.isClient()){
			File file = PC_Utils.getPowerCraftFile("code/weasel", name);
			if(!file.exists())
				return null;
			PC_WeaselContainer classSave = PC_Weasel.createClassSave(null);
			for(File c:file.listFiles()){
				loadFromInt(c, null, classSave);
			}
			return classSave;
		}
		return null;
	}*/
	
	private static void loadFromInt(File file, String name, PC_WeaselContainer classSave){
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

	@SideOnly(Side.CLIENT)
	public static void makeComplete(PC_GresComponent component, PC_GresDocument document, PC_GresDocumentLine line, int x, PC_AutoCompleteDisplay info, PC_WeaselGresEdit weaselGresEdit) {
		if(weaselModule!=null)
			weaselModule.makeComplete(component, document, line, x, info, weaselGresEdit);
	}
	
}
