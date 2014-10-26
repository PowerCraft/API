package powercraft.api.gres;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.inventory.Slot;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_ImmutableList;
import powercraft.api.PC_Rect;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.history.PC_GresHistory;
import powercraft.api.gres.layout.PC_IGresLayout;
import powercraft.api.renderer.PC_OpenGL;

@SideOnly(Side.CLIENT)
public abstract class PC_GresContainer extends PC_GresComponent {

	protected final List<PC_GresComponent> children = new CopyOnWriteArrayList<PC_GresComponent>();
	protected final List<PC_GresComponent> layoutChildOrder = new ArrayList<PC_GresComponent>();
	
	private PC_IGresLayout layout;

	private boolean updatingLayout;

	private boolean updatingLayoutAgain;

	protected final PC_RectI frame = new PC_RectI(0, 0, 0, 0);


	public PC_GresContainer() {

	}

	public PC_GresContainer(String text) {
		super(text);
	}

	public List<PC_GresComponent> getChildren(){
		return new PC_ImmutableList<PC_GresComponent>(this.children);
	}
	
	public List<PC_GresComponent> getLayoutChildOrder(){
		return this.layoutChildOrder;
	}

	public PC_RectI getFrame() {

		return new PC_RectI(this.frame);
	}


	public PC_RectI getChildRect() {

		return new PC_RectI(this.frame.x, this.frame.y, this.rect.width - this.frame.x - this.frame.width, this.rect.height - this.frame.y - this.frame.height);
	}


	public PC_GresContainer setLayout(PC_IGresLayout layout) {

		this.layout = layout;
		notifyChange();
		return this;
	}


	public PC_IGresLayout getLayout() {

		return this.layout;
	}


	public void updateLayout() {

		if (this.layout != null) {
			if (!this.updatingLayout) {
				this.updatingLayout = true;
				do {
					this.updatingLayoutAgain = false;
					this.layout.updateLayout(this);
				} while (this.updatingLayoutAgain);
				this.updatingLayout = false;
			} else {
				this.updatingLayoutAgain = true;
			}
		}
	}


	public void add(PC_GresComponent component) {

		if (!this.children.contains(component)) {
			addChild(component);
			component.setParent(this);
			if(component.getParent()==this){
				giveChildFocus(component);
				notifyChange();
			}else{
				this.children.remove(component);
				this.layoutChildOrder.remove(component);
			}
		}
	}

	protected void addChild(PC_GresComponent component){
		this.children.add(component);
		this.layoutChildOrder.add(component);
	}
	
	@SuppressWarnings("static-method")
	protected void giveChildFocus(PC_GresComponent component){
		component.takeFocus();
	}

	public void remove(PC_GresComponent component) {

		this.children.remove(component);
		this.layoutChildOrder.remove(component);
		if(component.hasFocusOrChild()){
			takeFocus();
		}
		if(component.getParent()==this)
			component.setParent(null);
		notifyChange();
	}
	
	public void removeOnly(PC_GresComponent component) {

		this.children.remove(component);
		this.layoutChildOrder.remove(component);
		if(component.hasFocusOrChild()){
			takeFocus();
		}
		notifyChange();
	}

	public void removeNoFocus(PC_GresComponent component) {

		this.children.remove(component);
		this.layoutChildOrder.remove(component);
		if(component.getParent()==this)
			component.setParent(null);
		notifyChange();
	}

	public void removeAll() {

		while (!this.children.isEmpty()){
			PC_GresComponent component = this.children.remove(0);
			this.layoutChildOrder.remove(component);
			if(component.hasFocus()){
				takeFocus();
			}
			component.setParent(null);
			notifyChange();
		}
		notifyChange();
	}


	public boolean isChild(PC_GresComponent component) {

		return this.children.contains(component);
	}


	public void notifyChildChange(PC_GresComponent component) {

		notifyChange();
	}


	@Override
	protected void notifyChange() {

		super.notifyChange();
		updateLayout();
	}


