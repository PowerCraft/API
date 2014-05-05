package powercraft.api.building;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec3I;
import powercraft.api.block.PC_AbstractBlockBase;


public final class PC_Build {
	
	private static List<PC_ISpecialHarvesting> specialHarvestings = new ArrayList<PC_ISpecialHarvesting>();
	
	public static void addSpecialHarvesting(PC_ISpecialHarvesting specialHarvesting){
		if(!specialHarvestings.contains(specialHarvesting)){
			specialHarvestings.add(specialHarvesting);
		}
	}
	
	public static class ItemStackSpawn{
		
		public PC_Vec3I pos;
		public ItemStack itemStack;
		
		public ItemStackSpawn(int x, int y, int z, ItemStack itemStack) {
			this.pos = new PC_Vec3I(x, y, z);
			this.itemStack = itemStack;
		}
		
	}
	
	public static PC_ISpecialHarvesting getSpecialHarvestingFor(World world, int x, int y, int z, Block block, int meta){
		for(int i=0; i<3; i++){
			for(PC_ISpecialHarvesting specialHarvesting:specialHarvestings){
				if(specialHarvesting.useFor(world, x, y, z, block, meta, i)){
					return specialHarvesting;
				}
			}
		}
		return null;
	}
	
	public static List<ItemStackSpawn> harvestWithDropPos(World world, PC_Vec3I pos, int fortune){
		return harvestWithDropPos(world, pos.x, pos.y, pos.z, fortune);
	}
	
	public static List<ItemStackSpawn> harvestWithDropPos(World world, int x, int y, int z, int fortune){
		Block block = PC_Utils.getBlock(world, x, y, z);
		if(block==null)
			return null;
		int meta = PC_Utils.getMetadata(world, x, y, z);
		PC_ISpecialHarvesting specialHarvesting = getSpecialHarvestingFor(world, x, y, z, block, meta);
		if(specialHarvesting!=null){
			return specialHarvesting.harvest(world, x, y, z, block, meta, fortune);
		}
		List<ItemStack> drops = harvestEasy(world, x, y, z, fortune);
		if(drops==null)
			return null;
		List<ItemStackSpawn> dropsWithPos = new ArrayList<ItemStackSpawn>();
		for(ItemStack drop:drops){
			dropsWithPos.add(new ItemStackSpawn(x, y, z, drop));
		}
		return dropsWithPos;
	}
	
	public static List<ItemStack> harvest(World world, PC_Vec3I pos, int fortune){
		return harvest(world, pos.x, pos.y, pos.z, fortune);
	}
	
	public static List<ItemStack> harvest(World world, int x, int y, int z, int fortune){
		Block block = PC_Utils.getBlock(world, x, y, z);
		if(block==null)
			return null;
		int meta = PC_Utils.getMetadata(world, x, y, z);
		PC_ISpecialHarvesting specialHarvesting = getSpecialHarvestingFor(world, x, y, z, block, meta);
		if(specialHarvesting!=null){
			List<ItemStackSpawn> dropsWithPos = specialHarvesting.harvest(world, x, y, z, block, meta, fortune);
			if(dropsWithPos==null)
				return null;
			List<ItemStack> drops = new ArrayList<ItemStack>();
			for(ItemStackSpawn dropWithPos:dropsWithPos){
				drops.add(dropWithPos.itemStack);
			}
			return drops;
		}
		return harvestEasy(world, x, y, z, fortune);
	}
	
	public static List<ItemStack> harvestEasy(World world, int x, int y, int z, int fortune){
		Block block = PC_Utils.getBlock(world, x, y, z);
		if(block==null)
			return null;
		if(!canHarvest(world, x, y, z, block)){
			return null;
		}
		List<ItemStack> drops = block.getDrops(world, x, y, z, PC_Utils.getMetadata(world, x, y, z), fortune);
		if(!world.isRemote)
			PC_Utils.setAir(world, x, y, z);
		return drops;
	}
	
	public static boolean canHarvest(World world, int x, int y, int z, Block block){
		if(block instanceof PC_AbstractBlockBase)
			return ((PC_AbstractBlockBase)block).canBeHarvested(world, x, y, z);
		return true;
	}
	
	public static boolean tryUseItem(World world, int x, int y, int z, PC_Direction dir, ItemStack itemStack){
		if(world instanceof WorldServer)
			return itemStack.getItem().onItemUse(itemStack, FakePlayerFactory.getMinecraft((WorldServer) world), world, x, y, z, dir.ordinal(), 0, 0, 0);
		return true;
	}
	
	private PC_Build(){
		PC_Utils.staticClassConstructor();
	}
	
}
