package powercraft.api.multiblock.cable;

import java.util.List;

import javax.swing.Icon;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import powercraft.api.PC_Api;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Field;
import powercraft.api.PC_Field.Flag;
import powercraft.api.PC_Utils;
import powercraft.api.grid.PC_IGridHolder;
import powercraft.api.multiblock.PC_BlockMultiblock;
import powercraft.api.multiblock.PC_MultiblockIndex;
import powercraft.api.multiblock.PC_MultiblockObject;
import powercraft.api.multiblock.PC_TileEntityMultiblock;

public abstract class PC_MultiblockObjectCable extends PC_MultiblockObject implements PC_IGridHolder {

	@PC_Field
	protected int width;
	@PC_Field
	private long connections = 0;
	
	protected boolean isIO;
	
	public PC_MultiblockObjectCable(int thickness, int width) {
		super(thickness);
		this.width = width;
	}
	
	public PC_MultiblockObjectCable(NBTTagCompound tagCompound, Flag flag) {
		super(tagCompound, flag);
	}


	protected abstract Icon getCableIcon();


	@SuppressWarnings("hiding")
	protected abstract Icon getCableLineIcon(int index);


	protected abstract boolean useOverlay();

	
	protected abstract int getColorForCable(int cableID);


	protected abstract int getMask();


	public int getConnections(int n) {

		return (int) ((this.connections>>>(n*16))&0xFFFF);
	}

	@SuppressWarnings("hiding")
	protected int canConnectToMultiblock(PC_MultiblockObject multiblock) {

		if (multiblock.getClass() != getClass()) return 0;
		return 0xFFFF;
	}

	@SuppressWarnings({ "static-method", "unused" })
	protected int canConnectToBlock(World world, int x, int y, int z, Block block, PC_Direction dir, PC_Direction dir2) {

		return 0;
	}


