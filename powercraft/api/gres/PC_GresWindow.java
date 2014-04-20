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

@SideOnly(Side.CLIENT)
public class PC_GresWindow extends PC_GresContainer {

	private static final String textureName = "Window";

	private List<PC_GresWindowSideTab> sideTabs = new ArrayList<PC_GresWindowSideTab>();
	
	public PC_GresWindow(String title) {
		this.frame.setTo(new PC_RectI(4, 4 + fontRenderer.getStringSize(title).y + 2, 4, 4));
		setText(title);
	}

	public void addSideTab(PC_GresWindowSideTab sideTab) {
		if (!this.sideTabs.contains(sideTab)) {
			this.sideTabs.add(sideTab);
			sideTab.setParent(this);
			sideTab.setSize(sideTab.getMinSize());
			notifyChange();
		}
	}


	public void removeSideTab(PC_GresWindowSideTab sideTab) {
		this.sideTabs.remove(sideTab);
		sideTab.setParent(null);
		notifyChange();
	}


	public void removeAllSideTabs() {
		while (!this.sideTabs.isEmpty())
			this.sideTabs.remove(0).setParent(null);
	}


	public boolean isSideTab(PC_GresWindowSideTab sideTab) {
		return this.sideTabs.contains(sideTab);
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		return getTextureMinSize(textureName).max(fontRenderer.getStringSize(this.text).x + 8, 0);
	}


	@Override
	protected PC_Vec2I calculateMaxSize() {
		return new PC_Vec2I(-1, -1);
	}


	@Override
	protected PC_Vec2I calculatePrefSize() {

		return new PC_Vec2I(-1, -1);
	}

	private void calculateMaxSideTabX(){
		for (PC_GresWindowSideTab sideTab : this.sideTabs) {
			if(sideTab.getSize().x+4>this.frame.x || sideTab.getSize().x+4>this.frame.width){
				this.frame.x = this.frame.width = sideTab.getSize().x+4;
			}
		}
	}
	
	@Override
	protected void notifyChange() {
		calculateMaxSideTabX();
		super.notifyChange();
		PC_Vec2I pos = new PC_Vec2I(this.rect.width-this.frame.width+4, 4);
		for (PC_GresWindowSideTab sideTab : this.sideTabs) {
			sideTab.setLocation(pos);
			pos.y += sideTab.getSize().y+2;
		}
	}
	
	@Override
	protected void setParentVisible(boolean visible) {
		super.setParentVisible(this.enabled);
		for (PC_GresWindowSideTab sideTab : this.sideTabs) {
			sideTab.setParentVisible(visible);
		}
	}
	
	@Override
	protected void setParentEnabled(boolean enabled) {
		super.setParentEnabled(enabled);
		for (PC_GresWindowSideTab sideTab : this.sideTabs) {
			sideTab.setParentEnabled(this.visible);
		}
	}
	
	@Override
	protected void paint(PC_RectI scissor, double scale, int displayHeight, float timeStamp, float zoom) {
		drawTexture(textureName, this.frame.x - 4, 0, this.rect.width - this.frame.width - this.frame.x + 8, this.rect.height);
		drawString(this.text, this.frame.x, 4, this.rect.width - this.frame.width - this.frame.x, PC_GresAlign.H.CENTER, false);
	}
	
	@SuppressWarnings("hiding")
	@Override
	protected void doPaint(PC_Vec2I offset, PC_RectI scissorOld, double scale, int displayHeight, float timeStamp, float zoom) {
		if (this.visible) {
			PC_RectI rect = new PC_RectI(this.rect);
			rect.x += offset.x;
			rect.y += offset.y;
			PC_RectI scissor = setDrawRect(scissorOld, rect, scale, displayHeight, zoom);
			if(scissor==null)
				return;
			GL11.glPushMatrix();
			GL11.glTranslatef(this.rect.x, this.rect.y, 0);
			GL11.glColor3f(1.0f, 1.0f, 1.0f);
			paint(scissor, scale, displayHeight, timeStamp, zoom);
			doDebugRendering(0, 0, rect.width, rect.height);
			rect.x += this.frame.x;
			rect.y += this.frame.y;
			GL11.glTranslatef(this.frame.x, this.frame.y, 0);
			PC_Vec2I noffset = rect.getLocation();
			rect.width -= this.frame.x + this.frame.width;
			rect.height -= this.frame.y + this.frame.height;
			PC_RectI nScissor = setDrawRect(scissor, rect, scale, displayHeight, zoom);
			if(nScissor!=null){
				ListIterator<PC_GresComponent> iterator = this.children.listIterator(this.children.size());
				while(iterator.hasPrevious()){
					iterator.previous().doPaint(noffset, nScissor, scale, displayHeight, timeStamp, zoom);
				}
			}
			GL11.glTranslatef(-this.frame.x, -this.frame.y, 0);
			noffset = noffset.sub(this.frame.getLocation());
			ListIterator<PC_GresWindowSideTab> iterator2 = this.sideTabs.listIterator(this.sideTabs.size());
			while(iterator2.hasPrevious()){
				iterator2.previous().doPaint(noffset, scissor, scale, displayHeight, timeStamp, zoom);
			}
			GL11.glPopMatrix();
		}
	}
	
	@SuppressWarnings("hiding")
	@Override
	protected PC_GresComponent getComponentAtPosition(PC_Vec2I position) {
		PC_GresComponent component = super.getComponentAtPosition(position);
		if(component!=this) return component;
		if (this.visible) {
			for (PC_GresWindowSideTab sideTab:this.sideTabs) {
				PC_RectI rect = sideTab.getRect();
				if (rect.contains(position)){
					component = sideTab.getComponentAtPosition(position.sub(rect.getLocation()));
					if (component != null) return component;
				}
			}
			PC_RectI rect = new PC_RectI(this.rect);
			rect.x = this.frame.x;
			rect.y = this.frame.y;
			rect.width -= this.frame.x+this.frame.width;
			rect.height -= this.frame.y+this.frame.height;
			if(rect.contains(position))
				return this;
		}
		return null;
	}

	@Override
	protected void onTick() {
		super.onTick();
		for(PC_GresWindowSideTab sideTab:this.sideTabs){
			sideTab.onTick();
		}
	}
	
	@Override
	protected void onDrawTick(float timeStamp) {
		super.onDrawTick(timeStamp);
		for(PC_GresWindowSideTab sideTab:this.sideTabs){
			sideTab.onDrawTick(timeStamp);
		}
	}
	
	@SuppressWarnings("hiding")
	@Override
	protected Slot getSlotAtPosition(PC_Vec2I position) {
		Slot slot = super.getSlotAtPosition(position);
		if (slot != null) return slot;
		if (this.visible) {
			for (PC_GresWindowSideTab sideTab:this.sideTabs) {
				PC_RectI rect = sideTab.getRect();
				slot = sideTab.getSlotAtPosition(position.sub(rect.getLocation()));
				if (slot != null) return slot;
			}
		}
		return null;
	}
	
	@Override
	protected void tryActionOnKeyTyped(char key, int keyCode, boolean repeat, PC_GresHistory history) {
		super.tryActionOnKeyTyped(key, keyCode, repeat, history);
		for(PC_GresWindowSideTab sideTab:this.sideTabs){
			sideTab.tryActionOnKeyTyped(key, keyCode, repeat, null);
		}
	}
	
}
