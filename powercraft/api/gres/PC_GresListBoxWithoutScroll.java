package powercraft.api.gres;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.history.PC_GresHistory;

public class PC_GresListBoxWithoutScroll extends PC_GresComponent {

	private static final String listElement = "ListElement";
	
	private List<String> elements;
	
	private int selected=-1;
	
	private int mouseOverElement=-1;
	
	public PC_GresListBoxWithoutScroll(List<String> elements){
		this.elements = new ArrayList<String>(elements);
		this.alignH = PC_GresAlign.H.LEFT;
	}
	
	public void setElements(List<String> elements){
		this.elements = new ArrayList<String>(elements);
		notifyChange();
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		PC_Vec2I size = new PC_Vec2I(getTextureMinSize(listElement));
		for(String element:this.elements){
			PC_Vec2I s = fontRenderer.getStringSize(element);
			int sizeX = s.x+2;
			if(size.x<sizeX)
				size.x = sizeX;
			size.y += s.y;
		}
		size.x += 6;
		size.y += 4;
		return size;
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
		
		PC_Vec2I offset = getRealLocation();
		setDrawRect(scissor, new PC_RectI(2+offset.x, 2+offset.y, this.rect.width - 3, this.rect.height - 3), scale, displayHeight);
		
		int element = 0;
		int y = 2;
		while(element<this.elements.size() && y<this.rect.height - 2){
			y += drawElement(element, 2, y);
			element++;
		}
		
		if(scissor==null){
			setDrawRect(scissor, new PC_RectI(-1, -1, -1, -1), scale, displayHeight);
		}else{
			setDrawRect(scissor, scissor, scale, displayHeight);
		}
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
		drawTexture(listElement, x, y, this.minSize.x-4, sizeY, state);
		drawString(text, x+1, y, this.minSize.x-4, sizeY, this.alignH, this.alignV, false, state);
		return sizeY;
	}
	
	@Override
	protected boolean handleKeyTyped(char key, int keyCode, boolean repeat, PC_GresHistory history) {
		if(keyCode==Keyboard.KEY_DOWN){
			if(this.selected<this.elements.size()-1){
				this.selected++;
			}
		}else if(keyCode==Keyboard.KEY_UP){
			if(this.selected>0){
				this.selected--;
			}
		}else{
			return false;
		}
		return true;
	}
	
	private int getElementUnderMouse(int my){
		int y = 0;
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
		this.selected = this.mouseOverElement = getElementUnderMouse(mouse.y);
		return true;
	}

	@Override
	protected void handleMouseLeave(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		this.mouseOver = false;
		this.mouseOverElement=-1;
	}

	@Override
	protected boolean handleMouseMove(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		super.handleMouseMove(mouse, buttons, history);
		this.mouseOverElement = -1;
		if(this.mouseOver && !this.mouseDown){
			this.mouseOverElement = getElementUnderMouse(mouse.y);
		}
		return true;
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