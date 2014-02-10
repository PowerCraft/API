package powercraft.api.gres;


import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;

@SideOnly(Side.CLIENT)
public class PC_GresTexture {

	private final ResourceLocation texture;
	private final PC_Vec2I size;
	private final PC_RectI frame;
	private final PC_Vec2I locations[];


	public PC_GresTexture(ResourceLocation texture, PC_Vec2I size, PC_RectI frames, PC_Vec2I locations[]) {

		this.texture = texture;
		this.size = size;
		this.frame = frames;
		this.locations = locations;
	}


	public PC_RectI getFrame() {

		return new PC_RectI(frame);
	}


	public PC_Vec2I getMinSize() {

		return frame.getSize().add(frame.getLocation());
	}


	public PC_Vec2I getDefaultSize() {

		return new PC_Vec2I(size);
	}


	public void drawBasic(int x, int y, int width, int height, float u, float v, int state) {

		float f = 0.00390625F;
		float f1 = 0.00390625F;
		float uc = (u * size.x) + locations[state].x;
		float vc = (v * size.y) + locations[state].y;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x, y + height, 0, uc * f, (vc + height) * f1);
		tessellator.addVertexWithUV(x + width, y + height, 0, (uc + width) * f, (vc + height) * f1);
		tessellator.addVertexWithUV(x + width, y, 0, (uc + width) * f, vc * f1);
		tessellator.addVertexWithUV(x, y, 0, uc * f, vc * f1);
		tessellator.draw();
	}


	public void draw(int x, int y, int width, int height, int state) {

		PC_ClientUtils.mc().getTextureManager().bindTexture(texture);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		PC_Vec2I location = locations[state];

		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		
		if (frame.y > 0) {
			if (frame.x > 0) {
				drawTexturedModalRect(x, y, location.x, location.y, frame.x, frame.y);
			}
			renderTextureSliced_static(x + frame.x, y, width - frame.x - frame.width, frame.y, location.x + frame.x, location.y, size.x - frame.x
					- frame.width, size.y);
			if (frame.width > 0) {
				drawTexturedModalRect(x + width - frame.width, y, location.x + size.x - frame.width, location.y, frame.width, frame.y);
			}
		}
		if (frame.x > 0) {
			renderTextureSliced_static(x, y + frame.y, frame.x, height - frame.y - frame.height, location.x, location.y + frame.y, size.x, size.y
					- frame.y - frame.height);
		}

		renderTextureSliced_static(x + frame.x, y + frame.y, width - frame.x - frame.width, height - frame.y - frame.height, location.x + frame.x,
				location.y + frame.y, size.x - frame.x - frame.width, size.y - frame.y - frame.height);

		if (frame.width > 0) {
			renderTextureSliced_static(x + width - frame.width, y + frame.y, frame.width, height - frame.y - frame.height, location.x + size.x
					- frame.width, location.y + frame.y, frame.width, size.y - frame.y - frame.height);
		}

		if (frame.height > 0) {
			if (frame.x > 0) {
				drawTexturedModalRect(x, y + height - frame.height, location.x, location.y + size.y - frame.height, frame.x, frame.height);
			}
			renderTextureSliced_static(x + frame.x, y + height - frame.height, width - frame.x - frame.width, frame.height, location.x + frame.x,
					location.y + size.y - frame.height, size.x - frame.x - frame.width, frame.height);
			if (frame.width > 0) {
				drawTexturedModalRect(x + width - frame.width, y + height - frame.height, location.x + size.x - frame.width, location.y + size.y
						- frame.height, frame.width, frame.height);
			}
		}
		
		tessellator.draw();
		
	}

	public void drawProzentual(int x, int y, int width, int height, float u, float v, float w, float h, int state) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		float uc = (u * size.x) + locations[state].x;
		float vc = (v * size.y) + locations[state].y;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x, y + height, 0, uc * f, (vc + h*size.y) * f1);
		tessellator.addVertexWithUV(x + width, y + height, 0, (uc + w*size.x) * f, (vc + h*size.y) * f1);
		tessellator.addVertexWithUV(x + width, y, 0, (uc + w*size.x) * f, vc * f1);
		tessellator.addVertexWithUV(x, y, 0, uc * f, vc * f1);
		tessellator.draw();
	}

	private static void renderTextureSliced_static(int x, int y, int width, int height, int u, int v, int imgWidth, int imgHeight) {

		for (int xx = 0; xx < width; xx += imgWidth) {
			for (int yy = 0; yy < height; yy += imgHeight) {
				int sx = imgWidth;
				int sy = imgHeight;
				if (xx + sx > width) {
					sx = width - xx;
				}
				if (yy + sy > height) {
					sy = height - yy;
				}
				drawTexturedModalRect(x + xx, y + yy, u, v, sx, sy);
			}
		}
	}


	private static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {

		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.instance;
		tessellator.addVertexWithUV(x, y + height, 0, u * f, (v + height) * f1);
		tessellator.addVertexWithUV(x + width, y + height, 0, (u + width) * f, (v + height) * f1);
		tessellator.addVertexWithUV(x + width, y, 0, (u + width) * f, v * f1);
		tessellator.addVertexWithUV(x, y, 0, u * f, v * f1);
	}

}
