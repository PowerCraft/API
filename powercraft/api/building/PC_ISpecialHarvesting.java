package powercraft.api.building;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import powercraft.api.building.PC_Build.ItemStackSpawn;

public interface PC_ISpecialHarvesting {

	public boolean useFor(World world, int x, int y, int z, Block block, int meta, int priority);
	
	public List<ItemStackSpawn> harvest(World world, int x, int y, int z, Block block, int meta, int fortune);

}
