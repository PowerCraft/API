package powercraft.api.multiblock;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_IconRegistry;
import powercraft.api.PC_TickHandler;
import powercraft.api.PC_TickHandler.PC_IRenderTickHandler;
import powercraft.api.PC_Utils;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_BlockType;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.network.packet.PC_PacketSelectMultiblockTile;
import powercraft.api.reflect.PC_Fields;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class PC_BlockMultiblock extends PC_BlockTileEntity implements PC_IRenderTickHandler {

	private static boolean damageDrawn;
	private static ISelector selector;
	static WeakHashMap<EntityPlayer, PC_MultiblockIndex> playerSelection = new WeakHashMap<EntityPlayer, PC_MultiblockIndex>();
	
	PC_BlockMultiblock() {
		super(PC_BlockType.MACHINE);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	public Class<? extends PC_TileEntity> getTileEntityClass() {
		return PC_TileEntityMultiblock.class;
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 pos, Vec3 ray) {
		PC_TileEntityMultiblock tem = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntityMultiblock.class);
		if(tem==null){
			return null;
		}
		MovingObjectPosition best = null;
		for (PC_MultiblockIndex index : PC_MultiblockIndex.values()) {
			PC_MultiblockObject obj = tem.getTile(index);
			if(obj!=null){
				List<AxisAlignedBB> collisionBoxes = obj.getCollisionBoundingBoxes();
				if (collisionBoxes != null) {
					for (AxisAlignedBB collisionBox : collisionBoxes) {
						MovingObjectPosition mop = doRay(world, x, y, z, pos, ray, collisionBox);
						if (mop != null && (best == null || mop.hitVec.distanceTo(pos) < best.hitVec .distanceTo(pos))) {
							best = mop;
							best.subHit = index.ordinal();
						}
					}
				}
			}
		}
		if(best!=null && world.isRemote && selector!=null){
			selector.select(world, x, y, z, best);
		}
		return best;
	}
	
	@SuppressWarnings("unused")
	private static MovingObjectPosition doRay(World world, int x, int y, int z, Vec3 inpos, Vec3 inray, AxisAlignedBB aabb) {
		Vec3 pos = inpos.addVector(-x, -y, -z);
		Vec3 ray = inray.addVector(-x, -y, -z);
        Vec3 vecXmin = pos.getIntermediateWithXValue(ray, aabb.minX);
        Vec3 vecXmax = pos.getIntermediateWithXValue(ray, aabb.maxX);
        Vec3 vecYmin = pos.getIntermediateWithYValue(ray, aabb.minY);
        Vec3 vecYmax = pos.getIntermediateWithYValue(ray, aabb.maxY);
        Vec3 vecZmin = pos.getIntermediateWithZValue(ray, aabb.minZ);
        Vec3 vecZmax = pos.getIntermediateWithZValue(ray, aabb.maxZ);

        if (!isVecInsideYZBounds(vecXmin, aabb)){
            vecXmin = null;
        }
        if (!isVecInsideYZBounds(vecXmax, aabb)){
            vecXmax = null;
        }
        if (!isVecInsideXZBounds(vecYmin, aabb)){
            vecYmin = null;
        }
        if (!isVecInsideXZBounds(vecYmax, aabb)){
            vecYmax = null;
        }
        if (!isVecInsideXYBounds(vecZmin, aabb)){
            vecZmin = null;
        }
        if (!isVecInsideXYBounds(vecZmax, aabb)){
            vecZmax = null;
        }

        Vec3 shortestVec = vecXmin;

        if (vecXmax != null && (shortestVec == null || pos.squareDistanceTo(vecXmax) < pos.squareDistanceTo(shortestVec))){
            shortestVec = vecXmax;
        }

        if (vecYmin != null && (shortestVec == null || pos.squareDistanceTo(vecYmin) < pos.squareDistanceTo(shortestVec))){
            shortestVec = vecYmin;
        }

        if (vecYmax != null && (shortestVec == null || pos.squareDistanceTo(vecYmax) < pos.squareDistanceTo(shortestVec))){
            shortestVec = vecYmax;
        }

        if (vecZmin != null && (shortestVec == null || pos.squareDistanceTo(vecZmin) < pos.squareDistanceTo(shortestVec))){
            shortestVec = vecZmin;
        }

        if (vecZmax != null && (shortestVec == null || pos.squareDistanceTo(vecZmax) < pos.squareDistanceTo(shortestVec))){
            shortestVec = vecZmax;
        }
        
        if (shortestVec == null){
            return null;
        }
		byte side = -1;

		if (shortestVec == vecXmin){
			side = 4;
		}else if (shortestVec == vecXmax){
			side = 5;
		}else if (shortestVec == vecYmin){
			side = 0;
		}else if (shortestVec == vecYmax){
			side = 1;
		}else if (shortestVec == vecZmin){
			side = 2;
		}else if (shortestVec == vecZmax){
			side = 3;
		}

		return new MovingObjectPosition(x, y, z, side, shortestVec.addVector(x, y, z));
    }
	
	private static boolean isVecInsideYZBounds(Vec3 vec, AxisAlignedBB aabb) {
        return vec == null ? false : vec.yCoord >= aabb.minY && vec.yCoord <= aabb.maxY && vec.zCoord >= aabb.minZ && vec.zCoord <= aabb.maxZ;
    }

    private static boolean isVecInsideXZBounds(Vec3 vec, AxisAlignedBB aabb){
        return vec == null ? false : vec.xCoord >= aabb.minX && vec.xCoord <= aabb.maxX && vec.zCoord >= aabb.minZ && vec.zCoord <= aabb.maxZ;
    }

    private static boolean isVecInsideXYBounds(Vec3 vec, AxisAlignedBB aabb){
        return vec == null ? false : vec.xCoord >= aabb.minX && vec.xCoord <= aabb.maxX && vec.yCoord >= aabb.minY && vec.yCoord <= aabb.maxY;
    }
	
	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		if(world.isRemote){
			if(PC_Utils.mcs()==null || PC_Utils.mcs().isServerStopped())
				playerSelection.remove(player);
			return false;
		}
		PC_TileEntityMultiblock tem = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntityMultiblock.class);
		if(tem==null){
			return super.removedByPlayer(world, player, x, y, z);
		}
		PC_MultiblockIndex selectionIndex = playerSelection.get(player);
		if (selectionIndex==null) return false;
		List<ItemStack> drops = tem.removeMultiblockTileEntity(selectionIndex);
		if (drops != null && !PC_Utils.isCreative(player)) {
			PC_Utils.spawnItems(world, x, y, z, drops);
		}
		playerSelection.remove(player);
		if (tem.noTiles()) return super.removedByPlayer(world, player, x, y, z);
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(World world, int x, int y, int z) {
		EntityPlayer player = PC_ClientUtils.mc().thePlayer;
		PC_MultiblockIndex selectionIndex = playerSelection.get(player);
		if(selectionIndex==null)
			return AxisAlignedBB.getAABBPool().getAABB(0, 0, 0, 0, 0, 0);
		PC_TileEntityMultiblock tem = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntityMultiblock.class);
		if(tem.getTile(selectionIndex)==null)
			return AxisAlignedBB.getAABBPool().getAABB(0, 0, 0, 0, 0, 0);
		return tem.getTile(selectionIndex).getSelectedBoundingBox();
	}

	private static interface ISelector{
		
		public void select(World world, int x, int y, int z, MovingObjectPosition select);
		
	}
	
	@SideOnly(Side.CLIENT)
	private static class ClientSelector implements ISelector{
		
		public ClientSelector() {
			
		}

		@Override
		public void select(World world, int x, int y, int z, MovingObjectPosition select){
			EntityPlayer player = PC_ClientUtils.mc().thePlayer;
			PC_MultiblockIndex selectionIndex = playerSelection.get(player);
			PC_MultiblockIndex bestIndex = PC_MultiblockIndex.values()[select.subHit];
			if(selectionIndex!=bestIndex){
				playerSelection.put(player, bestIndex);
				PC_PacketHandler.sendToServer(new PC_PacketSelectMultiblockTile(x, y, z, bestIndex));
				resetClientDigging(x, y, z, select.sideHit);
			}
		}
		
		private static void resetClientDigging(int x, int y, int z, int side){
			if(PC_Fields.Client.PlayerControllerMP_isHittingBlock.getValue(PC_ClientUtils.mc().playerController).booleanValue()){
				PC_ClientUtils.mc().playerController.resetBlockRemoving();
				if(!PC_Utils.isCreative(PC_ClientUtils.mc().thePlayer)){
					PC_ClientUtils.mc().playerController.clickBlock(x, y, z, side);
				}
			}
		}
		
	}

	public static void playerSelect(EntityPlayer player, PC_MultiblockIndex selectionIndex) {
		playerSelection.put(player, selectionIndex);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(PC_IconRegistry iconRegistry) {
		if(selector==null){
			selector = new ClientSelector();
			PC_TickHandler.registerTickHandler(this);
		}
		PC_Multiblocks.loadMultiblockIcons(iconRegistry);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, int modelId, RenderBlocks renderer) {
		if(renderer.hasOverrideBlockTexture()){
			if(!damageDrawn){
				damageDrawn = true;
				drawBlockDamages(renderer);
			}
			return true;
		}
		return super.renderWorldBlock(world, x, y, z, modelId, renderer);
	}

	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
	public static void drawBlockDamages(RenderBlocks renderer){
		RenderGlobal renderGlobal = PC_ClientUtils.mc().renderGlobal;
		IIcon[] destroyBlockIcons = PC_Fields.Client.RenderGlobal_destroyBlockIcons.getValue(renderGlobal);
		Map<Integer, DestroyBlockProgress> damagedBlocks =  PC_Fields.Client.RenderGlobal_damagedBlocks.getValue(renderGlobal);
		for(Entry<Integer, DestroyBlockProgress> e:damagedBlocks.entrySet()){
			EntityPlayer player = (EntityPlayer) PC_ClientUtils.mc().theWorld.getEntityByID(e.getKey().intValue());
			DestroyBlockProgress destroyblockprogress = e.getValue();
			int x = destroyblockprogress.getPartialBlockX();
			int y = destroyblockprogress.getPartialBlockY();
			int z = destroyblockprogress.getPartialBlockZ();
			PC_TileEntityMultiblock tem = PC_Utils.getTileEntity(PC_ClientUtils.mc().theWorld, x, y, z, PC_TileEntityMultiblock.class);
			PC_MultiblockIndex selection = playerSelection.get(player);
			if(tem!=null && selection!=null){
				renderer.setOverrideBlockTexture(destroyBlockIcons[destroyblockprogress.getPartialBlockDamage()]);
				PC_MultiblockObject obj = tem.getTile(selection);
				if(obj!=null){
					obj.renderWorldBlock(renderer);
				}
			}else{
				playerSelection.remove(player);
			}
		}
	}

	@Override
	public void onStartTick(float renderTickTime) {
		damageDrawn = false;
	}

	@Override
	public void onEndTick(float renderTickTime) {
		//
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
}
