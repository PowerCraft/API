package powercraft.api.script.weasel;

import java.awt.Font;

import powercraft.api.gres.autoadd.PC_AutoAdd;
import powercraft.api.gres.autoadd.PC_StringAdd;
import powercraft.api.gres.doc.PC_GresHighlighting;
import powercraft.api.gres.font.PC_Fonts;
import powercraft.api.gres.font.PC_Formatter;

public class PC_WeaselHighlighting {

	public static PC_GresHighlighting makeHighlighting(){
		PC_GresHighlighting highlighting = new PC_GresHighlighting();
		PC_GresHighlighting INNER = new PC_GresHighlighting();
		INNER.addWordHighlight(PC_GresHighlighting.msp(false, "weasel", "xscript"), PC_Formatter.color(84, 217, 255)+PC_Formatter.font(PC_Fonts.getByName("Consolas", Font.ITALIC, 24)));
		INNER.addWordHighlight(PC_GresHighlighting.msp(true, "TODO"), PC_Formatter.color(84, 217, 255)+PC_Formatter.font(PC_Fonts.getByName("Consolas", Font.BOLD, 24)));
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
		highlighting.addBlockHighlight(PC_GresHighlighting.msp(true, "\""), PC_GresHighlighting.msp(true, "\\"), PC_GresHighlighting.msp(true, "\""), true, PC_Formatter.color(255, 122, 122));
		highlighting.addBlockHighlight(PC_GresHighlighting.msp(true, "'"), PC_GresHighlighting.msp(true, "\\"), PC_GresHighlighting.msp(true, "'"), true, PC_Formatter.color(255, 122, 122));
		return highlighting;
	}
	
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
	
}
