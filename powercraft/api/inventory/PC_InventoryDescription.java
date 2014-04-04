package powercraft.api.inventory;

import powercraft.traffic.entity.PCtf_EntityMiner.INVENTORIES;

public class PC_InventoryDescription {
	public final int firstIndex, lastIndex;
	public final String inventoryName;
	public PC_InventoryDescription(int start, int lastIndex, String name){
		firstIndex=start;
		this.lastIndex=lastIndex;
		inventoryName = name;
	}
	
	public int offset(int offset){
		
		return firstIndex+offset;
	}
	
	public int globalToLocal(int global){
		return global-firstIndex;
	}
}
