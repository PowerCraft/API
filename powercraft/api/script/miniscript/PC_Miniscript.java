package powercraft.api.script.miniscript;

import java.awt.Font;
import java.util.HashMap;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.tools.DiagnosticListener;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import miniscript.MiniScriptLang;
import powercraft.api.PC_Api;
import powercraft.api.gres.doc.PC_GresHighlighting;
import powercraft.api.gres.doc.PC_GresHighlighting.MultipleRegexPossibilities;
import powercraft.api.gres.font.PC_FontRenderer;
import powercraft.api.gres.font.PC_Fonts;
import powercraft.api.gres.font.PC_Formatter;
import powercraft.api.reflect.PC_Security;

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
	
	@SideOnly(Side.CLIENT)
	public static final class Highlighting {
		
		public static final PC_GresHighlighting MINISCRIPT = new PC_GresHighlighting();
		
		public static final String REGISTER_REGEX = "[Rr](?:(?:[12]\\d?)|(?:3[012]?)|[0456789])";
		
		public static final String LABEL_REGEX = "\\w+:";
		
		public static final String[] MINISCRIPT_ASM = 
			{"NOT", "NEG", "INC", "DEC", "ADD", "SUB", "MUL", "DIV", "MOD", "SHL", "SHR", "USHR", "AND", "OR", 
			"XOR", "MOV", "CMP", "JMP", "JMPL", "JEQ", "JNE", "JL", "JLE", "JB", "JBE", "EXT", "SWITCH", "RND"};
		
		static{
			PC_GresHighlighting INNER = new PC_GresHighlighting();
			INNER.addWordHighlight(PC_GresHighlighting.msp(false, "miniscript"), ""+PC_Formatter.color(84, 217, 255)+PC_Formatter.font(PC_Fonts.create(PC_FontRenderer.getFont("Consolas", Font.ITALIC, 24), new char[0])));
			MINISCRIPT.addOperatorHighlight(PC_GresHighlighting.msp(true, "+", "-", "*"), "");
			MINISCRIPT.addOperatorHighlight(PC_GresHighlighting.msp(true, "[", "]"), "");
			MINISCRIPT.addOperatorHighlight(PC_GresHighlighting.msp(true, ","), "");
			MINISCRIPT.addBlockHighlight(PC_GresHighlighting.msp(true, ";"), null, null, false, ""+PC_Formatter.color(122, 122, 122), INNER);
			MINISCRIPT.addSpecialHighlight(new MultipleRegexPossibilities(LABEL_REGEX), ""+PC_Formatter.color(149, 0, 85)+PC_Formatter.font(PC_Fonts.create(PC_FontRenderer.getFont("Consolas", Font.BOLD, 24), new char[0])));
			MINISCRIPT.addWordHighlight(PC_GresHighlighting.msp(false, MINISCRIPT_ASM), ""+PC_Formatter.color(149, 0, 85)+PC_Formatter.font(PC_Fonts.create(PC_FontRenderer.getFont("Consolas", Font.BOLD, 24), new char[0])));
			MINISCRIPT.addWordHighlight(new MultipleRegexPossibilities(REGISTER_REGEX), ""+PC_Formatter.color(149, 0, 85)+PC_Formatter.font(PC_Fonts.create(PC_FontRenderer.getFont("Consolas", Font.BOLD, 24), new char[0])));
		}
		
	}
	
	private PC_Miniscript(){
		throw new InstantiationError();
	}
	
}
