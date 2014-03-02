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
		this.level = getTile().getEnergyLevel();
		this.maxIn = getTile().getEnergyMaxIn();
		this.maxOut = getTile().getEnergyMaxOut();
		this.used = 0;
	}

	@Override
	public void onTickEnd() {
		getTile().addEnergy(this.used);
	}

	@Override
	public float getFlow() {
		return this.used;
	}

	@Override
	public void addToInfo(PC_EnergyInfo info) {
		info.energyWantBuffers += this.maxIn + this.maxOut;
	}

	@Override
	public float takeEnergy() {
		this.level -= this.maxOut;
		this.used = -this.maxOut;
		return this.maxOut;
	}

	@Override
	public int compareTo(PC_EnergyNodeBuffer o) {
		return this.level>o.level?1:this.level<o.level?-1:0;
	}

	public float addEnergy(float energy, float toAdd) {
		this.used += toAdd;
		float nta = toAdd;
		if(this.used>this.maxIn){
			nta -= this.used-this.maxIn;
			this.used=this.maxIn;
		}
		return energy-nta;
	}

	public boolean full() {
		return this.used>=this.maxIn;
	}
	
}
