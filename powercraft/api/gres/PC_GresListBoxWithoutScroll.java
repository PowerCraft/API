package powercraft.api.gres;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import powercraft.api.PC_Rect;
import powercraft.api.PC_Vec2;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresAlign.H;
import powercraft.api.gres.PC_GresAlign.V;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.history.PC_GresHistory;
import powercraft.api.gres.layout.PC_GresLayoutVertical;

public class PC_GresListBoxWithoutScroll extends PC_GresComponent {

	private static final String listElement = "ListElement";
	private static final String arrowRight = "ArrowRight";
	
	private List<PC_GresListBoxElement> elements;
	
	private int selected=-1;
	
	private int mouseOverElement=-1;
	
	private int nameSizeX = 0;
	
	private int keySizeX = 0;
	
	private boolean arrow = false;
	
	private int countDown;
	
	private PC_GresNeedFocusFrame openedFrame;
	
	private PC_GresListBoxWithoutScroll pp;
	
	private boolean mouseOverIsSelected;
	
	public PC_GresListBoxWithoutScroll(List<?> elements){
		this.elements = new ArrayList<PC_GresListBoxElement>(elements.size());
		for(Object obj:elements){
			PC_GresListBoxElement lbe = obj2LBE(obj);
			if(lbe!=null){
				this.elements.add(lbe);
			}
		}
		this.alignH = PC_GresAlign.H.LEFT;
	}
	
	public PC_GresListBoxWithoutScroll(List<?> elements, boolean mouseOverIsSelected){
		this(elements);
		this.mouseOverIsSelected = mouseOverIsSelected;
	}
	
	private PC_GresListBoxWithoutScroll(List<?> elements, boolean mouseOverIsSelected, PC_GresListBoxWithoutScroll pp){
		this(elements, mouseOverIsSelected);
		this.pp = pp;
	}
	
	public void setElements(List<String> elements){
		this.elements = new ArrayList<PC_GresListBoxElement>(elements.size());
		for(Object obj:elements){
			PC_GresListBoxElement lbe = obj2LBE(obj);
			if(lbe!=null){
				this.elements.add(lbe);
			}
		}
		notifyChange();
	}
	
	private static PC_GresListBoxElement obj2LBE(Object obj){
		if(obj instanceof PC_GresListBoxElement){
			return (PC_GresListBoxElement)obj;
		}else if(obj instanceof String){
			return new PC_GresListBoxElement(null, (String)obj);
		}
		return null;
	}
	
	private static int[] getElementSize(PC_GresListBoxElement element, int ah){
		PC_Vec2I s = fontRenderer.getStringSize(element.name);
		int nameSizeX = s.x;
		int keySizeX = 0;
		int maxY = s.y;
		if(element.key!=null){
			s = fontRenderer.getStringSize(element.key);
			keySizeX = s.x;
			if(maxY<s.y){
				maxY = s.y;
			}
		}
		int arrow = 0;
		if(element.nextLayer!=null){
			arrow = 1;
			if(maxY<ah){
				maxY=ah;
			}
		}
		return new int[]{nameSizeX, keySizeX, maxY, arrow};
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		PC_Vec2I size = new PC_Vec2I(getTextureMinSize(listElement));
		PC_Vec2I ss = getTextureMinSize(arrowRight);
		this.nameSizeX = 0;
		this.keySizeX = 0;
		int sizeY = 0;
		this.arrow = false;
		for(PC_GresListBoxElement element:this.elements){
			int[] sizes = getElementSize(element, ss.y);
			if(this.nameSizeX<sizes[0])
				this.nameSizeX = sizes[0];
			if(this.keySizeX<sizes[1])
				this.keySizeX = sizes[1];
			sizeY += sizes[2];
			if(sizes[3]!=0){
				this.arrow = true;
			}
		}
		size.x += 6+this.nameSizeX+this.keySizeX+(this.arrow?ss.x:0);
		size.y += 4+sizeY;
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
	protected void paint(PC_Rect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
		
		PC_Vec2 offset = getRealLocation();
		setDrawRect(scissor, new PC_Rect(2+offset.x, 2+offset.y, this.rect.width - 3, this.rect.height - 3), scale, displayHeight, zoom);
		
		int element = 0;
		int y = 2;
		while(element<this.elements.size() && y<this.rect.height - 2){
			y += drawElement(element, 2, y);
			element++;
		}
		
		if(scissor==null){
			setDrawRect(scissor, new PC_Rect(-1, -1, -1, -1), scale, displayHeight, zoom);
		}else{
			setDrawRect(scissor, scissor, scale, displayHeight, zoom);
		}
	}
	
	private int drawElement(int element, int x, int y){
		PC_GresListBoxElement elem = this.elements.get(element);
		PC_Vec2I ss = getTextureMinSize(arrowRight);
		int state = this.enabled && this.parentEnabled ? this.selected==element ? 2 : this.mouseOverElement==element ? 1 : 0 : 3;
		int sizeY = getTextureMinSize(listElement).y;
		int oSizeY = getElementSize(elem, ss.y)[2];
		if(oSizeY>sizeY){
			sizeY = oSizeY;
		}
		drawTexture(listElement, x, y, this.minSize.x-4, sizeY, state);
		drawString(elem.name, x+1, y, this.nameSizeX, sizeY, H.LEFT, V.CENTER, false, state);
		int rigth = this.rect.width-2;
		if(this.arrow){
			rigth-=ss.x;
		}
		if(elem.key!=null){
			drawString(elem.key, rigth-this.keySizeX, y, this.keySizeX, sizeY, H.LEFT, V.CENTER, false, state);
		}
		if(elem.nextLayer!=null){
			drawTexture(arrowRight, rigth, y+(sizeY-ss.y)/2, ss.x, ss.y);
		}
		return sizeY;
	}
	
	private int getElementPos(int element){
		PC_Vec2I ss = getTextureMinSize(arrowRight);
		int y = 2;
		for(int i=0; i<element; i++){
			y += getElementSize(this.elements.get(i), ss.y)[2];
		}
		return y;
	}
	
	@Override
	protected boolean handleKeyTyped(char key, int keyCode, boolean repeat, PC_GresHistory history) {
		if(keyCode==Keyboard.KEY_DOWN){
			if(this.selected<this.elements.size()-1){
				this.selected++;
			}
			if(this.mouseOverIsSelected){
				this.mouseOverElement = this.selected;
			}
		}else if(keyCode==Keyboard.KEY_UP){
			if(this.selected>0){
				this.selected--;
			}
			if(this.mouseOverIsSelected){
				this.mouseOverElement = this.selected;
			}
		}else if(keyCode==Keyboard.KEY_RIGHT){
			tryToExpand(true);
		}else if(keyCode==Keyboard.KEY_LEFT){
			if(this.pp!=null){
				this.pp.closeOpened();
			}
		}else if(keyCode==Keyboard.KEY_RETURN){
			if(this.selected!=-1)
				fireClicked(new ElementClicked(this, this.elements.get(this.selected), -1, false));
		}else{
			return false;
		}
		return true;
	}
	
	private int getElementUnderMouse(int my){
		int y = 0;
		if(this.elements.isEmpty())
			return -1;
		PC_Vec2I ss = getTextureMinSize(arrowRight);
		int h = getElementSize(this.elements.get(y), ss.y)[2];
		while(h<my-1){
			y++;
			if(y>=this.elements.size())
				return -1;
			h += getElementSize(this.elements.get(y), ss.y)[2];
		}
		return y;
	}
	
	@Override
	protected boolean handleMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton, boolean doubleClick, PC_GresHistory history) {
		super.handleMouseButtonDown(mouse, buttons, eventButton, doubleClick, history);
		this.selected = this.mouseOverElement = getElementUnderMouse(mouse.y);
		fireClicked(new ElementClicked(this, this.elements.get(this.selected), eventButton, doubleClick));
		tryToExpand(false);
		return true;
	}

