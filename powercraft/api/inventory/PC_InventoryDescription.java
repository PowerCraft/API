package powercraft.api.inventory;


public class PC_InventoryDescription {
	public final int firstIndex, lastIndex;
	public final String inventoryName;
	
	public PC_InventoryDescription(int index, String name){
		this(index, index, name);
	}
	
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
	
	public boolean isInRange(int index, boolean globally){
		if(globally)
			return !(index<this.firstIndex || index>this.lastIndex);
		return !(index<0 || index>globalToLocal(this.lastIndex));
	}
	
	
	public static PC_InventoryDescription byName(String name, PC_InventoryDescription... array){
		for(PC_InventoryDescription desc:array){
			if(name.equalsIgnoreCase(desc.inventoryName)) return desc;
		}
		return null;
	}
	
	public static PC_InventoryDescription byIndex(int index, PC_InventoryDescription... array){
		for(PC_InventoryDescription desc:array){
			if(desc.isInRange(index, true)) return desc;
		}
		return null;
	}
}
