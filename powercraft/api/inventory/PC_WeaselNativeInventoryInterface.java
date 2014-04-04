package powercraft.api.inventory;

import java.util.Map;

import javax.script.Invocable;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import powercraft.api.script.weasel.grid.PC_IWeaselGridTileAddressable;
import xscript.runtime.nativemethod.XNativeClass;
import xscript.runtime.nativemethod.XNativeClass.XNativeMethod;
import xscript.runtime.nativemethod.XNativeClass.XParamSpecial;
import xscript.runtime.nativemethod.XNativeClass.XParamSpecial.XParamTypes;
import xscript.runtime.nativemethod.XNativeClass.XType;

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
		one = findInventoryForSlot(inventories, inventoryOne, offsetOne);
		two = findInventoryForSlot(inventories, inventoryTwo, offsetTwo);
		if(one[INDEX_INVENTORY]==-1 || one[OFFSET_INVENTORY]==-1 || two[INDEX_INVENTORY]==-1 || two[OFFSET_INVENTORY]==-1)
			return false;
		
		IInventory invOne = inventories[one[INDEX_INVENTORY]];
		IInventory invTwo = inventories[two[INDEX_INVENTORY]];
		ItemStack src1 = invOne.getStackInSlot(one[OFFSET_INVENTORY]);
		ItemStack src2 = invTwo.getStackInSlot(two[OFFSET_INVENTORY]);
		
		boolean equalStacks;
		
		if(src1==null && src2==null || ((equalStacks=itemStacksEqual(src1, src2)) && src1.stackSize==src2.stackSize))
			return true;
		
		int difFrom1To2 = canMoveStackTo(inventories, one, two);
		int difFrom2To1 = canMoveStackTo(inventories, two, one);
		
		if(src1 == null){
			combineStack(inventories, two, one, difFrom2To1);
			return true;
		}else if(src2==null){
			combineStack(inventories, one, two, difFrom1To2);
			return true;
		}else{
			if(equalStacks){
				if(src1.stackSize>src2.stackSize){
					combineStack(inventories, one, two, Math.min(difFrom1To2, src1.stackSize-src2.stackSize));
					return true;
				}else{
					combineStack(inventories, two, one, Math.min(difFrom2To1, src2.stackSize-src1.stackSize));
					return true;
				}
			}else{
				if(Math.abs(difFrom2To1)>=src2.stackSize && Math.abs(difFrom1To2)>=src1.stackSize){
					invOne.setInventorySlotContents(one[OFFSET_INVENTORY], src2);
					invTwo.setInventorySlotContents(two[OFFSET_INVENTORY], src1);
					return true;
				}else{
					return false;
				}
			}
		}
	}
	
	@XNativeMethod
	public static boolean combineStacks(@XParamSpecial(XParamTypes.USERDATA)PC_IWeaselGridTileAddressable anyTile, int address, String inventoryTarget, int offsetTarget, String inventoryFrom, int offsetFrom){
		PC_IWeaselGridTileAddressable targetTile = anyTile.getGrid().getTileByAddress(anyTile, address);
		if(!(targetTile instanceof PC_IWeaselInventory)){
			return false;
		}
		IInventory[] inventories = ((PC_IWeaselInventory)targetTile).getInventories();
		int[] to, from;
		to = findInventoryForSlot(inventories, inventoryTarget, offsetTarget);
		from = findInventoryForSlot(inventories, inventoryFrom, offsetFrom);
		if(from[INDEX_INVENTORY]==-1 || from[OFFSET_INVENTORY]==-1 || to[INDEX_INVENTORY]==-1 || to[OFFSET_INVENTORY]==-1)
			return false;
		
		int difference = canMoveStackTo(inventories, from, to);
		if(difference<=0)
			return false;
		combineStack(inventories, from, to, difference);
		return true;
	}
	
	@XNativeMethod
	public static int findSlotContainingItem(@XParamSpecial(XParamTypes.USERDATA)PC_IWeaselGridTileAddressable anyTile, int address, String inventory, @XType("weasel.type.ItemStack")Map<Object, Object> item){
		PC_IWeaselGridTileAddressable targetTile = anyTile.getGrid().getTileByAddress(anyTile, address);
		if(!(targetTile instanceof PC_IInventory && targetTile instanceof PC_IWeaselInventory)){
			return -1;
		}
		String itemName = (String)item.get("itemName");
		int meta = ((Integer)item.get("itemDamage")).intValue();
		
		return findItemSlot(inventories, inventory, itemName, meta);
	}
	
	@XNativeMethod
	public static @XType("weasel.type.ItemStack")Map<Object, Object> getItemStackAt(@XParamSpecial(XParamTypes.VM)Invocable vm, @XParamSpecial(XParamTypes.USERDATA)PC_IWeaselGridTileAddressable anyTile, int address, String inventory, int offset){
		PC_IWeaselGridTileAddressable targetTile = anyTile.getGrid().getTileByAddress(anyTile, address);
		if(!(targetTile instanceof PC_IInventory && targetTile instanceof PC_IWeaselInventory))
			return null;
		int realSlot = ((PC_IWeaselInventory)targetTile).getRealSlot(inventory, offset);
		if(realSlot==-1)
			return null;
		ItemStack is = ((PC_IInventory)targetTile).getStackInSlot(realSlot);
		if(is==null) return null;
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
	
	private static int findItemSlot(IInventory inventory, String name, String itemName, int meta){
		Item itemWanted = (Item)Item.itemRegistry.getObject(itemName);
		int overAllCount=-1;
		ItemStack is;
		if(name.equalsIgnoreCase("global")){
			for(int slot=0; slot<inventory.getSizeInventory(); slot++, overAllCount++){
				if((is=inventory.getStackInSlot(slot))!=null && is.getItem()==itemWanted && (meta<0 || is.getItemDamage()==meta)){
					return overAllCount;
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
	
	private static int getMaxStackSize(IInventory inventory, int realPos, boolean ignoreCurrent){
		int tmp;
		ItemStack isTarget = inventory.getStackInSlot(realPos);
		if(inventory instanceof PC_IInventory){
			tmp=((PC_IInventory) inventory).getSlotStackLimit(pos[OFFSET_INVENTORY]);
		}else{
			tmp=inventory.getInventoryStackLimit();
		}
		
		return isTarget==null || ignoreCurrent?tmp:Math.min(isTarget.getMaxStackSize(), tmp);
	}
	
	private static boolean canDragStack(IInventory[] inventories, int[] from){
		if(inventories[from[INDEX_INVENTORY]] instanceof PC_IInventory){
			return ((PC_IInventory) inventories[from[INDEX_INVENTORY]]).canBeDragged(from[OFFSET_INVENTORY]);
		}
		return true;
	}
	
	//>0-> remaining place; =0->impossible or unnecessary; <0 ->like >0 but slot already filled with something else
	private static int remainingPlace(IInventory inventory, int realPos, ItemStack stack){
		ItemStack target = inventory.getStackInSlot(realPos);
		if(stack==null){
			int max = getMaxStackSize(inventory, realPos, true);
			if(target==null)
				return max;
			else
				return -max;
		}else{
			if(target==null){
				return inventory.isItemValidForSlot(realPos, stack)?Math.min(stack.getMaxStackSize(), getMaxStackSize(inventory, realPos, true)):0;
			}else{
				if(itemStacksEqual(stack, target)){
					return getMaxStackSize(inventory, realPos, false)-target.stackSize;
				}else{
					return -Math.min(stack.getMaxStackSize(), getMaxStackSize(inventory, realPos, true));
				}
			}
		}
	}
	
	//>0-> remaining place; =0->impossible or unnecessary; <0 ->like >0 but slot already filled with something else
	private static int canMoveStackTo(IInventory[] inventories, int[] from, int[] to){
		return canDragStack(inventories, from)?remainingPlace(inventories, to, inventories[from[INDEX_INVENTORY]].getStackInSlot(from[OFFSET_INVENTORY])):0;
	}
	
	private static ItemStack splitStack(ItemStack is, int stackSizeNew){
		return is.splitStack(stackSizeNew);
	}
	
	private static boolean combineStack(IInventory[] inventories, int[] from, int[] to, int amount){
		if(amount==0)
			return true;
		ItemStack src = inventories[from[INDEX_INVENTORY]].getStackInSlot(from[OFFSET_INVENTORY]);
		ItemStack target = inventories[to[INDEX_INVENTORY]].getStackInSlot(to[OFFSET_INVENTORY]);
		if(src==null)
			return true;
		if(target==null){
			ItemStack stack = src;
			if(amount<src.stackSize){
				stack = splitStack(stack, amount);
			}else{
				inventories[from[INDEX_INVENTORY]].setInventorySlotContents(from[OFFSET_INVENTORY], null);
			}
			inventories[to[INDEX_INVENTORY]].setInventorySlotContents(to[OFFSET_INVENTORY], stack);
			return true;
		}else{
			if(!itemStacksEqual(src, target)){
				return false;
			}
			if(src.stackSize<amount)
				amount=src.stackSize;
			target.stackSize+=amount;
			if(amount<src.stackSize){
				src.stackSize-=amount;
			}else{
				inventories[from[INDEX_INVENTORY]].setInventorySlotContents(from[OFFSET_INVENTORY], null);
			}
			return true;
		}
			
	}
}
