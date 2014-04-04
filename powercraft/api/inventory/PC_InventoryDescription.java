package powercraft.api.inventory;


public class PC_InventoryDescription {
	public final int firstIndex, lastIndex;
	public final String inventoryName;
	public PC_InventoryDescription(int start, int lastIndex, String name){
		this.firstIndex=start;
		this.lastIndex=lastIndex;
		this.inventoryName = name;
	}
	
	public int offset(int offset){
		
		return this.firstIndex+offset;
	}
	
	public int globalToLocal(int global){
		return global-this.firstIndex;
	}
}
