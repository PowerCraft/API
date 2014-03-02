package powercraft.api.energy;

final class PC_EnergyNodeProvider extends PC_EnergyNode<PC_IEnergyGridProvider> {

	protected float useable;
	
	protected float used;
	
	protected boolean dynamic;
	
	PC_EnergyNodeProvider(PC_EnergyGrid grid, PC_IEnergyGridProvider tile) {
		super(grid, tile);
	}
	
	@Override
	protected boolean canBecomeEdge() {
		return false;
	}

	@Override
	public void onTickStart() {
		this.used = 0;
		this.useable = getTile().getEnergyUseable();
		this.dynamic = getTile().dynamic();
	}

	@Override
	public void onTickEnd() {
		getTile().takeEnergy(this.used);
	}

	@Override
	public float getFlow() {
		return -this.used;
	}

	@Override
	public void addToInfo(PC_EnergyInfo info) {
		if(this.dynamic)
			info.notProduceNeccecerly += this.useable;
	}

	@Override
	public float takeEnergy() {
		this.used = this.useable;
		return this.useable;
	}

	@Override
	public float notUsing(float energy, float p) {
		if(this.dynamic){
			this.used = this.useable*(1-p);
			return energy - this.useable + this.used;
		}
		return energy;
	}
	
}
