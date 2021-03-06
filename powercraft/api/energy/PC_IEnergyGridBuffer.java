package powercraft.api.energy;

public interface PC_IEnergyGridBuffer extends PC_IEnergyGridTile {

	public float getEnergyLevel();
	
	public float getEnergyMaxIn();
	
	public float getEnergyMaxOut();
	
	public float addEnergy(float energy);
	
}
