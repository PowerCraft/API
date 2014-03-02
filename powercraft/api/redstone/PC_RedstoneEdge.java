package powercraft.api.redstone;

import powercraft.api.grid.PC_Edge;

final class PC_RedstoneEdge extends PC_Edge<PC_RedstoneGrid, PC_IRedstoneGridTile, PC_RedstoneNode, PC_RedstoneEdge> {

	PC_RedstoneEdge(PC_RedstoneGrid grid, PC_RedstoneNode start, PC_RedstoneNode end) {
		super(grid, start, end);
	}

	public void onRedstonePowerChange() {
		for (PC_IRedstoneGridTile tile : getTiles()) {
			tile.onRedstonePowerChange();
		}
	}
	
}
