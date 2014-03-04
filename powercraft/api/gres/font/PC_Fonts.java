package powercraft.api.gres.font;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;
import powercraft.api.PC_Api;
import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_Logger;
import powercraft.api.PC_Utils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_Fonts {

	private static List<PC_FontTexture> fonts = new ArrayList<PC_FontTexture>();
	private static HashMap<Font, PC_FontTexture> fontTextures = new HashMap<Font, PC_FontTexture>();
	
	static int addFont(PC_FontTexture font){
		fonts.add(font);
		return fonts.size();
	}

	public static PC_FontTexture get(int fontID) {
		return fonts.get(fontID-1);
	}

	public static PC_FontTexture create(PC_FontContainer font) {
		return create(font, null);
	}
	
	public static PC_FontTexture create(PC_FontContainer font, char[] customCharsArray){
		PC_FontTexture texture = fontTextures.get(font);
		if(texture==null){
			fontTextures.put(font.getFont(), texture = new PC_FontTexture(font, true, customCharsArray));
		}else{
			texture.addCustomChars(customCharsArray);
		}
		texture.createTextures();
		return texture;
	}
	
	private static PC_FontContainer fromResourceLocation(ResourceLocation location){
		ITextureObject texture = PC_ClientUtils.mc().renderEngine.getTexture(location);
		PC_FontContainer fc = null;
		if(texture==null){
			fc = new PC_FontContainer();
			fc.setResourceLocation(location);
		}else if(texture instanceof PC_FontContainer){
			fc = (PC_FontContainer) texture;
		}
		if(fc!=null){
			PC_ClientUtils.mc().renderEngine.loadTexture(location, fc);
			return fc;
		}
		return null;
	}
	
	private static PC_FontContainer getFont(String name){
		return fromResourceLocation(PC_Utils.getResourceLocation(PC_Api.INSTANCE, "fonts/"+name.toLowerCase()+".ttf"));
	}
	
	private static final String defaultTextureName = "Minecraftia";
	
	public static PC_FontTexture getDefaultFont(){
		return getByName(defaultTextureName, 0, 8);
	}
	
	public static PC_FontTexture getByName(String fontName, int style, float size){
		return getByName(fontName, style, size, null);
	}
		
	public static PC_FontTexture getByName(String fontName, int style, float size, char[] customCharsArray){
		PC_FontContainer f=new PC_FontContainer();
		for(PC_FontTexture font:fontTextures.values()){
			if(font!=null && fontName.equalsIgnoreCase(font.getFont().getName())){
				f.copyFrom(font.getCont());
				break;
			}
		}
		if(f.noFont()){
			PC_Logger.warning("Font %s hasn't been loaded yet.", fontName);
			f.copyFrom(getFont(fontName));
		}
		if(f.noFont()){
			PC_Logger.warning("Font %s couldn't be found at the local files.", fontName);
			for (Font font : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
				if (font.getName().equalsIgnoreCase(fontName)) {
					f.setFont(font);
					break;
				}
			}
		}
		if(f.noFont() && !(fontName==defaultTextureName)){
			PC_Logger.severe("Font %s isn't existent in the system. Using Default Font instead.", fontName);
			PC_FontTexture ft = getDefaultFont();
			if(ft!=null && !ft.getCont().noFont())
				f.copyFrom(ft.getCont());
		}
		if(f.noFont()){
			PC_Logger.severe("Even the default font couldn't be found. Returning NULL");
			return null;
		}
		f.deriveFont(style, size);
		PC_Logger.warning("Font %s has been derived.", fontName);
		return create(f, customCharsArray);
	}
	
}
