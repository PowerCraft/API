package powercraft.api.energy;

public interface PC_IEnerigyGridProvider extends PC_IEnergyGridTile {

	public float getEnergyUseable();
	
	public float takeEnergy(float energy);
	
}
