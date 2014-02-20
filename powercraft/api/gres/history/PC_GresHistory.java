package powercraft.api.gres.history;

public class PC_GresHistory {

	private final PC_IGresHistoryEntry[] historyEntries;
	private int min;
	private int max;
	private int pos;
	private PC_IGresHistoryEntry newest;
	
	public PC_GresHistory(int size){
		historyEntries = new PC_IGresHistoryEntry[size];
	}
	
	public void addHistoryEntry(PC_IGresHistoryEntry historyEntry){
		if(newest!=null){
			if(newest.tryToMerge(historyEntry))
				return;
		}
		newest = historyEntry;
		historyEntries[pos] = historyEntry;
		pos = (pos+1)%historyEntries.length;
		max = pos;
		if(min==max){
			min = (min+1)%historyEntries.length;
		}
	}
	
	public void redo(){
		if(pos!=max){
			newest = null;
			historyEntries[pos].doAction();
			pos = (pos+1)%historyEntries.length;
		}
	}
	
	public void undo(){
		if(pos!=min){
			newest = null;
			if(pos<=0)
				pos = historyEntries.length;
			pos = pos-1;
			historyEntries[pos].undoAction();
		}
	}
	
}
