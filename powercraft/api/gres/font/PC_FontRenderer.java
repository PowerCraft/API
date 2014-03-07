package powercraft.api.gres.font;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;

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
	
	public PC_FontRenderer(ResourceLocation location){
		ITextureObject textureObject = PC_ClientUtils.mc().renderEngine.getTexture(location);
		if(textureObject instanceof PC_FontTexture){
			this.texture = (PC_FontTexture)textureObject;
		}
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
	
	public String warpStringToWidthBl(String text, int width, boolean wordSplit){
		return warpStringToWidthBl(text, this.texture, width, this.scale, wordSplit);
	}
	
	public List<String> warpStringToWidth(String text, int width, boolean wordSplit){
		return warpStringToWidth(text, this.texture, width, this.scale, wordSplit);
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
			drawStringInt(text, x+1, y+1, texture, color, true, scale, false);
		}
		drawStringInt(text, x, y, texture, color, false, scale, false);
	}
	
	private static void drawStringInt(String text, float x, float y, PC_FontTexture texture, int color, boolean shadow, float scale, boolean multiline){
		float s = scale;
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
        int errorRed = 0;
        int errorBlue = 0;
        int errorGreen = 0;
		boolean error = false;
		float nx = x;
		int maxY = -1;
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
					size = activeTexture.getTextureSize();
					PC_ClientUtils.mc().renderEngine.bindTexture(activeTexture.getResourceLocation());
					tessellator.startDrawingQuads();
				}else if(c==PC_Formatter.SCALE_SEQ){
					int sc = text.charAt(++i);
					s = 1.0f/sc;
				}else if(c==PC_Formatter.SCALE_SEQ){
					int font = text.charAt(++i);
					activeTexture = PC_Fonts.get(font);
					tessellator.draw();
					size = activeTexture.getTextureSize();
					PC_ClientUtils.mc().renderEngine.bindTexture(activeTexture.getResourceLocation());
					tessellator.startDrawingQuads();
				}else if(c==PC_Formatter.ERROR_SEQ){
					error = true;
					errorRed = text.charAt(++i);
					errorGreen = text.charAt(++i);
					errorBlue = text.charAt(++i);
				}else if(c==PC_Formatter.ERRORSTOP_SEQ){
					error = false;
				}else{
					red = ccolor >> 16 & 255;
			    	blue = ccolor >> 8 & 255;
			        green = ccolor & 255;
					activeTexture = texture;
					size = activeTexture.getTextureSize();
					s = scale;
					tessellator.draw();
					PC_ClientUtils.mc().renderEngine.bindTexture(activeTexture.getResourceLocation());
					tessellator.startDrawingQuads();
				}
			}else{
				if(c=='\n'){
					if(maxY==-1){
						y += activeTexture.getCharData(' ').height;
					}else{
						y += maxY;
					}
					maxY = -1;
				}else{
					PC_CharData data = activeTexture.getCharData(c);
					if(data!=null){
						if(data.height>maxY){
							maxY = data.height;
						}
						float tx = data.storedX/size;
						float ty = data.storedY/size;
						float tw = data.width/size;
						float th = data.height/size;
						tessellator.setColorRGBA(red, green, blue, alpha);
						tessellator.addVertexWithUV(nx, y+data.height*s, 0, tx, ty+th);
						tessellator.addVertexWithUV(nx+data.width*s, y+data.height*s, 0, tx+tw, ty+th);
						tessellator.addVertexWithUV(nx+data.width*s, y, 0, tx+tw, ty);
						tessellator.addVertexWithUV(nx, y, 0, tx, ty);
						if(error){
							tessellator.draw();
							GL11.glDisable(GL11.GL_TEXTURE_2D);
							tessellator.startDrawingQuads();
							tessellator.setColorRGBA(errorRed, errorGreen, errorBlue, alpha);
							tessellator.addVertex(nx, y+data.height*s, 0);
							tessellator.addVertex(nx+data.width*s, y+data.height*s, 0);
							tessellator.addVertex(nx+data.width*s, y+data.height*s-1, 0);
							tessellator.addVertex(nx, y+data.height*s-1, 0);
							tessellator.draw();
							GL11.glEnable(GL11.GL_TEXTURE_2D);
							tessellator.startDrawingQuads();
						}
						nx += data.width*s;
					}
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
		int maxY = -1;
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
					i+=3;
				}else if(c==PC_Formatter.ERRORSTOP_SEQ){
					//
				}else{
					activeTexture = texture;
				}
			}else{
				if(c=='\n'){
					if(maxY==-1){
						size.y += activeTexture.getCharData(' ').height;
					}else{
						size.y += maxY;
					}
					maxY = -1;
				}else{
					PC_CharData data = activeTexture.getCharData(c);
					if(data!=null){
						if(data.height>maxY)
							maxY=data.height;
						size.x += data.width;
					}
				}
			}
		}
		if(maxY==-1){
			size.y += activeTexture.getCharData(' ').height;
		}else{
			size.y += maxY;
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
	
	public static String warpStringToWidthBl(String text, PC_FontTexture texture, int width, float scale, boolean wordSplit){
		List<String> l = warpStringToWidth(text, texture, width, scale, wordSplit);
		int size = -1;
		for(String s:l){
			size += s.length()+1;
		}
		StringBuilder sb = new StringBuilder(size);
		sb.append(l.get(0));
		for(int i=1; i<l.size(); i++){
			sb.append('\n');
			sb.append(l.get(i));
		}
		return sb.toString();
	}
	
	public static List<String> warpStringToWidth(String text, PC_FontTexture texture, int width, float scale, boolean wordSplit){
		List<String> list = new ArrayList<String>();
		PC_FontTexture activeTexture = texture;
		int sizeX = 0;
		int sizeAtBest = 0;
		int bestSplit = -1;
		int start = 0;
		boolean isWhite = false;
		for(int i=0; i<text.length(); i++){
			char c = text.charAt(i);
			if(c==PC_Formatter.START_SEQ && i + 1 < text.length()){
				if(c==PC_Formatter.COLOR_SEQ){
					i+=3;
				}else if(c==PC_Formatter.FONT_SEQ){
					int font = text.charAt(++i);
					activeTexture = PC_Fonts.get(font);
				}else if(c==PC_Formatter.ERROR_SEQ){
					i+=3;
				}else if(c==PC_Formatter.ERRORSTOP_SEQ){
					//
				}else{
					activeTexture = texture;
				}
			}else{
				PC_CharData data = activeTexture.getCharData(c);
				int prevSize = sizeX;
				if(data!=null){
					sizeX += data.width;
				}
				if(!((c>='A' && c<='Z') || (c>='a' && c<='z') || (c>='0' && c<='9') || c=='_')){
					bestSplit = i;
					isWhite = c=='\n' || c=='\r' || c=='\t' || c==' ';
					sizeAtBest = isWhite?sizeX:prevSize;
				}
				if(sizeX>width || c=='\n'){
					if(bestSplit==-1 && wordSplit){
						if(start+1<i){
							list.add(text.substring(start, i));
							start = i;
							bestSplit = -1;
							sizeX -= sizeAtBest;
						}
					}else if(bestSplit!=-1){
						list.add(text.substring(start, bestSplit));
						start = isWhite?bestSplit+1:bestSplit;
						bestSplit = -1;
						sizeX -= sizeAtBest;
					}
				}
			}
		}
		list.add(text.substring(start));
		return list;
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
