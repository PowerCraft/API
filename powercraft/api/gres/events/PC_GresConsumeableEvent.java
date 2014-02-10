package powercraft.api.gres.events;

import powercraft.api.gres.PC_GresComponent;

public abstract class PC_GresConsumeableEvent extends PC_GresEvent {

	private boolean consumed;
	
	protected PC_GresConsumeableEvent(PC_GresComponent component) {
		super(component);
	}

	public void consume(){
		consumed = true;
	}
	
	public boolean isConsumed(){
		return consumed;
	}
	
}
