package powercraft.api.redstone;

import powercraft.api.grid.PC_IGridTile;

public interface PC_IRedstoneGridTile extends PC_IGridTile<PC_RedstoneGrid, PC_IRedstoneGridTile, PC_RedstoneNode, PC_RedstoneEdge> {

	public boolean isIO();

	public void onRedstonePowerChange();

	public int getPower();
	
}
