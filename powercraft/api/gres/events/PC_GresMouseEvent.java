package powercraft.api.gres.events;

import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresComponent;

public abstract class PC_GresMouseEvent extends PC_GresConsumeableEvent {

	private final PC_Vec2I mouse;
	private final int buttons;
	
	protected PC_GresMouseEvent(PC_GresComponent component, PC_Vec2I mouse, int buttons) {
		super(component);
		this.mouse = new PC_Vec2I(mouse);
		this.buttons = buttons;
	}
	
	public PC_Vec2I getMouse(){
		return mouse;
	}
	
	public int getButtonState(){
		return buttons;
	}
	
}
