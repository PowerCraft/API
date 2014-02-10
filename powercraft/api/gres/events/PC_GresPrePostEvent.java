package powercraft.api.gres.events;

import powercraft.api.gres.PC_GresComponent;

public abstract class PC_GresPrePostEvent extends PC_GresEvent {

	private EventType eventType;
	
	protected PC_GresPrePostEvent(PC_GresComponent component, EventType eventType) {
		super(component);
		this.eventType = eventType;
	}
	
	public EventType getEventType(){
		return eventType;
	}
	
	public static enum EventType{
		PRE, POST;
	}
	
}
