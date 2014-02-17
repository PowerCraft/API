package powercraft.api.gres.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_FontTexture extends AbstractTexture {
	
	private Font font;
	private boolean hasLocation;
	private ResourceLocation location;
	private boolean antiAlias;
	private int fontSize;
	private int fontHeight;
	private int textureSize;
	private char[] customCharsArray = new char[0];
	private PC_CharData[] charArray = new PC_CharData[256];
	private Map<Character, PC_CharData> customChars = new HashMap<Character, PC_CharData>();
	private int fontID;
	
	PC_FontTexture(Font font, boolean antiAlias, char[] customCharsArray){
		this.font = font;
		location = new ResourceLocation("PowerCraft", "*"+font.getFontName());
		this.antiAlias = antiAlias;
		addCustomChars(customCharsArray);
		fontID = PC_Fonts.addFont(this);
	}
	
	PC_FontTexture(ResourceLocation location, boolean antiAlias, char[] customCharsArray){
		this.location = location;
		this.hasLocation = true;
		this.antiAlias = antiAlias;
		addCustomChars(customCharsArray);
		fontID = PC_Fonts.addFont(this);
	}
	
	public int getFontID(){
		return fontID;
	}
	
	public ResourceLocation getResourceLocation(){
		return location;
	}
	
	public PC_CharData getCharData(char c){
		if(c<256){
			return charArray[c];
		}
		return customChars.get(c);
	}
	
	public int getTextureSize(){
		return textureSize;
	}
	
	@Override
	public void loadTexture(IResourceManager resourceManager) throws IOException {
		deleteGlTexture();
		if(hasLocation){
			InputStream inputstream = null;
			try{
				IResource resource = resourceManager.getResource(location);
				inputstream = resource.getInputStream();
				font = Font.createFont(Font.TRUETYPE_FONT, inputstream);
				inputstream.close();
			}catch (FontFormatException e) {
				throw new IOException(e);
			}finally{
				if(inputstream!=null){
					inputstream.close();
				}
			}
		}
		textureSize = 256;
		int lastTextureSize = 0;
		BufferedImage imgTemp = new BufferedImage(textureSize, textureSize, BufferedImage.TYPE_INT_ARGB);
		Graphics g = imgTemp.getGraphics();
		g.setColor(new Color(0, 0, 0, 0));
		g.fillRect(0, 0, textureSize, textureSize);
		int rowHeight = 0;
		int positionX = 0;
		int positionY = 0;

		int customCharsLength = (customCharsArray != null) ? customCharsArray.length : 0;
		
		for (int i = 0; i < 256 + customCharsLength; i++) {

			// get 0-255 characters and then custom characters
			char ch = (i < 256) ? (char) i : customCharsArray[i - 256];

			BufferedImage fontImage = getFontImage(ch);

			PC_CharData newIntObject = new PC_CharData();

			newIntObject.width = fontImage.getWidth();
			newIntObject.height = fontImage.getHeight();

			if (positionX + newIntObject.width >= textureSize) {
				positionY += rowHeight;
				rowHeight = 0;
				if(positionY<lastTextureSize){
					positionX = lastTextureSize;
				}else{
					positionX = 0;
				}
			}
			
			if (newIntObject.height > fontHeight) {
				fontHeight = newIntObject.height;
			}

			if (newIntObject.height > rowHeight) {
				rowHeight = newIntObject.height;
			}
			
			if(positionY + rowHeight >= textureSize){
				lastTextureSize = textureSize;
				textureSize*=2;
				positionY = 0;
				positionX = lastTextureSize;
				BufferedImage newImg = new BufferedImage(textureSize, textureSize, BufferedImage.TYPE_INT_ARGB);
				Graphics gn = newImg.getGraphics();
				gn.setColor(new Color(0, 0, 0, 0));
				gn.fillRect(0, 0, textureSize, textureSize);
				gn.drawImage(imgTemp, 0, 0, null);
				g = gn;
				imgTemp = newImg;
			}

			newIntObject.storedX = positionX;
			newIntObject.storedY = positionY;

			// Draw it here
			g.drawImage(fontImage, positionX, positionY, null);

			positionX += newIntObject.width;

			if (i < 256) { // standard characters
				charArray[i] = newIntObject;
			} else { // custom characters
				customChars.put(new Character(ch), newIntObject);
			}

			fontImage = null;
		}

		int bpp = imgTemp.getColorModel().getPixelSize();
		ByteBuffer byteBuffer;
		DataBuffer db = imgTemp.getData().getDataBuffer();
		if (db instanceof DataBufferInt) {
			int intI[] = ((DataBufferInt) db).getData();
			byteBuffer = ByteBuffer
					.allocateDirect(textureSize * textureSize * (bpp / 8))
					.order(ByteOrder.nativeOrder());
			for (int in:intI) {
				byteBuffer.put((byte) in);
				byteBuffer.put((byte) (in >> 8));
				byteBuffer.put((byte) (in >> 16));
				byteBuffer.put((byte) (in >> 24));
			}
		} else {
			byteBuffer = ByteBuffer
					.allocateDirect(textureSize * textureSize * (bpp / 8))
					.order(ByteOrder.nativeOrder()).put(((DataBufferByte)db).getData());
		}
		byteBuffer.flip();

		int internalFormat = GL11.GL_RGBA8, format = GL11.GL_RGBA;
		int textureId = getGlTextureId();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S,
				GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,
				GL11.GL_CLAMP);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
				GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
				GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

		GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE,
				GL11.GL_MODULATE);

		GLU.gluBuild2DMipmaps(GL11.GL_TEXTURE_2D, internalFormat, textureSize,
				textureSize, format, GL11.GL_UNSIGNED_BYTE, byteBuffer);
	}
	
	private BufferedImage getFontImage(char ch) {
		BufferedImage tempfontImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics g = tempfontImage.getGraphics();
		if (antiAlias == true && g instanceof Graphics2D) {
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}
		g.setFont(font);
		FontMetrics fontMetrics = g.getFontMetrics();
		int charwidth = fontMetrics.charWidth(ch) + 8;

		if (charwidth <= 0) {
			charwidth = 7;
		}
		int charheight = fontMetrics.getHeight() + 3;
		if (charheight <= 0) {
			charheight = fontSize;
		}

		// Create another image holding the character we are creating
		BufferedImage fontImage;
		fontImage = new BufferedImage(charwidth, charheight,
				BufferedImage.TYPE_INT_ARGB);
		Graphics gt = fontImage.getGraphics();
		if (antiAlias == true && gt instanceof Graphics2D) {
			((Graphics2D)gt).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}
		gt.setColor(new Color(0, 0, 0, 1));
		gt.fillRect(0, 0, charwidth, charheight);
		gt.setFont(font);
		gt.setColor(Color.WHITE);
		int charx = 3;
		int chary = 1;
		gt.drawString(String.valueOf(ch), (charx),
				(chary) + fontMetrics.getAscent());

		return fontImage;

	}

	public void addCustomChars(char[] customCharsArray) {
		if(customCharsArray!=null){
			List<Character> customCharsList = new ArrayList<Character>();
			for(int i=0; i<customCharsArray.length; i++){
				if(!customCharsList.contains(customCharsArray[i])){
					customCharsList.add(customCharsArray[i]);
				}
			}
			for(int i=0; i<this.customCharsArray.length; i++){
				if(!customCharsList.contains(this.customCharsArray[i])){
					customCharsList.add(this.customCharsArray[i]);
				}
			}
			this.customCharsArray = new char[customCharsList.size()];
			for(int i=0; i<this.customCharsArray.length; i++){
				this.customCharsArray[i] = customCharsList.get(i);
			}
		}
	}

}
