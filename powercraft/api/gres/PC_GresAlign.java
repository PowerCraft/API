package powercraft.api.gres;

import powercraft.api.PC_Utils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class PC_GresAlign {

	public static enum H {
		RIGHT, CENTER, LEFT
	}

	public static enum V {
		TOP, CENTER, BOTTOM
	}

	public static enum Fill {
		NONE, VERTICAL, HORIZONTAL, BOTH;
	}

	public static enum Size {
		SELV, BIGGEST;
	}
	
	private PC_GresAlign(){
		PC_Utils.staticClassConstructor();
	}
	
}
