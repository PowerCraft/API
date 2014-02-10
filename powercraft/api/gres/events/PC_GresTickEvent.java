package powercraft.api.gres.events;

import powercraft.api.gres.PC_GresComponent;

public class PC_GresTickEvent extends PC_GresPrePostEvent {

	public PC_GresTickEvent(PC_GresComponent component, EventType eventType) {
		super(component, eventType);
	}

}
