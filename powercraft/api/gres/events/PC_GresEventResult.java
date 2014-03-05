package powercraft.api.gres.events;

import powercraft.api.gres.PC_GresComponent;

public abstract class PC_GresEventResult extends PC_GresEvent{

	private boolean result;
	
	protected PC_GresEventResult(PC_GresComponent component, boolean result){
		super(component);
		this.result = result;
	}
	
	public boolean getResult(){
		return this.result;
	}
	
	public void setResult(boolean result){
		this.result = result;
	}
	
}
