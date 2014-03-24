package powercraft.api.script.weasel;

import java.awt.Font;

import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.autoadd.PC_AutoAdd;
import powercraft.api.gres.autoadd.PC_AutoComplete;
import powercraft.api.gres.autoadd.PC_AutoCompleteDisplay;
import powercraft.api.gres.autoadd.PC_StringAdd;
import powercraft.api.gres.doc.PC_GresDocInfoCollector;
import powercraft.api.gres.doc.PC_GresDocument;
import powercraft.api.gres.doc.PC_GresDocumentLine;
import powercraft.api.gres.doc.PC_GresHighlighting;
import powercraft.api.gres.font.PC_Fonts;
import powercraft.api.gres.font.PC_Formatter;

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
			if(add.toAdd.equals("[")){
				add.toAdd = "[]";
				add.cursorPos = 1;
			}else if(add.toAdd.equals("]")){
				if(!needClose('[', ']', add)){
					add.toAdd = "";
					add.cursorPos = 1;
				}
			}else if(add.toAdd.equals("(")){
				add.toAdd = "()";
				add.cursorPos = 1;
			}else if(add.toAdd.equals(")")){
				if(!needClose('(', ')', add)){
					add.toAdd = "";
					add.cursorPos = 1;
				}
			}else if(add.toAdd.equals("\"")){
				if(add.pos==0 || add.documentLine.getText().charAt(add.pos-1)!='\\'){
					add.toAdd = "\"\"";
					add.cursorPos = 1;
				}
			}else if(add.toAdd.equals("'")){
				if(add.pos==0 || add.documentLine.getText().charAt(add.pos-1)!='\\'){
					add.toAdd = "''";
					add.cursorPos = 1;
				}
			}else if(add.toAdd.equals("\t")){
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
					
					if(prev.trim().endsWith("{")){	
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
			}else if(add.toAdd.equals("\n")){
				String text = add.documentLine.getText();
				String ll = text.substring(0, add.pos);
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
					add.cursorPos = 2+start.length();
					add.toAdd += "\t";
					if(needBlockClose(add)){
						add.toAdd += "\n"+start+"}";
					}
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

		private static boolean needClose(char open, char close, PC_StringAdd add){
			String line = add.documentLine.getText();
			if(line.length()==add.pos)
				return true;
			if(line.charAt(add.pos)==close){
				int count = 0;
				String l = line;
				int i = add.pos-1;
				PC_GresDocumentLine lin = add.documentLine;
				int opened = 0;
				while(true){
					if(i==-1){
						lin = lin.prev;
						if(lin==null)
							break;
						l = lin.getText();
						i=l.length()-1;
					}else{
						char c = l.charAt(i--);
						if(c==';'){
							break;
						}else if(c=='{'){
							break;
						}else if(c=='}'){
							break;
						}else if(c==close){
							opened++;
						}else if(c==open){
							if(opened>0){
								opened--;
							}else{
								count++;
							}
						}
					}
				}
				l = line;
				i = add.pos;
				lin = add.documentLine;
				opened = 0;
				while(true){
					if(i==l.length()){
						lin = lin.next;
						if(lin==null)
							break;
						l = lin.getText();
						i=0;
					}else{
						char c = l.charAt(i++);
						if(c==';'){
							break;
						}else if(c=='{'){
							break;
						}else if(c=='}'){
							break;
						}else if(c==open){
							opened++;
						}else if(c==close){
							if(opened>0){
								opened--;
							}else{
								count--;
								if(count<0)
									break;
							}
						}
					}
				}
				return count<0;
			}
			return true;
		}
		
	}
	
	private static class LineInfo{

		int blocks;
		
		public LineInfo(int blocks) {
			this.blocks = blocks;
		}
		
	}
	
	public static PC_AutoComplete makeAutoComplete(){
		return new AutoComplete();
	}
	
	private static class AutoComplete implements PC_AutoComplete{
		
		AutoComplete() {
			
		}

		@Override
		public void onStringAdded(PC_GresComponent component, PC_GresDocument document, PC_GresDocumentLine line, String toAdd, int x, PC_AutoCompleteDisplay info) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void makeComplete(PC_GresComponent component, PC_GresDocument document, PC_GresDocumentLine line, int x, PC_AutoCompleteDisplay info) {
			// TODO Auto-generated method stub
			
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
			String text = line.getText();
			int blocks = 0;
			for(int i=0; i<text.length(); i++){
				char c= text.charAt(i);
				if(c=='{'){
					blocks++;
				}else if(c=='}'){
					blocks--;
				}
			}
			line.collectorInfo = new LineInfo(blocks);
		}
		
	}
	
}
