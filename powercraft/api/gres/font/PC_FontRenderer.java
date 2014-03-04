package powercraft.api.gres.font;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_Vec2I;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_FontRenderer {

	private PC_FontTexture texture;
	private float scale = 1.0f;
	
	public PC_FontRenderer(PC_FontTexture texture){
		this.texture = texture;
	}
	
	public void drawString(String text, int x, int y){
		drawString(text, x, y, this.texture, this.scale);
	}
	
	public void drawString(String text, int x, int y, int color){
		drawString(text, x, y, this.texture, color, this.scale);
	}
	
	public void drawString(String text, int x, int y, int color, boolean shadow){
		drawString(text, x, y, this.texture, color, shadow, this.scale);
	}
	
	public PC_Vec2I getStringSize(String text){
		return getStringSize(text, this.texture, this.scale);
	}
	
	public PC_Vec2I getCharSize(char c){
		return getCharSize(c, this.texture, this.scale);
	}
	
	public String trimStringToWidth(String text, int width){
		return trimStringToWidth(text, this.texture, width, this.scale);
	}
	
	public static void drawString(String text, float x, float y, ResourceLocation location, float scale){
		drawString(text, x, y, location, -1, false, scale);
	}
	
	public static void drawString(String text, float x, float y, ResourceLocation location, int color, float scale){
		drawString(text, x, y, location, color, false, scale);
	}
	
	public static void drawString(String text, float x, float y, PC_FontTexture texture, float scale){
		drawString(text, x, y, texture, -1, false, scale);
	}
	
	public static void drawString(String text, float x, float y, PC_FontTexture texture, int color, float scale){
		drawString(text, x, y, texture, color, false, scale);
	}
	
	public static void drawString(String text, float x, float y, ResourceLocation location, int color, boolean shadow, float scale){
		ITextureObject textureObject = PC_ClientUtils.mc().renderEngine.getTexture(location);
		if(textureObject instanceof PC_FontTexture){
			drawString(text, x, y, (PC_FontTexture)textureObject, color, shadow, scale);
		}
	}
	
	public static void drawString(String text, float x, float y, PC_FontTexture texture, int color, boolean shadow, float scale){
		if(shadow){
			drawStringInt(text, x+1, y+1, texture, color, true, scale);
		}
		drawStringInt(text, x, y, texture, color, false, scale);
	}
	
	private static void drawStringInt(String text, float x, float y, PC_FontTexture texture, int color, boolean shadow, float scale){
		PC_FontTexture activeTexture = texture;
		PC_ClientUtils.mc().renderEngine.bindTexture(activeTexture.getResourceLocation());
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		float size = texture.getTextureSize();
		int ccolor = color;
		if ((ccolor & -67108864) == 0){
			ccolor |= -16777216;
        }

        if (shadow){
        	ccolor = (ccolor & 16579836) >> 2 | ccolor & -16777216;
        }

        int red = ccolor >> 16 & 255;
        int blue = ccolor >> 8 & 255;
        int green = ccolor & 255;
        int alpha = ccolor >> 24 & 255;
		boolean error = false;
		float nx = x;
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
				}else if(c==PC_Formatter.ERROR_SEQ){
					error = true;
				}else if(c==PC_Formatter.ERRORSTOP_SEQ){
					error = false;
				}else{
					red = ccolor >> 16 & 255;
			    	blue = ccolor >> 8 & 255;
			        green = ccolor & 255;
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
					tessellator.setColorRGBA(red, green, blue, alpha);
					tessellator.addVertexWithUV(nx, y+data.height*scale, 0, tx, ty+th);
					tessellator.addVertexWithUV(nx+data.width*scale, y+data.height*scale, 0, tx+tw, ty+th);
					tessellator.addVertexWithUV(nx+data.width*scale, y, 0, tx+tw, ty);
					tessellator.addVertexWithUV(nx, y, 0, tx, ty);
					if(error){
						tessellator.draw();
						GL11.glDisable(GL11.GL_TEXTURE_2D);
						tessellator.startDrawingQuads();
						tessellator.setColorRGBA(255, 0, 0, alpha);
						tessellator.addVertex(nx, y+data.height*scale, 0);
						tessellator.addVertex(nx+data.width*scale, y+data.height*scale, 0);
						tessellator.addVertex(nx+data.width*scale, y+data.height*scale-1, 0);
						tessellator.addVertex(nx, y+data.height*scale-1, 0);
						tessellator.draw();
						GL11.glEnable(GL11.GL_TEXTURE_2D);
						tessellator.startDrawingQuads();
					}
					nx += data.width*scale;
				}
			}
		}
		tessellator.draw();
	}
	
	public static PC_Vec2I getStringSize(String text, ResourceLocation location, float scale){
		ITextureObject textureObject = PC_ClientUtils.mc().renderEngine.getTexture(location);
		if(textureObject instanceof PC_FontTexture){
			return getStringSize(text, (PC_FontTexture)textureObject, scale);
		}
		return null;
	}
	
	public static PC_Vec2I getStringSize(String text, PC_FontTexture texture, float scale){
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
				}else if(c==PC_Formatter.ERROR_SEQ){
					//
				}else if(c==PC_Formatter.ERRORSTOP_SEQ){
					//
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
		size.y *= scale;
		size.x *= scale;
		return size;
	}
	
	public static PC_Vec2I getCharSize(char c, ResourceLocation location, float scale){
		ITextureObject textureObject = PC_ClientUtils.mc().renderEngine.getTexture(location);
		if(textureObject instanceof PC_FontTexture){
			return getCharSize(c, (PC_FontTexture)textureObject, scale);
		}
		return null;
	}
	
	public static PC_Vec2I getCharSize(char c, PC_FontTexture texture, float scale){
		PC_CharData data = texture.getCharData(c);
		if(data==null){
			return new PC_Vec2I();
		}
		return new PC_Vec2I((int)(data.width*scale), (int)(data.height*scale));
	}

	public static String trimStringToWidth(String text, PC_FontTexture texture, int width, float scale){
		int length = PC_Formatter.removeFormatting(text).length();
		for(int i=1; i<=length; i++){
			int l = PC_FontRenderer.getStringSize(PC_Formatter.substring(text, 0, i), texture, scale).x;
			if(l>width){
				return PC_Formatter.substring(text, 0, i-1);
			}
		}
		return text;
	}
	
}
