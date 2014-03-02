package powercraft.api.script.weasel.grid;

import java.util.List;

import powercraft.api.grid.PC_Edge;

final class PC_WeaselEdge extends PC_Edge<PC_WeaselGrid, PC_IWeaselGridTile, PC_WeaselNode, PC_WeaselEdge> {

	PC_WeaselEdge(PC_WeaselGrid grid, PC_WeaselNode start, PC_WeaselNode end) {
		super(grid, start, end);
	}
	
	@Override
	protected List<PC_IWeaselGridTile> getTiles() {
		return this.tiles;
	}
	
}
