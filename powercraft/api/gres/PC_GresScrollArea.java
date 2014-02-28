package powercraft.api.gres;

import net.minecraft.inventory.Slot;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_RectI;
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
		container = new PC_GresScrollAreaContainer(this);
	}
	
	public PC_GresContainer getContainer(){
		return container;
	}
	
	public void setType(int type){
		this.type = type;
	}
	
	public int getType(){
		return type & (HSCROLL|VSCROLL);
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
		if(container==null)
			return new PC_Vec2I(-1, -1);
		return container.getPrefSize().add(12);
	}

	@Override
	protected void paint(PC_RectI scissor, double scale, int displayHeight, float timeStamp) {
		if(!mouseDown){
			calcScrollPosition();
		}
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int d2 = getTextureDefaultSize(scrollVFrame).x;
		boolean hScroll = (type & HSCROLL)!=0, vScroll = (type & VSCROLL)!=0;
		if(vScroll){
			drawTexture(scrollVFrame, rect.width-d2, 0, d2, rect.height-(hScroll?d1:0), getStateForBar(1));
			drawTexture(scrollV, rect.width-d2+1, (int)vScrollPos+1, d2-2, vScrollSize-1, getStateForBar(1));
		}
		if(hScroll){
			drawTexture(scrollHFrame, 0, rect.height-d1, rect.width-(vScroll?d2:0), d1, getStateForBar(0));
			drawTexture(scrollH, (int)hScrollPos+1, rect.height-d1+1, hScrollSize-1, d1-2, getStateForBar(0));
		}
	}

	private int getStateForBar(int bar){
		return enabled && parentEnabled ? mouseDown && selectBar==bar ? 2 : mouseOver && overBar==bar ? 1 : 0 : 3;
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		container.setParentVisible(visible);
	}

	@Override
	protected void setParentVisible(boolean visible) {
		super.setParentVisible(enabled);
		container.setParentVisible(visible);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		container.setParentVisible(visible);
	}

	@Override
	protected void setParentEnabled(boolean enabled) {
		super.setParentEnabled(enabled);
		container.setParentVisible(visible);
	}

	@Override
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
			offset = rect.getLocation();
			rect.width -= getTextureDefaultSize(scrollVFrame).x;
			rect.height -= getTextureDefaultSize(scrollHFrame).y;
			scissor = setDrawRect(scissor, rect, scale, displayHeight);
			container.doPaint(offset, scissor, scale, displayHeight, timeStamp);
			GL11.glPopMatrix();
		}
	}


	@Override
	protected PC_GresComponent getComponentAtPosition(PC_Vec2I position) {
		if (visible) {
			PC_RectI rect = container.getRect();
			if (rect.contains(position)&& position.x < this.rect.width-getTextureDefaultSize(scrollVFrame).x && position.y < this.rect.height-getTextureDefaultSize(scrollHFrame).y){
				PC_GresComponent component = container.getComponentAtPosition(position.sub(rect.getLocation()));
				if (component != null) return component;
			}
			return this;
		}
		return null;
	}


	@Override
	protected void onTick() {
		container.onTick();
	}
	
	@Override
	protected void onDrawTick(float timeStamp) {
		container.onDrawTick(timeStamp);
	}

	@Override
	protected Slot getSlotAtPosition(PC_Vec2I position) {
		if (visible) {
			PC_RectI rect = container.getRect();
			if (rect.contains(position) && position.x < this.rect.width-getTextureDefaultSize(scrollVFrame).x && position.y < this.rect.height-getTextureDefaultSize(scrollHFrame).y){
				Slot slot = container.getSlotAtPosition(position.sub(rect.getLocation()));
				if (slot != null) return slot;
			}
		}
		return null;
	}


	@Override
	protected void tryActionOnKeyTyped(char key, int keyCode, boolean repeat, PC_GresHistory history) {
		if (visible) {
			container.tryActionOnKeyTyped(key, keyCode, repeat, null);
		}
	}

	@Override
	protected boolean handleMouseMove(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		super.handleMouseMove(mouse, buttons, history);
		if(mouseDown){
			if(selectBar==0){
				hScrollPos += mouse.x - lastMousePosition.x;
			}else if(selectBar==1){
				vScrollPos += mouse.y - lastMousePosition.y;
			}
			updateScrollPosition();
			lastMousePosition.setTo(mouse);
		}
		if(mouseOver){
			overBar = mouseOverBar(mouse);
		}
		return true;
	}

	private void calcScrollPosition() {

		int sizeX = rect.width - ((type & VSCROLL) != 0?15:1);
		int maxSizeX = container.rect.width;
		int sizeOutOfFrame = maxSizeX - sizeX + 7;
		if (sizeOutOfFrame < 0) {
			sizeOutOfFrame = 0;
		}
		float prozent = maxSizeX > 0 ? ((float) sizeOutOfFrame / maxSizeX) : 0;
		hScrollPos = (sizeOutOfFrame > 0 ? (float) scroll.x / sizeOutOfFrame : 0) * prozent * sizeX;
		hScrollSize = (int) ((1 - prozent) * sizeX + 0.5);

		int sizeY = rect.height - ((type & HSCROLL) != 0?15:1);
		int maxSizeY = container.rect.height;
		sizeOutOfFrame = maxSizeY - sizeY + 7;
		if (sizeOutOfFrame < 0) {
			sizeOutOfFrame = 0;
		}
		prozent = maxSizeY > 0 ? ((float) sizeOutOfFrame / maxSizeY) : 0;
		vScrollPos = (sizeOutOfFrame > 0 ? (float) scroll.y / sizeOutOfFrame : 0) * prozent * sizeY;
		vScrollSize = (int) ((1 - prozent) * sizeY + 0.5);
		
		updateScrollPosition();
	}
	
	private void updateScrollPosition() {

		int sizeX = rect.width - ((type & VSCROLL) != 0?15:1);
		int maxSizeX = container.rect.width;
		int sizeOutOfFrame = maxSizeX - sizeX + 7;
		if (sizeOutOfFrame < 0) {
			sizeOutOfFrame = 0;
		}
		float prozent = maxSizeX > 0 ? ((float) sizeOutOfFrame / (maxSizeX)) : 0;
		if (hScrollPos < 0) {
			hScrollPos = 0;
		}
		if (hScrollPos > sizeX - hScrollSize) {
			hScrollPos = sizeX - hScrollSize;
		}
		scroll.x = (int) (hScrollPos / prozent / sizeX * sizeOutOfFrame + 0.5);

		int sizeY = rect.height - ((type & HSCROLL) != 0?15:1);
		int maxSizeY = container.rect.height;
		sizeOutOfFrame = maxSizeY - sizeY + 7;
		if (sizeOutOfFrame < 0) {
			sizeOutOfFrame = 0;
		}
		prozent = maxSizeY > 0 ? ((float) sizeOutOfFrame / (maxSizeY)) : 0;
		if (vScrollPos < 0) {
			vScrollPos = 0;
		}
		if (vScrollPos > sizeY - vScrollSize) {
			vScrollPos = sizeY - vScrollSize;
		}
		scroll.y = (int) (vScrollPos / prozent / sizeY * sizeOutOfFrame + 0.5);
		
		PC_Vec2I loc = new PC_Vec2I(2, 2);
		if((type & HSCROLL)!=0)
			loc.x -= scroll.x;
		if((type & VSCROLL)!=0)
			loc.y -= scroll.y;
		container.setLocation(loc);
	}
	
	@Override
	protected boolean handleMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton, boolean doubleClick, PC_GresHistory history) {
		super.handleMouseButtonDown(mouse, buttons, eventButton, doubleClick, history);
		if(mouseDown){
			lastMousePosition.setTo(mouse);
			selectBar = mouseOverBar(mouse);
		}
		if(mouseOver){
			overBar = mouseOverBar(mouse);
		}
		return true;
	}

	private int mouseOverBar(PC_Vec2I mouse){
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int d2 = getTextureDefaultSize(scrollVFrame).x;
		boolean hScroll = (type & HSCROLL)!=0, vScroll = (type & VSCROLL)!=0;
		if(vScroll && new PC_RectI(rect.width-d2+1, (int)vScrollPos+1, d2-2, vScrollSize-1).contains(mouse)){
			return 1;
		}
		if(hScroll && new PC_RectI((int)hScrollPos+1, rect.height-d1+1, hScrollSize-1, d1-2).contains(mouse)){
			return 0;
		}
		return -1;
	}

	@Override
	protected void handleMouseWheel(PC_GresMouseWheelEvent event, PC_GresHistory history) {
		if((type & VSCROLL)!=0){
			vScrollPos -= event.getWheel()*3;
		}else if((type & HSCROLL)!=0){
			hScrollPos -= event.getWheel()*3;
		}
		updateScrollPosition();
		event.consume();
	}

	@Override
	protected void handleMouseLeave(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		mouseOver = false;
	}
	
	
	
}
