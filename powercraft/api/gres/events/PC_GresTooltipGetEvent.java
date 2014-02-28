package powercraft.api.gres.events;

import java.util.List;

import powercraft.api.PC_ImmutableList;
import powercraft.api.gres.PC_GresComponent;

public class PC_GresTooltipGetEvent extends PC_GresConsumeableEvent {

	private List<String> tooltip;
	
	public PC_GresTooltipGetEvent(PC_GresComponent component, List<String> tooltip){
		super(component);
		this.tooltip = tooltip;
	}
	
	public List<String> getTooltip(){
		return tooltip==null?null:new PC_ImmutableList<String>(tooltip);
	}
	
	public void setTooltip(List<String> tooltip){
		this.tooltip = tooltip;
	}
	
}
