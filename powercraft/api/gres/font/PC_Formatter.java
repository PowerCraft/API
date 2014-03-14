package powercraft.api.gres.font;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class PC_Formatter {

	public static final char START_SEQ = 65535;
	public static final char COLOR_SEQ = 0;
	public static final char FONT_SEQ = 1;
	public static final char SCALE_SEQ = 2;
	public static final char ERROR_SEQ = 3;
	public static final char ERRORSTOP_SEQ =4;
	public static final char RESET_SEQ = 5;
	
	public static final byte data[] = {3, 1, 1, 3, 0, 0};
	
	public static String reset(){
		return ""+START_SEQ+RESET_SEQ;
	}
	
	public static String color(int red, int green, int blue){
		return ""+START_SEQ+COLOR_SEQ+(char)red+(char)green+(char)blue;
	}
	
	public static String font(PC_FontTexture font){
		return ""+START_SEQ+FONT_SEQ+(char)font.getFontID();
	}
	
	public static String scale(int scale){
		return ""+START_SEQ+SCALE_SEQ+(char)scale;
	}
	
	public static String error(int red, int green, int blue){
		return ""+START_SEQ+ERROR_SEQ+(char)red+(char)green+(char)blue;
	}
	
	public static String errorStop() {
		return ""+START_SEQ+ERRORSTOP_SEQ;
	}
	
	public static String removeFormatting(String s){
		char[]ca = s.toCharArray();
		String unFormatted = "";
		for(int i=0; i<s.length(); i++){
			if(ca[i]==START_SEQ && i + 1<s.length()){
				char c = ca[++i];
				if(c<data.length){
					i+=data[c];
				}
			}else{
				unFormatted += ca[i];
			}
		}
		return unFormatted;
	}
	
	public static String removeErrorFormatting(String s){
		char[]ca = s.toCharArray();
		String unFormatted = "";
		for(int i=0; i<s.length(); i++){
			if(ca[i]==START_SEQ && i + 1<s.length()){
				char c = ca[++i];
				if(c==ERROR_SEQ){
					i+=3;
				}else if(c== ERRORSTOP_SEQ){
					//
				}else{
					unFormatted += ca[i-1];
					unFormatted += c;
					if(c<data.length){
						for(int j=0; j<data[c]; j++){
							unFormatted += ca[++i];
						}
					}
				}
			}else{
				unFormatted += ca[i];
			}
		}
		return unFormatted;
	}
	
	public static String substring(String s, int start, int end){
		char[]ca = s.toCharArray();
		int pos = 0;
		String sub="";
		for(int i=0; i<s.length(); i++){
			if(ca[i]==START_SEQ && i + 1<s.length()){
				char c = ca[++i];
				if(c<data.length){
					i+=data[c];
				}
			}else{
				if(pos>=end){
					return sub;
				}
				if(pos>=start){
					sub += ca[i];
				}
				pos++;
			}
		}
		return sub;
	}
	
	public static int indexOf(String text, int ch, int start){
		char[]ca = text.toCharArray();
		int pos = 0;
		for(int i=0; i<text.length(); i++){
			if(ca[i]==START_SEQ && i + 1<text.length()){
				char c = ca[++i];
				if(c<data.length){
					i+=data[c];
				}
			}else{
				if((pos>=start || start==-1) && ca[i]==ch){
					return pos;
				}
				pos++;
			}
		}
		return -1;
	}
	
	public static int lastIndexOf(String text, int ch, int start){
		char[]ca = text.toCharArray();
		int pos = 0;
		int best = -1;
		for(int i=0; i<text.length(); i++){
			if(ca[i]==START_SEQ && i + 1<text.length()){
				char c = ca[++i];
				if(c<data.length){
					i+=data[c];
				}
			}else{
				if(ca[i]==ch){
					best = pos;
				}
				if(pos>=start && start!=-1)
					return best;
				pos++;
			}
		}
		return best;
	}
	
}
