package powercraft.api.script.miniscript;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.tools.DiagnosticListener;

import miniscript.MiniScriptLang;
import net.minecraft.item.Item;
import powercraft.api.PC_Api;
import powercraft.api.PC_Lang;
import powercraft.api.gres.autoadd.PC_StringWithInfo;
import powercraft.api.gres.font.PC_Formatter;
import powercraft.api.reflect.PC_Security;

public final class PC_Miniscript {

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
		Iterator<?> i = Item.itemRegistry.iterator();
		String fontBoldConsolasStart = PC_Formatter.color(0, 0, 0);
		while(i.hasNext()){
			Object obj = i.next();
			int id = Item.itemRegistry.getIDForObject(obj);
			String name = Item.itemRegistry.getNameForObject(obj);
			int index = name.indexOf(':');
			String mod = name.substring(0, index);
			String item = name.substring(index+1);
			mod = removeInvaliedChars(mod);
			item = removeInvaliedChars(item);
			String n = "Item."+mod+"."+item;
			defaultReplacementWorldList.add(n);
			defaultReplacements.put(n.toLowerCase(), Integer.valueOf(id));
			String[] info = new String[3];
			info[0] = fontBoldConsolasStart+"Item:"+PC_Formatter.reset()+" "+item;
			info[1] = fontBoldConsolasStart+"Mod:"+PC_Formatter.reset()+" "+mod;
			info[2] = fontBoldConsolasStart+"Name:"+PC_Formatter.reset()+" "+PC_Lang.translate(((Item)obj).getUnlocalizedName()+".name");
			defaultReplacementList.add(new PC_StringWithInfo(n, "Const: "+id, info));
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
	
	public static CompiledScript compile(String script, DiagnosticListener<Void> diagnosticListener, HashMap<String, Integer> replacements, String[] entryVectors) throws ScriptException{
		scriptEngine.getContext().setAttribute(MiniScriptLang.COMPILER_DIAGNOSTICLISTENER, diagnosticListener, ScriptContext.ENGINE_SCOPE);
		HashMap<String, Integer> r = new HashMap<String, Integer>(defaultReplacements);
		r.putAll(replacements);
		scriptEngine.getContext().setAttribute(MiniScriptLang.COMPILER_REPLACEMENTS, r, ScriptContext.ENGINE_SCOPE);
		scriptEngine.getContext().setAttribute(MiniScriptLang.COMPILER_START_VECTORS, entryVectors, ScriptContext.ENGINE_SCOPE);
		return ((Compilable)scriptEngine).compile(script);
	}
	
	public static void invoke(CompiledScript script, int[] ext, int entryIndex) throws ScriptException{
		scriptEngine.getContext().setAttribute(MiniScriptLang.BINDING_EXT, ext, ScriptContext.ENGINE_SCOPE);
		scriptEngine.getContext().setAttribute(MiniScriptLang.BINDING_START_VECTOR, entryIndex, ScriptContext.ENGINE_SCOPE);
		script.eval(scriptEngine.getContext());
	}
	
	private PC_Miniscript(){
		throw new InstantiationError();
	}
	
}
