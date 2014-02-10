package powercraft.api.gres;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.gres.slot.PC_Slot;


public abstract class PC_GresBaseWithInventory extends Container {

	protected final EntityPlayer player;

	protected final Slot[][] inventoryPlayerUpper = new Slot[9][3];

	protected final Slot[] inventoryPlayerLower = new Slot[9];

	protected final IInventory inventory;

	protected Slot[] invSlots;


	public PC_GresBaseWithInventory(EntityPlayer player, IInventory inventory) {

		this.player = player;

		this.inventory = inventory;

		if (inventory instanceof PC_TileEntity) {
			((PC_TileEntity) inventory).openContainer(this);
		}

		if (player != null) {
			for (int i = 0; i < 9; i++) {
				inventoryPlayerLower[i] = new PC_Slot(player.inventory, i);
				addSlotToContainer(inventoryPlayerLower[i]);
			}

			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 3; j++) {
					inventoryPlayerUpper[i][j] = new PC_Slot(player.inventory, i + j * 9 + 9);
					addSlotToContainer(inventoryPlayerUpper[i][j]);
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

		invSlots = new Slot[inventory.getSizeInventory()];
		for (int i = 0; i < invSlots.length; i++) {
			invSlots[i] = new PC_Slot(inventory, i);
		}
		return invSlots;
	}


	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {

		return inventory instanceof TileEntity ? ((TileEntity) inventory).getDistanceFrom(entityplayer.posX, entityplayer.posY, entityplayer.posZ) < 64
				: true;
	}


	public void sendProgressBarUpdate(int key, int value) {

		if (player instanceof EntityPlayerMP) {
			((EntityPlayerMP) player).sendProgressBarUpdate(this, key, value);
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer) {

		super.onContainerClosed(par1EntityPlayer);
		if (inventory instanceof PC_TileEntity) {
			((PC_TileEntity) inventory).closeContainer(this);
		}
	}


	@Override
	public void addCraftingToCrafters(ICrafting crafting) {

		super.addCraftingToCrafters(crafting);
		if (inventory instanceof PC_TileEntity) {
			((PC_TileEntity) inventory).sendProgressBarUpdates();
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
		ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (par2 < 4 * 9)
            {
                if (!this.mergeItemStack(itemstack1, 4 * 9, this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, 4 * 9, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
	}
	
	
	
}
