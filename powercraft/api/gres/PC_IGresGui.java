package powercraft.api.gres;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface PC_IGresGui {

	/**
	 * That's the function in which you organize the GUI.
	 * @param gui That's the GUI-Object on which you register all the GUI components
	 */
	public void initGui(PC_GresGuiHandler gui);

}
