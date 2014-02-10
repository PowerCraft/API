package powercraft.api;

import cpw.mods.fml.relauncher.Side;

public enum PC_Side {
	SERVER, CLIENT;

	public static PC_Side from(Side side) {
		switch (side) {
		case CLIENT:
			return CLIENT;
		case SERVER:
			return SERVER;
		default:
			return null;
		}
	}
}
