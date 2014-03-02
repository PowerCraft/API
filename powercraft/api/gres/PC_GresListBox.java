package powercraft.api.gres;

import java.util.List;

import org.lwjgl.input.Keyboard;

import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.events.PC_GresMouseWheelEvent;
import powercraft.api.gres.history.PC_GresHistory;

public class PC_GresListBox extends PC_GresComponent {

	private static final String scrollH = "ScrollH", scrollHFrame = "ScrollHFrame", scrollV = "ScrollV", scrollVFrame = "ScrollVFrame";
	
	private static final String listElement = "ListElement";
	
	private List<String> elements;

	private int maxSizeX;
	
	private int vScrollSize = 0, hScrollSize = 0;
	private float vScrollPos = 0, hScrollPos = 0;
	private PC_Vec2I scroll = new PC_Vec2I(0, 0);
	
	private static PC_Vec2I lastMousePosition = new PC_Vec2I(0, 0);
	private static int overBar=-1;
	private static int selectBar=-1;
	
	private int selected=-1;
	
	private int mouseOverElement=-1;
	
	public PC_GresListBox(List<String> elements){
		this.elements = elements;
		calcMaxSizeX();
		this.alignH = PC_GresAlign.H.LEFT;
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		return new PC_Vec2I(100, 100);
	}

	@Override
	protected PC_Vec2I calculateMaxSize() {
		return new PC_Vec2I(-1, -1);
	}

	@Override
	protected PC_Vec2I calculatePrefSize() {
		return new PC_Vec2I(-1, -1);
	}

	@Override
	protected void paint(PC_RectI scissor, double scale, int displayHeight, float timeStamp) {
		if(!this.mouseDown){
			calcScrollPosition();
		}
		
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int d2 = getTextureDefaultSize(scrollVFrame).x;
		
		drawTexture(scrollVFrame, this.rect.width-d2, 0, d2, this.rect.height-d1, getStateForBar(1));
		drawTexture(scrollV, this.rect.width-d2+1, (int)this.vScrollPos+1, d2-2, this.vScrollSize-1, getStateForBar(1));
		
		drawTexture(scrollHFrame, 0, this.rect.height-d1, this.rect.width-d2, d1, getStateForBar(0));
		drawTexture(scrollH, (int)this.hScrollPos+1, this.rect.height-d1+1, this.hScrollSize-1, d1-2, getStateForBar(0));
		
		PC_Vec2I offset = getRealLocation();
		setDrawRect(scissor, new PC_RectI(2+offset.x, 2+offset.y, this.rect.width - 3 - d2, this.rect.height - 3 - d1), scale, displayHeight);
		
		int element = this.scroll.y;
		int y = 2;
		while(element<this.elements.size() && y<this.rect.height - 2 - d1){
			y += drawElement(element, 2-this.scroll.x, y);
			element++;
		}
		
		if(scissor==null){
			setDrawRect(scissor, new PC_RectI(-1, -1, -1, -1), scale, displayHeight);
		}else{
			setDrawRect(scissor, scissor, scale, displayHeight);
		}
	}
	
	private void calcMaxSizeX(){
		this.maxSizeX = getTextureMinSize(listElement).x;
		for(String element:this.elements){
			int sizeX = fontRenderer.getStringSize(element).x+2;
			if(this.maxSizeX<sizeX)
				this.maxSizeX = sizeX;
		}
	}
	
	private int getStateForBar(int bar){
		return this.enabled && this.parentEnabled ? this.mouseDown && selectBar==bar ? 2 : this.mouseOver && overBar==bar ? 1 : 0 : 3;
	}
	
	@SuppressWarnings("hiding")
	private int drawElement(int element, int x, int y){
		String text = this.elements.get(element);
		int state = this.enabled && this.parentEnabled ? this.selected==element ? 2 : this.mouseOverElement==element ? 1 : 0 : 3;
		int sizeY = getTextureMinSize(listElement).y;
		int oSizeY = fontRenderer.getStringSize(text).y;
		if(oSizeY>sizeY){
			sizeY = oSizeY;
		}
		drawTexture(listElement, x, y, this.maxSizeX, sizeY, state);
		drawString(text, x+1, y, this.maxSizeX, sizeY, this.alignH, this.alignV, false, state);
		return sizeY;
	}
	
	private void calcScrollPosition() {
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int d2 = getTextureDefaultSize(scrollVFrame).x;
		int sizeX = this.rect.width - d2;
		int sizeOutOfFrame = this.maxSizeX - sizeX + 5;
		if (sizeOutOfFrame < 0) {
			sizeOutOfFrame = 0;
		}
		float prozent = this.maxSizeX > 0 ? ((float) sizeOutOfFrame / this.maxSizeX) : 0;
		this.hScrollPos = (sizeOutOfFrame > 0 ? (float) this.scroll.x / sizeOutOfFrame : 0) * prozent * sizeX;
		this.hScrollSize = (int) ((1 - prozent) * sizeX + 0.5);

		int sizeY = this.rect.height - d1;
		int lines = this.elements.size();
		if(lines>1){
			prozent = 1.0f/(lines-1);
			this.vScrollPos = this.scroll.y * prozent * sizeY * 0.9f;
			this.vScrollSize = (int) (0.1f * sizeY + 0.5);
		}else{
			this.vScrollPos = 0;
			this.vScrollSize = sizeY;
		}
		updateScrollPosition();
	}
	
