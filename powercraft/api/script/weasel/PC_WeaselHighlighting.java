package powercraft.api.script.weasel;

import java.awt.Font;

import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.autoadd.PC_AutoAdd;
import powercraft.api.gres.autoadd.PC_AutoComplete;
import powercraft.api.gres.autoadd.PC_AutoCompleteDisplay;
import powercraft.api.gres.autoadd.PC_StringAdd;
import powercraft.api.gres.autoadd.PC_StringListPart;
import powercraft.api.gres.doc.PC_GresDocInfoCollector;
import powercraft.api.gres.doc.PC_GresDocument;
import powercraft.api.gres.doc.PC_GresDocumentLine;
import powercraft.api.gres.doc.PC_GresHighlighting;
import powercraft.api.gres.font.PC_Fonts;
import powercraft.api.gres.font.PC_Formatter;
import powercraft.api.script.weasel.source.PC_WeaselSourceIterator;
import powercraft.api.script.weasel.source.PC_WeaselToken;
import powercraft.api.script.weasel.source.PC_WeaselTokenKind;

public class PC_WeaselHighlighting {

	public static PC_GresHighlighting makeHighlighting(){
		PC_GresHighlighting highlighting = new PC_GresHighlighting();
		PC_GresHighlighting INNER = new PC_GresHighlighting();
		INNER.addWordHighlight(PC_GresHighlighting.msp(false, "weasel", "xscript"), PC_Formatter.color(84, 217, 255)+PC_Formatter.font(PC_Fonts.getFontByName("Consolas", 24, Font.ITALIC)));
		INNER.addWordHighlight(PC_GresHighlighting.msp(true, "TODO"), PC_Formatter.color(84, 217, 255)+PC_Formatter.font(PC_Fonts.getFontByName("Consolas", 24, Font.BOLD)));
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
		highlighting.addWordHighlight(PC_GresHighlighting.msp(true, CONST), PC_Formatter.color(149, 0, 85)+PC_Formatter.font(PC_Fonts.getFontByName("Consolas", 24, Font.BOLD)));
		highlighting.addWordHighlight(PC_GresHighlighting.msp(true, PRIMITIVES), PC_Formatter.color(149, 0, 85)+PC_Formatter.font(PC_Fonts.getFontByName("Consolas", 24, Font.BOLD)));
		highlighting.addWordHighlight(PC_GresHighlighting.msp(true, CLASSES), PC_Formatter.color(149, 0, 85)+PC_Formatter.font(PC_Fonts.getFontByName("Consolas", 24, Font.BOLD)));
		highlighting.addWordHighlight(PC_GresHighlighting.msp(true, HEADER), PC_Formatter.color(149, 0, 85)+PC_Formatter.font(PC_Fonts.getFontByName("Consolas", 24, Font.BOLD)));
		highlighting.addWordHighlight(PC_GresHighlighting.msp(true, MODIFIER), PC_Formatter.color(149, 0, 85)+PC_Formatter.font(PC_Fonts.getFontByName("Consolas", 24, Font.BOLD)));
		highlighting.addWordHighlight(PC_GresHighlighting.msp(true, PARENTING), PC_Formatter.color(149, 0, 85)+PC_Formatter.font(PC_Fonts.getFontByName("Consolas", 24, Font.BOLD)));
		highlighting.addWordHighlight(PC_GresHighlighting.msp(true, CONDITIONS), PC_Formatter.color(149, 0, 85)+PC_Formatter.font(PC_Fonts.getFontByName("Consolas", 24, Font.BOLD)));
		highlighting.addWordHighlight(PC_GresHighlighting.msp(true, SWITCH), PC_Formatter.color(149, 0, 85)+PC_Formatter.font(PC_Fonts.getFontByName("Consolas", 24, Font.BOLD)));
		highlighting.addWordHighlight(PC_GresHighlighting.msp(true, BREAKS), PC_Formatter.color(149, 0, 85)+PC_Formatter.font(PC_Fonts.getFontByName("Consolas", 24, Font.BOLD)));
		highlighting.addWordHighlight(PC_GresHighlighting.msp(true, TRY), PC_Formatter.color(149, 0, 85)+PC_Formatter.font(PC_Fonts.getFontByName("Consolas", 24, Font.BOLD)));
		highlighting.addWordHighlight(PC_GresHighlighting.msp(true, OPERATORS), PC_Formatter.color(149, 0, 85)+PC_Formatter.font(PC_Fonts.getFontByName("Consolas", 24, Font.BOLD)));
		highlighting.addWordHighlight(PC_GresHighlighting.msp(true, EXTRENDS), PC_Formatter.color(149, 0, 85)+PC_Formatter.font(PC_Fonts.getFontByName("Consolas", 24, Font.BOLD)));
		highlighting.addWordHighlight(PC_GresHighlighting.msp(true, OTHERS), PC_Formatter.color(149, 0, 85)+PC_Formatter.font(PC_Fonts.getFontByName("Consolas", 24, Font.BOLD)));
		highlighting.addBlockHighlight(PC_GresHighlighting.msp(true, "//"), null, null, false, PC_Formatter.color(122, 122, 122), INNER);
		highlighting.addBlockHighlight(PC_GresHighlighting.msp(true, "/*"), null, PC_GresHighlighting.msp(true, "*/"), true, PC_Formatter.color(122, 122, 122), INNER);
		highlighting.addBlockHighlight(PC_GresHighlighting.msp(true, "/**"), null, PC_GresHighlighting.msp(true, "*/"), true, PC_Formatter.color(122, 122, 255), INNER);
		highlighting.addBlockHighlight(PC_GresHighlighting.msp(true, "\""), PC_GresHighlighting.msp(true, "\\"), PC_GresHighlighting.msp(true, "\""), false, PC_Formatter.color(255, 122, 122));
		highlighting.addBlockHighlight(PC_GresHighlighting.msp(true, "'"), PC_GresHighlighting.msp(true, "\\"), PC_GresHighlighting.msp(true, "'"), false, PC_Formatter.color(255, 122, 122));
		highlighting.addSpecialHighlight(PC_GresHighlighting.msp(true, "/**/"), PC_Formatter.color(122, 122, 122));
		return highlighting;
	}
	
