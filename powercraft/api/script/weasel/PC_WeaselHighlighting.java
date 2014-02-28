package powercraft.api.script.weasel;

import java.awt.Font;

import powercraft.api.gres.autoadd.PC_AutoAdd;
import powercraft.api.gres.autoadd.PC_StringAdd;
import powercraft.api.gres.doc.PC_GresHighlighting;
import powercraft.api.gres.font.PC_FontRenderer;
import powercraft.api.gres.font.PC_Fonts;
import powercraft.api.gres.font.PC_Formatter;

public class PC_WeaselHighlighting {

	public static PC_GresHighlighting makeHighlighting(){
		PC_GresHighlighting highlighting = new PC_GresHighlighting();
		PC_GresHighlighting INNER = new PC_GresHighlighting();
		INNER.addWordHighlight(PC_GresHighlighting.msp(false, "weasel", "xscript"), PC_Formatter.color(84, 217, 255)+PC_Formatter.font(PC_Fonts.create(PC_FontRenderer.getFont("Consolas", Font.ITALIC, 24), null)));
		INNER.addWordHighlight(PC_GresHighlighting.msp(true, "TODO"), PC_Formatter.color(84, 217, 255)+PC_Formatter.font(PC_Fonts.create(PC_FontRenderer.getFont("Consolas", Font.BOLD, 24), null)));
		highlighting.addOperatorHighlight(PC_GresHighlighting.msp(true, "+", "-", "*", "/", "%"), "");
		highlighting.addOperatorHighlight(PC_GresHighlighting.msp(true, ">", "<", "="), "");
		highlighting.addOperatorHighlight(PC_GresHighlighting.msp(true, "|", "&", "^"), "");
		highlighting.addOperatorHighlight(PC_GresHighlighting.msp(true, "[", "]"), "");
		highlighting.addOperatorHighlight(PC_GresHighlighting.msp(true, ","), "");
		highlighting.addOperatorHighlight(PC_GresHighlighting.msp(true, ":", "?"), "");
		highlighting.addOperatorHighlight(PC_GresHighlighting.msp(true, ";"), "");
		highlighting.addOperatorHighlight(PC_GresHighlighting.msp(true, "."), "");
		highlighting.addOperatorHighlight(PC_GresHighlighting.msp(true, "!", "~"), "");
		highlighting.addOperatorHighlight(PC_GresHighlighting.msp(true, "(", ")"), "");
		highlighting.addOperatorHighlight(PC_GresHighlighting.msp(true, "{", "}"), "");
		highlighting.addBlockHighlight(PC_GresHighlighting.msp(true, "//"), null, null, false, PC_Formatter.color(122, 122, 122), INNER);
		highlighting.addBlockHighlight(PC_GresHighlighting.msp(true, "/*"), null, PC_GresHighlighting.msp(true, "*/"), true, PC_Formatter.color(122, 122, 122), INNER);
		highlighting.addBlockHighlight(PC_GresHighlighting.msp(true, "/**"), null, PC_GresHighlighting.msp(true, "*/"), true, PC_Formatter.color(122, 122, 255), INNER);
		highlighting.addBlockHighlight(PC_GresHighlighting.msp(true, "\""), PC_GresHighlighting.msp(true, "\\"), PC_GresHighlighting.msp(true, "\""), true, PC_Formatter.color(122, 122, 255));
		highlighting.addBlockHighlight(PC_GresHighlighting.msp(true, "'"), PC_GresHighlighting.msp(true, "\\"), PC_GresHighlighting.msp(true, "'"), true, PC_Formatter.color(122, 122, 255));
		return highlighting;
	}
	
	/*public static PC_AutoComplete makeAutoComplete(List<PC_StringWithInfo> words){
		return new AutoComplete(words);
	}*/
	
	public static PC_AutoAdd makeAutoAdd(){
		return new AutoAdd();
	}
	
	private static final class AutoAdd implements PC_AutoAdd{
		
		@Override
		public void onCharAdded(PC_StringAdd add) {
			if(add.toAdd.equals("[")){
				add.toAdd = "[]";
				add.cursorPos = 1;
			}else if(add.toAdd.equals("(")){
				add.toAdd = "()";
				add.cursorPos = 1;
			}else if(add.toAdd.equals("\t")){
				if(add.documentLine.prev!=null){
					String text = add.documentLine.getText();
					String prev = add.documentLine.prev.getText();
//					if(add.cursorPos<=text.length()){
//						add.toAdd="";
//						char c;
//						int j=add.cursorPos-1;
//						add.pos = 0;
//						while(prev.length()>j && (prev.charAt(j)=='\t' || prev.charAt(j)==' ')){
//							c = add.cursorPos+add.pos==text.length()?'\n':text.charAt(add.cursorPos+add.pos);
//							if(c!=' ' &&  c!='\t'){
//								add.toAdd=(prev.charAt(j)+add.toAdd);
//							}
//							j++;
//							add.pos += 1;
//						}
//					}
//				a	
					int size = 0;
					for(int i=0; i<prev.length(); i++){
						char c = prev.charAt(i);
						if(c==' '|| c=='\t'){
							if(c==' ')
								size++;
							else
								size+=4;
						}else{
							break;
						}
					}
					int oSize = 0;
					for(int i=0; i<text.length(); i++){
						char c = text.charAt(i);
						if(c==' '|| c=='\t'){
							oSize += c;
							if(c==' ')
								oSize++;
							else
								oSize+=4;
						}else{
							break;
						}
					}
					
					if(prev.trim().endsWith("{")){	
						size+=4;
					}
					int diff = size-oSize;
					if(diff>0){
						add.pos = 0;
						add.toAdd="";
						while(diff>3){
							diff-=4;
							add.toAdd += "\t";
							add.pos++;
						}
						while(diff>0){
							diff--;
							add.toAdd += " ";
							add.pos++;
						}
						String s = text.substring(add.cursorPos);
						for(int i=0; i<s.length(); i++){
							char c = s.charAt(i);
							if(c==' '|| c=='\t'){
								add.pos++;
							}else{
								break;
							}
						}
					}
				}
			}else if(add.toAdd.equals("\n")){
				String text = add.documentLine.getText();
				String ll = text.substring(0, add.cursorPos);
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
				if(ll.endsWith("{")){
					add.pos = 2+start.length();
					add.toAdd += "\t\n"+start+"}";
				}
			}
		}
	}
	
	/*private static final class AutoComplete implements PC_AutoComplete{
		
		private HashMap<String, PC_GresDocumentLine> label2Line = new HashMap<String, PC_GresDocumentLine>();
		private HashMap<PC_GresDocumentLine, String> line2Label = new HashMap<PC_GresDocumentLine, String>();
		private InfoCollector infoCollector = new InfoCollector(this);
		private PC_SortedStringList labelNames = new PC_SortedStringList();
		private PC_SortedStringList asmInstructions = new PC_SortedStringList();
		private PC_SortedStringList words = new PC_SortedStringList();
		private PC_SortedStringList registers = new PC_SortedStringList();
		
		private AutoComplete(List<PC_StringWithInfo> words){
			for(String asm:MINISCRIPT_ASM){
				asmInstructions.add(new PC_StringWithInfo(asm, null));
			}
			for(int i=0; i<31; i++){
				registers.add(new PC_StringWithInfo("r"+i, "Register Nr "+i));
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
					autoComplete.labelNames.add(new PC_StringWithInfo(label, "In Line "+line));
					return;
				}else{
					return;
				}
				i++;
			}
		}
		
	}*/
	
}
