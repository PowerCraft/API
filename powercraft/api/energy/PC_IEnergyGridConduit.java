package powercraft.api.energy;

public interface PC_IEnergyGridConduit extends PC_IEnergyGridTile {

	public float getMaxEnergy();
	
	public void setEnergyFlow(float energy);
	
	public void handleToMuchEnergy(float energy);
	
}
