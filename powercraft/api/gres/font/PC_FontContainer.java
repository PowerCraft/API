package powercraft.api.gres.font;

import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import powercraft.api.PC_Logger;

public class PC_FontContainer extends AbstractTexture {
	private ResourceLocation res = null;
	private Font font = null;

	public PC_FontContainer() {
	}

	public void copyFrom(PC_FontContainer fc) {
		res = fc.res;
		font = fc.font;
	}

	public void setFont(Font f) {
		if (font != null || f == null)
			return;
		font = f;
	}

	public void setResourceLocation(ResourceLocation rl) {
		if (res != null || rl == null)
			return;
		res = rl;

	}

	public Font getFont() {
		return font;
	}

	public boolean noFont() {
		return font == null;
	}

	public ResourceLocation getResourceLocation() {
		return res;
	}

	public void deriveFont(int style, float size) {
		font = font.deriveFont(style, size);
	}

	@Override
	public void loadTexture(IResourceManager resourceManager) {
		if (font != null)
			return;
		PC_Logger.warning("trying to read %s", res.toString());
		InputStream inputstream = null;
		try {
			IResource resource = resourceManager.getResource(res);
			inputstream = resource.getInputStream();
			this.font = Font.createFont(Font.TRUETYPE_FONT, inputstream);
			this.font = this.font.deriveFont(8.0f);
			inputstream.close();
			PC_Logger.warning("read %s", res.toString());
		} catch (Exception e) { // Do not use Java 1.7, use Java 1.6
			this.font = null;
		} finally {
			if (inputstream != null)
				try {
					inputstream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

}