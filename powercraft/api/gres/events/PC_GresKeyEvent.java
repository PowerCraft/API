package powercraft.api.gres.events;

import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.history.PC_GresHistory;

public class PC_GresKeyEvent extends PC_GresConsumeableEvent {

	private final char key;
	private final int keyCode;
	private final boolean repeat;
	private final PC_GresHistory history;
	
	public PC_GresKeyEvent(PC_GresComponent component, char key, int keyCode, boolean repeat, PC_GresHistory history){
		super(component);
		this.key = key;
		this.keyCode = keyCode;
		this.history = history;
		this.repeat = repeat;
	}
	
	public char getKey(){
		return this.key;
	}
	
	public int getKeyCode(){
		return this.keyCode;
	}
	
	public boolean isRepeatEvents(){
		return this.repeat;
	}
	
	public PC_GresHistory getHistory(){
		return this.history;
	}
	
}
