package powercraft.api.grid;

public interface PC_IGridTile<G extends PC_Grid<G, T, N, E>, T extends PC_IGridTile<G, T, N, E>, N extends PC_Node<G, T, N, E>, E extends PC_Edge<G, T, N, E>> {

	public void setGrid(G grid);
	
	public G getGrid();
	
}
