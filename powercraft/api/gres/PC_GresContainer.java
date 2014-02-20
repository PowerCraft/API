package powercraft.api.gres;


import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.inventory.Slot;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.history.PC_GresHistory;
import powercraft.api.gres.layout.PC_IGresLayout;


@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public abstract class PC_GresContainer extends PC_GresComponent {

	protected final List<PC_GresComponent> children = new ArrayList<PC_GresComponent>();
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

	public List<PC_GresComponent> getLayoutChildOrder(){
		return layoutChildOrder;
	}

	public PC_RectI getFrame() {

		return new PC_RectI(frame);
	}


	public PC_RectI getChildRect() {

		return new PC_RectI(frame.x, frame.y, rect.width - frame.x - frame.width, rect.height - frame.y - frame.height);
	}


	public void setLayout(PC_IGresLayout layout) {

		this.layout = layout;
		notifyChange();
	}


	public PC_IGresLayout getLayout() {

		return layout;
	}


	public void updateLayout() {

		if (layout != null) {
			if (!updatingLayout) {
				updatingLayout = true;
				do {
					updatingLayoutAgain = false;
					layout.updateLayout(this);
				} while (updatingLayoutAgain);
				updatingLayout = false;
			} else {
				updatingLayoutAgain = true;
			}
		}
	}


	public void add(PC_GresComponent component) {

		if (!children.contains(component)) {
			children.add(component);
			layoutChildOrder.add(component);
			component.takeFocus();
			component.setParent(this);
			if(component.getParent()==this){
				notifyChange();
			}else{
				children.remove(component);
				layoutChildOrder.remove(component);
			}
		}
	}


	public void remove(PC_GresComponent component) {

		children.remove(component);
		layoutChildOrder.remove(component);
		if(component.hasFocus()){
			takeFocus();
		}
		component.setParent(null);
		notifyChange();
	}


	public void removeAll() {

		while (!children.isEmpty()){
			PC_GresComponent component = children.remove(0);
			layoutChildOrder.remove(component);
			if(component.hasFocus()){
				takeFocus();
			}
			component.setParent(null);
			notifyChange();
		}
		notifyChange();
	}


	public boolean isChild(PC_GresComponent component) {

		return children.contains(component);
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

		if (minSize == null && layout != null) {
			this.minSize.setTo(layout.getMinimumLayoutSize(this));
			this.minSize.x += frame.x + frame.width;
			this.minSize.y += frame.y + frame.height;
			minSizeSet = false;
		} else {
			if (minSize == null) {
				this.minSize.setTo(calculateMinSize());
				this.minSize.x += frame.x + frame.width;
				this.minSize.y += frame.y + frame.height;
				minSizeSet = false;
			} else {
				this.minSize.setTo(minSize);
				minSizeSet = true;
			}
		}
		setSize(getSize().max(this.minSize));
	}


	@Override
	public void setMaxSize(PC_Vec2I maxSize) {

		if (maxSize == null) {
			this.maxSize.setTo(calculateMaxSize());
			
			maxSizeSet = false;
		} else {
			this.maxSize.setTo(maxSize);
			maxSizeSet = true;
		}
	}


	@Override
	public void setPrefSize(PC_Vec2I prefSize) {

		if (prefSize == null && layout != null) {
			this.prefSize.setTo(layout.getPreferredLayoutSize(this));
			if(this.prefSize.x!=-1)
				this.prefSize.x += frame.x + frame.width;
			if(this.prefSize.y!=-1)
				this.prefSize.y += frame.y + frame.height;
			prefSizeSet = false;
		} else {
			if (prefSize == null) {
				this.prefSize.setTo(calculatePrefSize());
				if(this.prefSize.x!=-1)
					this.prefSize.x += frame.x + frame.width;
				if(this.prefSize.y!=-1)
					this.prefSize.y += frame.y + frame.height;
				prefSizeSet = false;
			} else {
				this.prefSize.setTo(prefSize);
				prefSizeSet = true;
			}
		}
		
	}


	@Override
	public void setVisible(boolean visible) {

		super.setVisible(visible);
		for (PC_GresComponent child : children) {
			child.setParentVisible(visible);
		}
	}


	@Override
	protected void setParentVisible(boolean visible) {

		super.setParentVisible(enabled);
		for (PC_GresComponent child : children) {
			child.setParentVisible(visible);
		}
	}


	@Override
	public void setEnabled(boolean enabled) {

		super.setEnabled(enabled);
		for (PC_GresComponent child : children) {
			child.setParentEnabled(enabled);
		}
	}


	@Override
	protected void setParentEnabled(boolean enabled) {

		super.setParentEnabled(enabled);
		for (PC_GresComponent child : children) {
			child.setParentEnabled(visible);
		}
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
			rect.x += frame.x;
			rect.y += frame.y;
			rect.width -= frame.x + frame.width;
			rect.height -= frame.y + frame.height;
			GL11.glTranslatef(frame.x, frame.y, 0);
			offset = rect.getLocation();
			ListIterator<PC_GresComponent> iterator = children.listIterator(children.size());
			scissor = setDrawRect(scissor, rect, scale, displayHeight);
			if(scissor!=null){
				while(iterator.hasPrevious()){
					iterator.previous().doPaint(offset, scissor, scale, displayHeight, timeStamp);
				}
			}
			GL11.glPopMatrix();
		}
	}


	@Override
	protected PC_GresComponent getComponentAtPosition(PC_Vec2I position) {

		if (visible) {
			if(getChildRect().contains(position)){
				position = position.sub(frame.getLocation());
				for (PC_GresComponent child : children) {
					PC_RectI rect = child.getRect();
					if (rect.contains(position)){
						PC_GresComponent component = child.getComponentAtPosition(position.sub(rect.getLocation()));
						if (component != null) return component;
					}
				}
			}
			return this;
		}
		return null;
	}


	@Override
	protected void onTick() {

		for (PC_GresComponent child : children) {
			child.onTick();
		}
	}


	@Override
	protected Slot getSlotAtPosition(PC_Vec2I position) {

		if (visible && getChildRect().contains(position)) {
			position = position.sub(frame.getLocation());
			for (PC_GresComponent child : children) {
				PC_RectI rect = child.getRect();
				if (rect.contains(position)){
					Slot slot = child.getSlotAtPosition(position.sub(rect.getLocation()));
					return slot;
				}
			}
		}
		return null;
	}


	@Override
	protected void tryActionOnKeyTyped(char key, int keyCode, PC_GresHistory history) {

		if (visible) {
			for (PC_GresComponent child : children) {
				child.tryActionOnKeyTyped(key, keyCode, history);
			}
		}
	}

	protected void moveToTop(PC_GresComponent component){
		if(children.remove(component)){
			children.add(0, component);
		}
		moveToTop();
	}
	
	protected void moveToBottom(PC_GresComponent component){
		if(children.remove(component)){
			children.add(component);
		}
	}
	
	@Override
	protected void onScaleChanged(int newScale){
		for (PC_GresComponent child : children) {
			child.onScaleChanged(newScale);
		}
	}
	
}
