package powercraft.api.gres.events;

import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent.Event;
import powercraft.api.gres.history.PC_GresHistory;



public class PC_GresMouseButtonEventResult extends PC_GresMouseEventResult {
	
	private final int eventButton;
	private final boolean doubleClick;
	private final Event event;
	
	public PC_GresMouseButtonEventResult(PC_GresComponent component, PC_Vec2I mouse, int buttons, int eventButton, boolean doubleClick, Event event, boolean result, PC_GresHistory history) {
		super(component, mouse, buttons, result, history);
		this.eventButton = eventButton;
		this.doubleClick = doubleClick;
		this.event = event;
	}

	public int getEventButton(){
		return this.eventButton;
	}
	
	public Event getEvent(){
		return this.event;
	}
	
	public boolean isDoubleClick(){
		return this.doubleClick;
	}
	
}
