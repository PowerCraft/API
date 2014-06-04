package powercraft.api.gres.events;

import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.events.PC_GresMouseMoveEvent.Event;
import powercraft.api.gres.history.PC_GresHistory;



public class PC_GresMouseMoveEventResult extends PC_GresMouseEventResult {
	
	private final Event event;
	
	public PC_GresMouseMoveEventResult(PC_GresComponent component, PC_Vec2I mouse, int buttons, Event event, boolean result, PC_GresHistory history) {
		super(component, mouse, buttons, result, history);
		this.event = event;
	}
	
	public Event getEvent(){
		return this.event;
	}
	
}
