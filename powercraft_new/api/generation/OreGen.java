package powercraft_new.api.generation;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import cpw.mods.fml.common.IWorldGenerator;

/**
 * @author James
 * Handles ore generation
 */
public class OreGen implements IWorldGenerator{

	private int[][] data;
	
	@Override
	public final void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {	
		for(int i = 0; this.data[0].length < i; i++){
			if((this.data[0][i] == world.getWorldInfo().getVanillaDimension()) || this.data[0][i] == 298){
				for(int i2 = 0; i2 < this.data[1][i]; i2++){
					int Xcoord = chunkX * 16 + random.nextInt(this.data[2][i]);
					int Ycoord = chunkZ * 16 + random.nextInt(this.data[3][i]);
					int Zcoord = chunkZ * 16 + random.nextInt(this.data[4][i]);
					(new WorldGenMinable(this.data[5][i], this.data[6][i], this.data[7][i])).generate(world, random, Xcoord, Ycoord, Zcoord);
				}
			}
		}
	}
	
	/**
	 * @param dim The dimension to add to
	 * @param hight The hight it generates at
	 * @param xChance The x chance
	 * @param yChance The y chance
	 * @param zChance The z chance
	 * @param blockID The block ID
	 * @param size The vein size
	 * @param replaceblock The block type to replace
	 */
	public final void addGen(int dim, int hight, int xChance, int yChance, int zChance, int blockID, int size, int replaceblock){
		this.data[0][this.data[0].length + 1] = dim;
		this.data[1][this.data[0].length + 1] = hight;
		this.data[2][this.data[0].length + 1] = xChance;
		this.data[3][this.data[0].length + 1] = yChance;
		this.data[4][this.data[0].length + 1] = zChance;
		this.data[5][this.data[0].length + 1] = blockID;
		this.data[6][this.data[0].length + 1] = size;
		this.data[7][this.data[0].length + 1] = replaceblock;
	}
	
	/**
	 * @param dim The dimension to add to
	 * @param hight The hight it generates at
	 * @param xChance The x chance
	 * @param yChance The y chance
	 * @param zChance The z chance
	 * @param blockID The block ID
	 * @param size The vein size
	 */
	public final void addGen(int dim, int hight, int xChance, int yChance, int zChance, int blockID, int size){
		this.addGen(dim, hight, xChance, yChance, zChance, blockID, size, 4);
	}
	
	/**
	 * @param dim The dimension to add to
	 * @param hight The hight it generates at
	 * @param Chance the chance
	 * @param blockID The block ID
	 * @param size The vein size
	 */
	public final void addGen(int dim, int hight, int Chance, int blockID, int size){
		this.addGen(dim, hight, Chance, Chance, Chance, blockID, size, 4);
	}
	
	/**
	 * @param hight The hight it generates at
	 * @param xChance The x chance
	 * @param yChance The y chance
	 * @param zChance The z chance
	 * @param blockID The block ID
	 * @param size The vein size
	 */
	public final void addGen(int hight, int xChance, int yChance, int zChance, int blockID, int size){
		this.addGen(298, hight, xChance, yChance, zChance, blockID, size, 4);
	}
	
	/**
	 * @param hight The hight it generates at
	 * @param Chance the chance
	 * @param blockID The block ID
	 * @param size The vein size
	 */
	public final void addGen(int hight, int Chance, int blockID, int size){
		this.addGen(298, hight, Chance, Chance, Chance, blockID, size, 4);
	}
}
