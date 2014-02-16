package powercraft.api.energy;

public class PC_EnergyNodeConsumer extends PC_EnergyNode<PC_IEnergyGridConsumer> {

	protected float requested;
	
	protected float useable;
	
	protected float maxWorkPercent;
	
	protected PC_EnergyNodeConsumer(PC_EnergyGrid grid, PC_IEnergyGridConsumer tile) {
		super(grid, tile);
	}

	@Override
	protected boolean canBecomeEdge() {
		return false;
	}
	
	@Override
	public void onTickStart(){
		requested = getTile().getEnergyRequested();
		maxWorkPercent = getTile().getMaxPercentToWork();
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
		info.energyRequested += requested;
	}

	@Override
	public float takeEnergy() {
		return 0;
	}

	@Override
	public float useEnergy(float energy, float p) {
		if(maxWorkPercent<=p){
			useable = requested*p;
			energy -= useable;
		}
		return energy;
	}
	
}
