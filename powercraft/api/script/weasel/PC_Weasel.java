package powercraft.api.script.weasel;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import powercraft.api.PC_Api;
import powercraft.api.reflect.PC_Security;
import xscript.runtime.XScriptLang;

public final class PC_Weasel {

	private static ScriptEngine scriptEngine;
	
	public static void register(){
		PC_Security.allowedCaller("PC_Weasel.register()", PC_Api.class);
		scriptEngine = new ScriptEngineManager().getEngineByName(XScriptLang.NAME);
	}
	
	public static boolean isWeaselPresent(){
		return scriptEngine!=null;
	}
	
	public static PC_WeaselEngine createVirtualMachine(int memSize){
		return new PC_WeaselEngine(null);//TODO
	}
	
	
	
	private PC_Weasel(){
		throw new InstantiationError();
	}
	
}
