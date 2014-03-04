package powercraft.api.gres.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
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

import powercraft.api.PC_Api;
import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_Logger;
import powercraft.api.PC_Utils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_FontTexture extends AbstractTexture{

	private String fontName = null;
	private Font font = null;
	private ResourceLocation res = null;
	private boolean antiAlias;
	private boolean isRendered = false;
	private int fontSize;
	private int fontHeight;
	private int textureSize;
	private char[] customCharsArray = new char[0];
	private PC_CharData[] charArray = new PC_CharData[256];
	private Map<Character, PC_CharData> customChars = new HashMap<Character, PC_CharData>();
	private int fontID;

	PC_FontTexture(String fontName, char[] customCharsArray) {
		this(fontName, true, customCharsArray);
	}
	
	PC_FontTexture(String fontName, boolean antiAlias, char[] customCharsArray) {
		this.fontName = fontName;
		this.antiAlias = antiAlias;
		addCustomChars(customCharsArray);
		this.isRendered = false;
	}
	
	PC_FontTexture(PC_FontTexture ft){
		this.fontName = ft.fontName;
		setFont(ft.font);
		this.res = ft.res;
		this.isRendered = false;
	}
	
	public void setAntiAliased(boolean b){
		antiAlias=b;
		isRendered=false;
	}
	
	public boolean isAntiAliased(){
		return antiAlias;
	}
	
	public void setFont(Font f) {
		if (font != null || f == null)
			return;
		font = f;
		isRendered=false;
	}

	public void setResourceLocation(ResourceLocation rl) {
		if (res != null || rl == null)
			return;
		res = rl;
		isRendered=false;
		reloadFromFile();
	}

	public ResourceLocation getResourceLocation() {
		return (res!=null?res:PC_Utils.getResourceLocation(PC_Api.INSTANCE, "fonts/"+(font!=null?font.hashCode():fontName)+".ttf"));
	}
	
	public Font getFont(){
		return font;
	}

	public boolean noFont() {
		return font == null;
	}
	
	public boolean canBeRendered(){
		return !isRendered && font != null;
	}
	
	public boolean readyToUse(){
		return isRendered;
	}
	
	public String getName(){
		return fontName;
	}
	
	public int getFontID() {
		return this.fontID;
	}
	
	public boolean reloadFromFile(){
		isRendered=false;
		if(res!=null)
			PC_ClientUtils.mc().renderEngine.loadTexture(res, this);
		return canBeRendered() || readyToUse();
	}
	
	public void deriveFont(int style, float size){
		font = font.deriveFont(style, size);
		isRendered=false;
		this.fontID = PC_Fonts.addFont(this);
		PC_ClientUtils.mc().renderEngine.loadTexture(res, this);
	}

	public PC_CharData getCharData(char c) {
		if (c < 256)
			return this.charArray[c];
		return this.customChars.get(Character.valueOf(c));
	}

	public int getTextureSize() {
		return this.textureSize;
	}
	
	@Override
	public void loadTexture(IResourceManager resourceManager) {
		if(noFont() && res!=null){
			PC_Logger.warning("trying to read %s", res.toString());
			InputStream inputstream = null;
			try {
				IResource resource = resourceManager.getResource(res);
				inputstream = resource.getInputStream();
				Font f = Font.createFont(Font.TRUETYPE_FONT, inputstream);
				inputstream.close();
				PC_Logger.warning("read %s", res.toString());
				setFont(f);
			} catch (Exception e) { // Do not use Java 1.7, use Java 1.6
				this.font = null;
				this.res = null;
			} finally {
				if (inputstream != null)
					try {
						inputstream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
		if(canBeRendered())
			createTextures();
	}

	public void createTextures(){
		PC_Logger.warning("entered: createTextures");
		if(!canBeRendered())
			return;
		isRendered=false;
		PC_Logger.warning("passed: font==null");
		deleteGlTexture();
		this.textureSize = 256;
		int lastTextureSize = 0;
		BufferedImage imgTemp = new BufferedImage(this.textureSize, this.textureSize,
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = imgTemp.getGraphics();
		g.setColor(new Color(0, 0, 0, 0));
		g.fillRect(0, 0, this.textureSize, this.textureSize);
		int rowHeight = 0;
		int positionX = 0;
		int positionY = 0;

		PC_Logger.warning("passed: random1");
		int customCharsLength = (this.customCharsArray != null) ? this.customCharsArray.length : 0;

		for (int i = 0; i < 256 + customCharsLength; i++) {

			// get 0-255 characters and then custom characters
			char ch = (i < 256) ? (char) i : this.customCharsArray[i - 256];

			BufferedImage fontImage = getFontImage(ch);

			PC_CharData newIntObject = new PC_CharData();

			newIntObject.width = fontImage.getWidth();
			newIntObject.height = fontImage.getHeight();
			
			if (positionX + newIntObject.width >= this.textureSize) {
				positionY += rowHeight;
				rowHeight = 0;
				if (positionY < lastTextureSize)
					positionX = lastTextureSize;
				else
					positionX = 0;
			}

			if (newIntObject.height > this.fontHeight)
				this.fontHeight = newIntObject.height;

			if (newIntObject.height > rowHeight)
				rowHeight = newIntObject.height;

			if (positionY + rowHeight >= this.textureSize) {
				lastTextureSize = this.textureSize;
				this.textureSize *= 2;
				positionY = 0;
				positionX = lastTextureSize;
				BufferedImage newImg = new BufferedImage(this.textureSize, this.textureSize,
						BufferedImage.TYPE_INT_ARGB);
				Graphics gn = newImg.getGraphics();
				gn.setColor(new Color(0, 0, 0, 0));
				gn.fillRect(0, 0, this.textureSize, this.textureSize);
				gn.drawImage(imgTemp, 0, 0, null);
				g = gn;
				imgTemp = newImg;
			}

			newIntObject.storedX = positionX;
			newIntObject.storedY = positionY;

			// Draw it here
			g.drawImage(fontImage, positionX, positionY, null);

			positionX += newIntObject.width;

			if (i < 256)
				this.charArray[i] = newIntObject;
			else
				this.customChars.put(new Character(ch), newIntObject);

			fontImage = null;
		}

		PC_Logger.warning("passed: random2");
		int bpp = imgTemp.getColorModel().getPixelSize();
		ByteBuffer byteBuffer;
		DataBuffer db = imgTemp.getData().getDataBuffer();
		if (db instanceof DataBufferInt) {
			int intI[] = ((DataBufferInt) db).getData();
			byteBuffer = ByteBuffer.allocateDirect(this.textureSize * this.textureSize * (bpp / 8)).order(
					ByteOrder.nativeOrder());
			for (int in : intI) {
				byteBuffer.put((byte) in);
				byteBuffer.put((byte) (in >> 8));
				byteBuffer.put((byte) (in >> 16));
				byteBuffer.put((byte) (in >> 24));
			}
		} else
			byteBuffer = ByteBuffer.allocateDirect(this.textureSize * this.textureSize * (bpp / 8))
					.order(ByteOrder.nativeOrder()).put(((DataBufferByte) db).getData());
		byteBuffer.flip();

		int internalFormat = GL11.GL_RGBA8, format = GL11.GL_RGBA;
		int textureId = getGlTextureId();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);//GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);//GL11.GL_LINEAR);

		GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);

		GLU.gluBuild2DMipmaps(GL11.GL_TEXTURE_2D, internalFormat, this.textureSize, this.textureSize, format,
				GL11.GL_UNSIGNED_BYTE, byteBuffer);
		PC_Logger.warning("exit: createTextures");
		isRendered=true;
	}

	private BufferedImage getFontImage(char ch) {
		BufferedImage tempfontImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics g = tempfontImage.getGraphics();
		if (this.antiAlias == true && g instanceof Graphics2D)
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(this.font);
		FontMetrics fontMetrics = g.getFontMetrics();
		Rectangle2D r = fontMetrics.getStringBounds(ch=='\t'?"    ":"" + ch, g);

		int charwidth = (int) r.getWidth();
		if (charwidth <= 0)
			charwidth = 7;
		int charheight = (int) r.getHeight();
		if (charheight <= 0)
			charheight = this.fontSize;

		// Create another image holding the character we are creating
		BufferedImage fontImage;
		fontImage = new BufferedImage(charwidth, charheight, BufferedImage.TYPE_INT_ARGB);
		Graphics gt = fontImage.getGraphics();
		if (this.antiAlias == true && gt instanceof Graphics2D)
			((Graphics2D) gt).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		gt.setColor(new Color(0, 0, 0, 1));
		gt.fillRect(0, 0, charwidth, charheight);
		gt.setFont(this.font);
		gt.setColor(Color.WHITE);
		int charx = 0;
		int chary = 0;
		gt.drawString(String.valueOf(ch), (charx), (chary) + fontMetrics.getAscent());

		return fontImage;

	}

	@SuppressWarnings("hiding")
	public void addCustomChars(char[] customCharsArray) {
		if (customCharsArray != null) {
			isRendered = false;
			List<Character> customCharsList = new ArrayList<Character>();
			for (int i = 0; i < customCharsArray.length; i++){
				Character c = Character.valueOf(customCharsArray[i]);
				if (!customCharsList.contains(c))
					customCharsList.add(c);
			}
			for (int i = 0; i < this.customCharsArray.length; i++){
				Character c = Character.valueOf(this.customCharsArray[i]);
				if (!customCharsList.contains(c))
					customCharsList.add(c);
			}
			this.customCharsArray = new char[customCharsList.size()];
			for (int i = 0; i < this.customCharsArray.length; i++)
				this.customCharsArray[i] = customCharsList.get(i).charValue();
		}
	}
    
}
