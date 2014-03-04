package powercraft.api.gres.font;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;
import powercraft.api.PC_Api;
import powercraft.api.PC_Logger;
import powercraft.api.PC_Utils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_Fonts {

	private static List<PC_FontTexture> fonts = new ArrayList<PC_FontTexture>();

	static int addFont(PC_FontTexture font) {
		fonts.add(font);
		return fonts.size();
	}

	public static PC_FontTexture get(int fontID) {
		return fonts.get(fontID - 1);
	}

	public static PC_FontTexture create(PC_FontTexture font) {
		return create(font, null);
	}

	public static PC_FontTexture create(PC_FontTexture font, char[] customCharsArray) {
		font.addCustomChars(customCharsArray);
		if (!font.canBeRendered()) {
			PC_Logger.warning("%s can't be rendered or is already rendered", font.getName());
			return null;
		}
		font.createTextures();
		return font;
	}

	private static final String defaultTextureName = "minecraftia";

	public static PC_FontTexture getDefaultFont() {
		return getByName(defaultTextureName, false, 0, 8, null, false);
	}

	public static PC_FontTexture getByName(String fontName, int style, float size) {
		return getByName(fontName, true, style, size, null);
	}

	public static PC_FontTexture getByName(String fontName, boolean antiAliased, int style, float size) {
		return getByName(fontName, antiAliased, style, size, null);
	}

	public static PC_FontTexture getByName(String fontName, boolean antiAliased, int style, float size,
			char[] customCharsArray) {
		return getByName(fontName, antiAliased, style, size, customCharsArray, true);
	}

	private static PC_FontTexture getByName(String fontName, boolean antiAliased, int style, float size,
			char[] customCharsArray, boolean canBeDefault) {
		PC_FontTexture f = new PC_FontTexture(fontName.toLowerCase(), antiAliased, customCharsArray);
		for (PC_FontTexture font : fonts) {
			if (font != null && fontName.equalsIgnoreCase(font.getName())) {
				Font fo = null;
				if (font.isAntiAliased() == antiAliased && !font.noFont()
						&& (fo = font.getFont()).getSize() == (int) size && fo.getStyle() == style) {
					PC_Logger.warning("return nr.1");
					return font;
				}
				if (fo != null) {
					f.setFont(fo);
					break;
				}
			}
		}

		if (f.noFont()) {
			PC_Logger.warning("Font %s hasn't been loaded yet.", fontName);
			f.setResourceLocation(PC_Utils.getResourceLocation(PC_Api.INSTANCE, "fonts/" + fontName + ".ttf"));
			f.reloadFromFile();
		}
		if (f.noFont()) {
			PC_Logger.warning("Font %s couldn't be found at the local files.", fontName);
			for (Font font : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
				if (font.getName().equalsIgnoreCase(fontName)) {
					f.setFont(font);
					break;
				}
			}
		}
		if (f.noFont() && canBeDefault) {
			PC_Logger.severe("Font %s isn't existent in the system. Using Default Font instead.", fontName);
			PC_FontTexture ft = getDefaultFont();
			if (ft != null)
				f = new PC_FontTexture(ft);
		}
		if (f.noFont()) {
			PC_Logger.severe("Even the default font couldn't be found. Returning NULL");
			PC_Logger.warning("return null (nr.2)");
			return null;
		}
		f.deriveFont(style, size);
		PC_Logger.warning("Font %s has been derived.", fontName);
		PC_Logger.warning("return nr.3");
		return create(f, customCharsArray);
	}

}
