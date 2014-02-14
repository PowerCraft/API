package powercraft.api.energy;

public interface PC_IEnerigyGridConduit extends PC_IEnergyGridTile {

	public float getMaxEnergy();
	
	public void setEnergyFlow(float energy);
	
	public void handleToMuchEnergy(float energy);
	
}
