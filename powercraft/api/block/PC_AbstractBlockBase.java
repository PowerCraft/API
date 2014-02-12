package powercraft.api.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import powercraft.api.PC_3DRotation;
import powercraft.api.PC_Api;
import powercraft.api.PC_Direction;
import powercraft.api.PC_IconRegistry;
import powercraft.api.PC_IconRegistryImpl;
import powercraft.api.PC_Module;
import powercraft.api.PC_Utils;
import powercraft.api.renderer.PC_Renderer;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class PC_AbstractBlockBase extends Block {

	private static final CreativeTabs[] NULLCREATIVTABS = {};
	
	private final ModContainer module;
	private CreativeTabs[] creativeTabs = NULLCREATIVTABS;
	private boolean constructed;
	
	PC_AbstractBlockBase(Material material) {
		super(material);
		PC_Blocks.addBlock(this);
		module = PC_Utils.getActiveMod();
	}

	public Class<? extends PC_ItemBlock> getItemBlock(){
		return PC_ItemBlock.class;
	}
	
	public Object[] getItemBlockConstructorData(){
		return null;
	}
	
	public String getRegisterName() {
		return getClass().getSimpleName();
	}
	
	public final PC_Module getModule() {
		return (PC_Module)module.getMod();
	}
	
	@Override
	public Block setCreativeTab(CreativeTabs creativeTab) {
		if(creativeTab==null){
			creativeTabs = NULLCREATIVTABS;
		}else{
			if(constructed){
				List<CreativeTabs> creativeTabList = new ArrayList<CreativeTabs>();
				creativeTabList.add(creativeTab);
				if(!creativeTabList.contains(getModule().getCreativeTab()))
					creativeTabList.add(getModule().getCreativeTab());
				if(!creativeTabList.contains(PC_Api.INSTANCE.getCreativeTab()))
					creativeTabList.add(PC_Api.INSTANCE.getCreativeTab());
				creativeTabs = creativeTabList.toArray(new CreativeTabs[creativeTabList.size()]);
			}else{
				creativeTabs = new CreativeTabs[]{creativeTab};
			}
		}
		return this;
	}

	void construct() {
		PC_Module module = getModule();
		Object[] itemBlockConstructorData = getItemBlockConstructorData();
		if(itemBlockConstructorData==null)
			itemBlockConstructorData = new Object[0];
		setBlockName(getRegisterName());
		GameRegistry.registerBlock(this, getItemBlock(), module.getName()+":"+getRegisterName(), module.getModId(), itemBlockConstructorData);
		constructed = true;
		if(creativeTabs.length>0)
			setCreativeTab(creativeTabs[0]);
	}
	
	@Override
	public final boolean isBlockSolid(IBlockAccess world, int x, int y, int z, int side) {
		return isSideSolid(world, x, y, z, PC_Utils.getSidePosition(world, x, y, z, side));
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, PC_Direction side){
		return getIcon(side, PC_Utils.getMetadata(world, x, y, z));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public final IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		return getIcon(world, x, y, z, PC_Utils.getSidePosition(world, x, y, z, side));
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(PC_Direction side, int metadata) {
		return super.getIcon(side.ordinal(), metadata);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int metadata) {
		return getIcon(PC_Direction.fromSide(side), metadata);
	}

	public List<AxisAlignedBB> getCollisionBoundingBoxes(World world, int x, int y, int z, Entity entity){
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public final void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB box, List list, Entity entity) {
		List<AxisAlignedBB> boxes = getCollisionBoundingBoxes(world, x, y, z, entity);
		if(boxes==null){
			AxisAlignedBB b = getCollisionBoundingBoxFromPool(world, x, y, z);
			if(b.intersectsWith(box)){
				list.add(b);
			}
		}else{
		for(AxisAlignedBB b:boxes){
			b = PC_Utils.rotateAABB(world, x, y, z, b).offset(x, y, z);;
			if(b.intersectsWith(box)){
				list.add(b);
			}
		}
		}
	}

	public AxisAlignedBB getMainCollisionBoundingBox(World world, int x, int y, int z){
		return getMainCollisionBoundingBoxPre(world, x, y, z);
	}
	
	public AxisAlignedBB getMainCollisionBoundingBoxPre(World world, int x, int y, int z) {
		return AxisAlignedBB.getAABBPool().getAABB(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	@Override
	public final AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		AxisAlignedBB box = getMainCollisionBoundingBox(world, x, y, z);
		return PC_Utils.rotateAABB(world, x, y, z, box).offset(x, y, z);
	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(World world, int x, int y, int z){
		return getMainCollisionBoundingBox(world, x, y, z);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public final AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		AxisAlignedBB box = getSelectedBoundingBox(world, x, y, z);
		return PC_Utils.rotateAABB(world, x, y, z, box).offset(x, y, z);
	}

	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, PC_Direction side) {
		return false;
	}
	
	@Override
	public final boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return onBlockActivated(world, x, y, z, player, PC_Utils.getSidePosition(world, x, y, z, side));
	}

	public int getWeakRedstonePower(IBlockAccess world, int x, int y, int z, PC_Direction side) {
		return 0;
	}
	
	@Override
	public final int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
		return getWeakRedstonePower(world, x, y, z, PC_Utils.getSidePosition(world, x, y, z, side));
	}

	public int getStrongRedstonePower(IBlockAccess world, int x, int y, int z, PC_Direction side) {
		return 0;
	}
	
	@Override
	public final int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
		return getStrongRedstonePower(world, x, y, z, PC_Utils.getSidePosition(world, x, y, z, side));
	}

	public int getComparatorInput(World world, int x, int y, int z, PC_Direction side) {
		return 0;
	}
	
	@Override
	public final int getComparatorInputOverride(World world, int x, int y, int z, int side) {
		return getComparatorInput(world, x, y, z, PC_Utils.getSidePosition(world, x, y, z, side));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void registerBlockIcons(IIconRegister iconRegister) {
		registerIcons(new PC_IconRegistryImpl(iconRegister, this));
	}

	public void registerIcons(PC_IconRegistry iconRegistry){
		
	}
	
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, PC_Direction side){
		return super.isSideSolid(world, x, y, z, side.toForgeDirection());
	}
	
	@Override
	public final boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return isSideSolid(world, x, y, z, PC_Utils.getSidePosition(world, x, y, z, side));
	}

	public int getFlammability(IBlockAccess world, int x, int y, int z, PC_Direction side) {
		return super.getFlammability(world, x, y, z, side.toForgeDirection());
	}
	
	@Override
	public final int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return getFlammability(world, x, y, z, PC_Utils.getSidePosition(world, x, y, z, face));
	}

	public boolean isFlammable(IBlockAccess world, int x, int y, int z, PC_Direction side) {
		return super.isFlammable(world, x, y, z, side.toForgeDirection());
	}
	
	@Override
	public final boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return isFlammable(world, x, y, z, PC_Utils.getSidePosition(world, x, y, z, face));
	}

	public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, PC_Direction side) {
		return super.getFireSpreadSpeed(world, x, y, z, side.toForgeDirection());
	}
	
	@Override
	public final int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return getFireSpreadSpeed(world, x, y, z, PC_Utils.getSidePosition(world, x, y, z, face));
	}

	public boolean isFireSource(World world, int x, int y, int z, PC_Direction side) {
		return super.isFireSource(world, x, y, z, side.toForgeDirection());
	}
	
	@Override
	public final boolean isFireSource(World world, int x, int y, int z, ForgeDirection side) {
		return isFireSource(world, x, y, z, PC_Utils.getSidePosition(world, x, y, z, side));
	}

	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, PC_Direction side) {
		return super.canConnectRedstone(world, x, y, z, side.ordinal());
	}
	
	@Override
	public final boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
		return canConnectRedstone(world, x, y, z, PC_Utils.getSidePosition(world, x, y, z, side));
	}

	public boolean canSustainPlant(IBlockAccess world, int x, int y, int z, PC_Direction side, IPlantable plantable) {
		return false;
	}
	
	@Override
	public final boolean canSustainPlant(IBlockAccess world, int x, int y, int z, ForgeDirection direction, IPlantable plantable) {
		return canSustainPlant(world, x, y, z, PC_Utils.getSidePosition(world, x, y, z, direction), plantable);
	}

	@Override
	public final boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
		return PC_Utils.rotateBlock(world, x, y, z, PC_Direction.fromSide(axis));
	}

	@Override
	public final ForgeDirection[] getValidRotations(World world, int x, int y, int z) {
		return PC_Utils.getValidRotations(world, x, y, z);
	}

	public boolean recolourBlock(World world, int x, int y, int z, PC_Direction side, int colour) {
		return false;
	}
	
	@Override
	public final boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int colour) {
		return recolourBlock(world, x, y, z, PC_Utils.getSidePosition(world, x, y, z, side), colour);
	}

	public boolean shouldCheckWeakPower(IBlockAccess world, int x, int y, int z, PC_Direction side) {
		 return isNormalCube();
	}
	
	@Override
	public final boolean shouldCheckWeakPower(IBlockAccess world, int x, int y, int z, int side) {
		return shouldCheckWeakPower(world, x, y, z, PC_Utils.getSidePosition(world, x, y, z, side));
	}
	
	public abstract boolean canRotate();
	
	public abstract boolean canRotate(IBlockAccess world, int x, int y, int z);
	
	public abstract PC_3DRotation getRotation(IBlockAccess world, int x, int y, int z);
	
	public abstract int modifiyMetadataPreSet(World world, int x, int y, int z, PC_Direction side, ItemStack stack, EntityPlayer player, float hitX, float hitY, float hitZ, int metadata);
	
	public abstract void onBlockPostSet(World world, int x, int y, int z, PC_Direction side, ItemStack stack, EntityPlayer player, float hitX, float hitY, float hitZ, int metadata);
	
	@SideOnly(Side.CLIENT)
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, int modelId, RenderBlocks renderer){
		return PC_Renderer.renderBlockInWorld(world, x, y, z, this, modelId, renderer);
	}
	
	@SideOnly(Side.CLIENT)
	public void renderInventoryBlock(int metadata, int modelId, RenderBlocks renderer){
		PC_Renderer.renderBlockInInventory(this, metadata, modelId, renderer);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return PC_Renderer.getInstance().getRenderId();
	}

	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		
	}

	public CreativeTabs[] getCreativeTabs() {
		return creativeTabs;
	}
	
}