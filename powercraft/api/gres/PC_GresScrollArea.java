package powercraft.api.gres;

import java.util.List;

import net.minecraft.inventory.Slot;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_Rect;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.events.PC_GresMouseWheelEvent;
import powercraft.api.gres.history.PC_GresHistory;

@SideOnly(Side.CLIENT)
public class PC_GresScrollArea extends PC_GresComponent {

	private static final String scrollH = "ScrollH", scrollHFrame = "ScrollHFrame", scrollV = "ScrollV", scrollVFrame = "ScrollVFrame";
	
	public static final int HSCROLL = 1, VSCROLL = 2;
	private int type;
	private int vScrollSize = 0, hScrollSize = 0;
	private float vScrollPos = 0, hScrollPos = 0;
	private PC_Vec2I scroll = new PC_Vec2I(0, 0);
	private static PC_Vec2I lastMousePosition = new PC_Vec2I(0, 0);
	private static int overBar=-1;
	private static int selectBar=-1;
	private PC_GresScrollAreaContainer container;
	
	public PC_GresScrollArea(){
		setType(HSCROLL|VSCROLL);
		this.container = new PC_GresScrollAreaContainer(this);
	}
	
	public PC_GresContainer getContainer(){
		return this.container;
	}
	
	public void setType(int type){
		this.type = type;
	}
	
	public int getType(){
		return this.type & (HSCROLL|VSCROLL);
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		return new PC_Vec2I(100, 40);
	}

	@Override
	protected PC_Vec2I calculateMaxSize() {
		return new PC_Vec2I(-1, -1);
	}

	@Override
	protected PC_Vec2I calculatePrefSize() {
		if(this.container==null)
			return new PC_Vec2I(-1, -1);
		return this.container.getPrefSize().add(12);
	}

	@Override
	protected void paint(PC_Rect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
		if(!this.mouseDown){
			calcScrollPosition();
		}
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int d2 = getTextureDefaultSize(scrollVFrame).x;
		boolean hScroll = (this.type & HSCROLL)!=0, vScroll = (this.type & VSCROLL)!=0;
		if(vScroll){
			drawTexture(scrollVFrame, this.rect.width-d2, 0, d2, this.rect.height-(hScroll?d1:0), getStateForBar(1));
			drawTexture(scrollV, this.rect.width-d2+1, (int)this.vScrollPos+1, d2-2, this.vScrollSize-1, getStateForBar(1));
		}
		if(hScroll){
			drawTexture(scrollHFrame, 0, this.rect.height-d1, this.rect.width-(vScroll?d2:0), d1, getStateForBar(0));
			drawTexture(scrollH, (int)this.hScrollPos+1, this.rect.height-d1+1, this.hScrollSize-1, d1-2, getStateForBar(0));
		}
	}

	private int getStateForBar(int bar){
		return this.enabled && this.parentEnabled ? this.mouseDown && selectBar==bar ? 2 : this.mouseOver && overBar==bar ? 1 : 0 : 3;
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		this.container.setParentVisible(visible);
	}

	@Override
	protected void setParentVisible(boolean visible) {
		super.setParentVisible(this.enabled);
		this.container.setParentVisible(visible);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.container.setParentVisible(this.visible);
	}

	@Override
	protected void setParentEnabled(boolean enabled) {
		super.setParentEnabled(enabled);
		this.container.setParentVisible(this.visible);
	}

	@Override
	protected void doPaint(PC_Vec2 offset, PC_Rect scissorOld, double scale, int displayHeight, float timeStamp, float zoom) {
		if (this.visible) {
			PC_Rect rect = new PC_Rect(this.rect);
			rect.x += offset.x;
			rect.y += offset.y;
			PC_Rect scissor = setDrawRect(scissorOld, rect, scale, displayHeight, zoom);
			if(scissor==null)
				return;
			GL11.glPushMatrix();
			GL11.glTranslatef(this.rect.x, this.rect.y, 0);
			GL11.glColor3f(1.0f, 1.0f, 1.0f);
			paint(scissor, scale, displayHeight, timeStamp, zoom);
			doDebugRendering(0, 0, rect.width, rect.height);
			PC_Vec2 noffset = rect.getLocation();
			rect.width -= getTextureDefaultSize(scrollVFrame).x;
			rect.height -= getTextureDefaultSize(scrollHFrame).y;
			scissor = setDrawRect(scissor, rect, scale, displayHeight, zoom);
			this.container.doPaint(noffset, scissor, scale, displayHeight, timeStamp, zoom);
			GL11.glPopMatrix();
		}
	}

