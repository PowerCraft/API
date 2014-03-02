package powercraft.api.grid;

import java.util.ArrayList;
import java.util.List;

public class PC_Edge<G extends PC_Grid<G, T, N, E>, T extends PC_IGridTile<G, T, N, E>, N extends PC_Node<G, T, N, E>, E extends PC_Edge<G, T, N, E>> extends PC_TileHolder<G,T,N,E> {

	protected N start;
	protected N end;
	protected final List<T> tiles = new ArrayList<T>();

	protected PC_Edge(G grid, N start, N end) {
		super(grid);
		this.start = start;
		this.end = end;
	}

	@Override
	protected boolean hasTile(T tile) {
		return this.tiles.contains(tile);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected N getAsNode(T tile) {
		int index = this.tiles.indexOf(tile);
		N node = this.grid.newNode(tile);
		E edge = this.grid.newEdge(node, this.end);
		this.end.replaceEdge((E) this, edge);
		this.end = node;
		this.tiles.remove(index);
		while(this.tiles.size()>index){
			edge.tiles.add(0, this.tiles.remove(index));
		}
		onChanged();
		node.connectEdge(edge);
		node.connectEdge((E) this);
		return node;
	}
	
	@SuppressWarnings("unchecked")
	protected void integrate(N node, T tile, E edge){
		if(this.start==node){
			this.tiles.add(0, tile);
			if(edge.start==node){
				this.start = edge.end;
				for(int i=0; i<edge.tiles.size(); i++){
					this.tiles.add(0, edge.tiles.get(i));
				}
			}else if(edge.end==node){
				this.start = edge.start;
				for(int i=edge.tiles.size()-1; i>=0; i--){
					this.tiles.add(0, edge.tiles.get(i));
				}
			}
			this.start.replaceEdge(edge, (E) this);
		}else if(this.end==node){
			this.tiles.add(tile);
			if(edge.start==node){
				this.end = edge.end;
				for(int i=0; i<edge.tiles.size(); i++){
					this.tiles.add(edge.tiles.get(i));
				}
			}else if(edge.end==node){
				this.end = edge.start;
				for(int i=edge.tiles.size()-1; i>=0; i--){
					this.tiles.add(edge.tiles.get(i));
				}
			}
			this.end.replaceEdge(edge, (E) this);
		}
		onChanged();
	}
	
	@SuppressWarnings("unchecked")
	protected void remove(N node) {
		if(this.tiles.isEmpty()){
			this.grid.removeEdge((E) this);
			if(node==this.start){
				this.end.remove((E) this);
			}else if(node==this.end){
				this.start.remove((E) this);
			}
		}else{
			if(node==this.start){
				this.start = this.grid.newNode(this.tiles.remove(0));
				this.start.connectEdge((E) this);
			}else if(node==this.end){
				this.end = this.grid.newNode(this.tiles.remove(this.tiles.size()-1));
				this.end.connectEdge((E) this);
			}
			onChanged();
		}
	}

	@Override
	protected List<T> getTiles() {
		return this.tiles;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void markVisibles(List<N> visibleNodes, List<E> visibleEdges) {
		if(!visibleEdges.contains(this)){
			visibleEdges.add((E) this);
			this.start.markVisibles(visibleNodes, visibleEdges);
			this.end.markVisibles(visibleNodes, visibleEdges);
		}
	}
	
	protected void onChanged(){
		//
	}
	
	@Override
	public String toString(){
		return this.tiles.toString();
	}
	
}
