package powercraft.api.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import powercraft.api.PC_3DRotation;
import powercraft.api.PC_BlockTemperatures;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Registry;
import powercraft.api.PC_Utils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class PC_BlockTileEntity extends PC_AbstractBlockBase implements ITileEntityProvider {
	
	public PC_BlockTileEntity(Material material) {
		super(material);
	}
	
	public abstract Class<? extends PC_TileEntity> getTileEntityClass();
	
	@Override
	public PC_TileEntity createNewTileEntity(World world, int metadata){
		try {
			return getTileEntityClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	void construct() {
		super.construct();
		Class<? extends PC_TileEntity> tileEntity = getTileEntityClass();
		PC_Registry.registerTileEntity(tileEntity);
	}
	
	@Override
	public boolean canRotate(){
		return getTileEntityClass().isAssignableFrom(PC_TileEntityRotateable.class);
	}
	
	@Override
	public boolean canRotate(IBlockAccess world, int x, int y, int z) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.canRotate();
		}
		return false;
	}

	@Override
	public PC_3DRotation getRotation(IBlockAccess world, int x, int y, int z) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null && te.canRotate()){
			return te.get3DRotation();
		}
		return null;
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			te.onBreak();
			world.removeTileEntity(x, y, z);
		}
	}

	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			float hardness = te.getHardness();
			if(!Float.isNaN(hardness)){
				return hardness;
			}
		}
		return super.getBlockHardness(world, x, y, z);
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, PC_Direction side) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			IIcon icon = te.getIcon(side);
			if(icon!=null){
				return icon;
			}
		}
		return super.getIcon(world, x, y, z, side);
	}
	
	@Override
	public List<AxisAlignedBB> getCollisionBoundingBoxes(World world, int x, int y, int z, Entity entity) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			List<AxisAlignedBB> list = te.getCollisionBoundingBoxes(entity);
			if(list!=null){
				return list;
			}
		}
		return super.getCollisionBoundingBoxes(world, x, y, z, entity);
	}

	@Override
	public AxisAlignedBB getMainCollisionBoundingBox(World world, int x, int y, int z) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			AxisAlignedBB box = te.getMainCollisionBoundingBox();
			if(box!=null){
				return box;
			}
		}
		return super.getMainCollisionBoundingBox(world, x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(World world, int x, int y, int z) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			AxisAlignedBB box = te.getSelectedBoundingBox();
			if(box!=null){
				return box;
			}
		}
		return super.getSelectedBoundingBox(world, x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			te.randomDisplayTick();
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			te.onNeighborBlockChange(neighbor);
		}
	}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
        float hardness = Float.NaN;
        
        PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			hardness = te.getPlayerRelativeHardness(player);
		}
		
		if(Float.isNaN(hardness)){
			hardness = getBlockHardness(world, x, y, z);
		}
		
        if (hardness < 0.0F){
            return 0.0F;
        }

        Block block = PC_Utils.getBlock(world, x, y, z);
        
        if (!ForgeHooks.canHarvestBlock(block, player, metadata)){
            return player.getBreakSpeed(block, true, metadata, x, y, z) / hardness / 100F;
        }
		return player.getBreakSpeed(block, false, metadata, x, y, z) / hardness / 30F;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, PC_Direction side) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.onBlockActivated(player, side);
		}
		return false;
	}

	@Override
	public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			te.onEntityWalking(entity);
		}
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			te.onBlockClicked(player);
		}
	}

	@Override
	public void velocityToAddToEntity(World world, int x, int y, int z, Entity entity, Vec3 velocity) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			te.velocityToAddToEntity(entity, velocity);
		}
	}

	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.getColorMultiplier();
		}
		return 16777215;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			te.onEntityCollidedWithBlock(entity);
		}
	}
	
	@Override
	public void onFallenUpon(World world, int x, int y, int z, Entity entity, float fallDistance) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			te.onFallenUpon(entity, fallDistance);
		}
	}

	@Override
	public int getDamageValue(World world, int x, int y, int z) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.getDamageValue();
		}
		return super.getDamageValue(world, x, y, z);
	}

	@Override
	public void fillWithRain(World world, int x, int y, int z) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			te.fillWithRain();
		}
	}

	@Override
	public int getComparatorInput(World world, int x, int y, int z, PC_Direction side) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.getComparatorInput(side);
		}
		return 0;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			int value = te.getLightValue();
			if(value!=-1)
				return value;
		}
		return super.getLightValue(world, x, y, z);
	}

	@Override
	public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.isLadder(entity);
		}
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, PC_Direction side) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.isSideSolid(side);
		}
		return super.isSideSolid(world, x, y, z, side);
	}

	@Override
	public boolean isBurning(IBlockAccess world, int x, int y, int z) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.isBurning();
		}
		return false;
	}

	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, PC_Direction side) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.getFlammability(side);
		}
		return 0;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, PC_Direction side) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.isFlammable(side);
		}
		return false;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, PC_Direction side) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.getFireSpreadSpeed(side);
		}
		return 0;
	}

	@Override
	public boolean isFireSource(World world, int x, int y, int z, PC_Direction side) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.isFireSource(side);
		}
		return false;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			ArrayList<ItemStack> drops = te.getDrops(fortune);
			if(drops!=null){
				return drops;
			}
		}
		return super.getDrops(world, x, y, z, metadata, fortune);
	}

	@Override
	public boolean canSilkHarvest(){
		return false;
	}
	
	@Override
	public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.canSilkHarvest(player);
		}
		return canSilkHarvest();
	}

	@Override
	public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.canCreatureSpawn(type);
		}
		return super.canCreatureSpawn(type, world, x, y, z);
	}

	@Override
	public boolean canSustainLeaves(IBlockAccess world, int x, int y, int z) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.canSustainLeaves();
		}
		return false;
	}

	@Override
	public float getExplosionResistance(Entity entity, World world, int x,
			int y, int z, double explosionX, double explosionY,
			double explosionZ) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			float resistance = te.getExplosionResistance(entity, explosionX, explosionY, explosionZ);
			if(!Float.isNaN(resistance)){
				return resistance;
			}
		}
		return getExplosionResistance(entity);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			ItemStack itemStack = te.getPickBlock(target);
			if(itemStack!=null)
				return itemStack;
		}
		return super.getPickBlock(target, world, x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.addDestroyEffects(effectRenderer);
		}
		return false;
	}

	@Override
	public boolean canSustainPlant(IBlockAccess world, int x, int y, int z, PC_Direction side, IPlantable plantable) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.canSustainPlant(side, plantable);
		}
		return false;
	}

	@Override
	public void onPlantGrow(World world, int x, int y, int z, int sourceX, int sourceY, int sourceZ) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			te.onPlantGrow(sourceX, sourceY, sourceZ);
		}
	}

	@Override
	public boolean isFertile(World world, int x, int y, int z) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.isFertile();
		}
		return false;
	}

	@Override
	public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			int opacity = te.getLightOpacity();
			if(opacity!=-1)
				return opacity;
		}
		return super.getLightOpacity(world, x, y, z);
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.canEntityDestroy(entity);
		}
		return true;
	}

	@Override
	public boolean isBeaconBase(IBlockAccess world, int x, int y, int z, int beaconX, int beaconY, int beaconZ) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.isBeaconBase(beaconX, beaconY, beaconZ);
		}
		return false;
	}

	@Override
	public float getEnchantPowerBonus(World world, int x, int y, int z) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.getEnchantPowerBonus();
		}
		return 0;
	}

	@Override
	public boolean recolourBlock(World world, int x, int y, int z, PC_Direction side, int colour) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.recolourBlock(side, colour);
		}
		return false;
	}

	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			te.onNeighborTEChange(tileX, tileY, tileZ);
		}
	}

	@Override
	public boolean shouldCheckWeakPower(IBlockAccess world, int x, int y, int z, PC_Direction side) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.shouldCheckWeakPower(side);
		}
		return super.shouldCheckWeakPower(world, x, y, z, side);
	}

	@Override
	public boolean getWeakChanges(IBlockAccess world, int x, int y, int z) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.getWeakChanges();
		}
		return false;
	}

	@Override
	public final void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int metadata){/**/}
	
	public void onHarvestBlock(World world, EntityPlayer player, int x, int y, int z){
		if(!PC_Utils.isCreative(player)){
			int metadata = PC_Utils.getMetadata(world, x, y, z);
			super.harvestBlock(world, player, x, y, z, metadata);
		}
	}
	
	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		int metadata = PC_Utils.getMetadata(world, x, y, z);
		if(canHarvestBlock(player, metadata)){
			onHarvestBlock(world, player, x, y, z);
			return super.removedByPlayer(world, player, x, y, z);
		}
		return false;
	}

	@Override
	public int modifiyMetadataPreSet(World world, int x, int y, int z, PC_Direction side, ItemStack stack, EntityPlayer player, float hitX, float hitY, float hitZ, int metadata) {
		return metadata;
	}

	@Override
	public void onBlockPostSet(World world, int x, int y, int z, PC_Direction side, ItemStack stack, EntityPlayer player, float hitX, float hitY, float hitZ, int metadata){
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			te.onBlockPostSet(side, stack, player, hitX, hitY, hitZ);
		}
	}
	
	@Override
	public boolean onBlockEventReceived(World world, int x, int y, int z, int event, int argument){
		TileEntity te = PC_Utils.getTileEntity(world, x, y, z);
		if(te!=null){
			te.receiveClientEvent(event, argument);
		}
	    return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, int modelId, RenderBlocks renderer) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.renderWorldBlock(modelId, renderer);
		}
		return super.renderWorldBlock(world, x, y, z, modelId, renderer);
	}

	@Override
	public boolean canRedstoneConnect(IBlockAccess world, int x, int y, int z, PC_Direction side, int faceSide) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.canRedstoneConnect(side, faceSide);
		}
		return canProvidePower();
	}

	@Override
	public boolean canProvideStrongPower(IBlockAccess world, int x, int y, int z, PC_Direction side){
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.canProvideStrongPower(side);
		}
		return true;
	}
	
	@Override
	public int getRedstonePowerValue(IBlockAccess world, int x, int y, int z, PC_Direction side, int faceSide) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.getRedstonePowerValue(side, faceSide);
		}
		return 0;
	}

	@Override
	public void setRedstonePowerValue(World world, int x, int y, int z, PC_Direction side, int faceSide, int value) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			te.setRedstonePowerValue(side, faceSide, value);
		}
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		PC_TileEntity tileEntity = createNewTileEntity(world, PC_Utils.getMetadata(world, x, y, z));
		tileEntity.validate();
		EntityPlayer player = PC_ItemBlock.playerStetting.get();
		tileEntity.onPreAdded(player);
		world.setTileEntity(x, y, z, tileEntity);
		tileEntity.updateContainingBlockInfo();
		tileEntity.onAdded(player);
	}
	
	@Override
	public int getTemperature(World world, int x, int y, int z) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntity.class);
		if(te!=null){
			return te.getTemperature();
		}
		return PC_BlockTemperatures.DEFAULT_TEMPERATURE;
	}
	
}
