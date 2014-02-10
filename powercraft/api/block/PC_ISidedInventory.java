package powercraft.api.block;

import net.minecraft.util.IIcon;
import powercraft.api.inventory.PC_IInventory;

public interface PC_ISidedInventory extends PC_IInventory {

	public void setSideGroup(int i, int j);

	public int getGroupCount();

	public int getSideGroup(int i);

	public IIcon getFrontIcon();

}
