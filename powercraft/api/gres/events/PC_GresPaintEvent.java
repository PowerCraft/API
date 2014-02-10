package powercraft.api.gres.events;

import powercraft.api.gres.PC_GresComponent;

public class PC_GresPaintEvent extends PC_GresPrePostEvent {

	private final float timeStamp;
	
	public PC_GresPaintEvent(PC_GresComponent component, EventType eventType, float timeStamp) {
		super(component, eventType);
		this.timeStamp = timeStamp;
	}

	public float getTimeStamp(){
		return timeStamp;
	}
	
}
