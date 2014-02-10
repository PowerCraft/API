package powercraft.api.gres;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.events.PC_GresMouseWheelEvent;

@SideOnly(Side.CLIENT)
class PC_GresScrollAreaContainer extends PC_GresContainer {
	
	private PC_GresScrollArea scrollArea;
	
	protected PC_GresScrollAreaContainer(PC_GresScrollArea scrollArea){
		this.scrollArea = scrollArea;
	}
	
	@Override
	protected void setParent(PC_GresContainer parent) {}

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
	protected void paint(PC_RectI scissor, double scale, int displayHeight, float timeStamp) {}

	@Override
	public boolean isRecursiveVisible() {
		return visible && scrollArea.isRecursiveVisible();
	}
	
	@Override
	public boolean isRecursiveEnabled() {
		return enabled && scrollArea.isRecursiveEnabled();
	}
	
	@Override
	protected void notifyParentOfChange() {
		scrollArea.notifyChange();
	}

	@Override
	protected void handleMouseWheel(PC_GresMouseWheelEvent event) {
		if(scrollArea.onMouseWheel(event.getMouse(), event.getButtonState(), event.getWheel())){
			event.consume();
		}
	}
	
	@Override
	protected PC_Vec2I getRealLocation() {
		return rect.getLocation().add(scrollArea.getRealLocation());
	}
	
	@Override
	public PC_GresGuiHandler getGuiHandler() {
		return scrollArea.getGuiHandler();
	}
	
	@Override
	protected void moveToTop(){
		scrollArea.moveToTop();
	}
	
	@Override
	protected void moveToBottom(){
		scrollArea.moveToBottom();
	}

	@Override
	public PC_GresContainer getParent() {
		return scrollArea.getParent();
	}
	
}
