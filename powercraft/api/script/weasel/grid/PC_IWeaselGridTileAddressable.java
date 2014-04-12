package powercraft.api.script.weasel.grid;

import powercraft.api.script.weasel.PC_IWeaselEvent;

public interface PC_IWeaselGridTileAddressable extends PC_IWeaselGridTile {

	public int getAddress();

	public void setAddressOccupied(boolean b);

	public void onEvent(PC_IWeaselEvent event);

	public int getType();

	public int getRedstoneValue(int side);

	public void setRedstoneValue(int side, int value);
	
	public PC_IWeaselGridTileAddressable getTileByAddress(int address);

	public void print(String out);

	public void cls();
	
}
