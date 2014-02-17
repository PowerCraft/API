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
        int tmp;

        if (x1 < x2){
        	tmp = x1;
            x1 = x2;
            x2 = tmp;
        }

        if (y1 < y2){
        	tmp = y1;
        	y1 = y2;
        	y2 = tmp;
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
        tessellator.addVertex(x1, y2, 0);
        tessellator.addVertex(x2, y2, 0);
        tessellator.addVertex(x2, y1, 0);
        tessellator.addVertex(x1, y1, 0);
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
        GL11.glEnable(GL11.GL_DEPTH_TEST);
		itemRenderer.renderItemAndEffectIntoGUI(font, mc.getTextureManager(), itemStack, x, y);
        itemRenderer.renderItemOverlayIntoGUI(font, mc.getTextureManager(), itemStack, x, y, text);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }
    
	public static void drawItemStack(int x, int y, ItemStack itemStack, String text) {
		if(itemStack==null)
			return;
		GL11.glTranslated(0, 0, 100);
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		int k = 240;
		int i1 = 240;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, k / 1.0F, i1 / 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawEasyItemStack(x, y, itemStack, text);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		GL11.glTranslated(0, 0, -100);
	}

	public static void drawTooltip(int x, int y, int wWidth, int wHeigth, List<String> list) {
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		int maxWidth = 0;
		for (String s : list) {
			int width = fontRenderer.getStringWidth(s);
			if (width > maxWidth) {
				maxWidth = width;
			}
		}

		x += 12;
		y += 12;
		int k1 = 8;

		if (list.size() > 1) {
			k1 += 2 + (list.size() - 1) * 10;
		}

		if (x + maxWidth > wWidth) {
			x -= 28 + maxWidth;
		}

		if (y + k1 + 6 > wHeigth) {
			y = wHeigth - k1 - 6;
		}

		final int l1 = -267386864;
		drawGradientRect(x - 3, y - 4, maxWidth + 6, 1, l1, l1);
		drawGradientRect(x - 3, y + k1 + 3, maxWidth + 6, 1, l1, l1);
		drawGradientRect(x - 3, y - 3, maxWidth + 6, k1 + 6, l1, l1);
		drawGradientRect(x - 4, y - 3, 1, k1 + 6, l1, l1);
		drawGradientRect(x + maxWidth + 3, y - 3, 1, k1 + 6, l1, l1);
		final int i2 = 1347420415;
		final int j2 = (i2 & 16711422) >> 1 | i2 & -16777216;
		drawGradientRect(x - 3, y - 3 + 1, 1, k1 + 4, i2, j2);
		drawGradientRect(x + maxWidth + 2, y - 3 + 1, 1, k1 + 4, i2, j2);
		drawGradientRect(x - 3, y - 3, maxWidth + 6, 1, i2, i2);
		drawGradientRect(x - 3, y + k1 + 2, maxWidth + 6, 1, j2, j2);

		boolean isMainLine = true;
		for (String s : list) {
			fontRenderer.drawStringWithShadow(s, x, y, -1);
			if (isMainLine) {
				y += 2;
				isMainLine = false;
			}
			y += 10;
		}
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		RenderHelper.enableStandardItemLighting();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
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
	
}
