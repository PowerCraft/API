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
		frame.setTo(new PC_RectI(4, 4 + fontRenderer.FONT_HEIGHT + 2, 4, 4));
		setText(title);
	}

	public void addSideTab(PC_GresWindowSideTab sideTab) {
		if (!sideTabs.contains(sideTab)) {
			sideTabs.add(sideTab);
			sideTab.setParent(this);
			sideTab.setSize(sideTab.getMinSize());
			notifyChange();
		}
	}


	public void removeSideTab(PC_GresWindowSideTab sideTab) {
		sideTabs.remove(sideTab);
		sideTab.setParent(null);
		notifyChange();
	}


	public void removeAllSideTabs() {
		while (!sideTabs.isEmpty())
			sideTabs.remove(0).setParent(null);
	}


	public boolean isSideTab(PC_GresWindowSideTab sideTab) {
		return sideTabs.contains(sideTab);
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		return getTextureMinSize(textureName).max(fontRenderer.getStringWidth(text) + 8, 0);
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
		for (PC_GresWindowSideTab sideTab : sideTabs) {
			if(sideTab.getSize().x+4>frame.x || sideTab.getSize().x+4>frame.width){
				frame.x = frame.width = sideTab.getSize().x+4;
			}
		}
	}
	
	@Override
	protected void notifyChange() {
		calculateMaxSideTabX();
		super.notifyChange();
		PC_Vec2I pos = new PC_Vec2I(rect.width-frame.width+4, 4);
		for (PC_GresWindowSideTab sideTab : sideTabs) {
			sideTab.setLocation(pos);
			pos.y += sideTab.getSize().y+2;
		}
	}
	
	@Override
	protected void setParentVisible(boolean visible) {
		super.setParentVisible(enabled);
		for (PC_GresWindowSideTab sideTab : sideTabs) {
			sideTab.setParentVisible(visible);
		}
	}
	
	@Override
	protected void setParentEnabled(boolean enabled) {
		super.setParentEnabled(enabled);
		for (PC_GresWindowSideTab sideTab : sideTabs) {
			sideTab.setParentEnabled(visible);
		}
	}
	
	@Override
	protected void paint(PC_RectI scissor, double scale, int displayHeight, float timeStamp) {
		drawTexture(textureName, frame.x - 4, 0, rect.width - frame.width - frame.x + 8, rect.height);
		drawString(text, frame.x, 4, rect.width - frame.width - frame.x, PC_GresAlign.H.CENTER, false);
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
			GL11.glTranslatef(frame.x, frame.y, 0);
			offset = rect.getLocation();
			rect.width -= frame.x + frame.width;
			rect.height -= frame.y + frame.height;
			PC_RectI nScissor = setDrawRect(scissor, rect, scale, displayHeight);
			if(nScissor!=null){
				ListIterator<PC_GresComponent> iterator = children.listIterator(children.size());
				while(iterator.hasPrevious()){
					iterator.previous().doPaint(offset, nScissor, scale, displayHeight, timeStamp);
				}
			}
			GL11.glTranslatef(-frame.x, -frame.y, 0);
			offset = offset.sub(frame.getLocation());
			ListIterator<PC_GresWindowSideTab> iterator2 = sideTabs.listIterator(sideTabs.size());
			while(iterator2.hasPrevious()){
				iterator2.previous().doPaint(offset, scissor, scale, displayHeight, timeStamp);
			}
			GL11.glPopMatrix();
		}
	}
	
	@Override
	protected PC_GresComponent getComponentAtPosition(PC_Vec2I position) {
		PC_GresComponent component = super.getComponentAtPosition(position);
		if(component!=this) return component;
		if (visible) {
			for (PC_GresWindowSideTab sideTab:sideTabs) {
				PC_RectI rect = sideTab.getRect();
				if (rect.contains(position)){
					component = sideTab.getComponentAtPosition(position.sub(rect.getLocation()));
					if (component != null) return component;
				}
			}
			PC_RectI rect = new PC_RectI(this.rect);
			rect.x = frame.x;
			rect.y = frame.y;
			rect.width -= frame.x+frame.width;
			rect.height -= frame.y+frame.height;
			if(rect.contains(position))
				return this;
		}
		return null;
	}

	@Override
	protected void onTick() {
		super.onTick();
		for(PC_GresWindowSideTab sideTab:sideTabs){
			sideTab.onTick();
		}
	}
	
	@Override
	protected Slot getSlotAtPosition(PC_Vec2I position) {
		Slot slot = super.getSlotAtPosition(position);
		if (slot != null) return slot;
		if (visible) {
			for (PC_GresWindowSideTab sideTab:sideTabs) {
				PC_RectI rect = sideTab.getRect();
				slot = sideTab.getSlotAtPosition(position.sub(rect.getLocation()));
				if (slot != null) return slot;
			}
		}
		return null;
	}
	
	@Override
	protected void tryActionOnKeyTyped(char key, int keyCode, PC_GresHistory history) {
		super.tryActionOnKeyTyped(key, keyCode, history);
		for(PC_GresWindowSideTab sideTab:sideTabs){
			sideTab.tryActionOnKeyTyped(key, keyCode, null);
		}
	}
	
}