	private void fireClicked(ElementClicked event){
		if(this.pp==null){
			fireEvent(event);
		}else{
			this.pp.fireClicked(event);
		}
	}
	
	private void closeOpened(){
		if(this.openedFrame!=null){
			this.openedFrame.close();
			this.openedFrame = null;
		}
		takeFocus();
	}
	
	private void tryToExpand(boolean selectFirst){
		closeOpened();
		if(this.selected!=-1){
			PC_GresListBoxElement elem = this.elements.get(this.selected);
			if(elem.nextLayer!=null){
				PC_Vec2I pos = new PC_Vec2I(getRealLocation());
				pos.x += this.rect.width * getRecursiveZoom();
				pos.y += getElementPos(this.selected);
				this.openedFrame = new PC_GresNeedFocusFrame(pos);
				this.openedFrame.addOtherAllowed(this);
				this.openedFrame.setLayout(new PC_GresLayoutVertical());
				PC_GresListBoxWithoutScroll lbws = new PC_GresListBoxWithoutScroll(elem.nextLayer, this.mouseOverIsSelected, this);
				if(selectFirst)
					lbws.setSelected(0);
				this.openedFrame.add(lbws);
				addToBase(this.openedFrame);
				lbws.takeFocus();
			}
		}
	}
	
	@Override
	protected void handleMouseLeave(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		this.mouseOver = false;
		this.mouseOverElement=this.mouseOverIsSelected?this.selected:-1;
	}

	@Override
	protected boolean handleMouseMove(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		super.handleMouseMove(mouse, buttons, history);
		this.mouseOverElement = -1;
		if(this.mouseOver && !this.mouseDown){
			this.mouseOverElement = getElementUnderMouse(mouse.y);
		}
		if(this.mouseOverIsSelected){
			if(this.mouseOverElement==-1){
				this.mouseOverElement = this.selected;
			}else if(this.selected != this.mouseOverElement){
				closeOpened();
				this.selected = this.mouseOverElement;
				this.countDown = 5;
			}
		}
		return true;
	}
	
	public String getSelected() {
		return this.selected==-1?null:this.elements.get(this.selected).name;
	}

	public int getSelection() {
		return this.selected;
	}

	public String getElement(int i) {
		return this.elements.size()>i?null:this.elements.get(i).name;
	}

	public int getMouseOver() {
		return this.mouseOverElement;
	}

	public void setSelected(int i) {
		this.selected = i;
		if(this.mouseOverIsSelected){
			this.mouseOverElement = this.selected;
		}
	}

	@Override
	protected void onTick() {
		if(this.countDown>0){
			if(--this.countDown==0){
				tryToExpand(false);
			}
		}
		super.onTick();
	}
	
	public static class ElementClicked extends PC_GresEvent{

		private PC_GresListBoxElement element;
		
		private int eventButton;
		
		private boolean doubleClick;
		
		ElementClicked(PC_GresComponent component, PC_GresListBoxElement element, int eventButton, boolean doubleClick) {
			super(component);
			this.element = element;
			this.eventButton = eventButton;
			this.doubleClick = doubleClick;
		}
		
		public PC_GresListBoxElement getElement(){
			return this.element;
		}
		
		public int getEventButton(){
			return this.eventButton;
		}
		
		public boolean isDoubleClicked(){
			return this.doubleClick;
		}
		
	}
	
}
