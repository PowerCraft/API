package powercraft.api.gres.events;

import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresComponent;

public class PC_GresMouseMoveEvent extends PC_GresMouseEvent {

	private final Event event;
	
	public PC_GresMouseMoveEvent(PC_GresComponent component, PC_Vec2I mouse, int buttons, Event event) {
		super(component, mouse, buttons);
		this.event = event;
	}

	public Event getEvent(){
		return event;
	}
	
	public static enum Event{
		MOVE, LEAVE, ENTER;
	}
	
}