	public static final String[] CONST = {"true", "false", "null"};
	public static final String[] PRIMITIVES = {"bool", "boolean", "byte", "char", "short", "int", "long", "float", "double", "void"};
	public static final String[] CLASSES = {"class", "enum", "interface", "@interface"};
	public static final String[] HEADER = {"package", "import"};
	public static final String[] MODIFIER = {"public", "private", "protected", "final", "abstract", "native", "static", "synchronized", "throws"};
	public static final String[] PARENTING = {"this", "super"};
	public static final String[] CONDITIONS = {"for", "while", "do", "if", "else"};
	public static final String[] SWITCH = {"switch", "case", "default"};
	public static final String[] BREAKS = {"return", "break", "continue", "throw"};
	public static final String[] TRY = {"try", "catch", "finally"};
	public static final String[] OPERATORS = {"and", "or", "xor", "bitand", "bitor", "mod", "not", "bitnot", "pow", "instanceof"};
	public static final String[] EXTRENDS = {"extends", "implements"};
	public static final String[] OTHERS = {"new", "asm", "assert"};
	
	public static PC_AutoAdd makeAutoAdd(){
		return new AutoAdd();
	}
	
	private static final class AutoAdd implements PC_AutoAdd{
		
		AutoAdd() {
			
		}

		@Override
		public void onCharAdded(PC_StringAdd add) {
			if(add.toAdd.length()==1 && "[]()\"'\t\n".indexOf(add.toAdd.charAt(0))!=-1){
				PC_WeaselSourceIterator iterator = new PC_WeaselSourceIterator(add.documentLine, add.pos);
				int type = iterator.getTypeAtPos();
				iterator.gotoInstructionStart(";");
				if(type==0){
					if(add.toAdd.equals("[")){
						add.toAdd = "[]";
						add.cursorPos = 1;
					}else if(add.toAdd.equals("]")){
						String text = add.documentLine.getText();
						if(text.length()>add.pos && text.charAt(add.pos)==']' && !needClose(PC_WeaselTokenKind.LINDEX, PC_WeaselTokenKind.RINDEX, add, iterator)){
							add.toAdd = "";
							add.cursorPos = 1;
						}
					}else if(add.toAdd.equals("(")){
						add.toAdd = "()";
						add.cursorPos = 1;
					}else if(add.toAdd.equals(")")){
						String text = add.documentLine.getText();
						if(text.length()>add.pos && text.charAt(add.pos)==')' && !needClose(PC_WeaselTokenKind.LGROUP, PC_WeaselTokenKind.RGROUP, add, iterator)){
							add.toAdd = "";
							add.cursorPos = 1;
						}
					}else if(add.toAdd.equals("\"")){
						add.toAdd = "\"\"";
						add.cursorPos = 1;
					}else if(add.toAdd.equals("'")){
						add.toAdd = "''";
						add.cursorPos = 1;
					}else if(add.toAdd.equals("\t")){
						doTabs(add);
					}else if(add.toAdd.equals("\n")){
						doNewLine(add);
					}
				}else if(type==1){
					if(add.toAdd.equals("\n")){
						doTabCalc(add);
						String tabs = add.toAdd;
						add.toAdd += " * ";
						PC_GresDocumentLine line = add.documentLine.next;
						while(line!=null && ((LineInfo)line.collectorInfo).lineEndsWithComment){
							if(line.getText().contains("/*")){
								line = null;
								break;
							}
							line = line.next;
						}
						if(line!=null){
							String text = line.getText();
							int i = text.indexOf("/*");
							if(i!=-1){
								int j = text.indexOf("*/");
								if(i+1<j)
									line = null;
							}
						}
						if(line==null){
							add.cursorPos = add.toAdd.length();
							add.toAdd += tabs+" */";
						}
					}else if(add.toAdd.equals("\t")){
						doTabs(add);
					}
				}else if(type==2){
					if(add.toAdd.equals("\n")){
						doTabCalc(add);
						add.toAdd = "\"+"+add.toAdd+"\"";
					}else if(add.toAdd.equals("\t")){
						doTabs(add);
					}
				}else if(type==3){
					if(add.toAdd.equals("\n")){
						doTabCalc(add);
					}else if(add.toAdd.equals("\t")){
						doTabs(add);
					}
				}else if(type==4){
					if(add.toAdd.equals("\n")){
						doTabCalc(add);
					}else if(add.toAdd.equals("\t")){
						doTabs(add);
					}
				}
			}
		}
		
