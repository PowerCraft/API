package powercraft.api.multiblock.cable;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Field;
import powercraft.api.PC_Field.Flag;
import powercraft.api.PC_Utils;
import powercraft.api.grid.PC_IGridHolder;
import powercraft.api.multiblock.PC_BlockMultiblock;
import powercraft.api.multiblock.PC_MultiblockIndex;
import powercraft.api.multiblock.PC_MultiblockObject;
import powercraft.api.multiblock.PC_TileEntityMultiblock;
import powercraft.api.renderer.PC_Renderer;
import powercraft.core.PCco_Core;

public abstract class PC_MultiblockObjectCable extends PC_MultiblockObject implements PC_IGridHolder {

	@PC_Field(flags={Flag.SAVE, Flag.SYNC})
	protected int width;
	@PC_Field(flags={Flag.SAVE, Flag.SYNC})
	protected long connections;
	@PC_Field(flags={Flag.SAVE, Flag.SYNC})
	protected int specialConnection;
	
	protected boolean isIO;
	
	public PC_MultiblockObjectCable(int thickness, int width) {
		super(thickness);
		this.width = width;
	}
	
	public PC_MultiblockObjectCable(NBTTagCompound tagCompound, Flag flag) {
		super(tagCompound, flag);
	}


	protected abstract IIcon getCableIcon();
	
	protected abstract IIcon getCableCornerIcon();

	protected abstract IIcon getCableSideIcon();
	
	@SuppressWarnings("hiding")
	protected abstract IIcon getCableLineIcon(int index);


	protected abstract boolean useOverlay();

	
	protected abstract int getColorForCable(int cableID);


	protected abstract int getMask();


	public int getConnections(int n) {
		return (int) ((this.connections>>>(n*16))&0xFFFF);
	}
	
	public int getSpecialConnections(int n) {
		return (this.specialConnection>>>(n*2))&0x3;
	}

