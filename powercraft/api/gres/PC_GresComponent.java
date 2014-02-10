package powercraft.api.gres;


import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Slot;

import org.lwjgl.opengl.GL11;

import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_Debug;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresFocusGotEvent;
import powercraft.api.gres.events.PC_GresFocusLostEvent;
import powercraft.api.gres.events.PC_GresKeyEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent;
import powercraft.api.gres.events.PC_GresMouseEvent;
import powercraft.api.gres.events.PC_GresMouseMoveEvent;
import powercraft.api.gres.events.PC_GresMouseWheelEvent;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.events.PC_IGresEventListenerEx;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public abstract class PC_GresComponent {

	protected PC_GresContainer parent;

	protected String text = "";

	protected final PC_RectI rect = new PC_RectI();

	protected final PC_RectI padding = new PC_RectI(0, 0, 0, 0);

	protected PC_GresAlign.H alignH = PC_GresAlign.H.CENTER;

	protected PC_GresAlign.V alignV = PC_GresAlign.V.CENTER;

	protected PC_GresAlign.Fill fill = PC_GresAlign.Fill.NONE;

	protected final PC_Vec2I minSize = new PC_Vec2I(0, 0);

	protected boolean minSizeSet;

	protected final PC_Vec2I maxSize = new PC_Vec2I(-1, -1);

	protected boolean maxSizeSet;

	protected final PC_Vec2I prefSize = new PC_Vec2I(-1, -1);

	protected boolean prefSizeSet;

	protected boolean visible = true;

	protected boolean parentVisible = true;

	protected boolean enabled = true;

	protected boolean parentEnabled = true;

	protected boolean mouseOver;

	protected boolean mouseDown;

	protected FontRenderer fontRenderer = PC_ClientUtils.mc().fontRenderer;

	protected final int fontColors[] = { 4210752, 4210752, 4210752, 4210752 };

	protected final List<PC_IGresEventListener> eventListeners = new ArrayList<PC_IGresEventListener>();

	protected Object layoutData;

	protected boolean focus;
	
	public PC_GresComponent() {

	}

	public PC_GresComponent(String text) {
		this.text = text;
	}

	protected void setLayoutData(Object layoutData){
		if(this.layoutData != layoutData){
			this.layoutData = layoutData;
			notifyChange();
		}
	}
	
	protected void setParent(PC_GresContainer parent) {

		if (parent == null) {
			if (!this.parent.isChild(this)) {
				this.parent = null;
				parentVisible = true;
				parentEnabled = true;
			}
		} else if (parent.isChild(this)) {
			this.parent = parent;
			parentVisible = parent.isRecursiveVisible();
			parentEnabled = parent.isRecursiveEnabled();
		}
	}


	public PC_GresContainer getParent() {

		return parent;
	}


	public void setText(String text) {

		if (!this.text.equals(text)) {
			this.text = text;
			notifyChange();
		}
	}


	public String getText() {

		return text;
	}


	public void setRect(PC_RectI rect) {

		if (!this.rect.equals(rect)) {
			this.rect.setTo(rect);
			notifyChange();
		}
	}


	public PC_RectI getRect() {

		return new PC_RectI(rect);
	}


	public void setLocation(PC_Vec2I location) {

		if (rect.setLocation(location)) {
			notifyChange();
		}
	}


	public PC_Vec2I getLocation() {

		return rect.getLocation();
	}


	public void setSize(PC_Vec2I size) {

		if (rect.setSize(size)) {
			notifyChange();
		}
	}


	public PC_Vec2I getSize() {

		return rect.getSize();
	}


	public void setPadding(PC_RectI rect) {

		padding.setTo(rect);
	}


	public PC_RectI getPadding() {

		return new PC_RectI(padding);
	}


	public void setAlignH(PC_GresAlign.H alignH) {

		this.alignH = alignH;
	}


	public PC_GresAlign.H getAlignH() {

		return alignH;
	}


	public void setAlignV(PC_GresAlign.V alignV) {

		this.alignV = alignV;
	}


	public PC_GresAlign.V getAlignV() {

		return alignV;
	}


	public void setFill(PC_GresAlign.Fill fill) {

		this.fill = fill;
	}


	public PC_GresAlign.Fill getFill() {

		return fill;
	}


	public void putInRect(int x, int y, int width, int height) {

		//if (width > maxSize.x && maxSize.x >= 0) width = maxSize.x;
		//if (height > maxSize.y && maxSize.y >= 0) height = maxSize.y;
		boolean needUpdate = false;
		if (fill == PC_GresAlign.Fill.BOTH || fill == PC_GresAlign.Fill.HORIZONTAL) {
			needUpdate |= rect.x != x;
			rect.x = x;
			needUpdate |= rect.width != width;
			rect.width = width;
		} else {
			switch (alignH) {
				case CENTER:
					needUpdate |= rect.x != x + width / 2 - rect.width / 2;
					rect.x = x + width / 2 - rect.width / 2;
					break;
				case RIGHT:
					needUpdate |= rect.x != x + width - rect.width;
					rect.x = x + width - rect.width;
					break;
				default:
					needUpdate |= rect.x != x;
					rect.x = x;
					break;
			}
		}
		if (fill == PC_GresAlign.Fill.BOTH || fill == PC_GresAlign.Fill.VERTICAL) {
			needUpdate |= rect.y != y;
			rect.y = y;
			needUpdate |= rect.height != height;
			rect.height = height;
		} else {
			switch (alignV) {
				case CENTER:
					needUpdate |= rect.y != y + (height - rect.height) / 2;
					rect.y = y + (height - rect.height) / 2;
					break;
				case BOTTOM:
					needUpdate |= rect.y != y + height - rect.height;
					rect.y = y + height - rect.height;
					break;
				default:
					needUpdate |= rect.y != y;
					rect.y = y;
					break;
			}
		}
		if (needUpdate) notifyChange();
	}


	public void setMinSize(PC_Vec2I minSize) {

		if (minSize == null) {
			this.minSize.setTo(calculateMinSize());
			minSizeSet = false;
		} else {
			this.minSize.setTo(minSize);
			minSizeSet = true;
		}
		setSize(getSize().max(this.minSize));
	}


	public PC_Vec2I getMinSize() {

		return minSize;
	}


	public void updateMinSize() {

		if (!minSizeSet) {
			setMinSize(null);
		}
	}


	protected abstract PC_Vec2I calculateMinSize();


	public void setMaxSize(PC_Vec2I maxSize) {

		if (maxSize == null) {
			this.maxSize.setTo(calculateMaxSize());
			maxSizeSet = false;
		} else {
			this.maxSize.setTo(maxSize);
			maxSizeSet = true;
		}
	}


	public PC_Vec2I getMaxSize() {

		return maxSize;
	}


	public void updateMaxSize() {

		if (!maxSizeSet) {
			setMaxSize(null);
		}
	}


	protected abstract PC_Vec2I calculateMaxSize();


	public void setPrefSize(PC_Vec2I prefSize) {

		if (prefSize == null) {
			this.prefSize.setTo(calculatePrefSize());
			prefSizeSet = false;
		} else {
			this.prefSize.setTo(prefSize);
			prefSizeSet = true;
		}
	}


	public PC_Vec2I getPrefSize() {

		return prefSize;
	}


	public void updatePrefSize() {

		if (!prefSizeSet) {
			setPrefSize(null);
		}
	}


	protected abstract PC_Vec2I calculatePrefSize();


	public void setVisible(boolean visible) {

		this.visible = visible;
		notifyParentOfChange();
	}


	protected void setParentVisible(boolean visible) {

		parentVisible = visible;
	}


	public boolean isVisible() {

		return visible;
	}


	public boolean isRecursiveVisible() {

		return visible && (parent == null || parent.isRecursiveVisible());
	}


	public void setEnabled(boolean enabled) {

		this.enabled = enabled;
	}


	protected void setParentEnabled(boolean enabled) {

		parentEnabled = enabled;
	}


	public boolean isEnabled() {

		return enabled;
	}


	public boolean isRecursiveEnabled() {

		return enabled && (parent == null || parent.isRecursiveEnabled());
	}

	public boolean hasFocus(){
		
		return focus;
		
	}

	public void takeFocus(){
		
		PC_GresGuiHandler guiHandler = getGuiHandler();
		if(guiHandler!=null){
			guiHandler.setFocus(this);
		}
		
	}
	
	protected void notifyChange() {

		updateMinSize();
		updatePrefSize();
		updateMaxSize();
		notifyParentOfChange();
	}


	protected void notifyParentOfChange() {

		if (parent != null) parent.notifyChildChange(this);
	}


	protected static PC_RectI setDrawRect(PC_RectI old, PC_RectI _new, double scale, int displayHeight) {

		PC_RectI rect;
		if (old == null) {
			rect = new PC_RectI(_new);
		} else {
			rect = old.averageQuantity(_new);
		}
		if (rect.width <= 0 || rect.height <= 0) return null;
		GL11.glScissor((int) (rect.x * scale), displayHeight - (int) ((rect.y + rect.height) * scale), (int) (rect.width * scale),
				(int) (rect.height * scale));
		return rect;
	}


	protected void doPaint(PC_Vec2I offset, PC_RectI scissorOld, double scale, int displayHeight, float timeStamp) {

		if (visible) {
			PC_RectI rect = new PC_RectI(this.rect);
			rect.x += offset.x;
			rect.y += offset.y;
			PC_RectI scissor = setDrawRect(scissorOld, rect, scale, displayHeight);
			if(scissor==null)
				return;
			GL11.glPushMatrix();
			GL11.glTranslatef(this.rect.x, this.rect.y, 0);
			GL11.glColor3f(1.0f, 1.0f, 1.0f);
			paint(scissor, scale, displayHeight, timeStamp);
			doDebugRendering(0, 0, rect.width, rect.height);
			GL11.glPopMatrix();
		}
	}

	protected void doDebugRendering(int x, int y, int width, int height){
		if(PC_Debug.DEBUG){
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			int hash = hashCode();
			int red = hash>>16&255;
			int green = hash>>8&255;
			int blue = hash&255;
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA(red, green, blue, 128);
			tessellator.addVertex(x, y + height, 0);
			tessellator.addVertex(x + width, y + height, 0);
			tessellator.addVertex(x + width, y, 0);
			tessellator.addVertex(x, y, 0);
			tessellator.draw();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
	}
	
	protected abstract void paint(PC_RectI scissor, double scale, int displayHeight, float timeStamp);


	protected boolean onKeyTyped(char key, int keyCode) {

		PC_GresKeyEvent event = new PC_GresKeyEvent(this, key, keyCode);
		fireEvent(event);
		if (!event.isConsumed()) {
			return handleKeyTyped(event.getKey(), event.getKeyCode());
		}
		return true;
	}


	protected boolean handleKeyTyped(char key, int keyCode) {

		return false;
	}


	protected void tryActionOnKeyTyped(char key, int keyCode) {

	}


	protected void onMouseEnter(PC_Vec2I mouse, int buttons) {

		PC_GresMouseEvent event = new PC_GresMouseMoveEvent(this, mouse, buttons, PC_GresMouseMoveEvent.Event.ENTER);
		fireEvent(event);
		if (!event.isConsumed()) {
			handleMouseEnter(mouse, buttons);
		}
	}


	protected void handleMouseEnter(PC_Vec2I mouse, int buttons) {

		mouseOver = enabled && parentEnabled;
	}


	protected void onMouseLeave(PC_Vec2I mouse, int buttons) {

		PC_GresMouseEvent event = new PC_GresMouseMoveEvent(this, mouse, buttons, PC_GresMouseMoveEvent.Event.LEAVE);
		fireEvent(event);
		if (!event.isConsumed()) {
			handleMouseLeave(mouse, buttons);
		}
	}


	protected void handleMouseLeave(PC_Vec2I mouse, int buttons) {

		mouseOver = false;
		mouseDown = false;
	}


	protected boolean onMouseMove(PC_Vec2I mouse, int buttons) {

		PC_GresMouseEvent event = new PC_GresMouseMoveEvent(this, mouse, buttons, PC_GresMouseMoveEvent.Event.MOVE);
		fireEvent(event);
		if (!event.isConsumed()) {
			return handleMouseMove(mouse, buttons);
		}
		return true;
	}


	protected boolean handleMouseMove(PC_Vec2I mouse, int buttons) {
		return false;
	}


	protected boolean onMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton) {

		PC_GresMouseEvent event = new PC_GresMouseButtonEvent(this, mouse, buttons, eventButton, PC_GresMouseButtonEvent.Event.DOWN);
		fireEvent(event);
		if (!event.isConsumed()) {
			return handleMouseButtonDown(mouse, buttons, eventButton);
		}
		return true;
	}


	protected boolean handleMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton) {

		mouseDown = enabled && parentEnabled;
		return false;
	}


	protected boolean onMouseButtonUp(PC_Vec2I mouse, int buttons, int eventButton) {

		PC_GresMouseEvent event = new PC_GresMouseButtonEvent(this, mouse, buttons, eventButton, PC_GresMouseButtonEvent.Event.UP);
		fireEvent(event);
		if (!event.isConsumed()) {
			return handleMouseButtonUp(mouse, buttons, eventButton);
		}
		return true;
	}


	protected boolean handleMouseButtonUp(PC_Vec2I mouse, int buttons, int eventButton) {

		boolean consumed = false;
		if (mouseDown) {
			PC_GresMouseEvent event = new PC_GresMouseButtonEvent(this, mouse, buttons, eventButton, PC_GresMouseButtonEvent.Event.CLICK);
			fireEvent(event);
			if (!event.isConsumed()) {
				consumed = handleMouseButtonClick(mouse, buttons, eventButton);
			}else{
				consumed = true;
			}
		}
		mouseDown = false;
		return consumed;
	}


	protected boolean handleMouseButtonClick(PC_Vec2I mouse, int buttons, int eventButton) {
		return false;
	}


	protected final boolean onMouseWheel(PC_Vec2I mouse, int buttons, int wheel) {
		PC_GresMouseWheelEvent event = new PC_GresMouseWheelEvent(this, mouse, buttons, wheel);
		onMouseWheel(event);
		return event.isConsumed();
	}

	protected void onMouseWheel(PC_GresMouseWheelEvent event) {
		fireEvent(event);
		if (!event.isConsumed()) {
			handleMouseWheel(event);
		}
	}

	protected void handleMouseWheel(PC_GresMouseWheelEvent event) {}

	protected void onFocusLost(PC_GresComponent newFocusedComponent) {
		PC_GresFocusLostEvent event = new PC_GresFocusLostEvent(this, newFocusedComponent);
		fireEvent(event);
		if (!event.isConsumed()) {
			handleFocusLost();
		}
	}

	protected void handleFocusLost() {
		focus = false;
	}
	
	protected void onFocusGot(PC_GresComponent oldFocusedComponent) {
		PC_GresFocusGotEvent event = new PC_GresFocusGotEvent(this, oldFocusedComponent);
		fireEvent(event);
		if (!event.isConsumed()) {
			handleFocusGot();
		}
	}

	protected void handleFocusGot() {
		moveToTop();
		focus = true;
	}
	
	protected PC_GresComponent getComponentAtPosition(PC_Vec2I mouse) {

		return visible ? this : null;
	}


	protected void onTick() {

	}


	protected Slot getSlotAtPosition(PC_Vec2I position) {

		return null;
	}


	protected List<String> getTooltip(PC_Vec2I position) {

		return null;
	}


	protected PC_Vec2I getRealLocation() {

		if (parent == null) {
			return rect.getLocation();
		} 
		return rect.getLocation().add(parent.getRealLocation()).add(parent.getFrame().getLocation());
	}


	public PC_GresGuiHandler getGuiHandler() {

		if (parent == null) return null;
		return parent.getGuiHandler();
	}


	public void addEventListener(PC_IGresEventListener eventListener) {

		if (!eventListeners.contains(eventListener)) eventListeners.add(eventListener);
	}


	public void removeEventListener(PC_IGresEventListener eventListener) {

		eventListeners.remove(eventListener);
	}


	protected void fireEvent(PC_GresEvent event) {

		for (PC_IGresEventListener eventListener : eventListeners) {
			if (eventListener instanceof PC_IGresEventListenerEx) {
				PC_IGresEventListenerEx eventListenerEx = (PC_IGresEventListenerEx) eventListener;
				Class<? extends PC_GresEvent>[] handelableEvents = eventListenerEx.getHandelableEvents();
				for (int i = 0; i < handelableEvents.length; i++) {
					if (handelableEvents[i].isInstance(event)) {
						eventListenerEx.onEvent(event);
						break;
					}
				}
			} else {
				eventListener.onEvent(event);
			}
		}
	}


	protected void drawTexture(String textureName, int x, int y, int width, int height) {

		drawTexture(textureName, x, y, width, height, enabled && parentEnabled ? mouseDown ? 2 : mouseOver ? 1 : 0 : 3);
	}


	protected void drawTexture(String textureName, int x, int y, int width, int height, int state) {

		PC_GresTexture texture = PC_Gres.getGresTexture(textureName);
		if (texture != null) {
			texture.draw(x, y, width, height, state);
		}
	}


	protected PC_Vec2I getTextureMinSize(String textureName) {

		PC_GresTexture texture = PC_Gres.getGresTexture(textureName);
		if (texture == null) {
			return new PC_Vec2I(0, 0);
		} 
		return texture.getMinSize();
	}

	protected PC_RectI getTextureFrame(String textureName) {

		PC_GresTexture texture = PC_Gres.getGresTexture(textureName);
		if (texture == null) {
			return new PC_RectI();
		} 
		return new PC_RectI(texture.getFrame());
	}


	protected PC_Vec2I getTextureDefaultSize(String textureName) {

		PC_GresTexture texture = PC_Gres.getGresTexture(textureName);
		if (texture == null) {
			return new PC_Vec2I(0, 0);
		} 
		return texture.getDefaultSize();
	}


	protected void drawString(String text, int x, int y, boolean shadow) {

		drawString(text, x, y, fontRenderer.getStringWidth(text), fontRenderer.FONT_HEIGHT, PC_GresAlign.H.LEFT, PC_GresAlign.V.TOP, shadow);
	}


	protected void drawString(String text, int x, int y, int width, PC_GresAlign.H alignH, boolean shadow) {

		drawString(text, x, y, width, fontRenderer.FONT_HEIGHT, alignH, PC_GresAlign.V.TOP, shadow);
	}


	protected void drawString(String text, int x, int y, int width, int height, PC_GresAlign.H alignH, PC_GresAlign.V alignV, boolean shadow) {

		switch (alignV) {
			case BOTTOM:
				y += height - fontRenderer.FONT_HEIGHT;
				break;
			case CENTER:
				y += height / 2 - fontRenderer.FONT_HEIGHT / 2;
				break;
			default:
				break;
		}
		String writeText = text;
		if (fontRenderer.getStringWidth(writeText) > width) {
			writeText = fontRenderer.trimStringToWidth(text, width - fontRenderer.getStringWidth("...")) + "...";
		}
		switch (alignH) {
			case CENTER:
				x += width / 2 - fontRenderer.getStringWidth(writeText) / 2;
				break;
			case RIGHT:
				x += width - fontRenderer.getStringWidth(writeText);
				break;
			default:
				break;
		}
		fontRenderer.drawString(writeText, x, y, fontColors[enabled && parentEnabled ? mouseDown ? 2 : mouseOver ? 1 : 0 : 3], shadow);
		GL11.glEnable(GL11.GL_BLEND);
	}

	protected void moveToTop(){
		if(parent!=null){
			parent.moveToTop(this);
		}
	}
	
	protected void moveToBottom(){
		if(parent!=null){
			parent.moveToBottom(this);
		}
	}
	
}
