package powercraft.api.inventory;

import net.minecraft.util.IIcon;

public interface PC_ISidedInventory extends PC_IInventory {

	public void setSideGroup(int i, int j);

	public int getGroupCount();

	public int getSideGroup(int i);

	public IIcon getFrontIcon();

}
