package powercraft.api.inventory;

import java.util.Iterator;
import java.util.ListIterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.PC_INBT;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Field.Flag;

public class PC_Inventory implements IInventory, Iterable<ItemStack>, PC_INBT {

	public static final int NOTUSABLEBYPLAYER = 1;
	public static final int SIDEINSERTABLE = 2;
	public static final int SIDEEXTRACTABLE = 4;
	public static final int DROPNOSTACKS = 8;
	
	private final String name;
	private final ItemStack[] inventoryContents;
	private final int stackLimit;
	private final int flags;
	private IInventory parentInventory;
	
	public PC_Inventory(NBTTagCompound tag, Flag flag){
		name = tag.getString("name");
		stackLimit = tag.getInteger("stackLimit");
		flags = tag.getInteger("flags");
		inventoryContents = new ItemStack[tag.getInteger("size")];
		PC_InventoryUtils.loadInventoryFromNBT(this, tag, "inv");
	}
	
	public PC_Inventory(String name, int size, int stackLimit, int flags){
		this.name = name;
		inventoryContents = new ItemStack[size];
		this.stackLimit = stackLimit;
		this.flags = flags;
	}
	
	public void setParentInventory(IInventory parentInventory){
		this.parentInventory = parentInventory;
	}
	
	public IInventory getParentInventory(){
		return parentInventory;
	}
	
	@Override
	public int getSizeInventory() {
		return inventoryContents.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventoryContents[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (inventoryContents[i] != null) {
			ItemStack itemstack;
			if (inventoryContents[i].stackSize <= j) {
				itemstack = this.inventoryContents[i];
				inventoryContents[i] = null;
				markDirty();
				return itemstack;
			} 
			itemstack = this.inventoryContents[i].splitStack(j);
			if (this.inventoryContents[i].stackSize == 0) {
				this.inventoryContents[i] = null;
			}
			markDirty();
			return itemstack;
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (this.inventoryContents[i] != null) {
			ItemStack itemstack = this.inventoryContents[i];
			this.inventoryContents[i] = null;
			return itemstack;
		} 
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventoryContents[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
		markDirty();
	}

	@Override
	public String getInventoryName() {
		return name;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return stackLimit;
	}

	@Override
	public void markDirty() {
		if(parentInventory!=null)
			parentInventory.markDirty();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		if((flags & NOTUSABLEBYPLAYER)!=0){
			return false;
		}
		if(parentInventory!=null)
			return parentInventory.isUseableByPlayer(entityplayer);
		return true;
	}

	@Override
	public void openInventory() {
		if(parentInventory!=null)
			parentInventory.openInventory();
	}

	@Override
	public void closeInventory() {
		if(parentInventory!=null)
			parentInventory.closeInventory();
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}
	
	public boolean canInsertItem(int i, ItemStack itemstack) {
		if(!isItemValidForSlot(i, itemstack))
			return false;
		return (flags & SIDEINSERTABLE)!=0;
	}

	public boolean canExtractItem(int i, ItemStack itemstack) {
		if(!isItemValidForSlot(i, itemstack))
			return false;
		return (flags & SIDEEXTRACTABLE)!=0;
	}

	public boolean canDropStacks() {
		return (flags & DROPNOSTACKS)==0;
	}

	@Override
	public Iterator<ItemStack> iterator() {
		return listIterator();
	}
	
	public ListIterator<ItemStack> listIterator(){
		return listIterator(0);
	}
	
	public ListIterator<ItemStack> listIterator(int index){
		if(index<0 ||index>=getSizeInventory())
			throw new IndexOutOfBoundsException();
		return new InventoryIterator(index-1);
	}
	
	private class InventoryIterator implements ListIterator<ItemStack>{

		private int pos;
		
		private InventoryIterator(int pos){
			this.pos = pos;
		}
		
		@Override
		public void add(ItemStack e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasNext() {
			return getSizeInventory()<pos-1;
		}

		@Override
		public boolean hasPrevious() {
			return pos>=0;
		}

		@Override
		public ItemStack next() {
			if(!hasNext())
				throw new IndexOutOfBoundsException();
			return getStackInSlot(++pos);
		}

		@Override
		public int nextIndex() {
			return pos+1;
		}

		@Override
		public ItemStack previous() {
			if(!hasPrevious())
				throw new IndexOutOfBoundsException();
			return getStackInSlot(--pos);
		}

		@Override
		public int previousIndex() {
			return pos-1;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void set(ItemStack itemstack) {
			if(isItemValidForSlot(pos, itemstack))
				setInventorySlotContents(pos, itemstack);
		}
		
	}

	@Override
	public void saveToNBT(NBTTagCompound tag, Flag flag) {
		tag.setString("name", name);
		tag.setInteger("stackLimit", stackLimit);
		tag.setInteger("flags", flags);
		tag.setInteger("size", inventoryContents.length);
		PC_InventoryUtils.saveInventoryToNBT(this, tag, "inv");
	}
	
}
