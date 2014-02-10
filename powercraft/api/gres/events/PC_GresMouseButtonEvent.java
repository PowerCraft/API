package powercraft.api.gres.events;

import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresComponent;

public class PC_GresMouseButtonEvent extends PC_GresMouseEvent {

	private final int eventButton;
	private final Event event;
	
	public PC_GresMouseButtonEvent(PC_GresComponent component, PC_Vec2I mouse, int buttons, int eventButton, Event event) {
		super(component, mouse, buttons);
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
