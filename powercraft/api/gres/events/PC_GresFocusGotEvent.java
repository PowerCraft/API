package powercraft.api.gres.events;

import powercraft.api.gres.PC_GresComponent;

public class PC_GresFocusGotEvent extends PC_GresConsumeableEvent {
	
	private final PC_GresComponent oldFocusedComponent;
	
	public PC_GresFocusGotEvent(PC_GresComponent component, PC_GresComponent oldFocusedComponent) {
		super(component);
		this.oldFocusedComponent = oldFocusedComponent;
	}
	
	public PC_GresComponent getOldFocusedComponent(){
		return oldFocusedComponent;
	}
	
}
