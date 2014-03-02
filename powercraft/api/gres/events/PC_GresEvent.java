package powercraft.api.gres.events;

import powercraft.api.gres.PC_GresComponent;

public abstract class PC_GresEvent {

	private final PC_GresComponent component;
	
	protected PC_GresEvent(PC_GresComponent component){
		this.component = component;
	}
	
	public PC_GresComponent getComponent(){
		return this.component;
	}
	
}
