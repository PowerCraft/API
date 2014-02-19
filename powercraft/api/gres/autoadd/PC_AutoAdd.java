package powercraft.api.gres.autoadd;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface PC_AutoAdd {

	public void onCharAdded(PC_StringAdd add);
	
}
