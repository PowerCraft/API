package powercraft.api.grid;

import java.util.ArrayList;
import java.util.List;

public abstract class PC_Grid<G extends PC_Grid<G, T, N, E>, T extends PC_IGridTile<G, T, N, E>, N extends PC_Node<G, T, N, E>, E extends PC_Edge<G, T, N, E>> {

	protected final List<N> nodes = new ArrayList<N>();
	protected final List<E> edges = new ArrayList<E>();
	
	protected PC_Grid(){
		
	}
	
	@SuppressWarnings("unchecked")
	public void addTile(T tile, T connection){
		tile.setGrid((G) this);
		newNode(tile);
		if(connection!=null)
			makeConnection(tile, connection);
	}
	
	protected abstract N createNode(T tile);
	
	protected N newNode(T tile){
		N node = createNode(tile);
		nodes.add(node);
		return node;
	}
	
	protected abstract E createEdge(N start, N end);
	
	protected E newEdge(N start, N end){
		E edge = createEdge(start, end);
		edges.add(edge);
		return edge;
	}
	
	public static <G extends PC_Grid<G, T, N, E>, T extends PC_IGridTile<G, T, N, E>, N extends PC_Node<G, T, N, E>, E extends PC_Edge<G, T, N, E>> void remove(T tile){
		tile.getGrid().removeTile(tile);
	}
	
	protected void removeTile(T tile){
		PC_TileHolder<G, T, N, E> tileHolder = getTileHolderFor(tile);
		tileHolder.getAsNode(tile).remove();
		tile.setGrid(null);
		splitGridsIfAble();
	}
	
	public static <G extends PC_Grid<G, T, N, E>, T extends PC_IGridTile<G, T, N, E>, N extends PC_Node<G, T, N, E>, E extends PC_Edge<G, T, N, E>> void connect(T connection1, T connection2){
		connection1.getGrid().makeConnection(connection1, connection2);
	}
	
	protected void makeConnection(T connection1, T connection2){
		if(connection2.getGrid()!=this)
			mergeGrids(connection2.getGrid());
		PC_TileHolder<G, T, N, E> tileHolder = getTileHolderFor(connection1);
		N node1 = tileHolder.getAsNode(connection1);
		tileHolder = getTileHolderFor(connection2);
		N node2 = tileHolder.getAsNode(connection2);
		node1.connectTo(node2);
	}
	
	protected void mergeGrids(G grid){
		addAll(grid.nodes, grid.edges);
		grid.destroy();
	}
	
	protected void destroy(){
		
	}
	
	@SuppressWarnings("unchecked")
	protected void addAll(List<N> nodes, List<E> edges){
		this.nodes.addAll(nodes);
		this.edges.addAll(edges);
		for(N node:nodes){
			node.grid = (G) this;
			for(T tile:node.getTiles()){
				tile.setGrid((G) this);
			}
		}
		for(E edge:edges){
			edge.grid = (G) this;
			for(T tile:edge.getTiles()){
				tile.setGrid((G) this);
			}
		}
	}
	
	protected PC_TileHolder<G, T, N, E> getTileHolderFor(T tile){
		for(N node:nodes){
			if(node.hasTile(tile)){
				return node;
			}
		}
		for(E edge:edges){
			if(edge.hasTile(tile)){
				return edge;
			}
		}
		return null;
	}
	
	protected void removeNode(N node){
		nodes.remove(node);
		if(nodes.isEmpty())
			destroy();
	}
	
	protected void removeEdge(E edge){
		edges.remove(edge);
	}
	
	protected void splitGridsIfAble(){
		List<N> remainingNodes = new ArrayList<N>(nodes);
		List<N> visibleNodes = new ArrayList<N>();
		List<E> visibleEdges = new ArrayList<E>();
		while(!remainingNodes.isEmpty()){
			remainingNodes.get(0).markVisibles(visibleNodes, visibleEdges);
			remainingNodes.removeAll(visibleNodes);
			if(remainingNodes.isEmpty())
				break;
			nodes.removeAll(visibleNodes);
			edges.removeAll(visibleEdges);
			G grid = createGrid();
			grid.addAll(visibleNodes, visibleEdges);
			visibleNodes.clear();
			visibleEdges.clear();
		}
	}
	
	protected abstract G createGrid();
	
	@Override
	public String toString(){
		String s = "";
		for(N node:nodes){
			s += node.toString()+"\n";
		}
		for(E edge:edges){
			s += edge.toString()+"\n";
		}
		return s;
	}
	
}
