package powercraft.api;

import java.util.List;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import powercraft.api.block.PC_AbstractBlockBase;
import powercraft.api.block.PC_Blocks;
import powercraft.api.item.PC_IItem;
import powercraft.api.reflect.PC_Security;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;

final class PC_ForgeHandler implements IFuelHandler, IWorldGenerator {

	public static final PC_ForgeHandler INSTANCE = new PC_ForgeHandler();
	
	static void register(){
		PC_Security.allowedCaller("PC_FuelHandler.register()", PC_Api.class);
		GameRegistry.registerFuelHandler(INSTANCE);
		GameRegistry.registerWorldGenerator(INSTANCE, 0);
	}
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		List<PC_AbstractBlockBase> list = PC_Blocks.getBlocks();
		for(PC_AbstractBlockBase block:list){
			block.generate(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
		}
	}

	@Override
	public int getBurnTime(ItemStack fuel) {
		PC_IItem item = PC_Utils.getItem(fuel, PC_IItem.class);
		if(item!=null){
			return item.getBurnTime(fuel);
		}
		return 0;
	}

}