	@Override
	public PC_GresComponent getComponentAtPosition(PC_Vec2I position) {
		if (this.visible) {
			PC_RectI rect = this.container.getRect();
			if (rect.contains(position)&& position.x < this.rect.width-getTextureDefaultSize(scrollVFrame).x && position.y < this.rect.height-getTextureDefaultSize(scrollHFrame).y){
				PC_GresComponent component = this.container.getComponentAtPosition(position.sub(rect.getLocation()));
				if (component != null) return component;
			}
			return this;
		}
		return null;
	}

	@Override
	public void getComponentsAtPosition(PC_Vec2I position, List<PC_GresComponent> list) {

		if (this.visible) {
			PC_RectI rect = this.container.getRectScaled();
			if (rect.contains(position)&& position.x < this.rect.width-getTextureDefaultSize(scrollVFrame).x && position.y < this.rect.height-getTextureDefaultSize(scrollHFrame).y){
				this.container.getComponentsAtPosition(position.sub(rect.getLocation()), list);
			}
			list.add(this);
		}
	}
	
	@Override
	protected void onTick() {
		this.container.onTick();
	}
	
	@Override
	protected void onDrawTick(float timeStamp) {
		this.container.onDrawTick(timeStamp);
	}

	@Override
	public Slot getSlotAtPosition(PC_Vec2I position) {
		if (this.visible) {
			PC_RectI rect = this.container.getRect();
			if (rect.contains(position) && position.x < this.rect.width-getTextureDefaultSize(scrollVFrame).x && position.y < this.rect.height-getTextureDefaultSize(scrollHFrame).y){
				Slot slot = this.container.getSlotAtPosition(position.sub(rect.getLocation()));
				if (slot != null) return slot;
			}
		}
		return null;
	}


	@Override
	protected void tryActionOnKeyTyped(char key, int keyCode, boolean repeat, PC_GresHistory history) {
		if (this.visible) {
			this.container.tryActionOnKeyTyped(key, keyCode, repeat, null);
		}
	}

	@Override
	protected boolean handleMouseMove(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		super.handleMouseMove(mouse, buttons, history);
		if(this.mouseDown){
			if(selectBar==0){
				this.hScrollPos += mouse.x - lastMousePosition.x;
			}else if(selectBar==1){
				this.vScrollPos += mouse.y - lastMousePosition.y;
			}
			updateScrollPosition();
			lastMousePosition.setTo(mouse);
		}
		if(this.mouseOver){
			overBar = mouseOverBar(mouse);
		}
		return true;
	}

	private void calcScrollPosition() {

		int sizeX = this.rect.width - ((this.type & VSCROLL) != 0?15:1);
		int maxSizeX = this.container.rect.width;
		int sizeOutOfFrame = maxSizeX - sizeX + 7;
		if (sizeOutOfFrame < 0) {
			sizeOutOfFrame = 0;
		}
		float prozent = maxSizeX > 0 ? ((float) sizeOutOfFrame / maxSizeX) : 0;
		this.hScrollPos = (sizeOutOfFrame > 0 ? (float) this.scroll.x / sizeOutOfFrame : 0) * prozent * sizeX;
		this.hScrollSize = (int) ((1 - prozent) * sizeX + 0.5);

		int sizeY = this.rect.height - ((this.type & HSCROLL) != 0?15:1);
		int maxSizeY = this.container.rect.height;
		sizeOutOfFrame = maxSizeY - sizeY + 7;
		if (sizeOutOfFrame < 0) {
			sizeOutOfFrame = 0;
		}
		prozent = maxSizeY > 0 ? ((float) sizeOutOfFrame / maxSizeY) : 0;
		this.vScrollPos = (sizeOutOfFrame > 0 ? (float) this.scroll.y / sizeOutOfFrame : 0) * prozent * sizeY;
		this.vScrollSize = (int) ((1 - prozent) * sizeY + 0.5);
		
		updateScrollPosition();
	}
	
