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
				if (!font.noFont() && (fo = font.getFont()).getSize() == (int) size) {
					PC_Logger.warning("Font %s, %s, %s was already loaded.1", fontName, style, size);
					return font;
				}
				if (fo != null) {
					f.setFont(fo);
					PC_Logger.warning("Font %s (, %s, %s) was already loaded.2", fontName, style, size);
					break;
				}
			}
		}

		if (f.noFont()) {
			f.setResourceLocation(PC_Utils.getResourceLocation(PC_Api.INSTANCE, "fonts/" + fontName + ".ttf"));
			if (f.reloadFromFile())
				PC_Logger.warning("Font %s, %s, %s was found locally.", fontName, style, size);
		}
		if (f.noFont()) {
			for (Font font : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
				if (font.getName().equalsIgnoreCase(fontName)) {
					f.setFont(font);
					PC_Logger.warning("Font %s, %s, %s found at the systems files.", fontName, style, size);
					break;
				}
			}
		}
		if (f.noFont() && canBeDefault) {
			PC_Logger.severe("Font %s, %s, %s isn't existent in the system. Using Default Font instead.", fontName,
					style, size);
			PC_FontTexture ft = getDefaultFont();
			if (ft != null)
				f = new PC_FontTexture(ft);
		}
		if (f.noFont()) {
			PC_Logger.severe("Even the default font couldn't be found. Returning NULL");
			return null;
		}
		f.deriveFont(style, size);
		PC_Logger.warning(
				"Font %s, %s, %s has been derived and is " + (f.readyToUse() ? "" : "not ") + "ready to use.",
				fontName, style, size);
		return f;
	}

}
