package powercraft.api.gres.font;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_Formatter {

	public static char START_SEQ = 65535;
	public static char COLOR_SEQ = 0;
	public static char FONT_SEQ = 1;
	public static char ERROR_SEQ = 2;
	public static char ERRORSTOP_SEQ = 3;
	public static char RESET_SEQ = 4;
	
	public static byte data[] = {3, 1, 0, 0, 0};
	
	public static String reset(){
		return ""+START_SEQ+RESET_SEQ;
	}
	
	public static String color(int red, int green, int blue){
		return ""+START_SEQ+COLOR_SEQ+(char)red+(char)green+(char)blue;
	}
	
	public static String font(PC_FontTexture font){
		return ""+START_SEQ+FONT_SEQ+(char)font.getFontID();
	}
	
	public static String error(){
		return ""+START_SEQ+ERROR_SEQ;
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
	
}
