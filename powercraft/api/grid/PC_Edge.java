package powercraft.api.grid;

import java.util.ArrayList;
import java.util.List;

public class PC_Edge<G extends PC_Grid<G, T, N, E>, T extends PC_IGridTile<G, T, N, E>, N extends PC_Node<G, T, N, E>, E extends PC_Edge<G, T, N, E>> extends PC_TileHolder<G,T,N,E> {

	private N start;
	private N end;
	protected final List<T> tiles = new ArrayList<T>();

	protected PC_Edge(G grid, N start, N end) {
		super(grid);
		this.start = start;
		this.end = end;
	}

	@Override
	protected boolean hasTile(T tile) {
		return tiles.contains(tile);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected N getAsNode(T tile) {
		int index = tiles.indexOf(tile);
		N node = grid.newNode(tile);
		E edge = grid.newEdge(node, end);
		end.replaceEdge((E) this, edge);
		end = node;
		tiles.remove(index);
		while(tiles.size()>index){
			edge.tiles.add(tiles.remove(index));
		}
		onChanged();
		return node;
	}
	
	protected void integrate(N node, T tile, E edge){
		if(start==node){
			tiles.add(0, tile);
			if(edge.start==node){
				start = edge.end;
				for(int i=0; i<edge.tiles.size(); i++){
					tiles.add(0, edge.tiles.get(i));
				}
			}else if(edge.end==node){
				start = edge.start;
				for(int i=edge.tiles.size()-1; i>=0; i++){
					tiles.add(0, edge.tiles.get(i));
				}
			}
		}else if(end==node){
			tiles.add(tile);
			if(edge.start==node){
				end = edge.end;
				for(int i=0; i<edge.tiles.size(); i++){
					tiles.add(edge.tiles.get(i));
				}
			}else if(edge.end==node){
				end = edge.start;
				for(int i=edge.tiles.size()-1; i>=0; i++){
					tiles.add(edge.tiles.get(i));
				}
			}
		}
		onChanged();
	}
	
	@SuppressWarnings("unchecked")
	protected void remove(N node) {
		if(tiles.isEmpty()){
			grid.removeEdge((E) this);
			if(node==start){
				end.remove((E) this);
			}else if(node==end){
				start.remove((E) this);
			}
		}else{
			if(node==start){
				N nnode = grid.newNode(tiles.remove(0));
				start = nnode;
				nnode.connectEdge((E) this);
			}else if(node==end){
				N nnode = grid.newNode(tiles.remove(tiles.size()-1));
				end = nnode;
				nnode.connectEdge((E) this);
			}
			onChanged();
		}
	}

	@Override
	protected List<T> getTiles() {
		return tiles;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void markVisibles(List<N> visibleNodes, List<E> visibleEdges) {
		if(!visibleEdges.contains(this)){
			visibleEdges.add((E) this);
			start.markVisibles(visibleNodes, visibleEdges);
			end.markVisibles(visibleNodes, visibleEdges);
		}
	}
	
	protected void onChanged(){
		
	}
	
}
