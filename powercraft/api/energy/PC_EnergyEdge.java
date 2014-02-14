package powercraft.api.energy;

import powercraft.api.grid.PC_Edge;

public class PC_EnergyEdge extends PC_Edge<PC_EnergyGrid, PC_IEnergyGridTile, PC_EnergyNode<?>, PC_EnergyEdge> {

	private float maxEnergy;
	
	protected PC_EnergyEdge(PC_EnergyGrid grid, PC_EnergyNode<?> start, PC_EnergyNode<?> end) {
		super(grid, start, end);
	}

	@Override
	protected void onChanged() {
		maxEnergy = Float.POSITIVE_INFINITY;
		for(PC_IEnergyGridTile tile:tiles){
			float conduitMax = ((PC_IEnerigyGridConduit)tile).getMaxEnergy();
			if(conduitMax>=0 && conduitMax<maxEnergy){
				maxEnergy = conduitMax;
			}
		}
	}
	
}
