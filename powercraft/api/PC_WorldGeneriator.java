package powercraft.api;

import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import powercraft.api.block.PC_AbstractBlockBase;
import powercraft.api.block.PC_Blocks;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;

public class PC_WorldGeneriator implements IWorldGenerator {

	public static final PC_WorldGeneriator INSTANCE = new PC_WorldGeneriator();
	
	public static void register(){
		PC_Security.allowedCaller("PC_WorldGeneriator.register()", PC_Api.class);
		GameRegistry.registerWorldGenerator(INSTANCE, 0);
	}
	
	private PC_WorldGeneriator(){}
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		List<PC_AbstractBlockBase> list = PC_Blocks.getBlocks();
		for(PC_AbstractBlockBase block:list){
			block.generate(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
		}
	}

}
