package powercraft.api.energy;

public class PC_EnergyNodeProvider extends PC_EnergyNode<PC_IEnerigyGridProvider> {

	protected float useable;
	
	protected float used;
	
	protected PC_EnergyNodeProvider(PC_EnergyGrid grid, PC_IEnerigyGridProvider tile) {
		super(grid, tile);
	}
	
	@Override
	protected boolean canBecomeEdge() {
		return false;
	}

	@Override
	public void onTickStart() {
		useable = getTile().getEnergyUseable();
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
		info.notProduceNeccecerly += useable;
	}

	@Override
	public float takeEnergy() {
		used = useable;
		return useable;
	}

	@Override
	public float notUsing(float energy, float p) {
		used = useable*(1-p);
		energy -= useable-used;
		return energy;
	}
	
}