	private void updateScrollPosition() {

		int sizeX = this.rect.width - ((this.type & VSCROLL) != 0?15:1);
		int maxSizeX = this.container.rect.width;
		int sizeOutOfFrame = maxSizeX - sizeX + 7;
		if (sizeOutOfFrame < 0) {
			sizeOutOfFrame = 0;
		}
		float prozent = maxSizeX > 0 ? ((float) sizeOutOfFrame / (maxSizeX)) : 0;
		if (this.hScrollPos < 0) {
			this.hScrollPos = 0;
		}
		if (this.hScrollPos > sizeX - this.hScrollSize) {
			this.hScrollPos = sizeX - this.hScrollSize;
		}
		this.scroll.x = (int) (this.hScrollPos / prozent / sizeX * sizeOutOfFrame + 0.5);

		int sizeY = this.rect.height - ((this.type & HSCROLL) != 0?15:1);
		int maxSizeY = this.container.rect.height;
		sizeOutOfFrame = maxSizeY - sizeY + 7;
		if (sizeOutOfFrame < 0) {
			sizeOutOfFrame = 0;
		}
		prozent = maxSizeY > 0 ? ((float) sizeOutOfFrame / (maxSizeY)) : 0;
		if (this.vScrollPos < 0) {
			this.vScrollPos = 0;
		}
		if (this.vScrollPos > sizeY - this.vScrollSize) {
			this.vScrollPos = sizeY - this.vScrollSize;
		}
		this.scroll.y = (int) (this.vScrollPos / prozent / sizeY * sizeOutOfFrame + 0.5);
		
		PC_Vec2I loc = new PC_Vec2I(2, 2);
		if((this.type & HSCROLL)!=0)
			loc.x -= this.scroll.x;
		if((this.type & VSCROLL)!=0)
			loc.y -= this.scroll.y;
		this.container.setLocation(loc);
	}
	
	@Override
	protected boolean handleMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton, boolean doubleClick, PC_GresHistory history) {
		super.handleMouseButtonDown(mouse, buttons, eventButton, doubleClick, history);
		if(this.mouseDown){
			lastMousePosition.setTo(mouse);
			selectBar = mouseOverBar(mouse);
		}
		if(this.mouseOver){
			overBar = mouseOverBar(mouse);
		}
		return true;
	}

	private int mouseOverBar(PC_Vec2I mouse){
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int d2 = getTextureDefaultSize(scrollVFrame).x;
		boolean hScroll = (this.type & HSCROLL)!=0, vScroll = (this.type & VSCROLL)!=0;
		if(vScroll && new PC_RectI(this.rect.width-d2+1, (int)this.vScrollPos+1, d2-2, this.vScrollSize-1).contains(mouse)){
			return 1;
		}
		if(hScroll && new PC_RectI((int)this.hScrollPos+1, this.rect.height-d1+1, this.hScrollSize-1, d1-2).contains(mouse)){
			return 0;
		}
		return -1;
	}

	@Override
	protected void handleMouseWheel(PC_GresMouseWheelEvent event, PC_GresHistory history) {
		if((this.type & VSCROLL)!=0){
			this.vScrollPos -= event.getWheel()*3;
		}else if((this.type & HSCROLL)!=0){
			this.hScrollPos -= event.getWheel()*3;
		}
		updateScrollPosition();
		event.consume();
	}

	@Override
	protected void handleMouseLeave(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		this.mouseOver = false;
	}
	
	@Override
	public boolean hasFocusOrChild(){
		if(this.focus)
			return true;
		return this.container.hasFocusOrChild();
	}
	
	@Override
	protected void onFocusChaned(PC_GresComponent oldFocus, PC_GresComponent newFocus){
		this.container.onFocusChaned(oldFocus, newFocus);
	}
	
}