	private void updateScrollPosition() {
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int d2 = getTextureDefaultSize(scrollVFrame).x;
		int sizeX = this.rect.width - d2;
		int sizeOutOfFrame = this.maxSizeX - sizeX + 5;
		if (sizeOutOfFrame < 0) {
			sizeOutOfFrame = 0;
		}
		float prozent = this.maxSizeX > 0 ? ((float) sizeOutOfFrame / (this.maxSizeX)) : 0;
		if (this.hScrollPos < 0) {
			this.hScrollPos = 0;
		}
		if (this.hScrollPos > sizeX - this.hScrollSize) {
			this.hScrollPos = sizeX - this.hScrollSize;
		}
		this.scroll.x = (int) (this.hScrollPos / prozent / sizeX * sizeOutOfFrame + 0.5);

		int sizeY = this.rect.height - d1;
		int lines = this.elements.size();
		if(lines>1){
			prozent = 1.0f / (lines - 1);
			if (this.vScrollPos < 0) {
				this.vScrollPos = 0;
			}
			if (this.vScrollPos > 0.9f * sizeY) {
				this.vScrollPos = 0.9f * sizeY;
			}
			this.scroll.y = (int) (this.vScrollPos / prozent / sizeY / 0.9f + 0.5);
		}else{
			this.vScrollPos = 0;
			this.scroll.y = 0;
		}
	}
	
	@Override
	protected boolean handleKeyTyped(char key, int keyCode, boolean repeat, PC_GresHistory history) {
		if(keyCode==Keyboard.KEY_DOWN){
			if(this.selected<this.elements.size()-1){
				this.selected++;
				moveViewToSelect();
			}
		}else if(keyCode==Keyboard.KEY_UP){
			if(this.selected>0){
				this.selected--;
				moveViewToSelect();
			}
		}else{
			return false;
		}
		return true;
	}

	private void moveViewToSelect(){
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int shouldY = this.selected;
		if(this.scroll.y>shouldY){
			this.scroll.y = shouldY;
		}else{
			int h = fontRenderer.getStringSize(this.elements.get(shouldY)).y;
			while(h<this.rect.height-d1-2){
				shouldY--;
				if(shouldY<0)
					break;
				h += fontRenderer.getStringSize(this.elements.get(shouldY)).y;
			}
			shouldY++;
			if(this.scroll.y<shouldY)
				this.scroll.y = shouldY;
		}
		calcScrollPosition();
	}
	
	@Override
	protected void handleMouseWheel(PC_GresMouseWheelEvent event, PC_GresHistory history) {
		this.scroll.y -= event.getWheel();
		calcScrollPosition();
		event.consume();
		if(!this.mouseDown && overBar==-1){
			this.mouseOverElement = getElementUnderMouse(event.getMouse().y);
		}
	}
	
	@Override
	protected boolean handleMouseMove(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		super.handleMouseMove(mouse, buttons, history);
		if(this.mouseDown){
			if(selectBar==0){
				this.hScrollPos += mouse.x - lastMousePosition.x;
				updateScrollPosition();
				lastMousePosition.setTo(mouse);
			}else if(selectBar==1){
				this.vScrollPos += mouse.y - lastMousePosition.y;
				updateScrollPosition();
				lastMousePosition.setTo(mouse);
			}
		}
		this.mouseOverElement = -1;
		if(this.mouseOver){
			overBar = mouseOverBar(mouse);
			if(!this.mouseDown && overBar==-1){
				this.mouseOverElement = getElementUnderMouse(mouse.y);
			}
		}
		return true;
	}
	
	private int getElementUnderMouse(int my){
		int y = this.scroll.y;
		if(this.elements.isEmpty())
			return -1;
		int h = fontRenderer.getStringSize(this.elements.get(y)).y;
		while(h<my-1){
			y++;
			if(y>=this.elements.size())
				return -1;
			h += fontRenderer.getStringSize(this.elements.get(y)).y;
		}
		return y;
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
		if(selectBar!=-1 || overBar!=-1)
			return true;
		this.selected = this.mouseOverElement = getElementUnderMouse(mouse.y);
		return true;
	}

	@Override
	protected void handleMouseLeave(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		this.mouseOver = false;
		this.mouseOverElement=-1;
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

	public String getSelected() {
		return this.selected==-1?null:this.elements.get(this.selected);
	}

	public int getSelection() {
		return this.selected;
	}

	public String getElement(int i) {
		return this.elements.size()>i?null:this.elements.get(i);
	}

	public int getMouseOver() {
		return this.mouseOverElement;
	}

	public void setSelected(int i) {
		this.selected = i;
	}
	
}
