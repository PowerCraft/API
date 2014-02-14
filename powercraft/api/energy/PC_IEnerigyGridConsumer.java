package powercraft.api.energy;

public interface PC_IEnerigyGridConsumer extends PC_IEnergyGridTile {

	public float getEnergyRequested();
	
	public void useEnergy(float energy);
	
}
