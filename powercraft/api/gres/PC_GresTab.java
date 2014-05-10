package powercraft.api.gres;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.inventory.Slot;

import org.lwjgl.opengl.GL11;

import powercraft.api.PC_Rect;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.events.PC_GresMouseWheelEvent;
import powercraft.api.gres.history.PC_GresHistory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_GresTab extends PC_GresContainer {
	
	private static final String textureName = "TabFrame";
	private static final String textureNameTab = "Tab";
	private static final String textureNameTabScroll = "TabScroll";
	
	private int tabsScroll;
	private List<Tab> tabs = new ArrayList<Tab>();
	private Tab mouseOverTab;
	private int xPosDown;
	private boolean move;
	
	public PC_GresTab(){
		this.frame.setTo(getTextureFrame(textureName));
	}
	
	@Override
	public void add(PC_GresComponent component){
		add("Tab"+(this.tabs.size()+1), component);
	}
	
	public void add(String tabName, PC_GresComponent component){
		super.add(component);
		if(this.children.contains(component)){
			this.tabs.add(new Tab(tabName, component));
		}
	}
	
	@Override
	public void remove(PC_GresComponent component) {
		Iterator<Tab> iterator = this.tabs.iterator();
		while(iterator.hasNext()){
			if(iterator.next().child==component){
				iterator.remove();
				break;
			}
		}
		super.remove(component);
	}
	
	@Override
	public void removeAll() {
		this.tabs.clear();
		super.removeAll();
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		return this.children.size()>0?this.children.get(0).getMinSize():new PC_Vec2I(10, 10);
	}

	@Override
	protected PC_Vec2I calculateMaxSize() {
		return this.children.size()>0?this.children.get(0).getMaxSize():new PC_Vec2I(-1, -1);
	}

	@Override
	protected PC_Vec2I calculatePrefSize() {
		return this.children.size()>0?this.children.get(0).getPrefSize():new PC_Vec2I(-1, -1);
	}

	@SuppressWarnings("hiding")
	@Override
	protected void doPaint(PC_Vec2 offset, PC_Rect scissorOld, double scale, int displayHeight, float timeStamp, float zoom) {

		if (this.visible) {
			PC_Rect rect = new PC_Rect(this.rect);
			rect.x += offset.x;
			rect.y += offset.y;
			PC_Rect scissor = setDrawRect(scissorOld, rect, scale, displayHeight, zoom);
			if(scissor==null)
				return;
			GL11.glPushMatrix();
			GL11.glTranslatef(this.rect.x, this.rect.y, 0);
			GL11.glColor3f(1.0f, 1.0f, 1.0f);
			paint(scissor, scale, displayHeight, timeStamp, zoom);
			doDebugRendering(0, 0, rect.width, rect.height);
			rect.x += this.frame.x*zoom;
			rect.y += this.frame.y*zoom;
			GL11.glTranslatef(this.frame.x, this.frame.y, 0);
			PC_Vec2 noffset = rect.getLocation();
			if(this.children.size()>0){
				this.children.get(0).doPaint(noffset, scissor, scale, displayHeight, timeStamp, zoom);
			}
			GL11.glPopMatrix();
		}
	}
	
	@Override
	protected void paint(PC_Rect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
		drawTexture(textureName, 0, 0, this.rect.width, this.rect.height);
		PC_Vec2 rl = getRealLocation();
		setDrawRect(scissor, new PC_Rect(1+rl.x, 1+rl.y, this.rect.width-2, 12), scale, displayHeight, zoom);
		int x = -this.tabsScroll+1;
		for(Tab tab:this.tabs){
			int width = fontRenderer.getStringSize(tab.tab).x+4;
			int state = tab.child == this.children.get(0)?2:tab==this.mouseOverTab&&this.mouseOver?1:0;
			drawTexture(textureNameTab, x, 1, width, 12, state);
			drawString(tab.tab, x+2, 3, false);
			GL11.glColor4f(1, 1, 1, 1);
			x+=width;
		}
		setDrawRect(scissor, new PC_Rect(rl.x, rl.y, this.rect.width, this.rect.height), scale, displayHeight, zoom);
		int width = getTextureDefaultSize(textureNameTabScroll).x;
		x = (int) (getTabScrollProz()*(this.rect.width-width-4));
		drawTexture(textureNameTabScroll, x+2, 13, width, 1, this.move?2:0);
	}
	
	private float getTabScrollProz(){
		int width = 0;
		for(Tab tab:this.tabs){
			width += fontRenderer.getStringSize(tab.tab).x+4;
		}
		int over = width-(this.rect.width-2);
		if(over<=0){
			return 0;
		}
		return this.tabsScroll/(float)over;
	}

	@SuppressWarnings("hiding")
	@Override
	public PC_GresComponent getComponentAtPosition(PC_Vec2I position) {

		if (this.visible && this.children.size()>0) {
			PC_Vec2I nposition = position.sub(this.frame.getLocation());
			PC_GresComponent child = this.children.get(0);
			PC_RectI rect = child.getRect();
			if (rect.contains(nposition)){
				PC_GresComponent component = child.getComponentAtPosition(nposition.sub(rect.getLocation()));
				if (component != null) return component;
			}
			return this;
		}
		return null;
	}

	@SuppressWarnings("hiding")
	@Override
	protected Slot getSlotAtPosition(PC_Vec2I position) {

		if (this.visible && this.children.size()>0) {
			PC_Vec2I nposition = position.sub(this.frame.getLocation());
			PC_GresComponent child = this.children.get(0);
			PC_RectI rect = child.getRect();
			if (rect.contains(nposition)){
				Slot slot = child.getSlotAtPosition(nposition.sub(rect.getLocation()));
				return slot;
			}
		}
		return null;
	}

	@Override
	protected void tryActionOnKeyTyped(char key, int keyCode, boolean repeat, PC_GresHistory history) {

		if (this.visible && this.children.size()>0) {
			this.children.get(0).tryActionOnKeyTyped(key, keyCode, repeat, null);
		}
	}
	
	private void clampTabScroll(){
		if(this.tabsScroll<0){
			this.tabsScroll = 0;
		}else{
			int width = 0;
			for(Tab tab:this.tabs){
				width += fontRenderer.getStringSize(tab.tab).x+4;
			}
			int over = width-(this.rect.width-2);
			if(over<0){
				this.tabsScroll = 0;
			}else{
				if(this.tabsScroll>over){
					this.tabsScroll = over;
				}
			}
		}
	}
	
	@Override
	protected void handleMouseLeave(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		this.mouseOver = false;
	}

	@Override
	protected boolean handleMouseMove(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		if(this.mouseDown){
			if(Math.abs(mouse.x-this.xPosDown)>5 || this.move){
				this.move = true;
				this.tabsScroll += this.xPosDown-mouse.x;
				clampTabScroll();
				this.xPosDown = mouse.x;
			}
		}
		if(!this.mouseDown || this.move){
			this.mouseOverTab = null;
			if(mouse.y>=1 && mouse.y<=13){
				int x = -this.tabsScroll+1;
				for(Tab tab:this.tabs){
					int width = fontRenderer.getStringSize(tab.tab).x+4;
					if(mouse.x>=x && mouse.x <= x + width){
						this.mouseOverTab = tab;
						break;
					}
					x += width;
				}
			}
		}
		return true;
	}
	
	@Override
	protected boolean handleMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton, boolean doubleClick, PC_GresHistory history) {
		this.move = false;
		this.xPosDown = mouse.x;
		return super.handleMouseButtonDown(mouse, buttons, eventButton, doubleClick, history);
	}

	@Override
	protected boolean handleMouseButtonUp(PC_Vec2I mouse, int buttons, int eventButton, PC_GresHistory history) {
		if(!this.move){
			if(this.mouseOverTab!=null){
				moveToTop(this.mouseOverTab.child);
				notifyChange();
			}
			return super.handleMouseButtonUp(mouse, buttons, eventButton, history);
		}
		this.move = false;
		this.mouseDown = false;
		return true;
	}

	@Override
	protected void handleMouseWheel(PC_GresMouseWheelEvent event, PC_GresHistory history) {
		float bev=this.tabsScroll;
		if(event.getWheel()>0){
			this.tabsScroll--;
		}else if(event.getWheel()<0){
			this.tabsScroll++;
		}
		clampTabScroll();
		if(bev!=this.tabsScroll)
			event.consume();
	}

	public String getVisibleTabName(){
		if(this.children.isEmpty())
			return null;
		PC_GresComponent visibleTab = this.children.get(0);
		for(Tab tab:this.tabs){
			if(tab.child==visibleTab){
				return tab.tab;
			}
		}
		return null;
	}
	
	public PC_GresComponent getVisibleTab(){
		return this.children.size()>0?this.children.get(0):null;
	}
	
	public PC_GresComponent getTab(String name) {
		for(Tab tab:this.tabs){
			if(tab.tab.equals(name)){
				return tab.child;
			}
		}
		return null;
	}
	
	public String getTabName(PC_GresComponent component) {
		for(Tab tab:this.tabs){
			if(tab.child == component){
				return tab.tab;
			}
		}
		return null;
	}
	
	private static class Tab{
		public String tab;
		public PC_GresComponent child;
		
		public Tab(String tab, PC_GresComponent child) {
			this.tab = tab;
			this.child = child;
		}
	}
	
}
