package powercraft.api.inventory;

import powercraft.api.script.weasel.PC_IWeaselInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import xscript.runtime.nativemethod.XNativeClass;
import xscript.runtime.nativemethod.XNativeClass.XNativeMethod;
import xscript.runtime.nativemethod.XNativeClass.XParamSpecial;
import xscript.runtime.nativemethod.XNativeClass.XParamSpecial.XParamTypes;

@XNativeClass("weasel.inventory.Inventory")
public class PC_WeaselNativeInventoryInterface {
	public static final int INDEX_INVENTORY=0, OFFSET_INVENTORY=1;
	
	@XNativeMethod
	public static boolean swapStacks(@XParamSpecial(XParamTypes.USERDATA)PC_IWeaselInventory inv, int address, String inventoryFrom, int offsetFrom, String inventoryTo, int offsetTo){
		System.out.println("entered");
		IInventory[] inventories = inv.getInventories(address);
		int[] from, to;
		IInventory src, target;
		from = findInventoryForSlot(inventories, inventoryFrom, offsetFrom);
		to = findInventoryForSlot(inventories, inventoryTo, offsetTo);
		System.out.println("going to check for invalid id");
		if(from[INDEX_INVENTORY]==-1 || from[OFFSET_INVENTORY]==-1 || to[INDEX_INVENTORY]==-1 || to[OFFSET_INVENTORY]==-1)
			return false;
		src = inventories[from[INDEX_INVENTORY]];
		target = inventories[to[INDEX_INVENTORY]];
		ItemStack src1, src2;
		src1 = src.getStackInSlot(from[OFFSET_INVENTORY]);
		src2 = target.getStackInSlot(to[OFFSET_INVENTORY]);
		System.out.println("src1:" + src1 + ", src2:" + src2);
		if((src2!=null && !src.isItemValidForSlot(from[OFFSET_INVENTORY], src2)) || (src1!=null && !target.isItemValidForSlot(to[OFFSET_INVENTORY], src1)))
			return false;
		System.out.println("behind last check");
		
		src.setInventorySlotContents(from[OFFSET_INVENTORY], src2);
		target.setInventorySlotContents(to[OFFSET_INVENTORY], src1);
		
		/*
		if(src2!=null) src.setInventorySlotContents(from[OFFSET_INVENTORY], src2);
		if(src1!=null) target.setInventorySlotContents(to[OFFSET_INVENTORY], src1);
		*/
		System.out.println("ALL WORKED!!!");
		return true;
	}
	

	
	private static int[] findInventoryForSlot(IInventory[] inventories, String name, int offset) {
		if(inventories==null || inventories.length==0 || offset<0) return new int[]{-1, -1};
		if(inventories.length==1) return new int[]{0, offset>=inventories[0].getSizeInventory()?-1:offset};
		IInventory inv;
		int count=0, tmp=0;
		for(int i=0; i<inventories.length; i++){
			inv = inventories[i];
			if(name.equalsIgnoreCase("global")){
				if(inv instanceof PC_InventoryMask){
					PC_InventoryMask mask = (PC_InventoryMask) inv;
					if(offset>=mask.inventoryStart && offset<=mask.inventoryLastIndex){
						return new int[]{i, offset-mask.inventoryStart};
					}
				}else{
					if(offset>=count && offset<count+(tmp=inv.getSizeInventory())){
						return new int[]{i, offset-count};
					}else{
						count+=tmp;
					}
				}
			}else if(name.equalsIgnoreCase(inv.getInventoryName())){
				return new int[]{i, offset>=inventories[i].getSizeInventory()?-1:offset};
			}else{
				continue;
			}
		}
		return new int[]{-1, -1};
	}
}
