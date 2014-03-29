package powercraft.api.inventory;

import java.util.Map;

import javax.script.Invocable;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import powercraft.api.script.weasel.PC_IWeaselInventory;
import powercraft.api.script.weasel.grid.PC_IWeaselGridTileAddressable;
import xscript.runtime.nativemethod.XNativeClass;
import xscript.runtime.nativemethod.XNativeClass.XNativeMethod;
import xscript.runtime.nativemethod.XNativeClass.XParamSpecial;
import xscript.runtime.nativemethod.XNativeClass.XParamSpecial.XParamTypes;

@XNativeClass("weasel.inventory.Inventory")
public class PC_WeaselNativeInventoryInterface {
	public static final int INDEX_INVENTORY=0, OFFSET_INVENTORY=1;
	
	@XNativeMethod
	public static boolean swapStacks(@XParamSpecial(XParamTypes.USERDATA)PC_IWeaselGridTileAddressable anyTile, int address, String inventoryOne, int offsetOne, String inventoryTwo, int offsetTwo){
		PC_IWeaselGridTileAddressable targetTile = anyTile.getGrid().getTileByAddress(anyTile, address);
		if(!(targetTile instanceof PC_IWeaselInventory)){
			return false;
		}
		IInventory[] inventories = ((PC_IWeaselInventory)targetTile).getInventories();
		int[] one, two;
		IInventory invOne, invTwo;
		one = findInventoryForSlot(inventories, inventoryOne, offsetOne);
		two = findInventoryForSlot(inventories, inventoryTwo, offsetTwo);
		if(one[INDEX_INVENTORY]==-1 || one[OFFSET_INVENTORY]==-1 || two[INDEX_INVENTORY]==-1 || two[OFFSET_INVENTORY]==-1)
			return false;
		invOne = inventories[one[INDEX_INVENTORY]];
		invTwo = inventories[two[INDEX_INVENTORY]];
		ItemStack src1, src2;
		src1 = invOne.getStackInSlot(one[OFFSET_INVENTORY]);
		src2 = invTwo.getStackInSlot(two[OFFSET_INVENTORY]);
		if((src2!=null && !invOne.isItemValidForSlot(one[OFFSET_INVENTORY], src2)) || (src1!=null && !invTwo.isItemValidForSlot(two[OFFSET_INVENTORY], src1)))
			return false;
		
		invOne.setInventorySlotContents(one[OFFSET_INVENTORY], src2);
		invTwo.setInventorySlotContents(two[OFFSET_INVENTORY], src1);
		return true;
	}
	
	@XNativeMethod
	public static boolean combineStacks(@XParamSpecial(XParamTypes.USERDATA)PC_IWeaselGridTileAddressable anyTile, int address, String inventoryTarget, int offsetTarget, String inventoryFrom, int offsetFrom){
		PC_IWeaselGridTileAddressable targetTile = anyTile.getGrid().getTileByAddress(anyTile, address);
		if(!(targetTile instanceof PC_IWeaselInventory)){
			return false;
		}
		IInventory[] inventories = ((PC_IWeaselInventory)targetTile).getInventories();
		int[] to, from;
		IInventory src, target;
		to = findInventoryForSlot(inventories, inventoryTarget, offsetTarget);
		from = findInventoryForSlot(inventories, inventoryFrom, offsetFrom);
		if(from[INDEX_INVENTORY]==-1 || from[OFFSET_INVENTORY]==-1 || to[INDEX_INVENTORY]==-1 || to[OFFSET_INVENTORY]==-1)
			return false;
		src = inventories[from[INDEX_INVENTORY]];
		target = inventories[to[INDEX_INVENTORY]];
		ItemStack isTarget, isSrc;
		isTarget = target.getStackInSlot(to[OFFSET_INVENTORY]);
		isSrc = src.getStackInSlot(from[OFFSET_INVENTORY]);
		if(!itemStacksEqual(isTarget, isSrc))
			return false;
		int difference = isTarget.getMaxStackSize()-isTarget.stackSize;
		
		if(isSrc.stackSize<=difference){
			isTarget.stackSize+=isSrc.stackSize;
			isSrc.stackSize = 0;
			src.setInventorySlotContents(from[OFFSET_INVENTORY], null);
		}else{
			isTarget.stackSize+=difference;
			isSrc.stackSize-=difference;
		}
		return true;
	}
	
