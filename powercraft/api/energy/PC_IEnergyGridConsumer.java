package powercraft.api.energy;

public interface PC_IEnergyGridConsumer extends PC_IEnergyGridTile {

	public float getEnergyRequested();
	
	public void useEnergy(float energy);
	
	public float getMaxPercentToWork();
	
}
