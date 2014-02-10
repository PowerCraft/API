package powercraft.api.gres;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.inventory.Slot;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.events.PC_GresMouseWheelEvent;

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
		frame.setTo(getTextureFrame(textureName));
	}
	
	@Override
	public void add(PC_GresComponent component){
		add("Tab"+(tabs.size()+1), component);
	}
	
	public void add(String tabName, PC_GresComponent component){
		super.add(component);
		if(children.contains(component)){
			tabs.add(new Tab(tabName, component));
		}
	}
	
	@Override
	public void remove(PC_GresComponent component) {
		Iterator<Tab> iterator = tabs.iterator();
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
		tabs.clear();
		super.removeAll();
	}
	
	private PC_Vec2I calcFrameSize(){
		return frame.getLocation().add(frame.getSize());
	}
	
	private PC_Vec2I addFrameSize(PC_Vec2I vec){
		PC_Vec2I vecS = calcFrameSize();
		vec = new PC_Vec2I(vec);
		if(vec.x>=0)
			vec.x += vecS.x;
		if(vec.y>=0)
			vec.y += vecS.y;
		return vec;
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		return addFrameSize(children.size()>0?children.get(0).getMinSize():new PC_Vec2I(10, 10));
	}

	@Override
	protected PC_Vec2I calculateMaxSize() {
		return addFrameSize(children.size()>0?children.get(0).getMaxSize():new PC_Vec2I(-1, -1));
	}

	@Override
	protected PC_Vec2I calculatePrefSize() {
		return addFrameSize(children.size()>0?children.get(0).getPrefSize():new PC_Vec2I(-1, -1));
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
			if(children.size()>0){
				children.get(0).doPaint(offset, scissor, scale, displayHeight, timeStamp);
			}
			GL11.glPopMatrix();
		}
	}
	
	@Override
	protected void paint(PC_RectI scissor, double scale, int displayHeight, float timeStamp) {
		drawTexture(textureName, 0, 0, rect.width, rect.height);
		PC_Vec2I rl = getRealLocation();
		setDrawRect(scissor, new PC_RectI(1+rl.x, 1+rl.y, rect.width-2, 12), scale, displayHeight);
		int x = -tabsScroll+1;
		for(Tab tab:tabs){
			int width = fontRenderer.getStringWidth(tab.tab)+4;
			int state = tab.child == children.get(0)?2:tab==mouseOverTab&&mouseOver?1:0;
			drawTexture(textureNameTab, x, 1, width, 12, state);
			drawString(tab.tab, x+2, 3, false);
			GL11.glColor4f(1, 1, 1, 1);
			x+=width;
		}
		setDrawRect(scissor, new PC_RectI(rl.x, rl.y, rect.width, rect.height), scale, displayHeight);
		int width = getTextureDefaultSize(textureNameTabScroll).x;
		x = (int) (getTabScrollProz()*(rect.width-width-4));
		drawTexture(textureNameTabScroll, x+2, 13, width, 1, move?2:0);
	}
	
	private float getTabScrollProz(){
		int width = 0;
		for(Tab tab:tabs){
			width += fontRenderer.getStringWidth(tab.tab)+4;
		}
		int over = width-(rect.width-2);
		if(over<=0){
			return 0;
		}
		return tabsScroll/(float)over;
	}

	@Override
	protected PC_GresComponent getComponentAtPosition(PC_Vec2I position) {

		if (visible && children.size()>0) {
			position = position.sub(frame.getLocation());
			PC_GresComponent child = children.get(0);
			PC_RectI rect = child.getRect();
			if (rect.contains(position)){
				PC_GresComponent component = child.getComponentAtPosition(position.sub(rect.getLocation()));
				if (component != null) return component;
			}
			return this;
		}
		return null;
	}

	@Override
	protected Slot getSlotAtPosition(PC_Vec2I position) {

		if (visible && children.size()>0) {
			position = position.sub(frame.getLocation());
			PC_GresComponent child = children.get(0);
			PC_RectI rect = child.getRect();
			if (rect.contains(position)){
				Slot slot = child.getSlotAtPosition(position.sub(rect.getLocation()));
				return slot;
			}
		}
		return null;
	}

	@Override
	protected void tryActionOnKeyTyped(char key, int keyCode) {

		if (visible && children.size()>0) {
			children.get(0).tryActionOnKeyTyped(key, keyCode);
		}
	}
	
	private void clampTabScroll(){
		if(tabsScroll<0){
			tabsScroll = 0;
		}else{
			int width = 0;
			for(Tab tab:tabs){
				width += fontRenderer.getStringWidth(tab.tab)+4;
			}
			int over = width-(rect.width-2);
			if(over<0){
				tabsScroll = 0;
			}else{
				if(tabsScroll>over){
					tabsScroll = over;
				}
			}
		}
	}
	
	@Override
	protected void handleMouseLeave(PC_Vec2I mouse, int buttons) {
		mouseOver = false;
	}

	@Override
	protected boolean handleMouseMove(PC_Vec2I mouse, int buttons) {
		if(mouseDown){
			if(Math.abs(mouse.x-xPosDown)>5 || move){
				move = true;
				tabsScroll += xPosDown-mouse.x;
				clampTabScroll();
				xPosDown = mouse.x;
			}
		}
		if(!mouseDown || move){
			mouseOverTab = null;
			if(mouse.y>=1 && mouse.y<=13){
				int x = -tabsScroll+1;
				for(Tab tab:tabs){
					int width = fontRenderer.getStringWidth(tab.tab)+4;
					if(mouse.x>=x && mouse.x <= x + width){
						mouseOverTab = tab;
						break;
					}
					x += width;
				}
			}
		}
		return true;
	}
	
	@Override
	protected boolean handleMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton) {
		move = false;
		xPosDown = mouse.x;
		return super.handleMouseButtonDown(mouse, buttons, eventButton);
	}

	@Override
	protected boolean handleMouseButtonUp(PC_Vec2I mouse, int buttons, int eventButton) {
		if(!move){
			moveToTop(mouseOverTab.child);
			return super.handleMouseButtonUp(mouse, buttons, eventButton);
		}
		move = false;
		mouseDown = false;
		return true;
	}

	@Override
	protected void handleMouseWheel(PC_GresMouseWheelEvent event) {
		float bev=tabsScroll;
		if(event.getWheel()>0){
			tabsScroll--;
		}else if(event.getWheel()<0){
			tabsScroll++;
		}
		clampTabScroll();
		if(bev!=tabsScroll)
			event.consume();
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
