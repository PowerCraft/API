package powercraft.api.grid;

import java.util.ArrayList;
import java.util.List;

public abstract class PC_Grid<G extends PC_Grid<G, T, N, E>, T extends PC_IGridTile<G, T, N, E>, N extends PC_Node<G, T, N, E>, E extends PC_Edge<G, T, N, E>> {

	protected final List<N> nodes = new ArrayList<N>();
	protected final List<E> edges = new ArrayList<E>();
	protected boolean spitting = true;
	
	protected PC_Grid(T tile){
		addTile(tile);
	}
	
	protected PC_Grid(){
		
	}
	
	public void addTile(T tile, T connection){
		addTile(tile);
		makeConnection(tile, connection);
	}
	
	@SuppressWarnings("unchecked")
	protected void addTile(T tile){
		tile.setGrid((G) this);
		newNode(tile);
	}
	
	protected abstract N createNode(T tile);
	
	protected N newNode(T tile){
		N node = createNode(tile);
		this.nodes.add(node);
		return node;
	}
	
	protected abstract E createEdge(N start, N end);
	
	protected E newEdge(N start, N end){
		E edge = createEdge(start, end);
		this.edges.add(edge);
		return edge;
	}
	
	protected void removeTile(T tile){
		PC_TileHolder<G, T, N, E> tileHolder = getTileHolderFor(tile);
		System.out.println(tileHolder+":"+this+":"+tile+":"+tile.getGrid());
		tileHolder.getAsNode(tile).remove();
		tile.setGrid(null);
		splitGridsIfAble();
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
		//
	}
	
	@SuppressWarnings({ "unchecked", "hiding" })
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
		for(N node:this.nodes){
			if(node.hasTile(tile)){
				return node;
			}
		}
		for(E edge:this.edges){
			if(edge.hasTile(tile)){
				return edge;
			}
		}
		return null;
	}
	
	protected void removeNode(N node){
		this.nodes.remove(node);
		if(this.nodes.isEmpty())
			destroy();
	}
	
	protected void removeEdge(E edge){
		this.edges.remove(edge);
	}
	
	protected void splitGridsIfAble(){
		if(this.spitting){
			List<N> remainingNodes = new ArrayList<N>(this.nodes);
			List<N> visibleNodes = new ArrayList<N>();
			List<E> visibleEdges = new ArrayList<E>();
			while(!remainingNodes.isEmpty()){
				remainingNodes.get(0).markVisibles(visibleNodes, visibleEdges);
				remainingNodes.removeAll(visibleNodes);
				if(remainingNodes.isEmpty())
					break;
				moveToOtherGrid(visibleNodes, visibleEdges);
				this.nodes.removeAll(visibleNodes);
				this.edges.removeAll(visibleEdges);
				G grid = createGrid();
				grid.addAll(visibleNodes, visibleEdges);
				visibleNodes.clear();
				visibleEdges.clear();
			}
		}
	}
	
	@SuppressWarnings({ "unused", "hiding" })
	protected void moveToOtherGrid(List<N> nodes, List<E> edges){
		//
	}
	
	protected abstract G createGrid();
	
	@Override
	public String toString(){
		String s = "";
		for(N node:this.nodes){
			s += node.toString()+"\n";
		}
		for(E edge:this.edges){
			s += edge.toString()+"\n";
		}
		return s;
	}
	
	public void disableSplitting() {
		this.spitting = false;
	}

	public void enableSplitting() {
		this.spitting = true;
		splitGridsIfAble();
	}
	
	public void update(){
		//
	}
	
}
