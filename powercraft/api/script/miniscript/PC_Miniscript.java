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

import miniscript.MiniScriptLang;
import powercraft.api.PC_Api;
import powercraft.api.gres.autoadd.PC_AutoAdd;
import powercraft.api.gres.autoadd.PC_StringAdd;
import powercraft.api.gres.doc.PC_GresHighlighting;
import powercraft.api.gres.doc.PC_GresHighlighting.IMultiplePossibilities;
import powercraft.api.gres.doc.PC_GresHighlighting.MultipleRegexPossibilities;
import powercraft.api.gres.font.PC_FontRenderer;
import powercraft.api.gres.font.PC_Fonts;
import powercraft.api.gres.font.PC_Formatter;
import powercraft.api.reflect.PC_Security;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
		
		public static final PC_AutoAdd MINISCRIPT_AUTOADD = new PC_AutoAdd(){

			@Override
			public void onCharAdded(PC_StringAdd add) {
				if(add.toAdd.equals("[")){
					add.toAdd = "[]";
					add.cursorPos = 1;
				}else if(add.toAdd.equals("\n")){
					String text = add.documentLine.getText();
					String start = "";
					for(int i=0; i<text.length(); i++){
						char c = text.charAt(i);
						if(c==' '|| c=='\t'){
							start += c;
						}else{
							break;
						}
					}
					add.toAdd += start;
				}
			}
			
		};
		
		public static final String REGISTER_REGEX = "[Rr](?:(?:[12]\\d?)|(?:3[012]?)|[0456789])";
		
		public static final String LABEL_REGEX = "\\w+:";
		
		public static final String[] MINISCRIPT_ASM = 
			{"NOT", "NEG", "INC", "DEC", "ADD", "SUB", "MUL", "DIV", "MOD", "SHL", "SHR", "USHR", "AND", "OR", 
			"XOR", "MOV", "CMP", "JMP", "JMPL", "JEQ", "JNE", "JL", "JLE", "JB", "JBE", "EXT", "SWITCH", "RND"};
		
		public static final String[] MINISCRIPT_ASM_JMP = {"JMP", "JMPL", "JEQ", "JNE", "JL", "JLE", "JB", "JBE"};
		
		private static final class JumperHightlights implements IMultiplePossibilities{

			@Override
			public int comesNowIn(String line, int i, Object lastInfo) {
				String l = line.substring(0, i).trim();
				for(String jmp:MINISCRIPT_ASM_JMP){
					if(l.equalsIgnoreCase(jmp)){
						int size = 0;
						char c;
						do{
							if(i>=line.length())
								return size;
							c = line.charAt(i);
							i++;
							size++;
						}while(Character.isAlphabetic(c) || Character.isDigit(c));
						size--;
						return size;
					}
				}
				if(l.indexOf(',')!=-1 && line.toLowerCase().startsWith("switch")){
					int size = 0;
					char c;
					do{
						if(i>=line.length())
							return size;
						c = line.charAt(i);
						i++;
						size++;
					}while(Character.isAlphabetic(c) || Character.isDigit(c));
					size--;
					return size;
				}
				return 0;
			}

			@Override
			public Object getInfo() {
				return null;
			}
			
		}
		
		static{
			PC_GresHighlighting INNER = new PC_GresHighlighting();
			INNER.addWordHighlight(PC_GresHighlighting.msp(false, "miniscript"), PC_Formatter.color(84, 217, 255)+PC_Formatter.font(PC_Fonts.create(PC_FontRenderer.getFont("Consolas", Font.ITALIC, 24), null)));
			INNER.addWordHighlight(PC_GresHighlighting.msp(true, "TODO"), PC_Formatter.color(84, 217, 255)+PC_Formatter.font(PC_Fonts.create(PC_FontRenderer.getFont("Consolas", Font.BOLD, 24), null)));
			MINISCRIPT.addOperatorHighlight(PC_GresHighlighting.msp(true, "+", "-", "*"), "");
			MINISCRIPT.addOperatorHighlight(PC_GresHighlighting.msp(true, "[", "]"), "");
			MINISCRIPT.addOperatorHighlight(PC_GresHighlighting.msp(true, ","), "");
			MINISCRIPT.addBlockHighlight(PC_GresHighlighting.msp(true, ";"), null, null, false, PC_Formatter.color(122, 122, 122), INNER);
			MINISCRIPT.addSpecialHighlight(new MultipleRegexPossibilities(LABEL_REGEX), PC_Formatter.color(100, 240, 135));
			MINISCRIPT.addWordHighlight(new JumperHightlights(), PC_Formatter.color(100, 135, 240));
			MINISCRIPT.addWordHighlight(PC_GresHighlighting.msp(false, MINISCRIPT_ASM), PC_Formatter.color(149, 0, 85)+PC_Formatter.font(PC_Fonts.create(PC_FontRenderer.getFont("Consolas", Font.BOLD, 24), null)));
			MINISCRIPT.addWordHighlight(new MultipleRegexPossibilities(REGISTER_REGEX), PC_Formatter.color(255, 113, 113));
		}
		
	}
	
	private PC_Miniscript(){
		throw new InstantiationError();
	}
	
}
