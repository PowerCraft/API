package powercraft.api.energy;

final class PC_EnergyNodeConsumer extends PC_EnergyNode<PC_IEnergyGridConsumer> {

	protected float requested;
	
	protected float useable;
	
	protected float maxWorkPercent;
	
	PC_EnergyNodeConsumer(PC_EnergyGrid grid, PC_IEnergyGridConsumer tile) {
		super(grid, tile);
	}

	@Override
	protected boolean canBecomeEdge() {
		return false;
	}
	
	@Override
	public void onTickStart(){
		this.useable = 0;
		this.requested = getTile().getEnergyRequested();
		this.maxWorkPercent = getTile().getMaxPercentToWork();
	}
	
	@Override
	public void onTickEnd() {
		getTile().useEnergy(this.useable);
	}

	@Override
	public float getFlow() {
		return this.useable;
	}

	@Override
	public void addToInfo(PC_EnergyInfo info) {
		info.energyRequested += this.requested;
	}

	@Override
	public float takeEnergy() {
		return 0;
	}

	@Override
	public float useEnergy(float energy, float p) {
		if(this.maxWorkPercent<=p){
			this.useable = this.requested*p;
			return energy - this.useable;
		}
		return energy;
	}
	
}
