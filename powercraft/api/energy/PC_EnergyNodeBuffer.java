package powercraft.api.energy;

final class PC_EnergyNodeBuffer extends PC_EnergyNode<PC_IEnergyGridBuffer> implements Comparable<PC_EnergyNodeBuffer> {

	protected float level;
	protected float maxIn;
	protected float maxOut;
	protected float used;
	
	PC_EnergyNodeBuffer(PC_EnergyGrid grid, PC_IEnergyGridBuffer tile) {
		super(grid, tile);
	}

	@Override
	protected boolean canBecomeEdge() {
		return false;
	}

	@Override
	public void onTickStart() {
		level = getTile().getEnergyLevel();
		maxIn = getTile().getEnergyMaxIn();
		maxOut = getTile().getEnergyMaxOut();
	}

	@Override
	public void onTickEnd() {
		getTile().addEnergy(used);
	}

	@Override
	public float getFlow() {
		return used;
	}

	@Override
	public void addToInfo(PC_EnergyInfo info) {
		info.energyWantBuffers += maxIn + maxOut;
	}

	@Override
	public float takeEnergy() {
		level -= maxOut;
		used = -maxOut;
		return maxOut;
	}

	@Override
	public int compareTo(PC_EnergyNodeBuffer o) {
		return level>o.level?1:level<o.level?-1:0;
	}

	public float addEnergy(float energy, float toAdd) {
		used += toAdd;
		if(used>maxIn){
			toAdd -= used-maxIn;
			used=maxIn;
		}
		return energy-toAdd;
	}

	public boolean full() {
		return used>=maxIn;
	}
	
}
