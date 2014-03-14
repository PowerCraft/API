package powercraft.api.gres.font;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import powercraft.api.PC_Api;
import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_Logger;
import powercraft.api.PC_Utils;
import powercraft.api.PC_ResourceReloadListener.PC_IResourceReloadListener;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_Fonts implements PC_IResourceReloadListener {
	
	private static List<PC_FontTexture> fonts = new ArrayList<PC_FontTexture>();
	private static HashMap<PC_FontData, PC_FontTexture> fontData = new HashMap<PC_FontData, PC_FontTexture>();
	private static HashMap<String, Font> loadedFonts = new HashMap<String, Font>();
	
	static int addFont(PC_FontTexture font) {
		fonts.add(font);
		return fonts.size();
	}
	
	public static PC_FontTexture get(int fontID) {
		return fonts.get(fontID - 1);
	}
	
	public static PC_FontTexture getFontByName(String name) {
		return getFontByName(name, 8.0f, 0);
	}
	
	public static PC_FontTexture getFontByName(String name, int style) {
		return getFontByName(name, 8.0f, style);
	}
	
	public static PC_FontTexture getFontByName(String name, float size) {
		return getFontByName(name, size, 0);
	}
	
	public static PC_FontTexture getFontByName(String name, float size, int style) {
		boolean isDefault = name.equals("Default") || name.equals("Minecraftia");
		String fontName;
		if (isDefault) {
			fontName = "Minecraftia";
		} else {
			fontName = name;
		}
		PC_FontData fd = new PC_FontData(fontName, size, style);
		PC_FontTexture fontTexture = fontData.get(fd);
		if(fontTexture==null){
			fontData.put(fd, fontTexture = new PC_FontTexture(fd));
			PC_ClientUtils.mc().renderEngine.loadTexture(fontTexture.getResourceLocation(), fontTexture);
		}
		return fontTexture;
	}
	
	public static Font getFontOrLoad(PC_FontData fd) {
		Font font = getFontOrLoad(fd.name);
		return font.deriveFont(fd.style, fd.size);
	}
	
	private static Font getFontOrLoad(String name){
		Font font = loadedFonts.get(name);
		if(font==null){
			loadedFonts.put(name, font = loadFont(name));
		}
		return font;
	}
	
	private static Font loadFont(String name){
		Font font = loadFromResourcePack(name);
		if(font!=null)
			return font;
		font = getSystemFont(name);
		if(font!=null)
			return font;
		if(name.equalsIgnoreCase("Minecraftia")){
			PC_Logger.severe("Default font not found, fallback to "+Font.SANS_SERIF);
			return loadFont(Font.SANS_SERIF);
		}else if(name.equalsIgnoreCase(Font.SANS_SERIF)){
			PC_Logger.severe(Font.SANS_SERIF+" font not found");
			return null;
		}
		PC_Logger.severe("%s font not found, fallback to Minecraftia", name);
		return loadFont("Minecraftia");
	}
	
	private static Font loadFromResourcePack(String name){
		ResourceLocation resourceLocation = PC_Utils.getResourceLocation(PC_Api.INSTANCE, "fonts/"+name+".ttf");
		InputStream inputstream = null;
		Font font = null;
		try {
			IResource resource = PC_ClientUtils.mc().getResourceManager().getResource(resourceLocation);
			inputstream = resource.getInputStream();
			font = Font.createFont(Font.TRUETYPE_FONT, inputstream);
			inputstream.close();
		} catch(FileNotFoundException e){
			PC_Logger.warning("Font %s not found in resource pack", name);
			return null;
		} catch (Exception e) { // Do not use Java 1.7, use Java 1.6
			throw new RuntimeException(e); // Should we create a runtime Error and crash report?
		} finally {
			if (inputstream != null)
				try {
					inputstream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return font;
	}

	private static Font[] getSystemFonts() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
	}

	private static Font getSystemFont(String name) {
		for (Font font : getSystemFonts()) {
			if (font.getName().equalsIgnoreCase(name)) {
				return font;
			}
		}
		return null;
	}
	
	@Override
	public void onResourceReload() {
		loadedFonts.clear();
	}
	
}
