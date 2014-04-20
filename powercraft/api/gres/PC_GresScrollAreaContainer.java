package powercraft.api.gres;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.events.PC_GresMouseWheelEvent;
import powercraft.api.gres.history.PC_GresHistory;

@SideOnly(Side.CLIENT)
class PC_GresScrollAreaContainer extends PC_GresContainer {
	
	private PC_GresScrollArea scrollArea;
	
	protected PC_GresScrollAreaContainer(PC_GresScrollArea scrollArea){
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
	protected void paint(PC_RectI scissor, double scale, int displayHeight, float timeStamp, float zoom) {
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
	public PC_Vec2I getRealLocation() {
		return this.rect.getLocation().add(this.scrollArea.getRealLocation());
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
	
}
