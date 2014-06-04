package powercraft.api.gres.events;

import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.history.PC_GresHistory;



public class PC_GresMouseWheelEventResult extends PC_GresMouseEventResult {
	
	public PC_GresMouseWheelEventResult(PC_GresComponent component, PC_Vec2I mouse, int buttons, boolean result, PC_GresHistory history) {
		super(component, mouse, buttons, result, history);
	}
	
}
