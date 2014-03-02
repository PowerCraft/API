package powercraft.api.script.weasel.grid;

import powercraft.api.grid.PC_Node;

final class PC_WeaselNode extends PC_Node<PC_WeaselGrid, PC_IWeaselGridTile, PC_WeaselNode, PC_WeaselEdge> {

	PC_WeaselNode(PC_WeaselGrid grid, PC_IWeaselGridTile tile) {
		super(grid, tile);
	}

	PC_IWeaselGridTile getTile() {
		return this.tile;
	}
	
}
