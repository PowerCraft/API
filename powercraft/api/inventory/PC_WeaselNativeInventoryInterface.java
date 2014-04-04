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
	
	@XNativeMethod
	public static boolean swapStacks(@XParamSpecial(XParamTypes.USERDATA)PC_IWeaselGridTileAddressable anyTile, int address, String inventoryOne, int offsetOne, String inventoryTwo, int offsetTwo){
		PC_IWeaselGridTileAddressable targetTile = anyTile.getGrid().getTileByAddress(anyTile, address);
		if(!(targetTile instanceof IInventory && targetTile instanceof PC_IWeaselInventory)){
			return false;
		}
		PC_InventoryDescription one = ((PC_IWeaselInventory)targetTile).getInventory(inventoryOne);
		PC_InventoryDescription two = ((PC_IWeaselInventory)targetTile).getInventory(inventoryTwo);

		IInventory tmp = ((IInventory)targetTile);
		
		int realOffsetOne = one.offset(offsetOne);
		int realOffsetTwo = two.offset(offsetTwo);
		
		ItemStack src1 = tmp.getStackInSlot(realOffsetOne);
		ItemStack src2 = tmp.getStackInSlot(realOffsetTwo);
		
		boolean equalStacks;
		
		if(src1==null && src2==null || ((equalStacks=itemStacksEqual(src1, src2)) && src1.stackSize==src2.stackSize))
			return true;
		
		int difFrom1To2 = canMoveStackTo(tmp, realOffsetOne, realOffsetTwo);
		int difFrom2To1 = canMoveStackTo(tmp, realOffsetTwo, realOffsetOne);
		
		if(src1 == null){
			combineStack(tmp, realOffsetTwo, realOffsetOne, difFrom2To1);
			return true;
		}else if(src2==null){
			combineStack(tmp, realOffsetOne, realOffsetTwo, difFrom1To2);
			return true;
		}else{
			if(equalStacks){
				if(src1.stackSize>src2.stackSize){
					combineStack(tmp, realOffsetOne, realOffsetTwo, Math.min(difFrom1To2, src1.stackSize-src2.stackSize));
					return true;
				}else{
					combineStack(tmp, realOffsetTwo, realOffsetOne, Math.min(difFrom2To1, src2.stackSize-src1.stackSize));
					return true;
				}
			}else{
				if(Math.abs(difFrom2To1)>=src2.stackSize && Math.abs(difFrom1To2)>=src1.stackSize){
					tmp.setInventorySlotContents(realOffsetOne, src2);
					tmp.setInventorySlotContents(realOffsetTwo, src1);
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
		if(!(targetTile instanceof IInventory && targetTile instanceof PC_IWeaselInventory)){
			return false;
		}
		PC_InventoryDescription from = ((PC_IWeaselInventory)targetTile).getInventory(inventoryFrom);
		PC_InventoryDescription to = ((PC_IWeaselInventory)targetTile).getInventory(inventoryTarget);
		int difference = canMoveStackTo((IInventory)targetTile, from.offset(offsetFrom), to.offset(offsetTarget));
		if(difference<=0)
			return false;
		combineStack((IInventory)targetTile, from.offset(offsetFrom), to.offset(offsetTarget), difference);
		return true;
	}
	
	@XNativeMethod
	public static int findSlotContainingItem(@XParamSpecial(XParamTypes.USERDATA)PC_IWeaselGridTileAddressable anyTile, int address, String inventory, @XType("weasel.type.ItemStack")Map<Object, Object> item){
		PC_IWeaselGridTileAddressable targetTile = anyTile.getGrid().getTileByAddress(anyTile, address);
		if(!(targetTile instanceof IInventory && targetTile instanceof PC_IWeaselInventory)){
			return -1;
		}
		PC_InventoryDescription inv = ((PC_IWeaselInventory)targetTile).getInventory(inventory);
		String itemName = (String)item.get("itemName");
		int meta = ((Integer)item.get("itemDamage")).intValue();
		
		return findItemSlot((IInventory)targetTile, inv, itemName, meta);
	}
	
	@XNativeMethod
	public static @XType("weasel.type.ItemStack")Map<Object, Object> getItemStackAt(@XParamSpecial(XParamTypes.VM)Invocable vm, @XParamSpecial(XParamTypes.USERDATA)PC_IWeaselGridTileAddressable anyTile, int address, String inventory, int offset){
		PC_IWeaselGridTileAddressable targetTile = anyTile.getGrid().getTileByAddress(anyTile, address);
		if(!(targetTile instanceof IInventory && targetTile instanceof PC_IWeaselInventory))
			return null;
		PC_InventoryDescription inv = ((PC_IWeaselInventory)targetTile).getInventory(inventory);
		if(inv==null)
			return null;
		ItemStack is = ((IInventory)targetTile).getStackInSlot(inv.offset(offset));
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
	
	private static int findItemSlot(IInventory inventory, PC_InventoryDescription inv, String itemName, int meta){
		Item itemWanted = (Item)Item.itemRegistry.getObject(itemName);
		ItemStack is;
		for(int slot=inv.firstIndex; slot<=inv.lastIndex; slot++){
			if((is=inventory.getStackInSlot(slot))!=null && is.getItem()==itemWanted && (meta<0 || is.getItemDamage()==meta)){
				return inv.globalToLocal(slot);
			}
		}
		
		return -1;
	}
	
	private static boolean itemStacksEqual(ItemStack one, ItemStack two){
		return ItemStack.areItemStacksEqual(one, two) && ItemStack.areItemStackTagsEqual(one, two);
	}
	
	private static int getMaxStackSize(IInventory inventory, int realPos, boolean ignoreCurrent){
		ItemStack isTarget = inventory.getStackInSlot(realPos);
		if(isTarget==null || ignoreCurrent){
			return PC_InventoryUtils.getSlotStackLimit(inventory, realPos);
		}
		return PC_InventoryUtils.getMaxStackSize(isTarget, inventory, realPos);
	}
	
	private static boolean canDragStack(IInventory inventory, int realFrom){
		if(inventory instanceof PC_IInventory){
			return ((PC_IInventory) inventory).canBeDragged(realFrom);
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
			return -max;
		}
		if(target==null){
			return inventory.isItemValidForSlot(realPos, stack)?Math.min(stack.getMaxStackSize(), getMaxStackSize(inventory, realPos, true)):0;
		}
		if(itemStacksEqual(stack, target)){
			return getMaxStackSize(inventory, realPos, false)-target.stackSize;
		}
		return -Math.min(stack.getMaxStackSize(), getMaxStackSize(inventory, realPos, true));
	}
	
	//>0-> remaining place; =0->impossible or unnecessary; <0 ->like >0 but slot already filled with something else
	private static int canMoveStackTo(IInventory inventory, int realFrom, int realTo){
		return canDragStack(inventory, realFrom)?remainingPlace(inventory, realTo, inventory.getStackInSlot(realFrom)):0;
	}
	
	private static ItemStack splitStack(ItemStack is, int stackSizeNew){
		return is.splitStack(stackSizeNew);
	}
	
	private static boolean combineStack(IInventory inventory, int from, int to, int amount){
		if(amount==0)
			return true;
		ItemStack src = inventory.getStackInSlot(from);
		ItemStack target = inventory.getStackInSlot(to);
		if(src==null)
			return true;
		if(target==null){
			ItemStack stack = src;
			if(amount<src.stackSize){
				stack = splitStack(stack, amount);
			}else{
				inventory.setInventorySlotContents(from, null);
			}
			inventory.setInventorySlotContents(to, stack);
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
				inventory.setInventorySlotContents(from, null);
			}
			return true;
		}
			
	}
}
