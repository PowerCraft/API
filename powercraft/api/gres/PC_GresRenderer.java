package powercraft.api.gres;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import powercraft.api.renderer.PC_OpenGL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_GresRenderer {

	private static Minecraft mc = Minecraft.getMinecraft();
	private static RenderItem itemRenderer = new RenderItem();
	private static FontRenderer fontRenderer = mc.fontRenderer;
	
	public static void setFontRenderer(FontRenderer fontRenderer){
		PC_GresRenderer.fontRenderer = fontRenderer;
	}
	
	public static void drawHorizontalLine(int x1, int x2, int y, int color){
        if (x2 < x1){
        	drawRect(x2, y, x1 + 1, y + 1, color);
        }else{
        	drawRect(x1, y, x2 + 1, y + 1, color);
        }
    }

	public static void drawVerticalLine(int x, int y1, int y2, int color){
        if (y2 < y1){
        	drawRect(x, y2, x + 1, y1 + 1, color);
        }else{
        	drawRect(x, y1, x + 1, y2 + 1, color);
        }

    }

    /**
     * Draws a solid color rectangle with the specified coordinates and color. Args: x1, y1, x2, y2, color
     */
    public static void drawRect(int x1, int y1, int x2, int y2, int color){
        int nx1, nx2, ny1, ny2;
        if (x1 < x2){
        	nx1 = x2;
        	nx2 = x1;
        }else{
        	nx1 = x1;
        	nx2 = x2;
        }

        if (y1 < y2){
        	ny1 = y2;
        	ny2 = y1;
        }else{
        	ny1 = y1;
        	ny2 = y2;
        }

        int alpha = (color >> 24 & 255);
        int red = (color >> 16 & 255);
        int green = (color >> 8 & 255);
        int blue = (color & 255) ;
        Tessellator tessellator = Tessellator.instance;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(red, green, blue, alpha);
        tessellator.addVertex(nx1, ny2, 0);
        tessellator.addVertex(nx2, ny2, 0);
        tessellator.addVertex(nx2, ny1, 0);
        tessellator.addVertex(nx1, ny1, 0);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static void drawEasyItemStack(int x, int y, ItemStack itemStack, String text) {
    	if(itemStack==null)
			return;
		FontRenderer font = itemStack.getItem().getFontRenderer(itemStack);
		if(font==null)
			font  = fontRenderer;
		GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        PC_OpenGL.pushMatrix();
        itemRenderer.zLevel = 200;
		itemRenderer.renderItemAndEffectIntoGUI(font, mc.getTextureManager(), itemStack, x, y);
        itemRenderer.renderItemOverlayIntoGUI(font, mc.getTextureManager(), itemStack, x, y, text);
        itemRenderer.zLevel = 0;
        PC_OpenGL.popMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthMask(false);
        PC_OpenGL.checkError("drawEasyItemStack");
    }
    
	public static void drawItemStack(int x, int y, ItemStack itemStack, String text) {
		if(itemStack==null)
			return;
		setupGuiItemLighting();
		enableGuiItemLighting();
		drawEasyItemStack(x, y, itemStack, text);
        GL11.glEnable(GL11.GL_BLEND);
        disableGuiItemLighting();
	}
	
	public static void drawItemStackAllreadyLighting(int x, int y, ItemStack itemStack, String text) {
		if(itemStack==null)
			return;
		enableGuiItemLighting();
		drawEasyItemStack(x, y, itemStack, text);
        GL11.glEnable(GL11.GL_BLEND);
        disableGuiItemLighting();
	}

	public static void drawTooltip(int x, int y, int wWidth, int wHeigth, List<String> list) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		int maxWidth = 0;
		for (String s : list) {
			int width = PC_GresRenderer.fontRenderer.getStringWidth(s);
			if (width > maxWidth) {
				maxWidth = width;
			}
		}

		int nx = x+12;
		int ny = y+12;
		int k1 = 8;

		if (list.size() > 1) {
			k1 += 2 + (list.size() - 1) * 10;
		}

		if (nx + maxWidth > wWidth) {
			nx -= 28 + maxWidth;
		}

		if (ny + k1 + 6 > wHeigth) {
			ny = wHeigth - k1 - 6;
		}

		final int l1 = -267386864;
		drawGradientRect(nx - 3, ny - 4, maxWidth + 6, 1, l1, l1);
		drawGradientRect(nx - 3, ny + k1 + 3, maxWidth + 6, 1, l1, l1);
		drawGradientRect(nx - 3, ny - 3, maxWidth + 6, k1 + 6, l1, l1);
		drawGradientRect(nx - 4, ny - 3, 1, k1 + 6, l1, l1);
		drawGradientRect(nx + maxWidth + 3, ny - 3, 1, k1 + 6, l1, l1);
		final int i2 = 1347420415;
		final int j2 = (i2 & 16711422) >> 1 | i2 & -16777216;
		drawGradientRect(nx - 3, ny - 3 + 1, 1, k1 + 4, i2, j2);
		drawGradientRect(nx + maxWidth + 2, ny - 3 + 1, 1, k1 + 4, i2, j2);
		drawGradientRect(nx - 3, ny - 3, maxWidth + 6, 1, i2, i2);
		drawGradientRect(nx - 3, ny + k1 + 2, maxWidth + 6, 1, j2, j2);

		boolean isMainLine = true;
		for (String s : list) {
			PC_GresRenderer.fontRenderer.drawStringWithShadow(s, nx, ny, -1);
			if (isMainLine) {
				ny += 2;
				isMainLine = false;
			}
			ny += 10;
		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
	}
    
	public static void drawGradientRect(int x, int y, int width, int height, int colorTop, int colorBottom) {

		float topAlpha = (colorTop >> 24 & 255) / 255.0F;
		float topRed = (colorTop >> 16 & 255) / 255.0F;
		float topGreen = (colorTop >> 8 & 255) / 255.0F;
		float topBlue = (colorTop & 255) / 255.0F;
		float bottomAlpha = (colorBottom >> 24 & 255) / 255.0F;
		float bottomRed = (colorBottom >> 16 & 255) / 255.0F;
		float bottomGreen = (colorBottom >> 8 & 255) / 255.0F;
		float bottomBlue = (colorBottom & 255) / 255.0F;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_F(topRed, topGreen, topBlue, topAlpha);
		tessellator.addVertex(x + width, y, 0);
		tessellator.addVertex(x, y, 0);
		tessellator.setColorRGBA_F(bottomRed, bottomGreen, bottomBlue, bottomAlpha);
		tessellator.addVertex(x, y + height, 0);
		tessellator.addVertex(x + width, y + height, 0);
		tessellator.draw();
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public static void drawTerrainIcon(int x, int y, int width, int height, IIcon icon) {
		mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		drawIcon(x, y, width, height, icon);
	}
	
	public static void drawIcon(int x, int y, int width, int height, IIcon icon) {
		Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, 0, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(x + width, y + height, 0, icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV(x + width, y, 0, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(x, y, 0, icon.getMinU(), icon.getMinV());
        tessellator.draw();
	}

	public static void setupGuiItemLighting() {
		RenderHelper.enableGUIStandardItemLighting();
	}

	public static void disableGuiItemLighting() {
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}
	
	public static void enableGuiItemLighting() {
		GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);
        GL11.glEnable(GL11.GL_LIGHT1);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		/*int k = 240;
		int i1 = 240;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, k / 1.0F, i1 / 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);*/
	}
	
}
