package powercraft.api.gres.events;

import powercraft.api.gres.PC_GresComponent;

public class PC_GresKeyEvent extends PC_GresConsumeableEvent {

	private final char key;
	private final int keyCode;
	
	public PC_GresKeyEvent(PC_GresComponent component, char key, int keyCode){
		super(component);
		this.key = key;
		this.keyCode = keyCode;
	}
	
	public char getKey(){
		return key;
	}
	
	public int getKeyCode(){
		return keyCode;
	}
	
}
