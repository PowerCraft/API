package powercraft.api.multiblock.conduit;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import powercraft.api.PC_Direction;
import powercraft.api.PC_Utils;
import powercraft.api.block.PC_Field;
import powercraft.api.block.PC_Field.Flag;
import powercraft.api.multiblock.PC_BlockMultiblock;
import powercraft.api.multiblock.PC_MultiblockIndex;
import powercraft.api.multiblock.PC_MultiblockObject;
import powercraft.api.multiblock.PC_TileEntityMultiblock;
import powercraft.api.renderer.PC_Renderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class PC_MultiblockObjectConduit extends PC_MultiblockObject {

	protected int connectionSize=8;
	protected int connectionLength=3;
	@PC_Field(flags={Flag.SAVE, Flag.SYNC})
	protected int connections;
	
	protected PC_MultiblockObjectConduit(NBTTagCompound nbtTagCompound, Flag flag){
		super(nbtTagCompound, flag);
	}
	
	protected PC_MultiblockObjectConduit(){
		super(6);
	}
	
	public void checkConnections(){
		if(isClient()){
			return;
		}
		int oldConnections = connections;
		connections = 0;
		for(PC_Direction dir:PC_Direction.VALID_DIRECTIONS){
			connections |= canConnectTo(dir, (oldConnections>>dir.ordinal()*5)&31)<<(dir.ordinal()*5);
		}
		if(oldConnections!=connections)
			sync();
	}

	@SuppressWarnings("unused")
	public int canConnectToBlock(World world, int x, int y, int z, PC_Direction side, Block block, int oldConnectionInfo){
		return 0;
	}
	
	public int canConnectTo(PC_Direction dir, int oldConnection){
		int x = multiblock.xCoord + dir.offsetX;
		int y = multiblock.yCoord + dir.offsetY;
		int z = multiblock.zCoord + dir.offsetZ;
		Block block = PC_Utils.getBlock(getWorld(), x, y, z);
		if(block==null)
			return 0;
		if(block instanceof PC_BlockMultiblock){
			PC_TileEntityMultiblock tileEntity = PC_Utils.getTileEntity(getWorld(), x, y, z, PC_TileEntityMultiblock.class);
			PC_MultiblockObject multiblockObject = tileEntity.getTile(PC_MultiblockIndex.CENTER);
			if(multiblockObject!=null && multiblockObject.getClass() == getClass()){
				return 1;
			}
		}
		int i = canConnectToBlock(getWorld(), x, y, z, dir.getOpposite(), block, oldConnection>>1);
		if(i>0){
			return (i&31);
		}
		return 0;
	}

	@Override
	public boolean onAdded() {
		checkConnections();
		return true;
	}
	
	@Override
	public void onNeighborBlockChange(Block neighbor) {
		checkConnections();
	}

	public abstract IIcon getNormalConduitIcon();
	public abstract IIcon getCornerConduitIcon();
	public abstract IIcon getConnectionConduitIcon(int connectionInfo);
	
	@SideOnly(Side.CLIENT)
	public boolean isTransparentConduit(){
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void renderWorldBlock(RenderBlocks renderer) {
		World world = getWorld();
		int x = multiblock.xCoord;
		int y = multiblock.yCoord;
		int z = multiblock.zCoord;
		boolean isTransparent = isTransparentConduit();
		if(isTransparent){
			Tessellator.instance.draw();
			GL11.glDisable(GL11.GL_CULL_FACE);
			Tessellator.instance.startDrawingQuads();
		}
		float s = thickness/32.0f;
		renderer.setRenderBounds(0.5f-s, 0.5f-s, 0.5f-s, 0.5f+s, 0.5f+s, 0.5f+s);
		PC_Direction first = null;
		PC_Direction secound = null;
		for(PC_Direction dir:PC_Direction.VALID_DIRECTIONS){
			if(!notingOnSide(dir)){
				if(first==null && secound==null){
					first = dir;
				}else if(secound==null){
					secound = dir;
				}else{
					first = null;
				}
			}
		}
		IIcon icon = getCornerConduitIcon();
		if(first!=null && first.getOpposite()==secound){
			icon = getNormalConduitIcon();
			IIcon icons[] = new IIcon[6];
			for(PC_Direction dir:PC_Direction.VALID_DIRECTIONS){
				icons[dir.ordinal()] = icon;
			}
			icons[first.ordinal()] = null;
			icons[first.getOpposite().ordinal()] = null;
			if(first==PC_Direction.NORTH || first==PC_Direction.SOUTH){
				renderer.uvRotateBottom = 1;
				renderer.uvRotateTop = 1;
			}else if(first==PC_Direction.UP || first==PC_Direction.DOWN){
				renderer.uvRotateEast = 1;
				renderer.uvRotateNorth = 1;
				renderer.uvRotateSouth = 1;
				renderer.uvRotateWest = 1;
			}
			renderer.setRenderBounds(first.offsetX==0?0.5f-s:0, first.offsetY==0?0.5f-s:0, first.offsetZ==0?0.5f-s:0,
					first.offsetX==0?0.5f+s:1, first.offsetY==0?0.5f+s:1, first.offsetZ==0?0.5f+s:1);
			PC_Renderer.renderStandardBlockInWorld(world, x, y, z, icons, -1, 0, renderer);
		}else{
			IIcon icons[] = new IIcon[6];
			for(PC_Direction dir:PC_Direction.VALID_DIRECTIONS){
				if(notingOnSide(dir)){
					icons[dir.ordinal()] = icon;
				}else{
					icons[dir.ordinal()] = null;
				}
			}
			PC_Renderer.renderStandardBlockInWorld(world, x, y, z, icons, -1, 0, renderer);
			icon = getNormalConduitIcon();
			for(PC_Direction dir:PC_Direction.VALID_DIRECTIONS){
				icons[dir.ordinal()] = icon;
			}
			for(PC_Direction dir:PC_Direction.VALID_DIRECTIONS){
				int infoOnSide = pipeInfoAtSide(dir);
				if(infoOnSide!=0){
					renderer.uvRotateBottom = 0;
					renderer.uvRotateTop = 0;
					renderer.uvRotateEast = 0;
					renderer.uvRotateNorth = 0;
					renderer.uvRotateSouth = 0;
					renderer.uvRotateWest = 0;
					if(dir==PC_Direction.NORTH || dir==PC_Direction.SOUTH){
						renderer.uvRotateBottom = 1;
						renderer.uvRotateTop = 1;
					}else if(dir==PC_Direction.UP || dir==PC_Direction.DOWN){
						renderer.uvRotateEast = 1;
						renderer.uvRotateNorth = 1;
						renderer.uvRotateSouth = 1;
						renderer.uvRotateWest = 1;
					}
					icons[dir.ordinal()] = null;
					icons[dir.getOpposite().ordinal()] = null;
					float length = getPipeConnetionLength(infoOnSide);
					renderer.setRenderBounds(offsetN(s, s, dir.offsetX, length), offsetN(s, s, dir.offsetY, length), offsetN(s, s, dir.offsetZ, length),
							offsetP(s, s, dir.offsetX, length), offsetP(s, s, dir.offsetY, length), offsetP(s, s, dir.offsetZ, length));
					PC_Renderer.renderStandardBlockInWorld(world, x, y, z, icons, -1, 0, renderer);
					icons[dir.ordinal()] = icon;
					icons[dir.getOpposite().ordinal()] = icon;
					if(length>0){
						IIcon icons2[] = new IIcon[6];
						IIcon icon2 = getConnectionConduitIcon(infoOnSide);
						for(PC_Direction dir2:PC_Direction.VALID_DIRECTIONS){
							if(dir2!=dir){
								icons2[dir2.ordinal()] = icon2;
							}else{
								icons2[dir2.ordinal()] = null;
							}
						}
						renderer.uvRotateBottom = 0;
						renderer.uvRotateTop = 0;
						renderer.uvRotateEast = dir==PC_Direction.UP || dir==PC_Direction.DOWN?0:1;
						renderer.uvRotateNorth = 0;
						renderer.uvRotateSouth = dir==PC_Direction.UP || dir==PC_Direction.DOWN?0:1;
						renderer.uvRotateWest = 0;
						float ns = connectionSize/32.0f;
						float l = 0.5f-length;
						renderer.setRenderBounds(offsetN(ns, l, dir.offsetX, 0), offsetN(ns, l, dir.offsetY, 0), offsetN(ns, l, dir.offsetZ, 0),
								offsetP(ns, l, dir.offsetX, 0), offsetP(ns, l, dir.offsetY, 0), offsetP(ns, l, dir.offsetZ, 0));
						PC_Renderer.renderStandardBlockInWorld(world, x, y, z, icons, -1, 0, renderer);
					}
				}
			}
		}
		renderer.uvRotateBottom = 0;
		renderer.uvRotateTop = 0;
		renderer.uvRotateEast = 0;
		renderer.uvRotateNorth = 0;
		renderer.uvRotateSouth = 0;
		renderer.uvRotateWest = 0;
		if(isTransparent){
			Tessellator.instance.draw();
			GL11.glEnable(GL11.GL_CULL_FACE);
			Tessellator.instance.startDrawingQuads();
		}
	}

	protected boolean notingOnSide(PC_Direction dir){
		return ((connections>>(dir.ordinal()*5)) & 31) == 0;
	}
	
	protected int pipeInfoAtSide(PC_Direction dir){
		return (connections>>(dir.ordinal()*5)) & 31;
	}
	
	protected float getPipeConnetionLength(int pipeInfo){
		if(pipeInfo==1)
			return 0;
		return connectionLength/16.0f;
	}
	
	private static float offsetN(float f, float f1, int off, float length){
		if(off<0)
			return 0+length;
		if(off>0)
			return 0.5f+f1;
		return 0.5f-f;
	}

	private static float offsetP(float f, float f1, int off, float length){
		if(off>0)
			return 1-length;
		if(off<0)
			return 0.5f-f1;
		return 0.5f+f;
	}

	@Override
	public List<AxisAlignedBB> getCollisionBoundingBoxes() {
		float s = thickness/32.0f;
		List<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
		list.add(AxisAlignedBB.getBoundingBox(0.5-s, 0.5-s, 0.5-s, 0.5+s, 0.5+s, 0.5+s));
		for(PC_Direction dir:PC_Direction.VALID_DIRECTIONS){
			int infoOnSide = pipeInfoAtSide(dir);
			if(infoOnSide!=0){
				float length = getPipeConnetionLength(infoOnSide);
				list.add(AxisAlignedBB.getBoundingBox(offsetN(s, s, dir.offsetX, length), offsetN(s, s, dir.offsetY, length), offsetN(s, s, dir.offsetZ, length),
						offsetP(s, s, dir.offsetX, length), offsetP(s, s, dir.offsetY, length), offsetP(s, s, dir.offsetZ, length)));
				if(length>0){
					float ns = connectionSize/32.0f;
					float l = 0.5f-length;
					list.add(AxisAlignedBB.getBoundingBox(offsetN(ns, l, dir.offsetX, 0), offsetN(ns, l, dir.offsetY, 0), offsetN(ns, l, dir.offsetZ, 0),
							offsetP(ns, l, dir.offsetX, 0), offsetP(ns, l, dir.offsetY, 0), offsetP(ns, l, dir.offsetZ, 0)));
				}
			}
		}
		return list;
	}
	
}
