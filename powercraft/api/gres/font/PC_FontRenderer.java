package powercraft.api.gres.font;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;
import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_Vec2I;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_FontRenderer {

	private PC_FontTexture texture;
	
	public PC_FontRenderer(PC_FontTexture texture){
		this.texture = texture;
	}
	
	public PC_FontRenderer(ResourceLocation location){
		ITextureObject textureObject = PC_ClientUtils.mc().renderEngine.getTexture(location);
		if(textureObject instanceof PC_FontTexture){
			texture = (PC_FontTexture)textureObject;
		}
	}
	
	public void drawString(String text, int x, int y){
		drawString(text, x, y, texture);
	}
	
	public PC_Vec2I getStringSize(String text){
		return getStringSize(text, texture);
	}
	
	public PC_Vec2I getCharSize(char c){
		return getCharSize(c, texture);
	}
	
	public static void drawString(String text, int x, int y, ResourceLocation location){
		ITextureObject textureObject = PC_ClientUtils.mc().renderEngine.getTexture(location);
		if(textureObject instanceof PC_FontTexture){
			drawString(text, x, y, (PC_FontTexture)textureObject);
		}
	}
	
	public static void drawString(String text, int x, int y, PC_FontTexture texture){
		PC_FontTexture activeTexture = texture;
		PC_ClientUtils.mc().renderEngine.bindTexture(activeTexture.getResourceLocation());
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		float size = texture.getTextureSize();
		int red = 255;
		int green = 255;
		int blue = 255;
		for(int i=0; i<text.length(); i++){
			char c = text.charAt(i);
			if(c==PC_Formatter.START_SEQ && i + 1 < text.length()){
				c = text.charAt(++i);
				if(c==PC_Formatter.COLOR_SEQ){
					red = text.charAt(++i);
					green = text.charAt(++i);
					blue = text.charAt(++i);
				}else if(c==PC_Formatter.FONT_SEQ){
					int font = text.charAt(++i);
					activeTexture = PC_Fonts.get(font);
					tessellator.draw();
					PC_ClientUtils.mc().renderEngine.bindTexture(activeTexture.getResourceLocation());
					tessellator.startDrawingQuads();
				}else{
					activeTexture = texture;
					tessellator.draw();
					PC_ClientUtils.mc().renderEngine.bindTexture(activeTexture.getResourceLocation());
					tessellator.startDrawingQuads();
				}
			}else{
				PC_CharData data = activeTexture.getCharData(c);
				if(data!=null){
					float tx = data.storedX/size;
					float ty = data.storedY/size;
					float tw = data.width/size;
					float th = data.height/size;
					tessellator.setColorOpaque(red, green, blue);
					tessellator.addVertexWithUV(x, y, 0, tx, ty);
					tessellator.addVertexWithUV(x+data.width, y, 0, tx+tw, ty);
					tessellator.addVertexWithUV(x+data.width, y+data.height, 0, tx+tw, ty+th);
					tessellator.addVertexWithUV(x, y+data.height, 0, tx, ty+th);
				}
			}
		}
		tessellator.draw();
	}
	
	public static PC_Vec2I getStringSize(String text, ResourceLocation location){
		ITextureObject textureObject = PC_ClientUtils.mc().renderEngine.getTexture(location);
		if(textureObject instanceof PC_FontTexture){
			return getStringSize(text, (PC_FontTexture)textureObject);
		}
		return null;
	}
	
	public static PC_Vec2I getStringSize(String text, PC_FontTexture texture){
		PC_FontTexture activeTexture = texture;
		PC_Vec2I size = new PC_Vec2I();
		for(int i=0; i<text.length(); i++){
			char c = text.charAt(i);
			if(c==PC_Formatter.START_SEQ && i + 1 < text.length()){
				c = text.charAt(++i);
				if(c==PC_Formatter.COLOR_SEQ){
					i+=3;
				}else if(c==PC_Formatter.FONT_SEQ){
					int font = text.charAt(++i);
					activeTexture = PC_Fonts.get(font);
				}else{
					activeTexture = texture;
				}
			}else{
				PC_CharData data = activeTexture.getCharData(c);
				if(data!=null){
					if(data.height>size.y)
						size.y=data.height;
					size.x += data.width;
				}
			}
		}
		return size;
	}
	
	public static PC_Vec2I getCharSize(char c, ResourceLocation location){
		ITextureObject textureObject = PC_ClientUtils.mc().renderEngine.getTexture(location);
		if(textureObject instanceof PC_FontTexture){
			return getCharSize(c, (PC_FontTexture)textureObject);
		}
		return null;
	}
	
	public static PC_Vec2I getCharSize(char c, PC_FontTexture texture){
		PC_CharData data = texture.getCharData(c);
		if(data==null){
			return new PC_Vec2I();
		}
		return new PC_Vec2I(data.width, data.height);
	}
	
	public static boolean isSupported(String fontname) {
		Font font[] = getFonts();
		for (int i = font.length - 1; i >= 0; i--) {
			if (font[i].getName().equalsIgnoreCase(fontname))
				return true;
		}
		return false;
	}

	public static Font[] getFonts() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
	}

	public static Font getFont(String fontname, int style, float size) {
		Font result = null;
		for (Font font : getFonts()) {
			if (font.getName().equalsIgnoreCase(fontname)) {
				result = font.deriveFont(style, size);
				break;
			}
		}
		return result;
	}
	
}