	@Override
	public void setMinSize(PC_Vec2I minSize) {

		if (minSize == null && this.layout != null) {
			this.minSize.setTo(this.layout.getMinimumLayoutSize(this));
			this.minSize.x += this.frame.x + this.frame.width;
			this.minSize.y += this.frame.y + this.frame.height;
			this.minSizeSet = false;
		} else {
			if (minSize == null) {
				this.minSize.setTo(calculateMinSize());
				this.minSize.x += this.frame.x + this.frame.width;
				this.minSize.y += this.frame.y + this.frame.height;
				this.minSizeSet = false;
			} else {
				this.minSize.setTo(minSize);
				this.minSizeSet = true;
			}
		}
		setSize(getSize().max(this.minSize));
	}


	@Override
	public void setMaxSize(PC_Vec2I maxSize) {

		if (maxSize == null) {
			this.maxSize.setTo(calculateMaxSize());
			
			this.maxSizeSet = false;
		} else {
			this.maxSize.setTo(maxSize);
			this.maxSizeSet = true;
		}
	}


	@Override
	public void setPrefSize(PC_Vec2I prefSize) {

		if (prefSize == null && this.layout != null) {
			this.prefSize.setTo(this.layout.getPreferredLayoutSize(this));
			if(this.prefSize.x!=-1)
				this.prefSize.x += this.frame.x + this.frame.width;
			if(this.prefSize.y!=-1)
				this.prefSize.y += this.frame.y + this.frame.height;
			this.prefSizeSet = false;
		} else {
			if (prefSize == null) {
				this.prefSize.setTo(calculatePrefSize());
				if(this.prefSize.x!=-1)
					this.prefSize.x += this.frame.x + this.frame.width;
				if(this.prefSize.y!=-1)
					this.prefSize.y += this.frame.y + this.frame.height;
				this.prefSizeSet = false;
			} else {
				this.prefSize.setTo(prefSize);
				this.prefSizeSet = true;
			}
		}
		
	}


	@Override
	public void setVisible(boolean visible) {

		super.setVisible(visible);
		for (PC_GresComponent child : this.children) {
			child.setParentVisible(visible);
		}
	}


	@Override
	protected void setParentVisible(boolean visible) {

		super.setParentVisible(visible);
		for (PC_GresComponent child : this.children) {
			child.setParentVisible(visible);
		}
	}


	@Override
	public void setEnabled(boolean enabled) {

		super.setEnabled(enabled);
		for (PC_GresComponent child : this.children) {
			child.setParentEnabled(enabled);
		}
	}


	@Override
	protected void setParentEnabled(boolean enabled) {

		super.setParentEnabled(enabled);
		for (PC_GresComponent child : this.children) {
			child.setParentEnabled(this.visible);
		}
	}

