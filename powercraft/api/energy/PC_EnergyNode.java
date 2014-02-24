package powercraft.api.energy;

import powercraft.api.grid.PC_Node;

abstract class PC_EnergyNode<T extends PC_IEnergyGridTile> extends PC_Node<PC_EnergyGrid, PC_IEnergyGridTile, PC_EnergyNode<?>, PC_EnergyEdge> {

	PC_EnergyNode(PC_EnergyGrid grid, T tile) {
		super(grid, tile);
	}
	
	@SuppressWarnings("unchecked")
	public T getTile(){
		return (T)tile;
	}
	
	public void onTickStart(){
		
	}
	
	public void onTickEnd(){
		
	}
	
	public abstract float getFlow();
	
	public abstract void addToInfo(PC_EnergyInfo info);

	public abstract float takeEnergy();

	public float useEnergy(float energy, float p) {
		return energy;
	}

	public float notUsing(float energy, float p) {
		return energy;
	}
	
}
