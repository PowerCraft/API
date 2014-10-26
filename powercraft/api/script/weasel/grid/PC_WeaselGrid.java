package powercraft.api.script.weasel.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import powercraft.api.grid.PC_Grid;
import powercraft.api.grid.PC_IGridFactory;
import powercraft.api.script.weasel.PC_IWeaselEvent;

public class PC_WeaselGrid extends PC_Grid<PC_WeaselGrid, PC_IWeaselGridTile, PC_WeaselNode, PC_WeaselEdge> {

	public static final PC_IGridFactory<PC_WeaselGrid, PC_IWeaselGridTile, PC_WeaselNode, PC_WeaselEdge> factory = new Factory();
	
	private static class Factory implements PC_IGridFactory<PC_WeaselGrid, PC_IWeaselGridTile, PC_WeaselNode, PC_WeaselEdge>{

		Factory() {
			
		}

		@Override
		public PC_WeaselGrid make(PC_IWeaselGridTile tile) {
			return new PC_WeaselGrid(tile);
		}
		
	}
	
	private List<PC_IWeaselGridTileAddressable> occupiedAddresses = new ArrayList<PC_IWeaselGridTileAddressable>();
	private HashMap<Integer, PC_IWeaselGridTileAddressable> addressTiles = new HashMap<Integer, PC_IWeaselGridTileAddressable>();
	
	PC_WeaselGrid(PC_IWeaselGridTile tile){
		addTile(tile);
	}
	
	PC_WeaselGrid(){
		
	}
	
	private void addAddressTile(PC_IWeaselGridTileAddressable tile){
		int address = tile.getAddress();
		if(address==0)
			return;
		Integer a = Integer.valueOf(address);
		PC_IWeaselGridTileAddressable oTile = this.addressTiles.get(a);
		if(oTile==tile)
			return;
		if(oTile!=null){
			this.occupiedAddresses.add(tile);
			tile.setAddressOccupied(true);
		}else{
			this.addressTiles.put(a, tile);
		}
	}
	
	private void removeAddressTile(PC_IWeaselGridTileAddressable tile){
		removeAddressTile(tile, tile.getAddress());
	}
	
	private void removeAddressTile(PC_IWeaselGridTileAddressable tile, int address){
		if(address==0)
			return;
		Integer a = Integer.valueOf(address);
		if(this.addressTiles.get(a)==tile){
			this.addressTiles.remove(a);
			Iterator<PC_IWeaselGridTileAddressable> i = this.occupiedAddresses.iterator();
			while(i.hasNext()){
				PC_IWeaselGridTileAddressable t = i.next();
				int na = t.getAddress();
				if(address==na){
					t.setAddressOccupied(false);
					this.addressTiles.put(Integer.valueOf(na), t);
					i.remove();
					break;
				}
			}
		}else{
			this.occupiedAddresses.remove(tile);
		}
	}
	
	@Override
	protected void addTile(PC_IWeaselGridTile tile){
		super.addTile(tile);
		if(tile instanceof PC_IWeaselGridTileAddressable){
			addAddressTile((PC_IWeaselGridTileAddressable)tile);
		}
	}

	@Override
	protected void removeTile(PC_IWeaselGridTile tile) {
		if(tile instanceof PC_IWeaselGridTileAddressable){
			removeAddressTile((PC_IWeaselGridTileAddressable)tile);
		}
		super.removeTile(tile);
	}

	@Override
	protected void moveToOtherGrid(List<PC_WeaselNode> nodes, List<PC_WeaselEdge> edges) {
		for(PC_WeaselNode node:nodes){
			PC_IWeaselGridTile tile = node.getTile();
			if(tile instanceof PC_IWeaselGridTileAddressable){
				removeAddressTile((PC_IWeaselGridTileAddressable)tile);
			}
		}
		for(PC_WeaselEdge edge:edges){
			List<PC_IWeaselGridTile> tiles = edge.getTiles();
			for(PC_IWeaselGridTile tile:tiles){
				if(tile instanceof PC_IWeaselGridTileAddressable){
					removeAddressTile((PC_IWeaselGridTileAddressable)tile);
				}
			}
		}
	}

	@Override
	protected void addAll(List<PC_WeaselNode> nodes, List<PC_WeaselEdge> edges) {
		for(PC_WeaselNode node:nodes){
			PC_IWeaselGridTile tile = node.getTile();
			if(tile instanceof PC_IWeaselGridTileAddressable){
				addAddressTile((PC_IWeaselGridTileAddressable)tile);
			}
		}
		for(PC_WeaselEdge edge:edges){
			List<PC_IWeaselGridTile> tiles = edge.getTiles();
			for(PC_IWeaselGridTile tile:tiles){
				if(tile instanceof PC_IWeaselGridTileAddressable){
					addAddressTile((PC_IWeaselGridTileAddressable)tile);
				}
			}
		}
	}

	@Override
	protected PC_WeaselNode createNode(PC_IWeaselGridTile tile) {
		return new PC_WeaselNode(this, tile);
	}

	@Override
	protected PC_WeaselEdge createEdge(PC_WeaselNode start, PC_WeaselNode end) {
		return new PC_WeaselEdge(this, start, end);
	}

	@Override
	protected PC_WeaselGrid createGrid() {
		return new PC_WeaselGrid();
	}
	
	public PC_IWeaselGridTileAddressable getTileByAddress(PC_IWeaselGridTileAddressable caller, int address){
		if(address==0)
			return caller;
		return this.addressTiles.get(Integer.valueOf(address));
	}
	
	public void onAddressChange(int oldAddress, PC_IWeaselGridTileAddressable tile){
		if(getTileHolderFor(tile)==null)
			return;
		removeAddressTile(tile, oldAddress);
		addAddressTile(tile);
	}

	public void sendEvent(PC_IWeaselEvent event) {
		for(PC_IWeaselGridTileAddressable tile:this.addressTiles.values()){
			tile.onEvent(event);
		}
	}
	
}
