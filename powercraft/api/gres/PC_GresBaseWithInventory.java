package powercraft.api.gres;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import powercraft.api.PC_Utils;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.entity.PC_Entity;
import powercraft.api.gres.slot.PC_Slot;
import powercraft.api.gres.slot.PC_SlotPhantom;
import powercraft.api.inventory.PC_IInventory;
import powercraft.api.inventory.PC_IInventorySizeOverrider;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.network.packet.PC_PacketSetSlot;
import powercraft.api.network.packet.PC_PacketWindowItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class PC_GresBaseWithInventory extends Container implements PC_IInventory, PC_IInventorySizeOverrider {

	public static boolean SETTING_OK;

	protected final EntityPlayer player;

	protected final Slot[][] inventoryPlayerUpper = new Slot[9][3];

	protected final Slot[] inventoryPlayerLower = new Slot[9];

	protected final IInventory inventory;

	protected Slot[] invSlots;

	private int dragType = -1;
    private int dragState;
    private final Set<Slot> dragSlots = new HashSet<Slot>();

	public PC_GresBaseWithInventory(EntityPlayer player, IInventory inventory) {

		SETTING_OK = PC_Utils.isServer();
		
		this.player = player;

		this.inventory = inventory;

		inventory.openInventory();
		
		if (inventory instanceof PC_TileEntity) {
			((PC_TileEntity) inventory).openContainer(this);
		}
		
		if (inventory instanceof PC_Entity) {
			((PC_Entity) inventory).openContainer(this);
		}

		if (player != null) {
			for (int i = 0; i < 9; i++) {
				this.inventoryPlayerLower[i] = new PC_Slot(player.inventory, i);
				addSlotToContainer(this.inventoryPlayerLower[i]);
			}

			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 3; j++) {
					this.inventoryPlayerUpper[i][j] = new PC_Slot(player.inventory, i + j * 9 + 9);
					addSlotToContainer(this.inventoryPlayerUpper[i][j]);
				}
			}
		}

		Slot[] sl = getAllSlots();
		if (sl != null) {
			for (Slot s : sl) {
				addSlotToContainer(s);
			}
		}

	}


	protected Slot[] getAllSlots() {

		this.invSlots = new Slot[this.inventory.getSizeInventory()];
		for (int i = 0; i < this.invSlots.length; i++) {
			this.invSlots[i] = createSlot(i);
		}
		return this.invSlots;
	}

	protected PC_Slot createSlot(int i){
		return new PC_Slot(this.inventory, i);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {

		return this.inventory instanceof TileEntity ? ((TileEntity) this.inventory).getDistanceFrom(entityplayer.posX, entityplayer.posY, entityplayer.posZ) < 64
				: true;
	}


	public void sendProgressBarUpdate(int key, int value) {

		if (this.player instanceof EntityPlayerMP) {
			((EntityPlayerMP) this.player).sendProgressBarUpdate(this, key, value);
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer) {

		super.onContainerClosed(par1EntityPlayer);
		closeInventory();
		if (this.inventory instanceof PC_TileEntity) {
			((PC_TileEntity) this.inventory).closeContainer(this);
		}
		if (this.inventory instanceof PC_Entity) {
			((PC_Entity) this.inventory).closeContainer(this);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addCraftingToCrafters(ICrafting crafting) {

		if(crafting instanceof EntityPlayerMP){
			if (this.crafters.contains(crafting)){
	            throw new IllegalArgumentException("Listener already listening");
	        }
			this.crafters.add(crafting);
			PC_PacketHandler.sendTo(new PC_PacketWindowItems(this.windowId, getInventory()), (EntityPlayerMP)crafting);
			PC_PacketHandler.sendTo(new PC_PacketSetSlot(-1, -1, ((EntityPlayerMP)crafting).inventory.getItemStack()), (EntityPlayerMP)crafting);
			crafting.sendContainerAndContentsToPlayer(this, this.getInventory());
			this.detectAndSendChanges();
		}else{
			super.addCraftingToCrafters(crafting);
		}
		if (this.inventory instanceof PC_TileEntity) {
			((PC_TileEntity) this.inventory).sendProgressBarUpdates();
		}
		if (this.inventory instanceof PC_Entity) {
			((PC_Entity) this.inventory).sendProgressBarUpdates();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void detectAndSendChanges(){
        for (int i = 0; i < this.inventorySlots.size(); ++i){
            ItemStack itemstack = ((Slot)this.inventorySlots.get(i)).getStack();
            ItemStack itemstack1 = (ItemStack)this.inventoryItemStacks.get(i);

            if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)){
                itemstack1 = itemstack == null ? null : itemstack.copy();
                this.inventoryItemStacks.set(i, itemstack1);

                for (int j = 0; j < this.crafters.size(); ++j){
                	sendSlotContentsTo((ICrafting) this.crafters.get(j), i, itemstack1);
                }
            }
        }
    }
	
	private void sendSlotContentsTo(ICrafting crafting, int i, ItemStack itemstack){
		if(crafting instanceof EntityPlayerMP){
			if (!((EntityPlayerMP)crafting).isChangingQuantityOnly){
				PC_PacketHandler.sendTo(new PC_PacketSetSlot(this.windowId, i, itemstack), (EntityPlayerMP)crafting);
            }
		}else{
			crafting.sendSlotContents(this, i, itemstack);
		}
	}
	
	@Override
	public void putStackInSlot(int slot, ItemStack itemStack){
		if(SETTING_OK){
			super.putStackInSlot(slot, itemStack);
		}
    }

    @Override
    @SideOnly(Side.CLIENT)
	public void putStacksInSlots(ItemStack[] itemStacks){
    	if(SETTING_OK){
    		super.putStacksInSlots(itemStacks);
    	}
    }
	
	@Override
	public boolean canDragIntoSlot(Slot slot){
		if(slot instanceof PC_Slot){
			return ((PC_Slot)slot).canDragIntoSlot();
		}
        return true;
    }


	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		Slot slot = (Slot)this.inventorySlots.get(par2);
		ItemStack itemstackOut = null;
        if (slot != null && slot.getHasStack()){
            ItemStack itemstack = slot.getStack();
            itemstackOut = itemstack.copy();

            if (par2 < 4 * 9){
                this.mergeItemStack(itemstack, 4 * 9, this.inventorySlots.size(), true);
            }else{
            	this.mergeItemStack(itemstack, 0, 4 * 9, false);
            }

            if (itemstack.stackSize == 0){
                slot.putStack((ItemStack)null);
            }else{
                slot.onSlotChanged();
            }
            if(itemstack.stackSize == itemstackOut.stackSize)
            	return null;
        }

        return itemstackOut;
	}
	
	@Override
	protected boolean mergeItemStack(ItemStack itemStack, int start, int end, boolean par4){
		return PC_InventoryUtils.storeItemStackToInventoryFrom(this, itemStack, PC_InventoryUtils.makeIndexList(start, end))==0;
    }


	@Override
	public int getSizeInventory() {
		return this.inventorySlots.size();
	}


	@Override
	public ItemStack getStackInSlot(int i) {
		return getSlot(i).getStack();
	}


	@Override
	public ItemStack decrStackSize(int i, int j) {
		return getSlot(i).decrStackSize(j);
	}


	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return getSlot(i).getStack();
	}


	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		getSlot(i).putStack(itemstack);
	}


	@Override
	public String getInventoryName() {
		return this.inventory.getInventoryName();
	}


	@Override
	public boolean hasCustomInventoryName() {
		return this.inventory.hasCustomInventoryName();
	}


	@Override
	public int getInventoryStackLimit() {
		return this.inventory.getInventoryStackLimit();
	}


	@Override
	public void markDirty() {
		this.inventory.markDirty();
	}


	@SuppressWarnings("hiding")
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.inventory.isUseableByPlayer(player);
	}


	@Override
	public void openInventory() {
		this.inventory.openInventory();
	}


	@Override
	public void closeInventory() {
		this.inventory.closeInventory();
	}


	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return getSlot(i).isItemValid(itemstack);
	}


	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return null;
	}


	@Override
	public boolean canInsertItem(int var1, ItemStack var2, int var3) {
		return false;
	}


	@Override
	public boolean canExtractItem(int var1, ItemStack var2, int var3) {
		return false;
	}


	@Override
	public int getSlotStackLimit(int i) {
		return getSlot(i).getSlotStackLimit();
	}


	@Override
	public boolean canTakeStack(int i, EntityPlayer entityPlayer) {
		return getSlot(i).canTakeStack(entityPlayer);
	}


	@Override
	public boolean canDropStack(int i) {
		return false;
	}


	@Override
	public void onTick(World world) {
		//
	}


	@Override
	public int[] getAppliedGroups(int i) {
		return null;
	}


	@Override
	public int[] getAppliedSides(int i) {
		return null;
	}
	
	public static boolean checkSlotAndItemStack(Slot slot, ItemStack itemStack, boolean par2){
        if (slot != null && slot.getHasStack() && itemStack != null && (slot instanceof PC_SlotPhantom || (itemStack.isItemEqual(slot.getStack()) && ItemStack.areItemStackTagsEqual(slot.getStack(), itemStack))))
        {
            int i = par2 ? 0 : itemStack.stackSize;
            int max = PC_InventoryUtils.getMaxStackSize(itemStack, slot);
            return slot.getStack().stackSize + i <= max;
        }

        return slot == null || !slot.getHasStack();
    }
	
	@Override
	protected void func_94533_d(){
        this.dragState = 0;
        this.dragSlots.clear();
    }
	
	@SuppressWarnings("hiding")
	@Override
	public ItemStack slotClick(int slotNumber, int mouseButton, int transfer, EntityPlayer player){
        ItemStack itemstack = null;
        InventoryPlayer inventoryplayer = player.inventory;
        int i1;
        ItemStack itemstack3;

        if (transfer == 5)
        {
            int l = this.dragState;
            this.dragState = func_94532_c(mouseButton);
            if ((l != 1 || this.dragState != 2) && l != this.dragState)
            {
            	this.func_94533_d();
            }
            else if (inventoryplayer.getItemStack() == null)
            {
            	this.func_94533_d();
            }
            else if (this.dragState == 0)
            {
                this.dragType = func_94529_b(mouseButton);

                if (func_94528_d(this.dragType))
                {
                    this.dragState = 1;
                    this.dragSlots.clear();
                }
                else
                {
                    this.func_94533_d();
                }
            }
            else if (this.dragState == 1)
            {
                Slot slot = getSlot(slotNumber);
                if (slot != null && checkSlotAndItemStack(slot, inventoryplayer.getItemStack(), true) && slot.isItemValid(inventoryplayer.getItemStack()) && inventoryplayer.getItemStack().stackSize > this.dragSlots.size() && this.canDragIntoSlot(slot))
                {
                	this.dragSlots.add(slot);
                }
            }
            else if (this.dragState == 2)
            {
            	if (!this.dragSlots.isEmpty())
                {
                    itemstack3 = inventoryplayer.getItemStack().copy();
                    i1 = inventoryplayer.getItemStack().stackSize;
                    Iterator<Slot> iterator = this.dragSlots.iterator();
                    
                    while (iterator.hasNext())
                    {
                        Slot slot1 = iterator.next();

                        if (slot1 != null && checkSlotAndItemStack(slot1, inventoryplayer.getItemStack(), true) && slot1.isItemValid(inventoryplayer.getItemStack()) && inventoryplayer.getItemStack().stackSize >= this.dragSlots.size() && this.canDragIntoSlot(slot1))
                        {
                            ItemStack itemstack1 = itemstack3.copy();
                            int j1 = slot1.getHasStack() && !(slot1 instanceof PC_SlotPhantom) ? slot1.getStack().stackSize : 0;
                            func_94525_a(this.dragSlots, this.dragType, itemstack1, j1);

                            int max = PC_InventoryUtils.getMaxStackSize(itemstack1, slot1);
                            if (itemstack1.stackSize > max)
                            {
                                itemstack1.stackSize = max;
                            }

                            i1 -= itemstack1.stackSize - j1;
                            slot1.putStack(itemstack1);
                        }
                    }

                    itemstack3.stackSize = i1;

                    if (itemstack3.stackSize <= 0)
                    {
                        itemstack3 = null;
                    }

                    inventoryplayer.setItemStack(itemstack3);
                }

                this.func_94533_d();
            }
            else
            {
                this.func_94533_d();
            }
        }
        else if (this.dragState != 0)
        {
            this.func_94533_d();
        }
        else
        {
            Slot slot2;
            int i2;
            ItemStack itemstack5;

            if ((transfer == 0 || transfer == 1) && (mouseButton == 0 || mouseButton == 1))
            {
                if (slotNumber == -999)
                {
                    if (inventoryplayer.getItemStack() != null && slotNumber == -999)
                    {
                        if (mouseButton == 0)
                        {
                            player.dropPlayerItemWithRandomChoice(inventoryplayer.getItemStack(), true);
                            inventoryplayer.setItemStack((ItemStack)null);
                        }

                        if (mouseButton == 1)
                        {
                            player.dropPlayerItemWithRandomChoice(inventoryplayer.getItemStack().splitStack(1), true);

                            if (inventoryplayer.getItemStack().stackSize == 0)
                            {
                                inventoryplayer.setItemStack((ItemStack)null);
                            }
                        }
                    }
                }
                else if (transfer == 1)
                {
                    if (slotNumber < 0)
                    {
                        return null;
                    }

                    slot2 = (Slot)this.inventorySlots.get(slotNumber);

                    if (slot2 != null && canTakeStack(slotNumber, player))
                    {
                        itemstack3 = this.transferStackInSlot(player, slotNumber);

                        if (itemstack3 != null)
                        {
                            Item item = itemstack3.getItem();
                            itemstack = itemstack3.copy();

                            if (slot2.getStack() != null && slot2.getStack().getItem() == item)
                            {
                                this.retrySlotClick(slotNumber, mouseButton, true, player);
                            }
                        }
                    }
                }
                else
                {
                    if (slotNumber < 0)
                    {
                        return null;
                    }

                    slot2 = (Slot)this.inventorySlots.get(slotNumber);

                    if (slot2 != null)
                    {
                        itemstack3 = slot2.getStack();
                        ItemStack itemstack4 = inventoryplayer.getItemStack();
                        
                        if(slot2 instanceof PC_SlotPhantom){
                        	itemstack3 = null;
                        	slot2.putStack(null);
                        }
                        
                        if (itemstack3 != null)
                        {
                            itemstack = itemstack3.copy();
                        }

                        if (itemstack3 == null)
                        {
                            if (itemstack4 != null && slot2.isItemValid(itemstack4))
                            {
                                i2 = mouseButton == 0 ? itemstack4.stackSize : 1;

                                if (i2 > slot2.getSlotStackLimit())
                                {
                                    i2 = slot2.getSlotStackLimit();
                                }

                                if (itemstack4.stackSize >= i2)
                                {
                                    slot2.putStack(itemstack4.splitStack(i2));
                                }

                                if (itemstack4.stackSize == 0)
                                {
                                    inventoryplayer.setItemStack((ItemStack)null);
                                }
                            }
                        }
                        else if (canTakeStack(slotNumber, player))
                        {
                            if (itemstack4 == null)
                            {
                                i2 = mouseButton == 0 ? itemstack3.stackSize : (itemstack3.stackSize + 1) / 2;
                                if(i2>itemstack3.getMaxStackSize()){
                                	i2 = itemstack3.getMaxStackSize();
                                }
                                itemstack5 = slot2.decrStackSize(i2);
                                inventoryplayer.setItemStack(itemstack5);

                                if (itemstack3.stackSize == 0)
                                {
                                    slot2.putStack((ItemStack)null);
                                }

                                slot2.onPickupFromSlot(player, inventoryplayer.getItemStack());
                            }
                            else if (slot2.isItemValid(itemstack4))
                            {
                                if (itemstack3.getItem() == itemstack4.getItem() && itemstack3.getItemDamage() == itemstack4.getItemDamage() && ItemStack.areItemStackTagsEqual(itemstack3, itemstack4))
                                {
                                    i2 = mouseButton == 0 ? itemstack4.stackSize : 1;

                                    int max = PC_InventoryUtils.getMaxStackSize(itemstack4, slot2);
                                    
                                    if(i2 > max  - itemstack3.stackSize){
                                    	i2 = max - itemstack3.stackSize;
                                    }

                                    itemstack4.splitStack(i2);

                                    if (itemstack4.stackSize == 0)
                                    {
                                        inventoryplayer.setItemStack((ItemStack)null);
                                    }

                                    itemstack3.stackSize += i2;
                                }
                                else if (itemstack4.stackSize <= slot2.getSlotStackLimit() && itemstack4.stackSize<=itemstack4.getMaxStackSize())
                                {
                                    slot2.putStack(itemstack4);
                                    inventoryplayer.setItemStack(itemstack3);
                                }
                            }
                            else if (itemstack3.getItem() == itemstack4.getItem() && itemstack4.getMaxStackSize() > 1 && (!itemstack3.getHasSubtypes() || itemstack3.getItemDamage() == itemstack4.getItemDamage()) && ItemStack.areItemStackTagsEqual(itemstack3, itemstack4))
                            {
                                i2 = itemstack3.stackSize;

                                if (i2 > 0 && i2 + itemstack4.stackSize <= itemstack4.getMaxStackSize())
                                {
                                    itemstack4.stackSize += i2;
                                    itemstack3 = slot2.decrStackSize(i2);

                                    if (itemstack3.stackSize == 0)
                                    {
                                        slot2.putStack((ItemStack)null);
                                    }

                                    slot2.onPickupFromSlot(player, inventoryplayer.getItemStack());
                                }
                            }
                        }

                        slot2.onSlotChanged();
                    }
                }
            }
            else if (transfer == 2 && mouseButton >= 0 && mouseButton < 9)
            {
                slot2 = (Slot)this.inventorySlots.get(slotNumber);

                if (canTakeStack(slotNumber, player))
                {
                    itemstack3 = inventoryplayer.getStackInSlot(mouseButton);
                    boolean flag = itemstack3 == null || slot2.inventory == inventoryplayer && slot2.isItemValid(itemstack3);
                    i2 = -1;

                    if (!flag)
                    {
                        i2 = inventoryplayer.getFirstEmptyStack();
                        flag |= i2 > -1;
                    }

                    if (slot2.getHasStack() && flag)
                    {
                        itemstack5 = slot2.getStack();
                        inventoryplayer.setInventorySlotContents(mouseButton, itemstack5.copy());

                        if ((slot2.inventory != inventoryplayer || !slot2.isItemValid(itemstack3)) && itemstack3 != null)
                        {
                            if (i2 > -1)
                            {
                                inventoryplayer.addItemStackToInventory(itemstack3);
                                slot2.decrStackSize(itemstack5.stackSize);
                                slot2.putStack((ItemStack)null);
                                slot2.onPickupFromSlot(player, itemstack5);
                            }
                        }
                        else
                        {
                            slot2.decrStackSize(itemstack5.stackSize);
                            slot2.putStack(itemstack3);
                            slot2.onPickupFromSlot(player, itemstack5);
                        }
                    }
                    else if (!slot2.getHasStack() && itemstack3 != null && slot2.isItemValid(itemstack3))
                    {
                        inventoryplayer.setInventorySlotContents(mouseButton, (ItemStack)null);
                        slot2.putStack(itemstack3);
                    }
                }
            }
            else if (transfer == 3 && player.capabilities.isCreativeMode && inventoryplayer.getItemStack() == null && slotNumber >= 0)
            {
                slot2 = (Slot)this.inventorySlots.get(slotNumber);

                if (slot2 != null && slot2.getHasStack())
                {
                    itemstack3 = slot2.getStack().copy();
                    itemstack3.stackSize = itemstack3.getMaxStackSize();
                    inventoryplayer.setItemStack(itemstack3);
                }
            }
            else if (transfer == 4 && inventoryplayer.getItemStack() == null && slotNumber >= 0)
            {
                slot2 = (Slot)this.inventorySlots.get(slotNumber);

                if (slot2 != null && slot2.getHasStack() && canTakeStack(slotNumber, player))
                {
                    itemstack3 = slot2.decrStackSize(mouseButton == 0 ? 1 : slot2.getStack().stackSize);
                    slot2.onPickupFromSlot(player, itemstack3);
                    player.dropPlayerItemWithRandomChoice(itemstack3, true);
                }
            }
            else if (transfer == 6 && slotNumber >= 0)
            {
                slot2 = (Slot)this.inventorySlots.get(slotNumber);
                itemstack3 = inventoryplayer.getItemStack();

                if (itemstack3 != null && (slot2 == null || !slot2.getHasStack() || !canTakeStack(slotNumber, player)))
                {
                    i1 = mouseButton == 0 ? 0 : this.inventorySlots.size() - 1;
                    i2 = mouseButton == 0 ? 1 : -1;

                    for (int l1 = 0; l1 < 2; ++l1)
                    {
                        for (int j2 = i1; j2 >= 0 && j2 < this.inventorySlots.size() && itemstack3.stackSize < itemstack3.getMaxStackSize(); j2 += i2)
                        {
                            Slot slot3 = (Slot)this.inventorySlots.get(j2);

                            if (slot3.getHasStack() && checkSlotAndItemStack(slot3, itemstack3, true) && canTakeStack(j2, player) && this.func_94530_a(itemstack3, slot3) && (l1 != 0 || slot3.getStack().stackSize != slot3.getStack().getMaxStackSize()))
                            {
                                int k1 = Math.min(itemstack3.getMaxStackSize() - itemstack3.stackSize, slot3.getStack().stackSize);
                                ItemStack itemstack2 = slot3.decrStackSize(k1);
                                itemstack3.stackSize += k1;

                                if (itemstack2.stackSize <= 0)
                                {
                                    slot3.putStack((ItemStack)null);
                                }

                                slot3.onPickupFromSlot(player, itemstack2);
                            }
                        }
                    }
                }

                this.detectAndSendChanges();
            }
        }

        return itemstack;
    }
	
	@Override
	public int getMaxStackSize(ItemStack itemStack, int slot) {
		return PC_InventoryUtils.getMaxStackSize(itemStack, getSlot(slot));
	}

	@Override
	public boolean canBeDragged(int i) {
		return true;
	}
	
}
