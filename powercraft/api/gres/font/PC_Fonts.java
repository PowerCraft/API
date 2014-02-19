package powercraft.api.gres.font;

import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;
import powercraft.api.PC_ClientUtils;

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
	
	public static PC_FontTexture create(Font font, char[] customCharsArray){
		PC_FontTexture texture = fontTextures.get(font);
		if(texture==null){
			fontTextures.put(font, texture = new PC_FontTexture(font, true, customCharsArray));
		}else{
			texture.addCustomChars(customCharsArray);
		}
		PC_ClientUtils.mc().renderEngine.loadTexture(texture.getResourceLocation(), texture);
		return texture;
	}
	
	public static PC_FontTexture create(ResourceLocation location, char[] customCharsArray){
		ITextureObject texture = PC_ClientUtils.mc().renderEngine.getTexture(location);
		if(texture==null){
			PC_FontTexture ft = new PC_FontTexture(location, true, customCharsArray);
			PC_ClientUtils.mc().renderEngine.loadTexture(location, ft);
			return ft;
		}else if(texture instanceof PC_FontTexture){
			((PC_FontTexture)texture).addCustomChars(customCharsArray);
			PC_ClientUtils.mc().renderEngine.loadTexture(location, texture);
			return (PC_FontTexture)texture;
		}
		return null;
	}
	
}
