package powercraft.api.multiblock;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Field;
import powercraft.api.PC_INBT;
import powercraft.api.PC_NBTTagHandler;
import powercraft.api.PC_Field.Flag;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.network.packet.PC_PacketMultiblockObjectSync;
import powercraft.api.reflect.PC_Processor;
import powercraft.api.reflect.PC_Reflection;

public abstract class PC_MultiblockObject implements PC_INBT{

	private boolean sync;
	
	protected PC_MultiblockIndex index;
	protected PC_TileEntityMultiblock multiblock;
	@PC_Field(flags={Flag.SAVE, Flag.SYNC})
	protected int thickness;
	protected NBTTagCompound tagCompound;
	
	public PC_MultiblockObject(NBTTagCompound tagCompound, Flag flag) {
		readFromNBT(tagCompound, flag);
	}
	
	public PC_MultiblockObject(int thickness) {
		this.thickness = thickness;
	}

	public PC_TileEntityMultiblock getTileEntity() {
		return multiblock;
	}


	public int getThickness() {
		return thickness;
	}
	
	public boolean isClient() {
		return multiblock.isClient();
	}
	
	public List<AxisAlignedBB> getCollisionBoundingBoxes() {
		return null;
	}
	public AxisAlignedBB getSelectedBoundingBox() {
		AxisAlignedBB aabb = AxisAlignedBB.getAABBPool().getAABB(0, 0, 0, 0, 0, 0);
		List<AxisAlignedBB> list = getCollisionBoundingBoxes();
		if(list==null){
			return aabb;
		}
		aabb = list.get(0);
		aabb = AxisAlignedBB.getBoundingBox(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
		for(AxisAlignedBB e:list){
			if(aabb.minX>e.minX){
				aabb.minX = e.minX;
			}
			if(aabb.minY>e.minY){
				aabb.minY = e.minY;
			}
			if(aabb.minZ>e.minZ){
				aabb.minZ = e.minZ;
			}
			if(aabb.maxX<e.maxX){
				aabb.maxX = e.maxX;
			}
			if(aabb.maxY<e.maxY){
				aabb.maxY = e.maxY;
			}
			if(aabb.maxZ<e.maxZ){
				aabb.maxZ = e.maxZ;
			}
		}
		return aabb;
	}
	
	public List<ItemStack> getDrop() {
		return null;
	}

	public void onPreRemove() {}

	public void onRemoved() {}

	public void onClicked(EntityPlayer player) {
		
	}

	public ItemStack getPickBlock() {
		return new ItemStack(PC_Multiblocks.getItem(this), 1, 0);
	}

	public boolean onBlockActivated(EntityPlayer player) {
		return false;
	}

	public void onNeighborBlockChange(Block neighbor) {
		
	}

	public float getPlayerRelativeHardness(EntityPlayer player) {
		return 0;
	}

	public void fillWithRain() {
		
	}

	public int getLightValue() {
		return 0;
	}

	public boolean isLadder(EntityLivingBase entity) {
		return false;
	}

	public boolean isBurning() {
		return false;
	}

	public float getEnchantPowerBonus() {
		return 0;
	}

	public void onNeighborTEChange(int tileX, int tileY, int tileZ) {
		
	}

	public void renderWorldBlock(RenderBlocks renderer) {
		
	}

	public boolean isSolid() {
		return false;
	}

	public boolean canConnectRedstone(PC_Direction side) {
		return false;
	}

	public boolean canMixWith(PC_MultiblockObject multiblockObject) {
		return false;
	}

	public PC_MultiblockObject mixWith(PC_MultiblockObject multiblockObject) {
		return null;
	}

	public void setIndexAndMultiblock(PC_MultiblockIndex index, PC_TileEntityMultiblock multiblock) {
		this.index = index;
		this.multiblock = multiblock;
	}

	public boolean onAdded() {
		return true;
	}

	public void updateObject() {
		if (!isClient() && sync) {
			PC_PacketHandler.sendToAllAround(getSyncPacket(), getWorld().getWorldInfo().getVanillaDimension(), multiblock.xCoord, multiblock.yCoord, multiblock.zCoord, 32);
			this.sync = false;
		}
	}

	public final PC_Packet getSyncPacket(){
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		makeSync(nbtTagCompound);
		return new PC_PacketMultiblockObjectSync(this, nbtTagCompound);
	}

	public final void makeSync(NBTTagCompound nbtTagCompound) {
		writeToNBT(nbtTagCompound, Flag.SYNC);
	}
	
	public void onChunkUnload() {
		
	}
	
	public void markDirty(){
		multiblock.markDirty();
	}
	
	public boolean isUsing(PC_MultiblockIndex index, PC_MultiblockObject multiblockObject){
		return this.index == index;
	}
	
	public World getWorld(){
		return multiblock.getWorldObj();
	}
	
	public void sync(){
		if (!isClient())
			sync = true;
	}

	@Override
	public void saveToNBT(NBTTagCompound tag, Flag flag) {
		writeToNBT(tag, flag);
	}
	
	protected final void readFromNBT(final NBTTagCompound nbtTagCompound, final Flag flag){
		PC_Reflection.processFields(this, new PC_Processor(){

			@Override
			public void process(Field field, Object value, EnumMap<Result, Object> results) {
				PC_Field info = field.getAnnotation(PC_Field.class);
				if(info!=null && flag.isIn(info)){
					String name = info.name();
					if(name.isEmpty()){
						name = field.getName();
					}
					Class<?> type = field.getType();
					value = PC_NBTTagHandler.loadFromNBT(nbtTagCompound, name, type, flag);
					results.put(Result.SET, value);
				}
			}
			
		});
	}
	
	protected final void writeToNBT(final NBTTagCompound nbtTagCompound, final Flag flag){
		PC_Reflection.processFields(this, new PC_Processor(){

			@Override
			public void process(Field field, Object value, EnumMap<Result, Object> results) {
				if(value==null)
					return;
				PC_Field info = field.getAnnotation(PC_Field.class);
				if(info!=null && flag.isIn(info)){
					String name = info.name();
					if(name.isEmpty()){
						name = field.getName();
					}
					PC_NBTTagHandler.saveToNBT(nbtTagCompound, name, value, flag);
				}
			}
			
		});
	}
	
	public PC_MultiblockIndex getIndex(){
		return index;
	}

	public final void applySync(NBTTagCompound nbtTagCompound) {
		readFromNBT(nbtTagCompound, Flag.SYNC);
		multiblock.renderUpdate();
	}
	
}
