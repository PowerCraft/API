package powercraft.api.gres.nodesys;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface PC_IGresNodesysLineDraw {
	
	public void drawLines();
	
}
