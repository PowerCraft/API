package powercraft.api.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public interface PC_ITileEntityRenderer {

	@SideOnly(Side.CLIENT)
	public void renderTielEntityAt(PC_TileEntitySpecialRenderer tileEntityRenderer, double x, double y, double z, float timeStamp);

}
