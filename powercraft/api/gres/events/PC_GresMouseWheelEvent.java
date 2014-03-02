package powercraft.api.gres.events;

import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.history.PC_GresHistory;

public class PC_GresMouseWheelEvent extends PC_GresMouseEvent {

	private final int wheel;
	
	public PC_GresMouseWheelEvent(PC_GresComponent component, PC_Vec2I mouse, int buttons, int wheel, PC_GresHistory history) {
		super(component, mouse, buttons, history);
		this.wheel = wheel;
	}

	public int getWheel(){
		return this.wheel;
	}

}
