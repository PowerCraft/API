package powercraft.api.script.miniscript;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.tools.DiagnosticListener;

import miniscript.MiniScriptLang;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.item.Item;
import powercraft.api.PC_Api;
import powercraft.api.PC_Lang;
import powercraft.api.PC_Logger;
import powercraft.api.PC_Utils;
import powercraft.api.gres.autoadd.PC_StringWithInfo;
import powercraft.api.gres.font.PC_Formatter;
import powercraft.api.reflect.PC_Security;
import powercraft.api.script.PC_DiagnosticTranslater;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;

public final class PC_Miniscript {

	public static final PC_DiagnosticTranslater DIAGNOSTIC_TRANSLATER = new PC_DiagnosticTranslater() {
		
		@Override
		public String translate(String message, String[] args, Locale locale) {
			return PC_Lang.translate("powercraft.miniscript."+message, Arrays.copyOf(args, args.length, Object[].class));
		}
	};
	
	private static ScriptEngine scriptEngine;
	private static HashMap<String, Integer> defaultReplacements = new HashMap<String, Integer>();
	private static List<PC_StringWithInfo> defaultReplacementList = new ArrayList<PC_StringWithInfo>();
	private static List<String> defaultReplacementWorldList = new ArrayList<String>();
	
	public static void register(){
		PC_Security.allowedCaller("PC_Miniscript.register()", PC_Api.class);
		scriptEngine = new ScriptEngineManager().getEngineByName(MiniScriptLang.NAME);
		scriptEngine.getContext().setAttribute(MiniScriptLang.COMPILER_BACKJUMPDISABLED, Boolean.TRUE, ScriptContext.ENGINE_SCOPE);
	}
	
	public static List<PC_StringWithInfo> getDefaultReplacements(){
		return defaultReplacementList;
	}
	
	public static Collection<? extends String> getDefaultReplacementWords() {
		return defaultReplacementWorldList;
	}
	
	public static void loadDefaultReplacements(){
		defaultReplacements.clear();
		defaultReplacementList.clear();
		defaultReplacementWorldList.clear();
		loadDefaultConstReplacements();
		loadDefaultItemReplacements();
		loadDefaultMobReplacements();
	}
	
	private static void loadDefaultConstReplacements(){
		defaultReplacements.put("true", Integer.valueOf(-1));
		defaultReplacementList.add(new PC_StringWithInfo("true", "Const: -1"));
		defaultReplacementWorldList.add("true");
		defaultReplacements.put("false", Integer.valueOf(0));
		defaultReplacementList.add(new PC_StringWithInfo("false", "Const: 0"));
		defaultReplacementWorldList.add("false");
	}
	
