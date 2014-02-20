package powercraft.api.gres.events;

import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.history.PC_GresHistory;

public class PC_GresMouseButtonEvent extends PC_GresMouseEvent {

	private final int eventButton;
	private final Event event;
	
	public PC_GresMouseButtonEvent(PC_GresComponent component, PC_Vec2I mouse, int buttons, int eventButton, Event event, PC_GresHistory history) {
		super(component, mouse, buttons, history);
		this.eventButton = eventButton;
		this.event = event;
	}

	public int getEventButton(){
		return eventButton;
	}
	
	public Event getEvent(){
		return event;
	}
	
	public static enum Event{
		DOWN, UP, CLICK
	}
	
}