	@SuppressWarnings("hiding")
	private int canConnectToBlock(World world, int x, int y, int z, PC_Direction dir, PC_Direction dir2) {

		Block block = PC_Utils.getBlock(world, x, y, z);
		if (block instanceof PC_BlockMultiblock) {
			PC_TileEntityMultiblock multiblock = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntityMultiblock.class);
			PC_MultiblockObject mbte = multiblock.getTile(PC_MultiblockIndex.FACEINDEXFORDIR[dir.ordinal()]);
			if (mbte != null) 
				return canConnectToMultiblock(mbte);
			return 0;
		}
		if (block != null) {
			int i = canConnectToBlock(world, x, y, z, block, dir, dir2);
			if (i != 0) this.isIO = true;
			return i;
		}
		return 0;
	}

	@SuppressWarnings({ "static-method", "unused" })
	protected boolean canConnectThrough(World world, int x, int y, int z, PC_Direction dir, PC_Direction dir2){
		Block block = PC_Utils.getBlock(world, x, y, z);
		if (block == null){
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unused")
	private int canConnectTo(PC_Direction dir, PC_Direction dir2, int oldConnection) {

		World world = getWorld();
		int x = this.multiblock.xCoord;
		int y = this.multiblock.yCoord;
		int z = this.multiblock.zCoord;
		int connection = 0;
		connection |= canConnectToBlock(world, x, y, z, dir2, dir);
		connection |= canConnectToBlock(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir2, dir.getOpposite());
		connection |= canConnectToBlock(world, x + dir2.offsetX, y + dir2.offsetY, z + dir2.offsetZ, dir, dir2.getOpposite());
		connection |= canConnectToBlock(world, x + dir2.offsetX, y + dir2.offsetY, z + dir2.offsetZ, dir2.getOpposite(), dir);
		if(canConnectThrough(world, x + dir2.offsetX, y + dir2.offsetY, z + dir2.offsetZ, dir2.getOpposite(), dir)){
			connection |= canConnectToBlock(world, x + dir2.offsetX + dir.offsetX, y + dir2.offsetY + dir.offsetY, z + dir2.offsetZ + dir.offsetZ, dir2.getOpposite(), dir.getOpposite());
			connection |= canConnectToBlock(world, x + dir2.offsetX + dir.offsetX, y + dir2.offsetY + dir.offsetY, z + dir2.offsetZ + dir.offsetZ, dir.getOpposite(), dir2.getOpposite());
		}else if(canConnectThrough(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir.getOpposite(), dir2)){
			connection |= canConnectToBlock(world, x + dir2.offsetX + dir.offsetX, y + dir2.offsetY + dir.offsetY, z + dir2.offsetZ + dir.offsetZ, dir.getOpposite(), dir2.getOpposite());
		}
		return connection & getMask();
	}


	public List<ItemStack> checkConnections(boolean b) {

		int i = 0;
		PC_Direction dir = PC_MultiblockIndex.getFaceDir(this.index);

		Block block = PC_Utils.getBlock(getWorld(), this.multiblock.xCoord + dir.offsetX, this.multiblock.yCoord + dir.offsetY, this.multiblock.zCoord
				+ dir.offsetZ);
		if (block == null
				|| !block.isSideSolid(getWorld(), this.multiblock.xCoord + dir.offsetX, this.multiblock.yCoord + dir.offsetY, this.multiblock.zCoord
						+ dir.offsetZ, ForgeDirection.values()[dir.getOpposite().ordinal()])) {
			if (this.multiblock.getTile(PC_MultiblockIndex.CENTER) == null){
				return this.multiblock.removeMultiblockTileEntity(this.index);
			}
		}

		if (isClient()) {
			return null;
		}

		boolean oldIO = this.isIO;
		this.isIO = false;
		for (PC_Direction dir2 : PC_Direction.VALID_DIRECTIONS) {
			if (dir2 == dir || dir2.getOpposite() == dir) continue;
			int oldConnection = (int) ((this.connections>>>i)&0xFFFF);
			this.connections &= ~(0xFFFF<<i);
			this.connections |= (canConnectTo(dir, dir2, oldConnection)&0xFFFF)<<i;
			i+=16;
		}
		if(oldIO != this.isIO)
			this.multiblock.notifyNeighbors();
		if(!b && oldIO != this.isIO)
			reconnect();
		this.multiblock.sync();
		return null;
	}

	public void reconnect(){
		//
	}

	@Override
	public void onNeighborBlockChange(Block neighbor) {

		List<ItemStack> drops = checkConnections(neighbor==PC_Api.MULTIBLOCK);
		if (drops != null) this.multiblock.drop(drops);
	}


	@Override
	public boolean onAdded() {

		if (checkConnections(false) != null) {
			return false;
		}
		getGridIfNull();
		return true;
	}

	

	/*@Override
	public AxisAlignedBB getSelectionBox() {

		float s = thickness / 16.0f;
		float w = width / 32.0f;
		float min[] = { 0, 0.5f - w, 0.5f + w };
		float max[] = { 0.5f - w, 0.5f + w, 1 };
		PC_Direction dir = PC_MultiblockIndex.getFaceDir(index);
		float min1 = 1 - s;
		float min2 = 0;
		float max1 = 1;
		float max2 = s;
		if (centerThickness > 0) {
			float t = (centerThickness + 2) / 32.0f;
			min1 = 0.5f + t;
			min2 = 0.5f - t - s;
			max1 = 0.5f + t + s;
			max2 = 0.5f - t;
		}
		if (dir == PC_Direction.UP || dir == PC_Direction.DOWN) {
			float minY = dir == PC_Direction.UP ? min1 : min2;
			float maxY = dir == PC_Direction.UP ? max1 : max2;
			return AxisAlignedBB.getBoundingBox(0, minY, 0, 1, maxY, 1);
		} else if (dir == PC_Direction.NORTH || dir == PC_Direction.SOUTH) {
			float minZ = dir == PC_Direction.SOUTH ? min1 : min2;
			float maxZ = dir == PC_Direction.SOUTH ? max1 : max2;
			return AxisAlignedBB.getBoundingBox(0, 0, minZ, 1, 1, maxZ);
		} else if (dir == PC_Direction.EAST || dir == PC_Direction.WEST) {
			float minX = dir == PC_Direction.EAST ? min1 : min2;
			float maxX = dir == PC_Direction.EAST ? max1 : max2;
			return AxisAlignedBB.getBoundingBox(minX, 0, 0, maxX, 1, 1);
		}
		return null;
	}*/


	@Override
	public void onRemoved() {
		removeFromGrid();
		super.onRemoved();
	}

	@Override
	public void onChunkUnload() {
		removeFromGrid();
		super.onChunkUnload();
	}

	@Override
	public void updateObject() {
		super.updateObject();
		if(!isClient()){
			getGridIfNull();
		}
	}
	
}
