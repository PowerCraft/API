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
import net.minecraftforge.oredict.OreDictionary;
import powercraft.api.PC_3DRotation;
import powercraft.api.PC_Api;
import powercraft.api.PC_BlockTemperatures;
import powercraft.api.PC_ClientRegistry;
import powercraft.api.PC_Direction;
import powercraft.api.PC_IconRegistry;
import powercraft.api.PC_Module;
import powercraft.api.PC_Utils;
import powercraft.api.redstone.PC_RedstoneConnectable;
import powercraft.api.renderer.PC_Renderer;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class PC_AbstractBlockBase extends Block implements PC_RedstoneConnectable {

	private static final CreativeTabs[] NULLCREATIVTABS = {};
	
	private final ModContainer module;
	private CreativeTabs[] creativeTabs = NULLCREATIVTABS;
	private boolean constructed;
	
	PC_AbstractBlockBase(Material material) {
		super(material);
		PC_Blocks.addBlock(this);
		this.module = PC_Utils.getActiveMod();
	}

	@SuppressWarnings("static-method")
	public Class<? extends PC_ItemBlock> getItemBlock(){
		return PC_ItemBlock.class;
	}
	
	@SuppressWarnings("static-method")
	public Object[] getItemBlockConstructorData(){
		return null;
	}
	
	public String getRegisterName() {
		return getClass().getSimpleName();
	}
	
	public String getTextureFolderName() {
		return getClass().getSimpleName().replaceAll("PC.*_(Block)?", "");
	}
	
	public final PC_Module getModule() {
		return (PC_Module)this.module.getMod();
	}
	
	@SuppressWarnings("static-method")
	public String[] getOreNames(){
		return null;
	}
	
	@Override
	public Block setCreativeTab(CreativeTabs creativeTab) {
		if(creativeTab==null){
			this.creativeTabs = NULLCREATIVTABS;
		}else{
			if(this.constructed){
				List<CreativeTabs> creativeTabList = new ArrayList<CreativeTabs>();
				creativeTabList.add(creativeTab);
				if(!creativeTabList.contains(getModule().getCreativeTab()))
					creativeTabList.add(getModule().getCreativeTab());
				if(!creativeTabList.contains(PC_Api.INSTANCE.getCreativeTab()))
					creativeTabList.add(PC_Api.INSTANCE.getCreativeTab());
				this.creativeTabs = creativeTabList.toArray(new CreativeTabs[creativeTabList.size()]);
			}else{
				this.creativeTabs = new CreativeTabs[]{creativeTab};
			}
		}
		return this;
	}

	@SuppressWarnings("hiding")
	void construct() {
		PC_Module module = getModule();
		Object[] itemBlockConstructorData = getItemBlockConstructorData();
		if(itemBlockConstructorData==null)
			itemBlockConstructorData = new Object[0];
		setBlockName(getRegisterName());
		GameRegistry.registerBlock(this, getItemBlock(), getRegisterName(), module.getModId(), itemBlockConstructorData);
		String[] oreNames = getOreNames();
		if(oreNames!=null){
			for(String oreName:oreNames){
				OreDictionary.registerOre(oreName, this);
			}
		}
		this.constructed = true;
		if(this.creativeTabs.length>0)
			setCreativeTab(this.creativeTabs[0]);
	}
	
	public void initRecipes(){
		//
	}
	
	@Override
	public final boolean isBlockSolid(IBlockAccess world, int x, int y, int z, int side) {
		return isSideSolid(world, x, y, z, PC_Utils.getSidePositionInv(world, x, y, z, side));
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, PC_Direction side){
		return getIcon(side, PC_Utils.getMetadata(world, x, y, z));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public final IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		return getIcon(world, x, y, z, PC_Utils.getSidePositionInv(world, x, y, z, side));
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(PC_Direction side, int metadata) {
		return super.getIcon(side.ordinal(), metadata);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public final IIcon getIcon(int side, int metadata) {
		return getIcon(PC_Direction.fromSide(side), metadata);
	}

	@SuppressWarnings({ "static-method", "unused" })
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
			b = PC_Utils.rotateAABB(world, x, y, z, b).offset(x, y, z);
			if(b.intersectsWith(box)){
				list.add(b);
			}
		}
		}
	}

	public AxisAlignedBB getMainCollisionBoundingBox(World world, int x, int y, int z){
		return getMainCollisionBoundingBoxPre(world, x, y, z);
	}
	
	@SuppressWarnings("unused")
	public AxisAlignedBB getMainCollisionBoundingBoxPre(World world, int x, int y, int z) {
		return AxisAlignedBB.getAABBPool().getAABB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
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

	@SuppressWarnings({ "static-method", "unused" })
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, PC_Direction side) {
		return false;
	}
	
	@Override
	public final boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return onBlockActivated(world, x, y, z, player, PC_Utils.getSidePosition(world, x, y, z, side));
	}
	
	@Override
	public final int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
		return isProvidingStrongPower(world, x, y, z, side);
	}
	
	@Override
	public final int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
		return getRedstonePowerValue(world, x, y, z, PC_Utils.getSidePosition(world, x, y, z, side), -1);
	}

	@SuppressWarnings("unused")
	public boolean canRedstoneConnect(IBlockAccess world, int x, int y, int z, PC_Direction side, int faceSide){
		return canProvidePower();
	}
	
	@Override
	public final boolean canRedstoneConnectTo(World world, int x, int y, int z, PC_Direction side, int faceSide){
		return canRedstoneConnect(world, x, y, z, PC_Utils.getSidePosition(world, x, y, z, side), PC_Utils.getSideRotation(world, x, y, z, side, faceSide));
	}
	
	@SuppressWarnings({ "static-method", "unused" })
	public int getRedstonePowerValue(IBlockAccess world, int x, int y, int z, PC_Direction side, int faceSide){
		return 0;
	}
	
	@Override
	public final int getRedstonePower(World world, int x, int y, int z, PC_Direction side, int faceSide){
		return getRedstonePowerValue(world, x, y, z, PC_Utils.getSidePosition(world, x, y, z, side), PC_Utils.getSideRotation(world, x, y, z, side, faceSide));
	}
	
	@SuppressWarnings("unused")
	public void setRedstonePowerValue(World world, int x, int y, int z, PC_Direction side, int faceSide, int value){
		//
	}
	
	@Override
	public final void setRedstonePower(World world, int x, int y, int z, PC_Direction side, int faceSide, int value){
		setRedstonePowerValue(world, x, y, z, PC_Utils.getSidePosition(world, x, y, z, side), PC_Utils.getSideRotation(world, x, y, z, side, faceSide), value);
	}
	
	@SuppressWarnings({ "static-method", "unused" })
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
		registerIcons(PC_ClientRegistry.getIconRegistry(iconRegister, this));
	}

	@SuppressWarnings("unused")
	public void registerIcons(PC_IconRegistry iconRegistry){
		//
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
	
	@Override
	public final boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
		return canRedstoneConnect(world, x, y, z, PC_Utils.getSidePosition(world, x, y, z, PC_Direction.DOWN), PC_Utils.getSideRotation(world, x, y, z, PC_Direction.DOWN, side));
	}

	@SuppressWarnings({ "static-method", "unused" })
	public boolean canSustainPlant(IBlockAccess world, int x, int y, int z, PC_Direction side, IPlantable plantable) {
		return false;
	}
	
	@Override
	public final boolean canSustainPlant(IBlockAccess world, int x, int y, int z, ForgeDirection direction, IPlantable plantable) {
		return canSustainPlant(world, x, y, z, PC_Utils.getSidePosition(world, x, y, z, direction), plantable);
	}

	@Override
	public final boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
		return PC_Utils.rotateBlock(world, x, y, z, PC_Direction.fromForgeDirection(axis));
	}

	@Override
	public final ForgeDirection[] getValidRotations(World world, int x, int y, int z) {
		return PC_Utils.getValidRotations(world, x, y, z);
	}

	@SuppressWarnings({ "unused", "static-method" })
	public boolean recolourBlock(World world, int x, int y, int z, PC_Direction side, int colour) {
		return false;
	}
	
	@Override
	public final boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int colour) {
		return recolourBlock(world, x, y, z, PC_Utils.getSidePosition(world, x, y, z, side), colour);
	}

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		//
	}

	public CreativeTabs[] getCreativeTabs() {
		return this.creativeTabs;
	}

	@SuppressWarnings({ "static-method", "unused" })
	public int getTemperature(World world, int x, int y, int z) {
		return PC_BlockTemperatures.DEFAULT_TEMPERATURE;
	}

	@SuppressWarnings("static-method")
	public int getTemperature() {
		return PC_BlockTemperatures.DEFAULT_TEMPERATURE;
	}
	
}