		private static void doTabs(PC_StringAdd add){
			if(add.documentLine.prev!=null){
				String text = add.documentLine.getText();
				String prev = add.documentLine.prev.getText();
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
				if(((LineInfo)add.documentLine.prev.collectorInfo).lineEndsWithComment && size>0){
					size--;
				}
				int oSize = 0;
				int p = 0;
				for(int i=0; i<text.length(); i++){
					char c = text.charAt(i);
					if(c==' '|| c=='\t'){
						p++;
						if(c==' ')
							oSize++;
						else
							oSize+=4;
					}else{
						break;
					}
				}
				
				if(prev.trim().endsWith("{") && !((LineInfo)add.documentLine.collectorInfo).lineEndsWithComment){	
					size+=4;
				}
				int diff = size-oSize;
				if(p>add.pos || (p==add.pos && diff>0)){
					add.cursorPos = 0;
					add.toAdd="";
					while(diff>3){
						diff-=4;
						add.toAdd += "\t";
						add.cursorPos++;
					}
					while(diff>0){
						diff--;
						add.toAdd += " ";
						add.cursorPos++;
					}
					for(int i=add.pos; i<text.length(); i++){
						char c = text.charAt(i);
						if(c==' '|| c=='\t'){
							add.cursorPos++;
						}else{
							break;
						}
					}
				}
			}
		}
		
		private static void doTabCalc(PC_StringAdd add){
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
			if(add.documentLine.prev!=null && ((LineInfo)add.documentLine.prev.collectorInfo).lineEndsWithComment && !start.isEmpty() && start.charAt(start.length()-1)==' '){
				start = start.substring(0, start.length()-1);
			}
			add.toAdd += start;
		}
		
		private static void doNewLine(PC_StringAdd add){
			String text = add.documentLine.getText();
			String ll = text.substring(0, add.pos);
			doTabCalc(add);
			if(ll.endsWith("{")){
				add.cursorPos = 1+add.toAdd.length();
				String tabs = add.toAdd;
				add.toAdd += "\t";
				if(needBlockClose(add)){
					add.toAdd += tabs+"}";
				}
			}
		}
		
