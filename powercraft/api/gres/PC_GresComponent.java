package powercraft.api.gres;


import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Slot;

import org.lwjgl.opengl.GL11;

import powercraft.api.PC_Debug;
import powercraft.api.PC_Rect;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresFocusGotEvent;
import powercraft.api.gres.events.PC_GresFocusLostEvent;
import powercraft.api.gres.events.PC_GresKeyEvent;
import powercraft.api.gres.events.PC_GresKeyEventResult;
import powercraft.api.gres.events.PC_GresMouseButtonEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEventResult;
import powercraft.api.gres.events.PC_GresMouseEvent;
import powercraft.api.gres.events.PC_GresMouseEventResult;
import powercraft.api.gres.events.PC_GresMouseMoveEvent;
import powercraft.api.gres.events.PC_GresMouseWheelEvent;
import powercraft.api.gres.events.PC_GresTooltipGetEvent;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.events.PC_IGresEventListenerEx;
import powercraft.api.gres.font.PC_FontRenderer;
import powercraft.api.gres.font.PC_Fonts;
import powercraft.api.gres.history.PC_GresHistory;
import powercraft.api.gres.nodesys.PC_GresNodesysNodeFrame;
import powercraft.api.renderer.PC_OpenGL;
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

	protected boolean editable = true;
	
	protected boolean mouseOver;

	protected boolean mouseDown;

	protected static PC_FontRenderer fontRenderer = new PC_FontRenderer(PC_Fonts.getFontByName("Default"));

	protected final int fontColors[] = { 0x000000, 0x000000, 0x000000, 0x333333 };

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
			if (this.parent!=null && !this.parent.isChild(this)) {
				this.parent = null;
				this.parentVisible = true;
				this.parentEnabled = true;
				notifyChange();
			}
		} else if (parent.isChild(this)) {
			this.parent = parent;
			this.parentVisible = parent.isRecursiveVisible();
			this.parentEnabled = parent.isRecursiveEnabled();
			if(getGuiHandler()!=null)
				getGuiHandler().onAdded(this);
			notifyChange();
		}
	}


	public PC_GresContainer getParent() {

		return this.parent;
	}


	public void setText(String text) {

		if (!this.text.equals(text)) {
			this.text = text;
			notifyChange();
		}
	}


	public String getText() {

		return this.text;
	}


	public void setRect(PC_RectI rect) {

		if (!this.rect.equals(rect)) {
			this.rect.setTo(rect);
			notifyChange();
		}
	}


	public PC_RectI getRect() {

		return new PC_RectI(this.rect);
	}
	
	public PC_RectI getRectScaled() {

		PC_RectI r = getRect();
		float zoom = getZoom();
		r.width *= zoom;
		r.height *= zoom;
		return r;
	}


	public PC_GresComponent setLocation(PC_Vec2I location) {

		if (this.rect.setLocation(location)) {
			notifyChange();
		}
		
		return this;
	}


	public PC_Vec2I getLocation() {

		return this.rect.getLocation();
	}


	public PC_GresComponent setSize(PC_Vec2I size) {

		if (this.rect.setSize(size)) {
			notifyChange();
		}
		
		return this;
	}


	public PC_Vec2I getSize() {

		return this.rect.getSize();
	}


	public PC_GresComponent setPadding(PC_RectI rect) {

		this.padding.setTo(rect);
		return this;
	}


	public PC_RectI getPadding() {

		return new PC_RectI(this.padding);
	}


	public PC_GresComponent setAlignH(PC_GresAlign.H alignH) {

		this.alignH = alignH;
		return this;
	}


	public PC_GresAlign.H getAlignH() {

		return this.alignH;
	}


	public PC_GresComponent setAlignV(PC_GresAlign.V alignV) {

		this.alignV = alignV;
		return this;
	}


	public PC_GresAlign.V getAlignV() {

		return this.alignV;
	}


	public PC_GresComponent setFill(PC_GresAlign.Fill fill) {

		this.fill = fill;
		return this;
	}


	public PC_GresAlign.Fill getFill() {

		return this.fill;
	}


	public void putInRect(int x, int y, int w, int h) {
		int width = w;
		int height = h;
		//if (width > maxSize.x && maxSize.x >= 0) width = maxSize.x;
		//if (height > maxSize.y && maxSize.y >= 0) height = maxSize.y;
		if (width < this.minSize.x && this.minSize.x >= 0) width = this.minSize.x;
		if (height < this.minSize.y && this.minSize.y >= 0) height = this.minSize.y;
		boolean needUpdate = false;
		if (this.fill == PC_GresAlign.Fill.BOTH || this.fill == PC_GresAlign.Fill.HORIZONTAL) {
			needUpdate |= this.rect.x != x;
			this.rect.x = x;
			needUpdate |= this.rect.width != width;
			this.rect.width = width;
		} else {
			switch (this.alignH) {
				case CENTER:
					needUpdate |= this.rect.x != x + width / 2 - this.rect.width / 2;
					this.rect.x = x + width / 2 - this.rect.width / 2;
					break;
				case RIGHT:
					needUpdate |= this.rect.x != x + width - this.rect.width;
					this.rect.x = x + width - this.rect.width;
					break;
				default:
					needUpdate |= this.rect.x != x;
					this.rect.x = x;
					break;
			}
		}
		if (this.fill == PC_GresAlign.Fill.BOTH || this.fill == PC_GresAlign.Fill.VERTICAL) {
			needUpdate |= this.rect.y != y;
			this.rect.y = y;
			needUpdate |= this.rect.height != height;
			this.rect.height = height;
		} else {
			switch (this.alignV) {
				case CENTER:
					needUpdate |= this.rect.y != y + (height - this.rect.height) / 2;
					this.rect.y = y + (height - this.rect.height) / 2;
					break;
				case BOTTOM:
					needUpdate |= this.rect.y != y + height - this.rect.height;
					this.rect.y = y + height - this.rect.height;
					break;
				default:
					needUpdate |= this.rect.y != y;
					this.rect.y = y;
					break;
			}
		}
		if (needUpdate) notifyChange();
	}


	public void setMinSize(PC_Vec2I minSize) {

		if (minSize == null) {
			this.minSize.setTo(calculateMinSize());
			this.minSizeSet = false;
		} else {
			this.minSize.setTo(minSize);
			this.minSizeSet = true;
		}
		setSize(getSize().max(this.minSize));
	}


	public PC_Vec2I getMinSize() {

		return this.minSize;
	}


	public void updateMinSize() {

		if (!this.minSizeSet) {
			setMinSize(null);
		}
	}


	protected abstract PC_Vec2I calculateMinSize();


	public void setMaxSize(PC_Vec2I maxSize) {

		if (maxSize == null) {
			this.maxSize.setTo(calculateMaxSize());
			this.maxSizeSet = false;
		} else {
			this.maxSize.setTo(maxSize);
			this.maxSizeSet = true;
		}
	}


	public PC_Vec2I getMaxSize() {

		return this.maxSize;
	}


	public void updateMaxSize() {

		if (!this.maxSizeSet) {
			setMaxSize(null);
		}
	}


	protected abstract PC_Vec2I calculateMaxSize();


	public void setPrefSize(PC_Vec2I prefSize) {

		if (prefSize == null) {
			this.prefSize.setTo(calculatePrefSize());
			this.prefSizeSet = false;
		} else {
			this.prefSize.setTo(prefSize);
			this.prefSizeSet = true;
		}
	}


	public PC_Vec2I getPrefSize() {

		return this.prefSize;
	}


	public void updatePrefSize() {

		if (!this.prefSizeSet) {
			setPrefSize(null);
		}
	}


	protected abstract PC_Vec2I calculatePrefSize();


	public void setVisible(boolean visible) {

		this.visible = visible;
		notifyParentOfChange();
	}


	protected void setParentVisible(boolean visible) {

		this.parentVisible = visible;
	}


	public boolean isVisible() {

		return this.visible;
	}


	public boolean isRecursiveVisible() {

		return this.visible && (this.parent == null || this.parent.isRecursiveVisible());
	}


	public void setEnabled(boolean enabled) {

		this.enabled = enabled;
	}


	protected void setParentEnabled(boolean enabled) {

		this.parentEnabled = enabled;
	}


	public boolean isEnabled() {

		return this.enabled;
	}


	public boolean isRecursiveEnabled() {

		return this.enabled && (this.parent == null || this.parent.isRecursiveEnabled());
	}
	
	public void setEditable(boolean editable) {

		this.editable = editable;
	}


	public boolean isEditable() {

		return this.editable;
	}

	public boolean hasFocus(){
		
		return this.focus;
		
	}

	public boolean hasFocusOrChild(){
		return this.focus;
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

		if (this.parent != null) this.parent.notifyChildChange(this);
	}


	protected static PC_Rect setDrawRect(PC_Rect old, PC_Rect _new, double scale, int displayHeight, float zoom) {

		PC_Rect rect;
		if (old == null) {
			rect = new PC_Rect(_new);
			rect.width *= zoom;
			rect.height *= zoom;
		} else {
			rect = new PC_Rect(_new);
			rect.width *= zoom;
			rect.height *= zoom;
			rect = old.averageQuantity(rect);
		}
		if (rect.width <= 0 || rect.height <= 0) return null;
		GL11.glScissor((int) (rect.x * scale), displayHeight - (int) ((rect.y + rect.height) * scale), (int) (rect.width * scale),
				(int) (rect.height * scale));
		return rect;
	}


	@SuppressWarnings("hiding")
	protected void doPaint(PC_Vec2 offset, PC_Rect scissorOld, double scale, int displayHeight, float timeStamp, float zoom) {

		if (this.visible) {
			float tzoom = getZoom();
			float zoomm = zoom * tzoom;
			PC_Rect rect = new PC_Rect(this.rect);
			rect.x *= zoom;
			rect.y *= zoom;
			rect.x += offset.x;
			rect.y += offset.y;
			PC_Rect scissor = setDrawRect(scissorOld, rect, scale, displayHeight, zoomm);
			if(scissor==null)
				return;
			PC_OpenGL.pushMatrix();
			GL11.glTranslatef(this.rect.x, this.rect.y, 0);
			GL11.glScalef(tzoom, tzoom, 1);
			GL11.glColor3f(1.0f, 1.0f, 1.0f);
			paint(scissor, scale, displayHeight, timeStamp, zoomm);
			doDebugRendering(0, 0, rect.width, rect.height);
			PC_OpenGL.popMatrix();
		}
	}

	protected void doDebugRendering(double x, double y, double width, double height){
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
	
	protected abstract void paint(PC_Rect scissor, double scale, int displayHeight, float timeStamp, float zoom);


	protected boolean onKeyTyped(char key, int keyCode, boolean repeat, PC_GresHistory history) {

		PC_GresKeyEvent event = new PC_GresKeyEvent(this, key, keyCode, repeat, history);
		fireEvent(event);
		boolean result = true;
		if (!event.isConsumed()) {
			result = handleKeyTyped(key, keyCode, repeat, history);
		}
		PC_GresKeyEventResult postEvent = new PC_GresKeyEventResult(this, key, keyCode, repeat, result, history);
		fireEvent(postEvent);
		return postEvent.getResult();
	}


	@SuppressWarnings("static-method")
	protected boolean handleKeyTyped(char key, int keyCode, boolean repeat, PC_GresHistory history) {

		return false;
	}


	protected void tryActionOnKeyTyped(char key, int keyCode, boolean repeat, PC_GresHistory history) {
		//
	}


	protected void onMouseEnter(PC_Vec2I mouse, int buttons, PC_GresHistory history) {

		PC_GresMouseEvent event = new PC_GresMouseMoveEvent(this, mouse, buttons, PC_GresMouseMoveEvent.Event.ENTER, history);
		fireEvent(event);
		if (!event.isConsumed()) {
			handleMouseEnter(mouse, buttons, history);
		}
	}


	protected void handleMouseEnter(PC_Vec2I mouse, int buttons, PC_GresHistory history) {

		this.mouseOver = this.enabled && this.parentEnabled;
	}


	protected void onMouseLeave(PC_Vec2I mouse, int buttons, PC_GresHistory history) {

		PC_GresMouseEvent event = new PC_GresMouseMoveEvent(this, mouse, buttons, PC_GresMouseMoveEvent.Event.LEAVE, history);
		fireEvent(event);
		if (!event.isConsumed()) {
			handleMouseLeave(mouse, buttons, history);
		}
	}


	protected void handleMouseLeave(PC_Vec2I mouse, int buttons, PC_GresHistory history) {

		this.mouseOver = false;
		this.mouseDown = false;
	}


	protected boolean onMouseMove(PC_Vec2I mouse, int buttons, PC_GresHistory history) {

		PC_GresMouseEvent event = new PC_GresMouseMoveEvent(this, mouse, buttons, PC_GresMouseMoveEvent.Event.MOVE, history);
		fireEvent(event);
		if (!event.isConsumed()) {
			return handleMouseMove(mouse, buttons, history);
		}
		return true;
	}


	@SuppressWarnings("static-method")
	protected boolean handleMouseMove(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		return false;
	}


	protected boolean onMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton, boolean doubleClick, PC_GresHistory history) {

		PC_GresMouseEvent event = new PC_GresMouseButtonEvent(this, mouse, buttons, eventButton, doubleClick, PC_GresMouseButtonEvent.Event.DOWN, history);
		fireEvent(event);
		boolean result = true;
		if (!event.isConsumed()) {
			result = handleMouseButtonDown(mouse, buttons, eventButton, doubleClick, history);
		}
		PC_GresMouseEventResult ev = new PC_GresMouseButtonEventResult(this, mouse, buttons, eventButton, false, PC_GresMouseButtonEvent.Event.DOWN, result, history);
		fireEvent(ev);
		return ev.getResult();
	}


	protected boolean handleMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton, boolean doubleClick, PC_GresHistory history) {

		this.mouseDown = this.enabled && this.parentEnabled;
		return false;
	}


	protected boolean onMouseButtonUp(PC_Vec2I mouse, int buttons, int eventButton, PC_GresHistory history) {

		PC_GresMouseEvent event = new PC_GresMouseButtonEvent(this, mouse, buttons, eventButton, false, PC_GresMouseButtonEvent.Event.UP, history);
		fireEvent(event);
		boolean result = true;
		if (!event.isConsumed()) {
			result = handleMouseButtonUp(mouse, buttons, eventButton, history);
		}
		PC_GresMouseEventResult ev = new PC_GresMouseButtonEventResult(this, mouse, buttons, eventButton, false, PC_GresMouseButtonEvent.Event.UP, result, history);
		fireEvent(ev);
		return ev.getResult();
	}


	protected boolean handleMouseButtonUp(PC_Vec2I mouse, int buttons, int eventButton, PC_GresHistory history) {

		boolean consumed = false;
		if (this.mouseDown) {
			PC_GresMouseEvent event = new PC_GresMouseButtonEvent(this, mouse, buttons, eventButton, false, PC_GresMouseButtonEvent.Event.CLICK, history);
			fireEvent(event);
			if (!event.isConsumed()) {
				consumed = handleMouseButtonClick(mouse, buttons, eventButton, history);
			}else{
				consumed = true;
			}
			PC_GresMouseEventResult ev = new PC_GresMouseButtonEventResult(this, mouse, buttons, eventButton, false, PC_GresMouseButtonEvent.Event.CLICK, consumed, history);
			fireEvent(ev);
			consumed = ev.getResult();
		}
		this.mouseDown = false;
		return consumed;
	}


	@SuppressWarnings("static-method")
	protected boolean handleMouseButtonClick(PC_Vec2I mouse, int buttons, int eventButton, PC_GresHistory history) {
		return false;
	}


	protected final boolean onMouseWheel(PC_Vec2I mouse, int buttons, int wheel, PC_GresHistory history) {
		PC_GresMouseWheelEvent event = new PC_GresMouseWheelEvent(this, mouse, buttons, wheel, history);
		onMouseWheel(event, history);
		return event.isConsumed();
	}

	protected void onMouseWheel(PC_GresMouseWheelEvent event, PC_GresHistory history) {
		fireEvent(event);
		if (!event.isConsumed()) {
			handleMouseWheel(event, history);
		}
	}

	protected void handleMouseWheel(PC_GresMouseWheelEvent event, PC_GresHistory history) {
		//
	}

	protected void onFocusLost(PC_GresComponent newFocusedComponent, PC_GresHistory history) {
		PC_GresFocusLostEvent event = new PC_GresFocusLostEvent(this, newFocusedComponent);
		fireEvent(event);
		if (!event.isConsumed()) {
			handleFocusLost(history);
		}
	}

	protected void handleFocusLost(PC_GresHistory history) {
		this.focus = false;
		this.mouseDown = false;
	}
	
	protected void onFocusGot(PC_GresComponent oldFocusedComponent, PC_GresHistory history) {
		PC_GresFocusGotEvent event = new PC_GresFocusGotEvent(this, oldFocusedComponent);
		fireEvent(event);
		if (!event.isConsumed()) {
			handleFocusGot(history);
		}
	}

	protected void handleFocusGot(PC_GresHistory history) {
		moveToTop();
		this.focus = true;
	}
	
	public PC_GresComponent getComponentAtPosition(PC_Vec2I mouse) {

		return this.visible ? this : null;
	}
	
	public void getComponentsAtPosition(PC_Vec2I mouse, List<PC_GresComponent> list) {

		if(this.visible){
			list.add(this);
		}
	}


	protected void onTick() {
		//
	}
	
	protected void onDrawTick(float timeStamp) {
		//
	}


	@SuppressWarnings("static-method")
	protected Slot getSlotAtPosition(PC_Vec2I position) {

		return null;
	}

	protected List<String> onGetTooltip(PC_Vec2I position) {
		List<String> tooltip = getTooltip(position);
		PC_GresTooltipGetEvent event = new PC_GresTooltipGetEvent(this, tooltip);
		fireEvent(event);
		if (event.isConsumed()) {
			return event.getTooltip();
		}
		return tooltip;
	}

	@SuppressWarnings("static-method")
	protected List<String> getTooltip(PC_Vec2I position) {

		return null;
	}


	public PC_Vec2 getRealLocation() {

		if (this.parent == null) {
			return this.rect.getLocationF().mul(getRecursiveZoom());
		} 
		return this.rect.getLocationF().mul(getRecursiveZoom()).add(this.parent.getRealLocation()).add(this.parent.getFrame().getLocationF().mul(this.parent.getRecursiveZoom()));
	}


	public PC_GresGuiHandler getGuiHandler() {

		if (this.parent == null) return null;
		return this.parent.getGuiHandler();
	}


	public PC_GresComponent addEventListener(PC_IGresEventListener eventListener) {

		if (!this.eventListeners.contains(eventListener)) this.eventListeners.add(eventListener);
		return this;
	}


	public void removeEventListener(PC_IGresEventListener eventListener) {

		this.eventListeners.remove(eventListener);
	}


	protected void fireEvent(PC_GresEvent event) {

		for (PC_IGresEventListener eventListener : this.eventListeners) {
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

	public int getCState(){
		return this.enabled && this.parentEnabled ? this.mouseDown ? 2 : this.mouseOver ? 1 : 0 : 3;
	}

	protected void drawTexture(String textureName, int x, int y, int width, int height) {

		drawTexture(textureName, x, y, width, height, getCState());
	}


	@SuppressWarnings("static-method")
	protected void drawTexture(String textureName, int x, int y, int width, int height, int state) {

		PC_GresTexture texture = PC_Gres.getGresTexture(textureName);
		if (texture != null) {
			texture.draw(x, y, width, height, state);
		}
	}


	@SuppressWarnings("static-method")
	protected PC_Vec2I getTextureMinSize(String textureName) {

		PC_GresTexture texture = PC_Gres.getGresTexture(textureName);
		if (texture == null) {
			return new PC_Vec2I(0, 0);
		} 
		return texture.getMinSize();
	}

	@SuppressWarnings("static-method")
	protected PC_RectI getTextureFrame(String textureName) {

		PC_GresTexture texture = PC_Gres.getGresTexture(textureName);
		if (texture == null) {
			return new PC_RectI();
		} 
		return new PC_RectI(texture.getFrame());
	}


	@SuppressWarnings("static-method")
	protected PC_Vec2I getTextureDefaultSize(String textureName) {

		PC_GresTexture texture = PC_Gres.getGresTexture(textureName);
		if (texture == null) {
			return new PC_Vec2I(0, 0);
		} 
		return texture.getDefaultSize();
	}


	@SuppressWarnings("hiding")
	protected void drawString(String text, int x, int y, boolean shadow) {
		PC_Vec2I size = fontRenderer.getStringSize(text);
		drawString(text, x, y, size.x, size.y, PC_GresAlign.H.LEFT, PC_GresAlign.V.TOP, shadow);
	}


	@SuppressWarnings("hiding")
	protected void drawString(String text, int x, int y, int width, PC_GresAlign.H alignH, boolean shadow) {
		PC_Vec2I size = fontRenderer.getStringSize(text);
		drawString(text, x, y, width, size.y, alignH, PC_GresAlign.V.TOP, shadow);
	}

	@SuppressWarnings("hiding")
	protected void drawString(String text, int x, int y, int width, int height, PC_GresAlign.H alignH, PC_GresAlign.V alignV, boolean shadow) {
		drawString(text, x, y, width, height, alignH, alignV, shadow, getCState());
	}

	@SuppressWarnings("hiding")
	protected void drawString(String text, int x, int y, int width, int height, PC_GresAlign.H alignH, PC_GresAlign.V alignV, boolean shadow, int state) {
		int nx = x, ny = y;
		PC_Vec2I size = fontRenderer.getStringSize(text);
		switch (alignV) {
			case BOTTOM:
				ny += height - size.y;
				break;
			case CENTER:
				ny += height / 2 - size.y / 2;
				break;
			default:
				break;
		}
		String writeText = text;
		if (size.x > width) {
			PC_Vec2I sizeD = fontRenderer.getStringSize("...");
			writeText = fontRenderer.trimStringToWidth(text, width - sizeD.x) + "...";
		}
		size = fontRenderer.getStringSize(writeText);
		switch (alignH) {
			case CENTER:
				nx += width / 2 - size.x / 2;
				break;
			case RIGHT:
				nx += width - size.x;
				break;
			default:
				break;
		}
		fontRenderer.drawString(writeText, nx, ny, this.fontColors[state], shadow);
		GL11.glEnable(GL11.GL_BLEND);
	}

	public void moveToTop(){
		if(this.parent!=null){
			this.parent.moveToTop(this);
		}
	}
	
	public void moveToBottom(){
		if(this.parent!=null){
			this.parent.moveToBottom(this);
		}
	}
	
	protected void onScaleChanged(int newScale){
		//
	}
	
	protected void onFocusChaned(PC_GresComponent oldFocus, PC_GresComponent newFocus){
		//
	}
	
	@SuppressWarnings("static-method")
	public float getZoom(){
		return 1.0f;
	}
	
	public float getRecursiveZoom(){
		if(this.parent!=null){
			return getZoom()*this.parent.getRecursiveZoom();
		}
		return getZoom();
	}
	
	protected void addToBase(PC_GresComponent c){
		if(this.getParent()!=null){
			this.getParent().addToBase(c);
		}
	}

	public boolean canAddTo(PC_GresComponent c) {
		PC_GresComponent cc = this;
		while(cc!=null){
			if(cc==c)
				return false;
			cc = cc.getParent();
		}
		return true;
	}
	
}
