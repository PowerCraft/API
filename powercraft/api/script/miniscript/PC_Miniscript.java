package powercraft.api.script.miniscript;

import java.util.HashMap;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.tools.DiagnosticListener;

import net.minecraft.item.Item;
import miniscript.MiniScriptLang;
import powercraft.api.PC_Api;
import powercraft.api.PC_Security;

public final class PC_Miniscript {

	private static ScriptEngine scriptEngine;
	private static HashMap<String, Integer> defaultReplacements = new HashMap<String, Integer>();
	
	public static void register(){
		PC_Security.allowedCaller("PC_Miniscript.register()", PC_Api.class);
		scriptEngine = new ScriptEngineManager().getEngineByName(MiniScriptLang.NAME);
		scriptEngine.getContext().setAttribute(MiniScriptLang.COMPILER_BACKJUMPDISABLED, true, ScriptContext.ENGINE_SCOPE);
	}
	
	public static CompiledScript compile(String script, DiagnosticListener<Void> diagnosticListener, HashMap<String, Integer> replacements) throws ScriptException{
		scriptEngine.getContext().setAttribute(MiniScriptLang.COMPILER_DIAGNOSTICLISTENER, diagnosticListener, ScriptContext.ENGINE_SCOPE);
		HashMap<String, Integer> r = new HashMap<String, Integer>(defaultReplacements);
		r.putAll(replacements);
		scriptEngine.getContext().setAttribute(MiniScriptLang.COMPILER_REPLACEMENTS, r, ScriptContext.ENGINE_SCOPE);
		return ((Compilable)scriptEngine).compile(script);
	}
	
	public static void invoke(CompiledScript script, int[] ext) throws ScriptException{
		scriptEngine.getContext().setAttribute(MiniScriptLang.BINDING_EXT, ext, ScriptContext.ENGINE_SCOPE);
		script.eval(scriptEngine.getContext());
	}
	
	private PC_Miniscript(){
		throw new InstantiationError();
	}

	public static int getItemWrapper(Item item) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