	@SuppressWarnings("hiding")
	protected int canConnectToMultiblock(PC_MultiblockObject multiblock, PC_Direction dir, PC_Direction dir2) {
		if (multiblock.getClass() != getClass()) return 0;
		PC_MultiblockObjectCable cable = (PC_MultiblockObjectCable)multiblock;
		if(dir.offsetY!=0){
			return cable.getMask()|1<<16;
		}else if(dir.offsetX!=0 && dir2.offsetY==0){
			return cable.getMask()|1<<16;
		}
		return cable.getMask();
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
				return canConnectToMultiblock( mbte, dir, dir2);
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
		if (block == null || block.isAir(world, x, y, z) || block instanceof PC_BlockMultiblock){
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unused")
	private int canConnectTo(PC_Direction dir, PC_Direction dir2, int oldConnection, int oldSpecialConnection) {

		World world = getWorld();
		int x = this.multiblock.xCoord;
		int y = this.multiblock.yCoord;
		int z = this.multiblock.zCoord;
		int connection = 0;
		connection |= canConnectToBlock(world, x, y, z, dir2, dir) & 0xFFFF;
		connection |= canConnectToBlock(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir2, dir.getOpposite()) & 0xFFFF;
		connection |= canConnectToBlock(world, x + dir2.offsetX, y + dir2.offsetY, z + dir2.offsetZ, dir, dir2.getOpposite()) & 0xFFFF;
		connection |= canConnectToBlock(world, x + dir2.offsetX, y + dir2.offsetY, z + dir2.offsetZ, dir2.getOpposite(), dir) & 0xFFFF;
		if(canConnectThrough(world, x + dir2.offsetX, y + dir2.offsetY, z + dir2.offsetZ, dir2.getOpposite(), dir)){
			int inner1 = canConnectToBlock(world, x + dir2.offsetX + dir.offsetX, y + dir2.offsetY + dir.offsetY, z + dir2.offsetZ + dir.offsetZ, dir2.getOpposite(), dir.getOpposite());
			int inner2 = canConnectToBlock(world, x + dir2.offsetX + dir.offsetX, y + dir2.offsetY + dir.offsetY, z + dir2.offsetZ + dir.offsetZ, dir.getOpposite(), dir2.getOpposite()) & 0xFFFF;
			if(inner1!=0 && inner2!=0){
				connection |= inner1|inner2 & 0xFFFF;
			}else if(inner1!=0){
				connection |= inner1;
			}else if(inner2!=0){
				connection |= inner2|1<<16;
			}
		}else if(canConnectThrough(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir.getOpposite(), dir2)){
			connection |= canConnectToBlock(world, x + dir2.offsetX + dir.offsetX, y + dir2.offsetY + dir.offsetY, z + dir2.offsetZ + dir.offsetZ, dir.getOpposite(), dir2.getOpposite()) & 0xFFFF;
		}
		return connection & (getMask() | ~0xFFFF);
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
			int oldConnection = (int) ((this.connections>>>(i*8))&0xFFFF);
			int oldSpecialConnection = (this.specialConnection>>>i)&0x3;
			this.connections &= ~((long)0xFFFF<<(i*8));
			this.specialConnection &= ~(0x3<<i);
			int newConnection = canConnectTo(dir, dir2, oldConnection, oldSpecialConnection);
			this.connections |= ((long)newConnection&0xFFFF)<<(i*8);
			this.specialConnection |= ((newConnection>>>16)&0x3)<<i;
			i+=2;
		}
		if(oldIO != this.isIO)
			this.multiblock.notifyNeighbors();
		if(!b && oldIO != this.isIO)
			reconnect();
		sync();
		return null;
	}

	public void reconnect(){
		//
	}

	@Override
	public void onNeighborBlockChange(Block neighbor) {

		List<ItemStack> drops = checkConnections(neighbor==PCco_Core.MULTIBLOCK);
		if (drops != null) this.multiblock.drop(drops);
	}

	@Override
	public void onInternalChange() {
		
		List<ItemStack> drops = checkConnections(false);
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

	@Override
	public List<AxisAlignedBB> getCollisionBoundingBoxes() {
		List<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
		float s = this.thickness / 16.0f;
		float w = this.width / 32.0f;
		PC_Direction dir = PC_MultiblockIndex.getFaceDir(this.index);
		switch(dir){
		case DOWN:
			list.add(AxisAlignedBB.getBoundingBox(0.5-w, 0, 0.5-w, 0.5+w, s, 0.5+w));
			break;
		case EAST:
			list.add(AxisAlignedBB.getBoundingBox(1-s, 0.5-w, 0.5-w, 1, 0.5+w, 0.5+w));
			break;
		case NORTH:
			list.add(AxisAlignedBB.getBoundingBox(0.5-w, 0.5-w, 0, 0.5+w, 0.5+w, s));
			break;
		case SOUTH:
			list.add(AxisAlignedBB.getBoundingBox(0.5-w, 0.5-w, 1-s, 0.5+w, 0.5+w, 1));
			break;
		case UP:
			list.add(AxisAlignedBB.getBoundingBox(0.5-w, 1-s, 0.5-w, 0.5+w, 1, 0.5+w));
			break;
		case WEST:
			list.add(AxisAlignedBB.getBoundingBox(0, 0.5-w, 0.5-w, s, 0.5+w, 0.5+w));
			break;
		default:
			return null;
		}
		double[] d = new double[4];
		double[] buf = new double[6];
		for(int i=0; i<4; i++){
			if(getConnections(i)!=0){
				for(int j=0; j<4; j++){
					d[j] = w;
				}
				d[i] = -w;
				int j = i%2==0?i+1:i-1;
				d[j] = 0.5;
				makeWithRot(d, buf);
				list.add(AxisAlignedBB.getBoundingBox(buf[0], buf[1], buf[2], buf[3], buf[4], buf[5]));
			}
		}
		return list;
	}

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

	@SuppressWarnings("hiding")
	@Override
	public void renderWorldBlock(RenderBlocks renderer) {
		int[] connections = new int[4];
		double[] d = new double[4];
		double[] buf = new double[6];
		for(int i=0; i<4; i++){
			connections[i] = getConnections(i);
		}
		float w = this.width / 32.0f;
		IIcon[] icons = new IIcon[6];
		IIcon[] iicons = new IIcon[4];
		World world = getWorld();
		int x = this.multiblock.xCoord;
		int y = this.multiblock.yCoord;
		int z = this.multiblock.zCoord;
		for(int i=0; i<4; i++){
			d[i] = w;
		}
		boolean overlay = useOverlay();
		int mask;
		if(overlay) {
			mask = getMask();
		}else{
			mask = 0;
		}
		IIcon side = getCableSideIcon();
		for(int i=0; i<6; i++){
			icons[i] = side;
		}
		for(int i=0; i<4; i++){
			iicons[i] = side;
		}
		PC_Direction dir = PC_MultiblockIndex.getFaceDir(this.index);
		switch(dir){
		case EAST:
			renderer.uvRotateEast = 1;
			renderer.uvRotateWest = 2;
			renderer.uvRotateBottom = 2;
			renderer.uvRotateTop = 1;
			break;
		case NORTH:
			renderer.uvRotateSouth = 1;
			renderer.uvRotateNorth = 2;
			break;
		case SOUTH:
			renderer.uvRotateSouth = 1;
			renderer.uvRotateNorth = 2;
			break;
		case UP:
			renderer.uvRotateSouth = 3;
			break;
		case WEST:
			renderer.uvRotateEast = 1;
			renderer.uvRotateWest = 2;
			renderer.uvRotateBottom = 2;
			renderer.uvRotateTop = 1;
			break;
		default:
			break;
		
		}
		int topIcon = dir.getOpposite().ordinal();
		int botIcon = dir.ordinal();
		IIcon icon = getCableIcon();
		IIcon corner = getCableCornerIcon();
		boolean rot = false;
		boolean renderConnections = false;
		boolean onlyExtension = false;
		if(connections[0]==0 && connections[1]==0){
			icons[topIcon] = icon;
			icons[botIcon] = icon;
			rot = true;
			if(connections[2]!=0){
				d[3] = 0.5;
				iicons[2] = null;
			}
			if(connections[3]!=0){
				d[2] = 0.5;
				iicons[3] = null;
			}
			onlyExtension = getSpecialConnections(2)!=0 || getSpecialConnections(3)!=0;
		}else if(connections[2]==0 && connections[3]==0){
			icons[topIcon] = icon;
			icons[botIcon] = icon;
			if(connections[0]!=0){
				d[1] = 0.5;
				iicons[0] = null;
			}
			if(connections[1]!=0){
				d[0] = 0.5;
				iicons[1] = null;
			}
			onlyExtension = getSpecialConnections(0)!=0 || getSpecialConnections(1)!=0;
		}else{
			icons[topIcon] = corner;
			icons[botIcon] = corner;
			renderConnections = true;
		}
		makeWithRot(iicons, icons);
		makeWithRot(d, buf);
		renderer.overrideBlockBounds(buf[0], buf[1], buf[2], buf[3], buf[4], buf[5]);
		makeRot(rot, renderer);
		renderTileInWorld(world, x, y, z, icons, renderer, mask);
		if(renderConnections || onlyExtension){
			icons[topIcon] = icon;
			icons[botIcon] = icon;
			for(int i=0; i<4; i++){
				if(connections[i]!=0){
					for(int j=0; j<4; j++){
						d[j] = w;
						iicons[j] = side;
					}
					d[i] = -w;
					iicons[i] = null;
					int j = i%2==0?i+1:i-1;
					d[j] = 0.5;
					iicons[j] = null;
					rot = i>1;
					makeRot(rot, renderer);
					if(!onlyExtension){
						makeWithRot(iicons, icons);
						makeWithRot(d, buf);
						renderer.overrideBlockBounds(buf[0], buf[1], buf[2], buf[3], buf[4], buf[5]);
						renderTileInWorld(world, x, y, z, icons, renderer, mask);
					}
					if(getSpecialConnections(i)!=0){
						PC_Direction di = dirFrom(i);
						d[i] = 0.5;
						iicons[i] = icon;
						d[j] = -0.5+this.thickness/16.0;
						iicons[j] = icon;
						makeWithRot(iicons, icons);
						makeWithRot(d, buf);
						renderer.overrideBlockBounds(buf[0], buf[1], buf[2], buf[3], buf[4], buf[5]);
						renderTileInWorld(world, x+di.offsetX, y+di.offsetY, z+di.offsetZ, icons, renderer, mask);
					}
				}
			}
		}
		renderer.unlockBlockBounds();
		PC_Renderer.resetRotation(renderer);
	}
	
	private void renderTileInWorld(World world, int x, int y, int z, IIcon[] icons, RenderBlocks renderer, int mask){
		PC_Renderer.renderStandardBlockInWorld(world, x, y, z, icons, 0xFFFFFFFF, 0, renderer);
		if(mask!=0){
			int j = 0;
			for(int i=0; i<16; i++){
				if((mask & 1<<i)!=0){
					IIcon icon = getCableLineIcon(j++);
					int color = getColorForCable(i);
					IIcon[] iicons = new IIcon[]{icon, icon, icon, icon, icon, icon};
					PC_Renderer.renderStandardBlockInWorld(world, x, y, z, iicons, color, 0, renderer);
				}
			}
		}
	}
	
	private PC_Direction dirFrom(int i) {
		int p = i;
		PC_Direction d = PC_MultiblockIndex.getFaceDir(this.index);
		for(PC_Direction dir:PC_Direction.VALID_DIRECTIONS){
			if(dir!=d && dir!=d.getOpposite()){
				if(p<=0)
					return dir;
				p--;
			}
		}
		return null;
	}

	private void makeRot(boolean rot, RenderBlocks renderer) {
		PC_Direction dir = PC_MultiblockIndex.getFaceDir(this.index);
		switch(dir){
		case DOWN:
			if(rot){
				renderer.uvRotateBottom = 2;
				renderer.uvRotateTop = 1;
			}else{
				renderer.uvRotateBottom = 0;
				renderer.uvRotateTop = 0;
			}
			break;
		case EAST:
			if(rot){
				renderer.uvRotateSouth = 1;
				renderer.uvRotateNorth = 2;
			}else{
				renderer.uvRotateSouth = 0;
				renderer.uvRotateNorth = 0;
			}
			break;
		case NORTH:
			if(rot){
				renderer.uvRotateEast = 1;
				renderer.uvRotateWest = 2;
			}else{
				renderer.uvRotateEast = 0;
				renderer.uvRotateWest = 0;
			}
			break;
		case SOUTH:
			if(rot){
				renderer.uvRotateEast = 1;
				renderer.uvRotateWest = 2;
			}else{
				renderer.uvRotateEast = 0;
				renderer.uvRotateWest = 0;
			}
			break;
		case UP:
			if(rot){
				renderer.uvRotateBottom = 2;
				renderer.uvRotateTop = 1;
			}else{
				renderer.uvRotateBottom = 0;
				renderer.uvRotateTop = 0;
			}
			break;
		case WEST:
			if(rot){
				renderer.uvRotateSouth = 1;
				renderer.uvRotateNorth = 2;
			}else{
				renderer.uvRotateSouth = 0;
				renderer.uvRotateNorth = 0;
			}
			break;
		default:
			break;
		
		}
	}
	
	private static final int[] MAPPING = {4, 1, 5, 2, 3, 0};
	
	private void makeWithRot(double d[], double[] buf){
		float s = this.thickness / 16.0f;
		PC_Direction dir = PC_MultiblockIndex.getFaceDir(this.index);
		int i = 0;
		for(PC_Direction dir2:PC_Direction.VALID_DIRECTIONS){
			int id = MAPPING[dir2.ordinal()];
			if(dir2==dir){
				if(dir.offsetX==-1 || dir.offsetY==-1 || dir.offsetZ==-1)
					buf[id] = s;
				else
					buf[id] = 1-s;
			}else if(dir2==dir.getOpposite()){
				if(dir.offsetX==-1 || dir.offsetY==-1 || dir.offsetZ==-1)
					buf[id] = 0;
				else
					buf[id] = 1;
			}else{
				if(id<3){
					buf[id] = 0.5-d[i++];
				}else{
					buf[id] = 0.5+d[i++];
				}
			}
		}
	}
	
	private void makeWithRot(IIcon d[], IIcon[] buf){
		PC_Direction dir = PC_MultiblockIndex.getFaceDir(this.index);
		int i = 0;
		for(PC_Direction dir2:PC_Direction.VALID_DIRECTIONS){
			int id = dir2.ordinal();
			if(dir2!=dir && dir2!=dir.getOpposite()){
				buf[id] = d[i++];
			}
		}
	}
	
}
