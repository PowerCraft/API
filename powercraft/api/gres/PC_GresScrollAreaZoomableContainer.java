package powercraft.api.gres;

import powercraft.api.PC_Rect;
import powercraft.api.PC_Vec2;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.events.PC_GresMouseWheelEvent;
import powercraft.api.gres.history.PC_GresHistory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class PC_GresScrollAreaZoomableContainer extends PC_GresContainer {
	
	private PC_GresScrollAreaZoomable scrollArea;
	
	protected PC_GresScrollAreaZoomableContainer(PC_GresScrollAreaZoomable scrollArea){
		this.scrollArea = scrollArea;
	}
	
	@Override
	protected void setParent(PC_GresContainer parent) {
		//
	}

	@Override
	protected PC_Vec2I calculateMinSize() {
		return new PC_Vec2I(-1, -1);
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
		//
	}

	@Override
	public boolean isRecursiveVisible() {
		return this.visible && this.scrollArea.isRecursiveVisible();
	}
	
	@Override
	public boolean isRecursiveEnabled() {
		return this.enabled && this.scrollArea.isRecursiveEnabled();
	}
	
	@Override
	protected void notifyParentOfChange() {
		this.scrollArea.notifyChange();
	}

	@Override
	protected void handleMouseWheel(PC_GresMouseWheelEvent event, PC_GresHistory history) {
		if(this.scrollArea.onMouseWheel(event.getMouse(), event.getButtonState(), event.getWheel(), null)){
			event.consume();
		}
	}
	
	@Override
	public PC_Vec2 getRealLocation() {
		return this.rect.getLocationF().mul(getRecursiveZoom()).add(this.scrollArea.getRealLocation());
	}
	
	@Override
	public PC_GresGuiHandler getGuiHandler() {
		return this.scrollArea.getGuiHandler();
	}
	
	@Override
	public void moveToTop(){
		this.scrollArea.moveToTop();
	}
	
	@Override
	public void moveToBottom(){
		this.scrollArea.moveToBottom();
	}

	@Override
	public PC_GresContainer getParent() {
		return this.scrollArea.getParent();
	}

	@Override
	public float getZoom() {
		return this.scrollArea.getComponentZoom();
	}

	@Override
	public float getRecursiveZoom() {
		return getZoom()*this.scrollArea.getRecursiveZoom();
	}	
	
}
