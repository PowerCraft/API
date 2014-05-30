package powercraft.api.building;

import net.minecraft.world.World;

public interface PC_ISpecialHarvesting {

	public boolean useFor(World world, int x, int y, int z, int priority);
	
	public PC_Harvest harvest(World world, int x, int y, int z, int usesLeft);
	
}
