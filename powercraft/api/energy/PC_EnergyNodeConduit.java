package powercraft.api.energy;

public class PC_EnergyNodeConduit extends PC_EnergyNode<PC_IEnerigyGridConduit> {

	protected PC_EnergyNodeConduit(PC_EnergyGrid grid, PC_IEnerigyGridConduit tile) {
		super(grid, tile);
	}
	
	public float getMaxEnergy(){
		return getTile().getMaxEnergy();
	}

	@Override
	public float getFlow() {
		return 0;
	}

	@Override
	public void addToInfo(PC_EnergyInfo info) {
		
	}

	@Override
	public float takeEnergy() {
		return 0;
	}
	
}
