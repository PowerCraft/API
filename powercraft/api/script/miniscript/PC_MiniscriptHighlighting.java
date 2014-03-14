package powercraft.api.script.miniscript;

import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import powercraft.api.PC_Lang;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.autoadd.PC_AutoAdd;
import powercraft.api.gres.autoadd.PC_AutoComplete;
import powercraft.api.gres.autoadd.PC_AutoCompleteDisplay;
import powercraft.api.gres.autoadd.PC_SortedStringList;
import powercraft.api.gres.autoadd.PC_StringAdd;
import powercraft.api.gres.autoadd.PC_StringListPart;
import powercraft.api.gres.autoadd.PC_StringWithInfo;
import powercraft.api.gres.doc.PC_GresDocInfoCollector;
import powercraft.api.gres.doc.PC_GresDocument;
import powercraft.api.gres.doc.PC_GresDocumentLine;
import powercraft.api.gres.doc.PC_GresHighlighting;
import powercraft.api.gres.doc.PC_GresHighlighting.IMultiplePossibilities;
import powercraft.api.gres.doc.PC_GresHighlighting.MultipleRegexPossibilities;
import powercraft.api.gres.font.PC_Fonts;
import powercraft.api.gres.font.PC_Formatter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_MiniscriptHighlighting {

	public static PC_GresHighlighting makeHighlighting(Set<String> consts, Set<String> pointers){
		List<String> wordList = new ArrayList<String>(consts);
		wordList.addAll(pointers);
		wordList.addAll(PC_Miniscript.getDefaultReplacementWords());
		PC_GresHighlighting highlighting = new PC_GresHighlighting();
		PC_GresHighlighting INNER = new PC_GresHighlighting();
		INNER.addWordHighlight(PC_GresHighlighting.msp(false, "miniscript"), PC_Formatter.color(84, 217, 255)+PC_Formatter.font(PC_Fonts.getFontByName("Consolas", 24, Font.ITALIC)));
		INNER.addWordHighlight(PC_GresHighlighting.msp(true, "TODO"), PC_Formatter.color(84, 217, 255)+PC_Formatter.font(PC_Fonts.getFontByName("Consolas", 24, Font.BOLD)));
		highlighting.addOperatorHighlight(PC_GresHighlighting.msp(true, "+", "-", "*"), "");
		highlighting.addOperatorHighlight(PC_GresHighlighting.msp(true, "[", "]"), "");
		highlighting.addOperatorHighlight(PC_GresHighlighting.msp(true, ","), "");
		highlighting.addOperatorHighlight(PC_GresHighlighting.msp(true, ":"), "");
		ElementHighlight eh = new ElementHighlight();
		highlighting.addOperatorHighlight(eh, "");
		highlighting.addBlockHighlight(PC_GresHighlighting.msp(true, ";"), null, null, false, PC_Formatter.color(122, 122, 122), INNER);
		highlighting.addSpecialHighlight(new LabelHighlight(), PC_Formatter.color(100, 240, 135));
		highlighting.addWordHighlight(new JumperHightlights(), PC_Formatter.color(100, 135, 240));
		highlighting.addWordHighlight(PC_GresHighlighting.msp(false, MINISCRIPT_ASM), PC_Formatter.color(149, 0, 85)+PC_Formatter.font(PC_Fonts.getFontByName("Consolas", 24, Font.BOLD)));
		highlighting.addWordHighlight(new MultipleRegexPossibilities(REGISTER_REGEX), PC_Formatter.color(255, 113, 113));
		highlighting.addWordHighlight(new WordHighlight1(eh, wordList), PC_Formatter.color(255, 144, 48));
		highlighting.addWordHighlight(new WordHighlight2(eh, wordList), PC_Formatter.color(50, 71, 255)+PC_Formatter.font(PC_Fonts.getFontByName("Consolas", 24, Font.ITALIC)));
		return highlighting;
	}
	
	public static PC_AutoComplete makeAutoComplete(List<PC_StringWithInfo> consts, List<PC_StringWithInfo> pointers){
		return new AutoComplete(consts, pointers);
	}
	
	public static PC_AutoAdd makeAutoAdd(){
		return new AutoAdd();
	}
	
	public static final String REGISTER_REGEX = "[Rr](?:(?:[12]\\d?)|(?:3[01]?)|[0456789])";
	
	public static final String[] MINISCRIPT_ASM = 
		{"NOT", "NEG", "INC", "DEC", "ADD", "SUB", "MUL", "DIV", "MOD", "SHL", "SHR", "USHR", "AND", "OR", 
		"XOR", "MOV", "CMP", "JMP", "JMPL", "JEQ", "JNE", "JL", "JLE", "JB", "JBE", "EXT", "SWITCH", "RND", "ELM"};
	
	public static final String[] MINISCRIPT_ASM_JMP = {"JMP", "JMPL", "JEQ", "JNE", "JL", "JLE", "JB", "JBE"};
	
	private static final class AutoAdd implements PC_AutoAdd{
		
		AutoAdd() {
			
		}

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
		
		HashMap<String, PC_GresDocumentLine> label2Line = new HashMap<String, PC_GresDocumentLine>();
		HashMap<PC_GresDocumentLine, String> line2Label = new HashMap<PC_GresDocumentLine, String>();
		private InfoCollector infoCollector = new InfoCollector(this);
		PC_SortedStringList labelNames = new PC_SortedStringList();
		private PC_SortedStringList asmInstructions = new PC_SortedStringList();
		private PC_SortedStringList registers = new PC_SortedStringList();
		private PC_SortedStringList consts = new PC_SortedStringList();
		private PC_SortedStringList pointers = new PC_SortedStringList();
		
		AutoComplete(List<PC_StringWithInfo> consts, List<PC_StringWithInfo> pointers){
			for(String asm:MINISCRIPT_ASM){
				this.asmInstructions.add(new PC_StringWithInfo(asm, PC_Lang.translate("miniscript.tooltip."+asm.toLowerCase()), PC_Lang.translate("miniscript.desk."+asm.toLowerCase()).split("\n")));
			}
			for(int i=0; i<31; i++){
				this.registers.add(new PC_StringWithInfo("r"+i, "Register Nr "+i));
			}
			this.consts.addAll(consts);
			this.pointers.addAll(pointers);
			this.consts.addAll(PC_Miniscript.getDefaultReplacements());
		}

		@Override
		public void onStringAdded(PC_GresComponent component, PC_GresDocument document, PC_GresDocumentLine line, String toAdd, int x, PC_AutoCompleteDisplay info) {
			if(info.display){
				if(toAdd.matches("[\\w\\.]+")){
					int num = 0;
					for(PC_StringListPart part:info.parts){
						part.searchForAdd(toAdd);
						num += part.size();
					}
					info.done += toAdd;
					if(num==0)
						info.display = false;
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
					info.parts = new PC_StringListPart[]{new PC_StringListPart(this.asmInstructions)};
					break;
				case LABEL:
					info.parts = new PC_StringListPart[]{new PC_StringListPart(this.labelNames)};
					break;
				case STACK:
					info.parts = new PC_StringListPart[]{new PC_StringListPart(this.pointers), new PC_StringListPart(this.registers)};
					break;
				case WORD:
					info.parts = new PC_StringListPart[]{new PC_StringListPart(this.consts), new PC_StringListPart(this.registers)};
					break;
				case REGISTERS:
					info.parts = new PC_StringListPart[]{new PC_StringListPart(this.registers)};
					break;
				default:
					info.display = false;
					return;
				}
			}
			info.done = start;
			int num = 0;
			for(PC_StringListPart part:info.parts){
				part.searchFor(start);
				num += part.size();
			}
			if(num==0)
				info.display = false;
		}

		@Override
		public PC_GresDocInfoCollector getInfoCollector() {
			return this.infoCollector;
		}
		
		enum Type{
			INSTRUCTION("\\s*(?<part>[\\w\\.]*)"),
			LABEL("\\s*(?:(?:jmp|jmpl|jeq|jne|jl|jle|jb|jbe)\\s+|switch.*(:?,\\s*.*:[\\w\\.]*\\s)*,\\s*.*:)(?<part>[\\w\\.]*)"),
			STACK(".*\\[(?<part>[^\\]]*)"),
			WORD("\\s*(?:ext|cmp|[^\\[,]*+)(?:[\\W&&[^;]]+(?<part>[\\w\\.]*))+"),
			REGISTERS("\\s*\\w*(?:[\\W&&[^;]]+(?<part>[\\w\\.]*))+");
			
			public final String regex;
			
			Type(String regex){
				this.regex = regex;
			}
		}
		
	}
	
	private static final class InfoCollector implements PC_GresDocInfoCollector{
		
		private AutoComplete autoComplete;
		
		InfoCollector(AutoComplete autoComplete){
			this.autoComplete = autoComplete;
		}
		
		@Override
		public void onLineChange(PC_GresDocumentLine line) {
			String label = this.autoComplete.line2Label.remove(line);
			if(label!=null){
				this.autoComplete.label2Line.remove(label);
				this.autoComplete.labelNames.remove(label);
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
					int lineNum = 1;
					PC_GresDocumentLine l = line;
					while(l.prev!=null){
						l = l.prev;
						lineNum++;
					}
					this.autoComplete.line2Label.put(line, label);
					this.autoComplete.label2Line.put(label, line);
					this.autoComplete.labelNames.add(new PC_StringWithInfo(label, "In Line "+lineNum));
					return;
				}else{
					return;
				}
				i++;
			}
		}
		
	}
	
	private static final class LabelHighlight implements IMultiplePossibilities{

		LabelHighlight() {
			
		}

		@Override
		public int comesNowIn(String line, int index, Object lastInfo) {
			int i = index;
			if(line.substring(0, i).trim().isEmpty()){
				int size = 0;
				boolean isEnd = false;
				while(i<line.length()){
					char c = line.charAt(i);
					if(c==':')
						return size;
					if(!isEnd && !Character.isLetterOrDigit(c)){
						isEnd = true;
					}
					if(isEnd && !Character.isWhitespace(c)){
						return 0;
					}
					size++;
					i++;
				}
			}
			return 0;
		}

		@Override
		public Object getInfo() {
			return null;
		}
		
	}
	
	private static final class WordHighlight1 implements IMultiplePossibilities{

		private ElementHighlight elementHighlight;
		private List<String> words;
		
		public WordHighlight1(ElementHighlight elementHighlight, List<String> words){
			this.elementHighlight = elementHighlight;
			this.words = words;
		}
		
		@Override
		public int comesNowIn(String line, int index, Object lastInfo) {
			int i = index;
			if(lastInfo instanceof WordInfo){
				return 0;
			}
			this.elementHighlight.wordInfo = new WordInfo("");
			char c = line.charAt(i);
			int length = 0;
			while(Character.isAlphabetic(c) || Character.isDigit(c) || c=='_'){
				this.elementHighlight.wordInfo.start += c;
				length++;
				i++;
				if(i>=line.length())
					break;
				c = line.charAt(i);
			}
			Iterator<String> it = this.words.iterator();
			while(it.hasNext()){
				String s = it.next().split("\\.", 2)[0];
				if(s.equals(this.elementHighlight.wordInfo.start)){
					return length;
				}
			}
			return 0;
		}

		@Override
		public Object getInfo() {
			return null;
		}
		
	}
	
	private static final class WordHighlight2 implements IMultiplePossibilities{

		private ElementHighlight elementHighlight;
		private List<String> words;
		
		public WordHighlight2(ElementHighlight elementHighlight, List<String> words){
			this.elementHighlight = elementHighlight;
			this.words = words;
		}
		
		@Override
		public int comesNowIn(String line, int index, Object lastInfo) {
			int i = index;
			if(lastInfo instanceof WordInfo){
				this.elementHighlight.wordInfo = (WordInfo) lastInfo;
				this.elementHighlight.wordInfo.start += ".";
			}else{
				return 0;
			}
			char c = line.charAt(i);
			int length = 0;
			while(Character.isAlphabetic(c) || Character.isDigit(c) || c=='_'){
				this.elementHighlight.wordInfo.start += c;
				length++;
				i++;
				if(i>=line.length())
					break;
				c = line.charAt(i);
			}
			Iterator<String> it = this.words.iterator();
			while(it.hasNext()){
				String s = it.next();
				if(s.startsWith(this.elementHighlight.wordInfo.start+".") || s.equals(this.elementHighlight.wordInfo.start)){
					return length;
				}
			}
			return 0;
		}

		@Override
		public Object getInfo() {
			return null;
		}
		
	}
	
	private static final class ElementHighlight implements IMultiplePossibilities{

		WordInfo wordInfo;
		
		ElementHighlight() {
			
		}

		@Override
		public int comesNowIn(String line, int i, Object lastInfo) {
			if(line.charAt(i)=='.'){
				return 1;
			}
			return 0;
		}

		@Override
		public Object getInfo() {
			Object o = this.wordInfo;
			this.wordInfo = null;
			return o;
		}
		
	}
	
	private static final class WordInfo{
		
		String start;
		
		WordInfo(String start) {
			this.start = start;
		}
		
	}
	
	private static final class JumperHightlights implements IMultiplePossibilities{

		JumperHightlights() {
			
		}

		@Override
		public int comesNowIn(String line, int index, Object lastInfo) {
			int i = index;
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
			int last1 = l.lastIndexOf(',');
			int last2 = l.lastIndexOf(':');
			if(last1!=-1 && last2!=-1 && last1<last2 && line.trim().toLowerCase().startsWith("switch")){
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
