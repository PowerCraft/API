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
		useable = getTile().getEnergyUseable();
		dynamic = getTile().dynamic();
	}

	@Override
	public void onTickEnd() {
		getTile().takeEnergy(used);
	}

	@Override
	public float getFlow() {
		return -used;
	}

	@Override
	public void addToInfo(PC_EnergyInfo info) {
		if(dynamic)
			info.notProduceNeccecerly += useable;
	}

	@Override
	public float takeEnergy() {
		used = useable;
		return useable;
	}

	@Override
	public float notUsing(float energy, float p) {
		if(dynamic){
			used = useable*(1-p);
			energy -= useable-used;
		}
		return energy;
	}
	
}
