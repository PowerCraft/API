package powercraft.api.gres.events;

import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresComponent;

public class PC_GresMouseWheelEvent extends PC_GresMouseEvent {

	private final int wheel;
	
	public PC_GresMouseWheelEvent(PC_GresComponent component, PC_Vec2I mouse, int buttons, int wheel) {
		super(component, mouse, buttons);
		this.wheel = wheel;
	}

	public int getWheel(){
		return wheel;
	}

}