	private static void loadDefaultItemReplacements(){
		Iterator<?> i = Item.itemRegistry.iterator();
		String fontBoldConsolasStart = PC_Formatter.color(0, 0, 0);
		String n;
		while(i.hasNext()){
			Object obj = i.next();
			int id = Item.itemRegistry.getIDForObject(obj);
			String name = Item.itemRegistry.getNameForObject(obj);
			int index = name.indexOf(':');
			String mod = name.substring(0, index);
			String item = name.substring(index+1);
			mod = removeInvaliedChars(mod);
			item = removeInvaliedChars(item);
			n = "Item."+mod+"."+item;
			defaultReplacementWorldList.add(n);
			defaultReplacements.put(n.toLowerCase(), Integer.valueOf(id));
			String[] info = new String[4];
			info[0] = fontBoldConsolasStart+"Additional Data:"+PC_Formatter.reset();
			info[1] = fontBoldConsolasStart+"\tItem:"+PC_Formatter.reset()+" "+item+":"+id;
			info[2] = fontBoldConsolasStart+"\tMod:"+PC_Formatter.reset()+" "+mod;
			info[3] = fontBoldConsolasStart+"\tName:"+PC_Formatter.reset()+" "+PC_Lang.translate(((Item)obj).getUnlocalizedName()+".name");
			defaultReplacementList.add(new PC_StringWithInfo(n, "Const: "+id, info));
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void loadDefaultMobReplacements(){
		String fontBoldConsolasStart = PC_Formatter.color(0, 0, 0);
		String n;
		for(Entry<Integer, Class<? extends Entity>> e: (Set<Entry<Integer, Class<? extends Entity>>>)EntityList.IDtoClassMapping.entrySet()){
			Class<? extends Entity> entity = e.getValue();
			if(EntityCreature.class.isAssignableFrom(entity) || EntitySlime.class.isAssignableFrom(entity)){
				int id = e.getKey().intValue();
				String name = EntityList.getStringFromID(id);
				EntityRegistration entityRegistration = EntityRegistry.instance().lookupModSpawn(entity, false);
				String mod;
				if(entityRegistration==null){
					mod = "minecraft";
				}else{
					mod = entityRegistration.getContainer().getName();
				}
				mod = removeInvaliedChars(mod);
				name = removeInvaliedChars(name);
				n = "Entity."+mod+"."+name;
				defaultReplacementWorldList.add(n);
				defaultReplacements.put(n.toLowerCase(), Integer.valueOf(id));
				String[] info = new String[4];
				info[0] = fontBoldConsolasStart+"Additional Data:"+PC_Formatter.reset();
				info[1] = fontBoldConsolasStart+"\tEntity:"+PC_Formatter.reset()+" "+name+":"+id;
				info[2] = fontBoldConsolasStart+"\tMod:"+PC_Formatter.reset()+" "+mod;
				info[3] = fontBoldConsolasStart+"\tName:"+PC_Formatter.reset()+" "+PC_Lang.translate("entity."+name+".name");
				defaultReplacementList.add(new PC_StringWithInfo(n, "Const: "+id, info));
			}
		}
	}
	
	public static String removeInvaliedChars(String name){
		String nName = "";
		for(int i=0; i<name.length(); i++){
			char c = name.charAt(i);
			if((c>='A' && c<='Z') || (c>='a' && c<='z') || c=='_' || c=='.'){
				nName += c;
			}else if(c==' '||c=='\t'){
				nName += '_';
			}
		}
		return nName;
	}
	
	public static CompiledScript compile(String script, DiagnosticListener<Void> diagnosticListener, HashMap<String, Integer> replacements, int entryVectorCount) throws ScriptException{
		scriptEngine.getContext().setAttribute(MiniScriptLang.COMPILER_DIAGNOSTICLISTENER, diagnosticListener, ScriptContext.ENGINE_SCOPE);
		HashMap<String, Integer> r = new HashMap<String, Integer>(defaultReplacements);
		r.putAll(replacements);
		scriptEngine.getContext().setAttribute(MiniScriptLang.COMPILER_REPLACEMENTS, r, ScriptContext.ENGINE_SCOPE);
		scriptEngine.getContext().setAttribute(MiniScriptLang.COMPILER_START_VECTOR_COUNT, Integer.valueOf(entryVectorCount), ScriptContext.ENGINE_SCOPE);
		return ((Compilable)scriptEngine).compile(script);
	}
	
	public static void invoke(CompiledScript script, int[] ext, int entryIndex) throws ScriptException{
		scriptEngine.getContext().setAttribute(MiniScriptLang.BINDING_EXT, ext, ScriptContext.ENGINE_SCOPE);
		scriptEngine.getContext().setAttribute(MiniScriptLang.BINDING_START_VECTOR, Integer.valueOf(entryIndex), ScriptContext.ENGINE_SCOPE);
		script.eval(scriptEngine.getContext());
	}
	
	private PC_Miniscript(){
		throw new InstantiationError();
	}

	public static String generateDefaultSource(String type, String[] vectors) {
		String source = ";A MiniScript powered "+type;
		if(vectors!=null && vectors.length>1){
			for(String vector:vectors){
				source += "\n\tjmp "+vector;
			}
			for(String vector:vectors){
				source += "\n"+vector+":\n\t";
				source += "\n\tjmp Exit";
			}
			source += "\nExit:";
		}
		return source;
	}
	
	public static void saveAs(String name, String source){
		if(PC_Utils.isClient()){
			File file = PC_Utils.getPowerCraftFile("code/miniscript", name+".txt");
			try {
				FileWriter fw = new FileWriter(file, false);
				fw.write(source);
				fw.close();
			} catch (IOException e) {
				PC_Logger.throwing("PC_Miniscipt", "saveAs(String, String)", e);
			}
		}
	}
	
	public static boolean exists(String name){
		if(PC_Utils.isClient()){
			return PC_Utils.getPowerCraftFile("code/miniscript", name+".txt").exists();
		}
		return true;
	}
	
	public static String loadFrom(String name){
		if(PC_Utils.isClient()){
			File file = PC_Utils.getPowerCraftFile("code/miniscript", name+".txt");
			if(!file.exists())
				return "";
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
			if(source==null)
				return "";
			return source;
		}
		return "";
	}
	
}