	@Override
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
			PC_Rect oldRect = new PC_Rect(rect);
			PC_OpenGL.pushMatrix();
			GL11.glTranslatef(this.rect.x, this.rect.y, 0);
			GL11.glScalef(tzoom, tzoom, 1);
			GL11.glColor3f(1.0f, 1.0f, 1.0f);
			paint(scissor, scale, displayHeight, timeStamp, zoomm);
			doDebugRendering(0, 0, rect.width, rect.height);
			rect.x += this.frame.x*zoom;
			rect.y += this.frame.y*zoom;
			rect.width -= this.frame.x + this.frame.width;
			rect.height -= this.frame.y + this.frame.height;
			GL11.glTranslatef(this.frame.x, this.frame.y, 0);
			PC_Vec2 noffset = rect.getLocation();
			ListIterator<PC_GresComponent> iterator = this.children.listIterator(this.children.size());
			scissor = setDrawRect(scissor, rect, scale, displayHeight, zoomm);
			if(scissor!=null){
				while(iterator.hasPrevious()){
					iterator.previous().doPaint(noffset, scissor, scale, displayHeight, timeStamp, zoomm);
				}
			}
			PC_OpenGL.popMatrix();
			PC_OpenGL.pushMatrix();
			GL11.glTranslatef(this.rect.x, this.rect.y, 0);
			GL11.glScalef(tzoom, tzoom, 1);
			GL11.glColor3f(1.0f, 1.0f, 1.0f);
			setDrawRect(scissorOld, oldRect, scale, displayHeight, zoomm);
			postPaint(scissor, scale, displayHeight, timeStamp, zoomm);
			PC_OpenGL.popMatrix();
		}
	}

	protected void postPaint(PC_Rect scissor, double scale, int displayHeight, float timeStamp, float zoom){
		//
	}

	@Override
	public PC_GresComponent getComponentAtPosition(PC_Vec2I position) {

		if (this.visible) {
			position.x /= getZoom();
			position.y /= getZoom();
			if(getChildRect().contains(position)){
				PC_Vec2I nposition = position.sub(this.frame.getLocation());
				for (PC_GresComponent child : this.children) {
					PC_RectI rect = child.getRect();
					if (rect.contains(nposition)){
						PC_GresComponent component = child.getComponentAtPosition(nposition.sub(rect.getLocation()));
						if (component != null) return component;
					}
				}
			}
			return this;
		}
		return null;
	}

	@Override
	public void getComponentsAtPosition(PC_Vec2I position, List<PC_GresComponent> list) {

		if (this.visible) {
			position.x /= getZoom();
			position.y /= getZoom();
			if(getChildRect().contains(position)){
				PC_Vec2I nposition = position.sub(this.frame.getLocation());
				for (PC_GresComponent child : this.children) {
					PC_RectI rect = child.getRect();
					if (rect.contains(nposition)){
						child.getComponentsAtPosition(nposition.sub(rect.getLocation()), list);
					}
				}
			}
			list.add(this);
		}
	}

	@Override
	protected void onTick() {

		for (PC_GresComponent child : this.children) {
			child.onTick();
		}
	}
	
	@Override
	protected void onDrawTick(float timeStamp) {

		for (PC_GresComponent child : this.children) {
			child.onDrawTick(timeStamp);
		}
	}

	@Override
	public Slot getSlotAtPosition(PC_Vec2I position) {

		if (this.visible && getChildRect().contains(position)) {
			PC_Vec2I nposition = position.sub(this.frame.getLocation());
			for (PC_GresComponent child : this.children) {
				PC_RectI rect = child.getRect();
				if (rect.contains(nposition)){
					Slot slot = child.getSlotAtPosition(nposition.sub(rect.getLocation()));
					return slot;
				}
			}
		}
		return null;
	}


	@Override
	protected void tryActionOnKeyTyped(char key, int keyCode, boolean repeat, PC_GresHistory history) {

		if (this.visible) {
			for (PC_GresComponent child : this.children) {
				child.tryActionOnKeyTyped(key, keyCode, repeat, history);
			}
		}
	}

	protected void moveToTop(PC_GresComponent component){
		if(this.children.remove(component)){
			this.children.add(0, component);
		}
		moveToTop();
	}
	
	protected void moveToBottom(PC_GresComponent component){
		if(this.children.remove(component)){
			this.children.add(component);
		}
	}
	
	@Override
	protected void onScaleChanged(int newScale){
		for (PC_GresComponent child : this.children) {
			child.onScaleChanged(newScale);
		}
	}
	
	@Override
	protected void onFocusChaned(PC_GresComponent oldFocus, PC_GresComponent newFocus){
		for (PC_GresComponent child : this.children) {
			child.onFocusChaned(oldFocus, newFocus);
		}
	}
	
	@Override
	public boolean hasFocusOrChild(){
		if(this.focus)
			return true;
		for(PC_GresComponent child:this.children){
			if(child.hasFocusOrChild()){
				return true;
			}
		}
		return false;
	}
	
}
