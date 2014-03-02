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

		return new PC_RectI(this.frame);
	}


	public PC_Vec2I getMinSize() {

		return this.frame.getSize().add(this.frame.getLocation());
	}


	public PC_Vec2I getDefaultSize() {

		return new PC_Vec2I(this.size);
	}


	public void drawBasic(int x, int y, int width, int height, float u, float v, int state) {

		float f = 0.00390625F;
		float f1 = 0.00390625F;
		float uc = (u * this.size.x) + this.locations[state].x;
		float vc = (v * this.size.y) + this.locations[state].y;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x, y + height, 0, uc * f, (vc + height) * f1);
		tessellator.addVertexWithUV(x + width, y + height, 0, (uc + width) * f, (vc + height) * f1);
		tessellator.addVertexWithUV(x + width, y, 0, (uc + width) * f, vc * f1);
		tessellator.addVertexWithUV(x, y, 0, uc * f, vc * f1);
		tessellator.draw();
	}


	public void draw(int x, int y, int width, int height, int state) {

		PC_ClientUtils.mc().getTextureManager().bindTexture(this.texture);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		PC_Vec2I location = this.locations[state];

		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		
		if (this.frame.y > 0) {
			if (this.frame.x > 0) {
				drawTexturedModalRect(x, y, location.x, location.y, this.frame.x, this.frame.y);
			}
			renderTextureSliced_static(x + this.frame.x, y, width - this.frame.x - this.frame.width, this.frame.y, location.x + this.frame.x, location.y, this.size.x - this.frame.x
					- this.frame.width, this.size.y);
			if (this.frame.width > 0) {
				drawTexturedModalRect(x + width - this.frame.width, y, location.x + this.size.x - this.frame.width, location.y, this.frame.width, this.frame.y);
			}
		}
		if (this.frame.x > 0) {
			renderTextureSliced_static(x, y + this.frame.y, this.frame.x, height - this.frame.y - this.frame.height, location.x, location.y + this.frame.y, this.size.x, this.size.y
					- this.frame.y - this.frame.height);
		}

		renderTextureSliced_static(x + this.frame.x, y + this.frame.y, width - this.frame.x - this.frame.width, height - this.frame.y - this.frame.height, location.x + this.frame.x,
				location.y + this.frame.y, this.size.x - this.frame.x - this.frame.width, this.size.y - this.frame.y - this.frame.height);

		if (this.frame.width > 0) {
			renderTextureSliced_static(x + width - this.frame.width, y + this.frame.y, this.frame.width, height - this.frame.y - this.frame.height, location.x + this.size.x
					- this.frame.width, location.y + this.frame.y, this.frame.width, this.size.y - this.frame.y - this.frame.height);
		}

		if (this.frame.height > 0) {
			if (this.frame.x > 0) {
				drawTexturedModalRect(x, y + height - this.frame.height, location.x, location.y + this.size.y - this.frame.height, this.frame.x, this.frame.height);
			}
			renderTextureSliced_static(x + this.frame.x, y + height - this.frame.height, width - this.frame.x - this.frame.width, this.frame.height, location.x + this.frame.x,
					location.y + this.size.y - this.frame.height, this.size.x - this.frame.x - this.frame.width, this.frame.height);
			if (this.frame.width > 0) {
				drawTexturedModalRect(x + width - this.frame.width, y + height - this.frame.height, location.x + this.size.x - this.frame.width, location.y + this.size.y
						- this.frame.height, this.frame.width, this.frame.height);
			}
		}
		
		tessellator.draw();
		
	}

	public void drawProzentual(int x, int y, int width, int height, float u, float v, float w, float h, int state) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		float uc = (u * this.size.x) + this.locations[state].x;
		float vc = (v * this.size.y) + this.locations[state].y;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x, y + height, 0, uc * f, (vc + h*this.size.y) * f1);
		tessellator.addVertexWithUV(x + width, y + height, 0, (uc + w*this.size.x) * f, (vc + h*this.size.y) * f1);
		tessellator.addVertexWithUV(x + width, y, 0, (uc + w*this.size.x) * f, vc * f1);
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
