package powercraft.api.script.miniscript;

import java.awt.Font;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.autoadd.PC_AutoAdd;
import powercraft.api.gres.autoadd.PC_AutoComplete;
import powercraft.api.gres.autoadd.PC_AutoCompleteDisplay;
import powercraft.api.gres.autoadd.PC_SortedStringList;
import powercraft.api.gres.autoadd.PC_StringAdd;
import powercraft.api.gres.autoadd.PC_StringListPart;
import powercraft.api.gres.doc.PC_GresDocInfoCollector;
import powercraft.api.gres.doc.PC_GresDocument;
import powercraft.api.gres.doc.PC_GresDocumentLine;
import powercraft.api.gres.doc.PC_GresHighlighting;
import powercraft.api.gres.doc.PC_GresHighlighting.IMultiplePossibilities;
import powercraft.api.gres.doc.PC_GresHighlighting.MultipleRegexPossibilities;
import powercraft.api.gres.font.PC_FontRenderer;
import powercraft.api.gres.font.PC_Fonts;
import powercraft.api.gres.font.PC_Formatter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_MiniScriptHighlighting {

	public static PC_GresHighlighting makeHighlighting(){
		PC_GresHighlighting highlighting = new PC_GresHighlighting();
		PC_GresHighlighting INNER = new PC_GresHighlighting();
		INNER.addWordHighlight(PC_GresHighlighting.msp(false, "miniscript"), PC_Formatter.color(84, 217, 255)+PC_Formatter.font(PC_Fonts.create(PC_FontRenderer.getFont("Consolas", Font.ITALIC, 24), null)));
		INNER.addWordHighlight(PC_GresHighlighting.msp(true, "TODO"), PC_Formatter.color(84, 217, 255)+PC_Formatter.font(PC_Fonts.create(PC_FontRenderer.getFont("Consolas", Font.BOLD, 24), null)));
		highlighting.addOperatorHighlight(PC_GresHighlighting.msp(true, "+", "-", "*"), "");
		highlighting.addOperatorHighlight(PC_GresHighlighting.msp(true, "[", "]"), "");
		highlighting.addOperatorHighlight(PC_GresHighlighting.msp(true, ","), "");
		highlighting.addBlockHighlight(PC_GresHighlighting.msp(true, ";"), null, null, false, PC_Formatter.color(122, 122, 122), INNER);
		highlighting.addSpecialHighlight(new MultipleRegexPossibilities(LABEL_REGEX), PC_Formatter.color(100, 240, 135));
		highlighting.addWordHighlight(new JumperHightlights(), PC_Formatter.color(100, 135, 240));
		highlighting.addWordHighlight(PC_GresHighlighting.msp(false, MINISCRIPT_ASM), PC_Formatter.color(149, 0, 85)+PC_Formatter.font(PC_Fonts.create(PC_FontRenderer.getFont("Consolas", Font.BOLD, 24), null)));
		highlighting.addWordHighlight(new MultipleRegexPossibilities(REGISTER_REGEX), PC_Formatter.color(255, 113, 113));
		return highlighting;
	}
	
	public static PC_AutoComplete makeAutoComplete(Set<String> words){
		return new AutoComplete(words);
	}
	
	public static PC_AutoAdd makeAutoAdd(){
		return new AutoAdd();
	}
	
	public static final String REGISTER_REGEX = "[Rr](?:(?:[12]\\d?)|(?:3[01]?)|[0456789])";
	
	public static final String LABEL_REGEX = "\\w+:";
	
	public static final String[] MINISCRIPT_ASM = 
		{"NOT", "NEG", "INC", "DEC", "ADD", "SUB", "MUL", "DIV", "MOD", "SHL", "SHR", "USHR", "AND", "OR", 
		"XOR", "MOV", "CMP", "JMP", "JMPL", "JEQ", "JNE", "JL", "JLE", "JB", "JBE", "EXT", "SWITCH", "RND"};
	
	public static final String[] MINISCRIPT_ASM_JMP = {"JMP", "JMPL", "JEQ", "JNE", "JL", "JLE", "JB", "JBE"};
	
	private static final class AutoAdd implements PC_AutoAdd{
		
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
		
	}
	
	private static final class AutoComplete implements PC_AutoComplete{
		
		private HashMap<String, PC_GresDocumentLine> label2Line = new HashMap<String, PC_GresDocumentLine>();
		private HashMap<PC_GresDocumentLine, String> line2Label = new HashMap<PC_GresDocumentLine, String>();
		private InfoCollector infoCollector = new InfoCollector(this);
		private PC_SortedStringList labelNames = new PC_SortedStringList();
		private PC_SortedStringList asmInstructions = new PC_SortedStringList();
		private PC_SortedStringList words = new PC_SortedStringList();
		private PC_SortedStringList registers = new PC_SortedStringList();
		
		private AutoComplete(Set<String> words){
			for(String asm:MINISCRIPT_ASM){
				asmInstructions.add(asm);
			}
			for(int i=0; i<31; i++){
				registers.add("r"+i);
			}
			this.words.addAll(words);
		}

		@Override
		public void onStringAdded(PC_GresComponent component, PC_GresDocument document, PC_GresDocumentLine line, String toAdd, int x, PC_AutoCompleteDisplay info) {
			if(info.display){
				if(toAdd.matches("[\\w\\.]+")){
					for(PC_StringListPart part:info.parts)
						part.searchForAdd(toAdd);
					info.done += toAdd;
				}else{
					info.display = false;
				}
			}else if(toAdd.equals(".")){
				makeComplete(component, document, line, x, info);
			}
		}
		
		@Override
		public void makeComplete(PC_GresComponent component, PC_GresDocument document, PC_GresDocumentLine line, int x, PC_AutoCompleteDisplay info) {
			info.display = true;
			String text = line.getText().substring(0, x);
			String start = null;
			Type type = null;
			for(Type t:Type.values()){
				String regex = t.regex;
				Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(text);
				if(matcher.find() && matcher.start()==0 && matcher.end()==text.length()){
					start = matcher.group("part");
					type = t;
					break;
				}
			}
			if(type==null){
				info.display = false;
				return;
			}
			if(info.info!=type){
				info.info = type;
				switch(type){
				case INSTRUCTION:
					info.parts = new PC_StringListPart[]{new PC_StringListPart(asmInstructions)};
					break;
				case LABEL:
					info.parts = new PC_StringListPart[]{new PC_StringListPart(labelNames)};
					break;
				case WORD:
					info.parts = new PC_StringListPart[]{new PC_StringListPart(words), new PC_StringListPart(registers)};
					break;
				case REGISTERS:
					info.parts = new PC_StringListPart[]{new PC_StringListPart(registers)};
					break;
				default:
					info.display = false;
					return;
				}
			}
			info.done = start;
			for(PC_StringListPart part:info.parts)
				part.searchFor(start);
		}

		@Override
		public PC_GresDocInfoCollector getInfoCollector() {
			return infoCollector;
		}
		
		enum Type{
			INSTRUCTION("\\s*(?<part>[\\w\\.]*)"),
			LABEL("\\s*(?:(?:jmp|jmpl|jeq|jne|jl|jle|jb|jbe)\\s+|switch.*(:?,\\s*[\\w\\.]*\\s)*,\\s*)(?<part>[\\w\\.]*)"),
			WORD("\\s*(?:ext|cmp|[^\\[,]*)(?:[\\W&&[^;]]+(?<part>[\\w\\.]*))+"),
			REGISTERS("\\s*\\w*(?:[\\W&&[^;]]+(?<part>[\\w\\.]*))+");
			
			public final String regex;
			
			Type(String regex){
				this.regex = regex;
			}
		}
		
	}
	
	private static final class InfoCollector implements PC_GresDocInfoCollector{
		
		private AutoComplete autoComplete;
		
		private InfoCollector(AutoComplete autoComplete){
			this.autoComplete = autoComplete;
		}
		
		@Override
		public void onLineChange(PC_GresDocumentLine line) {
			String label = autoComplete.line2Label.remove(line);
			if(label!=null){
				autoComplete.label2Line.remove(label);
				autoComplete.labelNames.remove(label);
			}
		}

		@Override
		public void onLineChanged(PC_GresDocumentLine line) {
			String text = line.getText().trim();
			String label = "";
			int i=0; 
			while(i<text.length()){
				char c = text.charAt(i);
				if(!(c==' ' || c=='\t' || c=='\n' || c=='\r'))
					break;
				i++;
			}
			while(i<text.length()){
				char c = text.charAt(i);
				if((c>='A' && c<='Z') || (c>='a' && c<='z') || c=='_' || ((c>='0' && c<='9')&&i>0)){
					label += c;
				}else if(c==':'){
					autoComplete.line2Label.put(line, label);
					autoComplete.label2Line.put(label, line);
					autoComplete.labelNames.add(label);
					return;
				}else{
					return;
				}
				i++;
			}
		}
		
	}
	
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
	
}
