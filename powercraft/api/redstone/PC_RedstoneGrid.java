package powercraft.api.redstone;

import java.util.List;

import net.minecraft.init.Blocks;
import powercraft.api.grid.PC_Grid;
import powercraft.api.grid.PC_IGridFactory;

public class PC_RedstoneGrid extends PC_Grid<PC_RedstoneGrid, PC_IRedstoneGridTile, PC_RedstoneNode, PC_RedstoneEdge> {

	public static final PC_IGridFactory<PC_RedstoneGrid, PC_IRedstoneGridTile, PC_RedstoneNode, PC_RedstoneEdge> factory = new Factory();
	
	private static class Factory implements PC_IGridFactory<PC_RedstoneGrid, PC_IRedstoneGridTile, PC_RedstoneNode, PC_RedstoneEdge>{

		Factory() {
			
		}

		@Override
		public PC_RedstoneGrid make(PC_IRedstoneGridTile tile) {
			return new PC_RedstoneGrid(tile);
		}
		
	}
	
	protected boolean firstTick = true;
	
	private int power = -1;
	
	PC_RedstoneGrid(PC_IRedstoneGridTile tile){
		super(tile);
	}
	
	PC_RedstoneGrid(){
		
	}
	
	public void onUpdateTick() {

		if(this.firstTick)
			return;
		int newPower = 0;
		for(PC_RedstoneNode node:this.nodes){
			int p = node.getPower();
			if(p>newPower){
				newPower = p;
			}
		}
		if (this.power != newPower) {
			this.power = newPower;
			for (PC_RedstoneNode node : this.nodes) {
				node.onRedstonePowerChange();
			}
			for (PC_RedstoneEdge edge : this.edges) {
				edge.onRedstonePowerChange();
			}
		}
	}
	
	@Override
	protected void addTile(PC_IRedstoneGridTile tile) {
		super.addTile(tile);
		onUpdateTick();
	}

	@Override
	protected void removeTile(PC_IRedstoneGridTile tile) {
		super.removeTile(tile);
		onUpdateTick();
	}

	@SuppressWarnings("hiding")
	@Override
	protected void addAll(List<PC_RedstoneNode> nodes, List<PC_RedstoneEdge> edges) {
		super.addAll(nodes, edges);
		onUpdateTick();
	}

	@Override
	protected void splitGridsIfAble() {
		super.splitGridsIfAble();
		onUpdateTick();
	}

	@Override
	protected PC_RedstoneNode createNode(PC_IRedstoneGridTile tile) {
		return new PC_RedstoneNode(this, tile);
	}

	@Override
	protected PC_RedstoneEdge createEdge(PC_RedstoneNode start, PC_RedstoneNode end) {
		return new PC_RedstoneEdge(this, start, end);
	}

	@Override
	protected PC_RedstoneGrid createGrid() {
		return new PC_RedstoneGrid();
	}

	public int getRedstonePowerValue() {
		if (Blocks.redstone_wire.canProvidePower()) return this.power;
		return this.power == 0 ? 0 : this.power - 1;
	}
	
}