	@XNativeMethod
	public static int findSlotContainingItem(@XParamSpecial(XParamTypes.USERDATA)PC_IWeaselGridTileAddressable anyTile, int address, String inventory, Map<Object, Object> item){
		PC_IWeaselGridTileAddressable targetTile = anyTile.getGrid().getTileByAddress(anyTile, address);
		if(!(targetTile instanceof PC_IWeaselInventory)){
			return -1;
		}
		IInventory[] inventories = ((PC_IWeaselInventory)targetTile).getInventories();
		String itemName = (String)item.get("itemName");
		int meta = ((Integer)item.get("itemDamage")).intValue();
		
		return findItemSlot(inventories, inventory, itemName, meta);
	}
	
	@XNativeMethod
	public static Map<Object, Object> getItemStackAt(@XParamSpecial(XParamTypes.VM)Invocable vm, @XParamSpecial(XParamTypes.USERDATA)PC_IWeaselGridTileAddressable anyTile, int address, String inventory, int offset){
		PC_IWeaselGridTileAddressable targetTile = anyTile.getGrid().getTileByAddress(anyTile, address);
		if(!(targetTile instanceof PC_IWeaselInventory)){
			return null;
		}
		IInventory[] inventories = ((PC_IWeaselInventory)targetTile).getInventories();
		int pos[] = findInventoryForSlot(inventories, inventory, offset);
		if(pos[INDEX_INVENTORY]==-1 || pos[OFFSET_INVENTORY]==-1)
			return null;
		ItemStack is = inventories[pos[INDEX_INVENTORY]].getStackInSlot(pos[OFFSET_INVENTORY]);
		Map<Object, Object> target;
		try {
			target = (Map<Object, Object>) vm.invokeFunction("weasel.type.ItemStack");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		target.put("itemName", Item.itemRegistry.getNameForObject(is.getItem()));
		target.put("stackSize", is.stackSize);
		target.put("itemDamage", is.getItemDamage());
		return target;
	}
	
	/*	@XNativeMethod
	public static boolean combineStacks(@XParamSpecial(XParamTypes.USERDATA)PC_IWeaselGridTileAddressable anyTile, int address, String inventory, int offset){
		PC_IWeaselGridTileAddressable targetTile = anyTile.getGrid().getTileByAddress(anyTile, address);
		if(!(targetTile instanceof PC_IWeaselInventory)){
			return -1;
		}
		IInventory[] inventories = ((PC_IWeaselInventory)targetTile).getInventories();
		int pos[] = findInventoryForSlot(inventories, inventory, offset);
		if(pos[INDEX_INVENTORY]==-1 || pos[OFFSET_INVENTORY]==-1)
			return false;
	}*/
	
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
	
	private static int findItemSlot(IInventory[] inventories, String name, String itemName, int meta){
		Item itemWanted = (Item)Item.itemRegistry.getObject(itemName);
		int overAllCount=-1;
		ItemStack is;
		if(name.equalsIgnoreCase("global")){
			for(int i=0;i<inventories.length; i++){
				for(int slot=0; slot<inventories[i].getSizeInventory(); slot++, overAllCount++){
					if((is=inventories[i].getStackInSlot(slot))!=null && is.getItem()==itemWanted && (meta<0 || is.getItemDamage()==meta)){
						return overAllCount;
					}
				}
			}
		}else{
			int pos[] = findInventoryForSlot(inventories, name, 0);
			if(pos[INDEX_INVENTORY]==-1 || pos[OFFSET_INVENTORY]==-1)
				return -1;
			for(overAllCount=0; overAllCount<inventories[pos[INDEX_INVENTORY]].getSizeInventory(); overAllCount++){
				if((is=inventories[pos[INDEX_INVENTORY]].getStackInSlot(overAllCount))!=null && is.getItem()==itemWanted && (meta<0 || is.getItemDamage()==meta)){
					return overAllCount;
				}
			}
			
			
		}
		
		return -1;
	}
	
	private static boolean itemStacksEqual(ItemStack one, ItemStack two){
		return ItemStack.areItemStacksEqual(one, two) && ItemStack.areItemStackTagsEqual(one, two);
	}
}
