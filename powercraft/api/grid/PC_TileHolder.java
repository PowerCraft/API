package powercraft.api.grid;

import java.util.List;

public abstract class PC_TileHolder<G extends PC_Grid<G, T, N, E>, T extends PC_IGridTile<G, T, N, E>, N extends PC_Node<G, T, N, E>, E extends PC_Edge<G, T, N, E>> {

	protected G grid;
	
	protected PC_TileHolder(G grid){
		this.grid = grid;
	}
	
	protected abstract boolean hasTile(T tile);
	
	protected abstract N getAsNode(T tile);
	
	protected abstract List<T> getTiles();
	
	protected abstract void markVisibles(List<N> visibleNodes, List<E> visibleEdges);
	
}