		private static boolean needBlockClose(PC_StringAdd add) {
			int num = 0;
			PC_GresDocumentLine line = add.document.getLine(1);
			boolean after = false;
			while(line!=null){
				num += ((LineInfo)line.collectorInfo).blocks;
				if(num<0){
					if(after)
						break;
					num=0;
				}
				if(line==add.documentLine)
					after = true;
				line = line.next;
			}
			return num>0;
		}

		private static boolean needClose(PC_WeaselTokenKind open, PC_WeaselTokenKind close, PC_StringAdd add, PC_WeaselSourceIterator iterator){
			boolean after = false;
			int count = 0;
			int opened = 0;
			while(true){
				PC_WeaselToken token = iterator.readNextToken();
				if(!after){
					after = !token.lineDesk.isAfter(add.documentLine, add.pos);
				}
				if(token.kind == open){
					if(after){
						opened++;
					}else{
						count++;
					}
				}else if(token.kind == close){
					if(after && opened>0){
						opened--;
					}else{
						count--;
						if(count<0){
							if(after)
								break;
							count = 0;
						}
					}
				}else if(token.kind==PC_WeaselTokenKind.SEMICOLON){
					if(after)
						break;
				}else if(token.kind==PC_WeaselTokenKind.EOF || token.kind==PC_WeaselTokenKind.UNKNOWN){
					break;
				}
			}
			return count>0;
		}
		
	}
	
	public static class LineInfo{

		int blocks;
		
		public boolean lineEndsWithComment;
		
		LineInfo(int blocks, boolean lineEndsWithComment) {
			this.blocks = blocks;
			this.lineEndsWithComment = lineEndsWithComment;
		}
		
	}
	
	public static PC_AutoComplete makeAutoComplete(PC_WeaselGresEdit weaselGresEdit){
		return new AutoComplete(weaselGresEdit);
	}
	
	private static class AutoComplete implements PC_AutoComplete{
		
		private PC_WeaselGresEdit weaselGresEdit;
		
		AutoComplete(PC_WeaselGresEdit weaselGresEdit) {
			this.weaselGresEdit = weaselGresEdit;
		}

		@Override
		public void onStringAdded(PC_GresComponent component, PC_GresDocument document, PC_GresDocumentLine line, String toAdd, int x, PC_AutoCompleteDisplay info) {
			if(info.display){
				if(toAdd.matches("\\w+")){
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
			PC_Weasel.makeComplete(component, document, line, x, info, this.weaselGresEdit);
		}

		@Override
		public PC_GresDocInfoCollector getInfoCollector() {
			return new LineInfomaker();
		}
		
	}
	
	private static class LineInfomaker implements PC_GresDocInfoCollector{

		LineInfomaker() {
			
		}

		@Override
		public void onLineChange(PC_GresDocumentLine line) {
			//
		}

		@Override
		public void onLineChanged(PC_GresDocumentLine line) {
			//
		}

		@Override
		public boolean onLineRecalc(PC_GresDocumentLine line) {
			String text = line.getText();
			int blocks = 0;
			int type = line.prev!=null && ((LineInfo)line.prev.collectorInfo).lineEndsWithComment?1:0;
			for(int i=0; i<text.length(); i++){
				char c= text.charAt(i);
				if(type==1){
					if(c=='*'){
						if(i+1<text.length()){
							c=text.charAt(i+1);
							if(c=='/'){
								type = 0;
								i++;
							}
						}
					}
				}else if(type==2){
					if(c=='\\'){
						if(i+1<text.length()){
							c = text.charAt(++i);
						}
					}else if(c=='"'){
						type = 0;
					}
				}else if(type==3){
					if(c=='\\'){
						if(i+1<text.length()){
							c = text.charAt(++i);
						}
					}else if(c=='\''){
						type = 0;
					}
				}else{
					if(c=='{'){
						blocks++;
					}else if(c=='}'){
						blocks--;
					}else if(c=='"'){
						type = 2;
					}else if(c=='\''){
						type = 3;
					}else if(c=='/'){
						if(i+1<text.length()){
							c=text.charAt(i+1);
							if(c=='*'){
								type = 1;
								i++;
							}else if(c=='/'){
								break;
							}
						}
					}
				}
			}
			boolean b = line.collectorInfo==null||((LineInfo)line.collectorInfo).lineEndsWithComment!=(type==1);
			line.collectorInfo = new LineInfo(blocks, type==1);
			return b;
		}
		
	}
	
}
