package powercraft.api.grid;

import java.util.ArrayList;
import java.util.List;

public class PC_Node<G extends PC_Grid<G, T, N, E>, T extends PC_IGridTile<G, T, N, E>, N extends PC_Node<G, T, N, E>, E extends PC_Edge<G, T, N, E>> extends PC_TileHolder<G, T, N, E> {

	protected final List<E> edges = new ArrayList<E>();
	protected final T tile;
	
	protected PC_Node(G grid, T tile){
		super(grid);
		this.tile = tile;
	}
	
	protected boolean canBecomeEdge(){
		return true;
	}
	
	@Override
	protected boolean hasTile(T tile) {
		return this.tile==tile;
	}
	
	protected void replaceEdge(E edge, E replace){
		edges.set(edges.indexOf(edge), replace);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected N getAsNode(T tile) {
		return (N) this;
	}
	
	protected void connectTo(N node){
		@SuppressWarnings("unchecked")
		E edge = grid.newEdge((N) this, node);
		edges.add(edge);
		node.edges.add(edge);
		removeWhenAble();
		node.removeWhenAble();
	}
	
	@SuppressWarnings("unchecked")
	protected void removeWhenAble(){
		if(edges.isEmpty()){
			grid.removeNode((N) this);
		}else if(edges.size()==2 && canBecomeEdge()){
			E edge = edges.get(0);
			E edge2Delete = edges.get(1);
			edge.integrate((N) this, tile, edge2Delete);
			grid.removeNode((N) this);
			grid.removeEdge(edge2Delete);
		}
	}

	@SuppressWarnings("unchecked")
	protected void remove() {
		for(E edge:edges){
			edge.remove((N) this);
		}
		grid.removeNode((N) this);
	}
	
	protected void remove(E edge) {
		edges.remove(edge);
		removeWhenAble();
	}
	
	protected void connectEdge(E edge){
		edges.add(edge);
	}

	@Override
	protected List<T> getTiles() {
		List<T> list = new ArrayList<T>();
		list.add(tile);
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void markVisibles(List<N> visibleNodes, List<E> visibleEdges) {
		if(!visibleNodes.contains(this)){
			visibleNodes.add((N) this);
			for(E edge:edges){
				edge.markVisibles(visibleNodes, visibleEdges);
			}
		}
	}
	
}
