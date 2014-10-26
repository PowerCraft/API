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
	
	@SuppressWarnings("static-method")
	protected boolean canBecomeEdge(){
		return true;
	}
	
	@Override
	protected boolean hasTile(T tile) {
		return this.tile==tile;
	}
	
	protected void replaceEdge(E edge, E replace){
		this.edges.set(this.edges.indexOf(edge), replace);
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	protected N getAsNode(T tile) {
		return (N) this;
	}
	
	protected void connectTo(N node){
		@SuppressWarnings("unchecked")
		E edge = this.grid.newEdge((N) this, node);
		this.edges.add(edge);
		node.edges.add(edge);
		removeWhenAble();
		node.removeWhenAble();
	}
	
	@SuppressWarnings("unchecked")
	protected void removeWhenAble(){
		if(this.edges.size()==2 && canBecomeEdge()){
			E edge = this.edges.get(0);
			E edge2Delete = this.edges.get(1);
			edge.integrate((N) this, this.tile, edge2Delete);
			this.grid.removeNode((N) this);
		}
	}

	@SuppressWarnings("unchecked")
	protected void remove() {
		for(E edge:this.edges){
			edge.remove((N) this);
		}
		this.grid.removeNode((N) this);
	}
	
	protected void remove(E edge) {
		this.edges.remove(edge);
		removeWhenAble();
	}
	
	protected void connectEdge(E edge){
		this.edges.add(edge);
	}

	@Override
	protected List<T> getTiles() {
		List<T> list = new ArrayList<T>();
		list.add(this.tile);
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void markVisibles(List<N> visibleNodes, List<E> visibleEdges) {
		if(!visibleNodes.contains(this)){
			visibleNodes.add((N) this);
			for(E edge:this.edges){
				edge.markVisibles(visibleNodes, visibleEdges);
			}
		}
	}

	@Override
	public String toString() {
		return this.tile.toString();
	}
	
}
