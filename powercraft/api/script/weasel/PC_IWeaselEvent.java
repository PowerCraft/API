package powercraft.api.script.weasel;

public interface PC_IWeaselEvent {
	
	public String getEventName();
	
	public String getEntryClass();
	
	public String getEntryMethod();
	
	public long[] getParams();
	
}
