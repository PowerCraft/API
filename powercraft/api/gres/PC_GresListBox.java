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
		alignH = PC_GresAlign.H.LEFT;
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
		if(!mouseDown){
			calcScrollPosition();
		}
		
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int d2 = getTextureDefaultSize(scrollVFrame).x;
		
		drawTexture(scrollVFrame, rect.width-d2, 0, d2, rect.height-d1, getStateForBar(1));
		drawTexture(scrollV, rect.width-d2+1, (int)vScrollPos+1, d2-2, vScrollSize-1, getStateForBar(1));
		
		drawTexture(scrollHFrame, 0, rect.height-d1, rect.width-d2, d1, getStateForBar(0));
		drawTexture(scrollH, (int)hScrollPos+1, rect.height-d1+1, hScrollSize-1, d1-2, getStateForBar(0));
		
		PC_Vec2I offset = getRealLocation();
		setDrawRect(scissor, new PC_RectI(2+offset.x, 2+offset.y, rect.width - 3 - d2, rect.height - 3 - d1), scale, displayHeight);
		
		int element = scroll.y;
		int y = 2;
		while(element<elements.size() && y<rect.height - 2 - d1){
			y += drawElement(element, 2-scroll.x, y);
			element++;
		}
		
		if(scissor==null){
			setDrawRect(scissor, new PC_RectI(-1, -1, -1, -1), scale, displayHeight);
		}else{
			setDrawRect(scissor, scissor, scale, displayHeight);
		}
	}
	
	private void calcMaxSizeX(){
		maxSizeX = getTextureMinSize(listElement).x;
		for(String element:elements){
			int sizeX = fontRenderer.getStringSize(element).x+2;
			if(maxSizeX<sizeX)
				maxSizeX = sizeX;
		}
	}
	
	private int getStateForBar(int bar){
		return enabled && parentEnabled ? mouseDown && selectBar==bar ? 2 : mouseOver && overBar==bar ? 1 : 0 : 3;
	}
	
	private int drawElement(int element, int x, int y){
		String text = elements.get(element);
		int state = enabled && parentEnabled ? selected==element ? 2 : mouseOverElement==element ? 1 : 0 : 3;
		int sizeY = getTextureMinSize(listElement).y;
		int oSizeY = fontRenderer.getStringSize(text).y;
		if(oSizeY>sizeY){
			sizeY = oSizeY;
		}
		drawTexture(listElement, x, y, maxSizeX, sizeY, state);
		drawString(text, x+1, y, maxSizeX, sizeY, alignH, alignV, false, state);
		return sizeY;
	}
	
	private void calcScrollPosition() {
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int d2 = getTextureDefaultSize(scrollVFrame).x;
		int sizeX = rect.width - d2;
		int sizeOutOfFrame = maxSizeX - sizeX + 5;
		if (sizeOutOfFrame < 0) {
			sizeOutOfFrame = 0;
		}
		float prozent = maxSizeX > 0 ? ((float) sizeOutOfFrame / maxSizeX) : 0;
		hScrollPos = (sizeOutOfFrame > 0 ? (float) scroll.x / sizeOutOfFrame : 0) * prozent * sizeX;
		hScrollSize = (int) ((1 - prozent) * sizeX + 0.5);

		int sizeY = rect.height - d1;
		int lines = elements.size();
		if(lines>1){
			prozent = 1.0f/(lines-1);
			vScrollPos = scroll.y * prozent * sizeY * 0.9f;
			vScrollSize = (int) (0.1f * sizeY + 0.5);
		}else{
			vScrollPos = 0;
			vScrollSize = sizeY;
		}
		updateScrollPosition();
	}
	
	private void updateScrollPosition() {
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int d2 = getTextureDefaultSize(scrollVFrame).x;
		int sizeX = rect.width - d2;
		int sizeOutOfFrame = maxSizeX - sizeX + 5;
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

		int sizeY = rect.height - d1;
		int lines = elements.size();
		if(lines>1){
			prozent = 1.0f / (lines - 1);
			if (vScrollPos < 0) {
				vScrollPos = 0;
			}
			if (vScrollPos > 0.9f * sizeY) {
				vScrollPos = 0.9f * sizeY;
			}
			scroll.y = (int) (vScrollPos / prozent / sizeY / 0.9f + 0.5);
		}else{
			vScrollPos = 0;
			scroll.y = 0;
		}
	}
	
	@Override
	protected boolean handleKeyTyped(char key, int keyCode, boolean repeat, PC_GresHistory history) {
		if(keyCode==Keyboard.KEY_DOWN){
			if(selected<elements.size()-1){
				selected++;
				moveViewToSelect();
			}
		}else if(keyCode==Keyboard.KEY_UP){
			if(selected>0){
				selected--;
				moveViewToSelect();
			}
		}else{
			return false;
		}
		return true;
	}

	private void moveViewToSelect(){
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int shouldY = selected;
		if(scroll.y>shouldY){
			scroll.y = shouldY;
		}else{
			int h = fontRenderer.getStringSize(elements.get(shouldY)).y;
			while(h<rect.height-d1-2){
				shouldY--;
				if(shouldY<0)
					break;
				h += fontRenderer.getStringSize(elements.get(shouldY)).y;
			}
			shouldY++;
			if(scroll.y<shouldY)
				scroll.y = shouldY;
		}
		calcScrollPosition();
	}
	
	@Override
	protected void handleMouseWheel(PC_GresMouseWheelEvent event, PC_GresHistory history) {
		scroll.y -= event.getWheel();
		calcScrollPosition();
		event.consume();
		if(!mouseDown && overBar==-1){
			mouseOverElement = getElementUnderMouse(event.getMouse().y);
		}
	}
	
	@Override
	protected boolean handleMouseMove(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		super.handleMouseMove(mouse, buttons, history);
		if(mouseDown){
			if(selectBar==0){
				hScrollPos += mouse.x - lastMousePosition.x;
				updateScrollPosition();
				lastMousePosition.setTo(mouse);
			}else if(selectBar==1){
				vScrollPos += mouse.y - lastMousePosition.y;
				updateScrollPosition();
				lastMousePosition.setTo(mouse);
			}
		}
		mouseOverElement = -1;
		if(mouseOver){
			overBar = mouseOverBar(mouse);
			if(!mouseDown && overBar==-1){
				mouseOverElement = getElementUnderMouse(mouse.y);
			}
		}
		return true;
	}
	
	private int getElementUnderMouse(int my){
		int y = scroll.y;
		if(elements.isEmpty())
			return -1;
		int h = fontRenderer.getStringSize(elements.get(y)).y;
		while(h<my-1){
			y++;
			if(y>=elements.size())
				return -1;
			h += fontRenderer.getStringSize(elements.get(y)).y;
		}
		return y;
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
		if(selectBar!=-1 || overBar!=-1)
			return true;
		selected = mouseOverElement = getElementUnderMouse(mouse.y);
		return true;
	}

	@Override
	protected void handleMouseLeave(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		mouseOver = false;
		mouseOverElement=-1;
	}
	
	private int mouseOverBar(PC_Vec2I mouse){
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int d2 = getTextureDefaultSize(scrollVFrame).x;
		if(new PC_RectI(rect.width-d2+1, (int)vScrollPos+1, d2-2, vScrollSize-1).contains(mouse)){
			return 1;
		}
		if(new PC_RectI((int)hScrollPos+1, rect.height-d1+1, hScrollSize-1, d1-2).contains(mouse)){
			return 0;
		}
		return -1;
	}
	
	public boolean enableRepeatEvents(){
		return true;
	}

	public String getSelected() {
		return selected==-1?null:elements.get(selected);
	}

	public int getSelection() {
		return selected;
	}

	public String getElement(int i) {
		return elements.size()>i?null:elements.get(i);
	}

	public int getMouseOver() {
		return mouseOverElement;
	}

	public void setSelected(int i) {
		selected = i;
	}
	
}
