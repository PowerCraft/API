package powercraft.api.gres.events;

public interface PC_IGresEventListenerEx extends PC_IGresEventListener {

	public Class<? extends PC_GresEvent>[] getHandelableEvents();
	
}
