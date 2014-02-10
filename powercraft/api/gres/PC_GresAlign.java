package powercraft.api.gres;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_GresAlign {

	public static enum H {
		RIGHT, CENTER, LEFT
	}

	public static enum V {
		TOP, CENTER, BOTTOM
	}

	public static enum Fill {
		NONE, VERTICAL, HORIZONTAL, BOTH;
	}

}
