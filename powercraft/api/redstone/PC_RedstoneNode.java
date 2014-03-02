package powercraft.api.redstone;

import powercraft.api.grid.PC_Node;

final class PC_RedstoneNode extends PC_Node<PC_RedstoneGrid, PC_IRedstoneGridTile, PC_RedstoneNode, PC_RedstoneEdge> {

	PC_RedstoneNode(PC_RedstoneGrid grid, PC_IRedstoneGridTile tile) {
		super(grid, tile);
	}

	@Override
	protected boolean canBecomeEdge() {
		return !this.tile.isIO();
	}

	public int getPower() {
		if(this.tile.isIO()){
			return this.tile.getPower();
		}
		return 0;
	}

	public void onRedstonePowerChange() {
		this.tile.onRedstonePowerChange();
	}
	
}
