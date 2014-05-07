package powercraft.api.redstone.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Field.Flag;
import powercraft.api.PC_Utils;
import powercraft.api.grid.PC_GridHelper;
import powercraft.api.multiblock.PC_MultiblockIndex;
import powercraft.api.multiblock.PC_TileEntityMultiblock;
import powercraft.api.multiblock.cable.PC_MultiblockObjectCable;
import powercraft.api.redstone.PC_IRedstoneGridTile;
import powercraft.api.redstone.PC_RedstoneGrid;
import powercraft.core.PCco_Core;


public class PC_MultiblockObjectRedstoneCable extends PC_MultiblockObjectCable implements PC_IRedstoneGridTile{
	
	private PC_RedstoneGrid grid;
	
	public PC_MultiblockObjectRedstoneCable() {
		super(1, 2);
	}
	
	public PC_MultiblockObjectRedstoneCable(NBTTagCompound tagCompound, Flag flag) {
		super(tagCompound, flag);
	}

	@Override
	public void getGridIfNull() {
		World world = getWorld();
		int x = this.multiblock.xCoord;
		int y = this.multiblock.yCoord;
		int z = this.multiblock.zCoord;
		if(!world.isRemote){
			PC_GridHelper.getGridIfNull(world, x, y, z, -1, PC_MultiblockIndex.getFaceDir(this.index), this, PC_RedstoneGrid.factory, PC_IRedstoneGridTile.class);
		}
	}
	
	@Override
	public void removeFromGrid() {
		PC_GridHelper.removeFromGrid(getWorld(), (PC_IRedstoneGridTile)this);
	}
	
	@Override
	protected IIcon getCableIcon() {
		return PC_MultiblockItemRedstoneCable.icon;
	}
	
	@Override
	protected IIcon getCableCornerIcon() {
		return PC_MultiblockItemRedstoneCable.icon;
	}
	
	@Override
	protected IIcon getCableSideIcon() {
		return PC_MultiblockItemRedstoneCable.icon;
	}
	
	@SuppressWarnings("hiding")
	@Override
	protected IIcon getCableLineIcon(int index) {
		return null;
	}
	
	@Override
	protected boolean useOverlay() {
		return false;
	}
	
	@Override
	protected int getColorForCable(int cableID) {
		return 0xFF0000;
	}
	
	@Override
	protected int getMask() {
		return 0xFFFF;
	}

	@Override
	public void setGrid(PC_RedstoneGrid grid) {
		this.grid = grid;
	}

	@Override
	public PC_RedstoneGrid getGrid() {
		return this.grid;
	}

	@Override
	public boolean isIO() {
		PC_TileEntityMultiblock tem = getTileEntity();
		if(this.index==PC_MultiblockIndex.FACEBOTTOM){
			for(PC_Direction dir:PC_Direction.VALID_DIRECTIONS){
				Block block = PC_Utils.getBlock(getWorld(), tem.xCoord-dir.offsetX, tem.yCoord-dir.offsetY, tem.zCoord-dir.offsetZ);
				if(block!=null && block!=PCco_Core.MULTIBLOCK){
					if(block instanceof BlockRedstoneWire || block.canConnectRedstone(getWorld(), tem.xCoord-dir.offsetX, tem.yCoord-dir.offsetY, tem.zCoord-dir.offsetZ, dir.ordinal())){
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void onRedstonePowerChange() {
		PC_TileEntityMultiblock tem = getTileEntity();
		PC_Utils.notifyBlockChange(getWorld(), tem.xCoord, tem.yCoord, tem.zCoord, PCco_Core.MULTIBLOCK);
	}

	@Override
	public int getPower() {
		int max = 0;
		PC_TileEntityMultiblock tem = getTileEntity();
		if(this.index==PC_MultiblockIndex.FACEBOTTOM){
			for(PC_Direction dir:PC_Direction.VALID_DIRECTIONS){
				Block block = PC_Utils.getBlock(getWorld(), tem.xCoord-dir.offsetX, tem.yCoord-dir.offsetY, tem.zCoord-dir.offsetZ);
				if(block!=null && block!=PCco_Core.MULTIBLOCK){
					int power;
					if(block instanceof BlockRedstoneWire){
						power = PC_Utils.getMetadata(getWorld(), tem.xCoord-dir.offsetX, tem.yCoord-dir.offsetY, tem.zCoord-dir.offsetZ);
						if(power>0){
							power--;
						}
					}else{
						power = getWorld().getIndirectPowerLevelTo(tem.xCoord-dir.offsetX, tem.yCoord-dir.offsetY, tem.zCoord-dir.offsetZ, dir.ordinal());
					}
					if(power==15)
						return 15;
					if(power>max){
						max=power;
					}
				}
			}
		}
		return max;
	}

	@Override
	public boolean canConnectRedstone(PC_Direction side) {
		return this.index==PC_MultiblockIndex.FACEBOTTOM;
	}

	@Override
	protected int canConnectToBlock(World world, int x, int y, int z, Block block, PC_Direction dir, PC_Direction dir2) {
		if(block instanceof BlockRedstoneWire){
			return this.index==PC_MultiblockIndex.FACEBOTTOM?1:0;
		}
		return super.canConnectToBlock(world, x, y, z, block, dir, dir2);
	}

	@Override
	public void onNeighborBlockChange(Block neighbor) {
		super.onNeighborBlockChange(neighbor);
		if(this.grid!=null)
			this.grid.update();
	}

	@Override
	public void onInternalChange() {
		super.onInternalChange();
		this.grid.update();
	}
	
	@Override
	public int getRedstonePowerValue(PC_Direction side) {
		getGridIfNull();
		World world = getWorld();
		int x = this.multiblock.xCoord;
		int y = this.multiblock.yCoord;
		int z = this.multiblock.zCoord;
		Block block = PC_Utils.getBlock(world, x+side.offsetX, y+side.offsetY, z+side.offsetZ);
		if(block instanceof BlockRedstoneWire){
			System.out.println("Redstone");
			return this.grid.getRedstonePowerValue()-1;
		}
		return this.grid.getRedstonePowerValue()-1;
	}
	
}
