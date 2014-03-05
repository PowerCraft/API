package powercraft.api.gres.events;

import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.history.PC_GresHistory;


public class PC_GresMouseEventResult extends PC_GresEventResult {
	
	private final PC_Vec2I mouse;
	private final int buttons;
	private final PC_GresHistory history;
	
	protected PC_GresMouseEventResult(PC_GresComponent component, PC_Vec2I mouse, int buttons, boolean result, PC_GresHistory history) {
		super(component, result);
		this.mouse = new PC_Vec2I(mouse);
		this.buttons = buttons;
		this.history = history;
	}
	
	public PC_Vec2I getMouse(){
		return this.mouse;
	}
	
	public int getButtonState(){
		return this.buttons;
	}
	
	public PC_GresHistory getHistory(){
		return this.history;
	}
	
}
