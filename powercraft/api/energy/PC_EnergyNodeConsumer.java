package powercraft.api.energy;

public class PC_EnergyNodeConsumer extends PC_EnergyNode<PC_IEnerigyGridConsumer> {

	protected float reqested;
	
	protected float useable;
	
	protected PC_EnergyNodeConsumer(PC_EnergyGrid grid, PC_IEnerigyGridConsumer tile) {
		super(grid, tile);
	}

	@Override
	protected boolean canBecomeEdge() {
		return false;
	}
	
	@Override
	public void onTickStart(){
		reqested = getTile().getEnergyRequested();
	}
	
	@Override
	public void onTickEnd() {
		getTile().useEnergy(useable);
	}

	@Override
	public float getFlow() {
		return useable;
	}

	@Override
	public void addToInfo(PC_EnergyInfo info) {
		info.energyRequested += reqested;
	}

	@Override
	public float takeEnergy() {
		return 0;
	}

	@Override
	public float useEnergy(float energy, float p) {
		useable = reqested*p;
		energy -= useable;
		return energy;
	}
	
}
