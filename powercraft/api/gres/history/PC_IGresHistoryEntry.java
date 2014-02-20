package powercraft.api.gres.history;

public interface PC_IGresHistoryEntry {

	public void doAction();
	
	public void undoAction();

	public boolean tryToMerge(PC_IGresHistoryEntry historyEntry);
	
}
