package powercraft.api.energy;

public interface PC_IEnergyGridProvider extends PC_IEnergyGridTile {

	public float getEnergyUseable();
	
	public void takeEnergy(float energy);
	
	public boolean dynamic();
	
}
