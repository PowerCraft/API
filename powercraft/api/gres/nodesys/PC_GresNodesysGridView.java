package powercraft.api.gres.nodesys;

import powercraft.api.PC_Rect;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresContainer;
import powercraft.api.gres.events.PC_GresMouseWheelEvent;
import powercraft.api.gres.history.PC_GresHistory;


public class PC_GresNodesysGridView extends PC_GresContainer {
	
	private static final String scrollH = "ScrollH", scrollHFrame = "ScrollHFrame", scrollV = "ScrollV", scrollVFrame = "ScrollVFrame";
	
	private int vScrollSize = 0, hScrollSize = 0;
	private float vScrollPos = 0, hScrollPos = 0;
	private float zoom = 1;
	private PC_Vec2I scroll = new PC_Vec2I(0, 0);
	private static PC_Vec2I lastMousePosition = new PC_Vec2I(0, 0);
	private static int overBar=-1;
	private static int selectBar=-1;
	
	public PC_GresNodesysGridView(){
		this.frame.width = getTextureDefaultSize(scrollVFrame).x;
		this.frame.height = getTextureDefaultSize(scrollHFrame).y;
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
		return calculateMinSize();
	}

	@Override
	protected void paint(PC_Rect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
		if(!this.mouseDown){
			calcScrollPosition();
		}
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int d2 = getTextureDefaultSize(scrollVFrame).x;
		drawTexture(scrollVFrame, this.rect.width-d2, 0, d2, this.rect.height-d1, getStateForBar(1));
		drawTexture(scrollV, this.rect.width-d2+1, (int)this.vScrollPos+1, d2-2, this.vScrollSize-1, getStateForBar(1));
		drawTexture(scrollHFrame, 0, this.rect.height-d1, this.rect.width-d2, d1, getStateForBar(0));
		drawTexture(scrollH, (int)this.hScrollPos+1, this.rect.height-d1+1, this.hScrollSize-1, d1-2, getStateForBar(0));
	}

	private int getStateForBar(int bar){
		return this.enabled && this.parentEnabled ? this.mouseDown && selectBar==bar ? 2 : this.mouseOver && overBar==bar ? 1 : 0 : 3;
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
		if(this.children.isEmpty())
			return;
		int sizeX = this.rect.width - 15;
		int maxSizeX = this.children.get(0).getRect().width;
		int sizeOutOfFrame = maxSizeX - sizeX + 7;
		if (sizeOutOfFrame < 0) {
			sizeOutOfFrame = 0;
		}
		float prozent = maxSizeX > 0 ? ((float) sizeOutOfFrame / maxSizeX) : 0;
		this.hScrollPos = (sizeOutOfFrame > 0 ? (float) this.scroll.x / sizeOutOfFrame : 0) * prozent * sizeX;
		this.hScrollSize = (int) ((1 - prozent) * sizeX + 0.5);

		int sizeY = this.rect.height - 15;
		int maxSizeY = this.children.get(0).getRect().height;
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

		if(this.children.isEmpty())
			return;
		int sizeX = this.rect.width - 15;
		int maxSizeX = this.children.get(0).getRect().width;
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

		int sizeY = this.rect.height - 15;
		int maxSizeY = this.children.get(0).getRect().height;
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
		
		PC_Vec2I loc = new PC_Vec2I(2-this.scroll.x, 2-this.scroll.y);
		this.children.get(0).setLocation(loc);
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
		if(new PC_RectI(this.rect.width-d2+1, (int)this.vScrollPos+1, d2-2, this.vScrollSize-1).contains(mouse)){
			return 1;
		}
		if(new PC_RectI((int)this.hScrollPos+1, this.rect.height-d1+1, this.hScrollSize-1, d1-2).contains(mouse)){
			return 0;
		}
		return -1;
	}

	@Override
	protected void handleMouseWheel(PC_GresMouseWheelEvent event, PC_GresHistory history) {
		if(event.getWheel()>0){
			this.zoom *= 1.1;
		}else if(event.getWheel()<0){
			this.zoom /= 1.1;
		}
		updateScrollPosition();
		event.consume();
	}

	@Override
	protected void handleMouseLeave(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		this.mouseOver = false;
	}

	public float getComponentZoom() {
		return this.zoom;
	}
	
}
