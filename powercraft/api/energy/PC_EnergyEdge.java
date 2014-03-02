package powercraft.api.energy;

import powercraft.api.grid.PC_Edge;

final class PC_EnergyEdge extends PC_Edge<PC_EnergyGrid, PC_IEnergyGridTile, PC_EnergyNode<?>, PC_EnergyEdge> {

	private float maxEnergy;
	
	PC_EnergyEdge(PC_EnergyGrid grid, PC_EnergyNode<?> start, PC_EnergyNode<?> end) {
		super(grid, start, end);
	}

	@Override
	protected void onChanged() {
		this.maxEnergy = Float.POSITIVE_INFINITY;
		for(PC_IEnergyGridTile tile:this.tiles){
			float conduitMax = ((PC_IEnergyGridConduit)tile).getMaxEnergy();
			if(conduitMax>=0 && conduitMax<this.maxEnergy){
				this.maxEnergy = conduitMax;
			}
		}
	}
	
}
