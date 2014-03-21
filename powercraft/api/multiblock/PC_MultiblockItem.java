package powercraft.api.multiblock;

import java.lang.reflect.Constructor;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercraft.api.PC_Api;
import powercraft.api.PC_Direction;
import powercraft.api.PC_IconRegistry;
import powercraft.api.PC_Logger;
import powercraft.api.PC_Utils;
import powercraft.api.item.PC_Item;
import powercraft.core.PCco_Core;

public abstract class PC_MultiblockItem extends PC_Item {

	public PC_MultiblockItem() {
		PC_Multiblocks.addMultiblock(this, getMultiblockObjectClass());
	}


	public abstract Class<? extends PC_MultiblockObject> getMultiblockObjectClass();


	public abstract PC_MultiblockType getMultiblockType();

	@Override
	public String getTextureFolderName() {
		return getClass().getSimpleName().replaceAll("PC.*_(Multiblock)?(Item)?", "");
	}

	public PC_MultiblockObject getMultiblockObject(ItemStack itemStack) {
		Class<? extends PC_MultiblockObject> c = getMultiblockObjectClass();
		try {
			Constructor<? extends PC_MultiblockObject> constr = c.getConstructor(ItemStack.class);
			try{
				return constr.newInstance(itemStack);
			}catch(Exception e){
				e.printStackTrace();
				PC_Logger.severe("Faild to generate multiblock tile entity");
			}
			return null;
		} catch (Exception e) {
			//
		}
		try {
			return c.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			PC_Logger.severe("Faild to generate multiblock tile entity");
		}
		return null;
	}


	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int iside, float xHit, float yHit, float zHit) {
		int nx = x, ny = y, nz = z;
		float nxHit = xHit, nyHit = yHit, nzHit = zHit;
		Block block = PC_Utils.getBlock(world, nx, ny, nz);
		PC_Direction side = PC_Direction.fromSide(iside);
		boolean replaceAble = false;
		if (block instanceof PC_BlockMultiblock) {
			int ret = handleMultiblockClick(itemStack, entityPlayer, world, nx, ny, nz, side, nxHit, nyHit, nzHit, false);
			if (ret != -1) {
				return ret != 0;
			}
		}
		if (block == Blocks.snow && (world.getBlockMetadata(nx, ny, nz) & 7) < 1) {
			side = PC_Direction.UP;
			replaceAble = true;
		} else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush
				&& (block == null || !block.isReplaceable(world, nx, ny, nz))) {
			nx += side.offsetX;
        	ny += side.offsetY;
        	nz += side.offsetZ;
			block = PC_Utils.getBlock(world, nx, ny, nz);
			if ((block == Blocks.snow && (world.getBlockMetadata(nx, ny, nz) & 7) < 1) || !(block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush
					&& (block == null || !block.isReplaceable(world, nx, ny, nz)))) {
				replaceAble = true;
			}
		} else {
			replaceAble = true;
		}
		switch (side) {
		case DOWN:
			nyHit = 1;
			break;
		case UP:
			nyHit = 0;
			break;
		case NORTH:
			nzHit = 1;
			break;
		case SOUTH:
			nzHit = 0;
			break;
		case WEST:
			nxHit = 1;
			break;
		case EAST:
			nxHit = 0;
			break;
		default:
			break;
		}
		if (block == null || replaceAble) {
			world.setBlock(nx, ny, nz, PCco_Core.MULTIBLOCK);
			block = PC_Utils.getBlock(world, nx, ny, nz);
		}
		if (block instanceof PC_BlockMultiblock) {
			int ret = handleMultiblockClick(itemStack, entityPlayer, world, nx, ny, nz, side, nxHit, nyHit, nzHit, true);
			if (ret != -1) {
				return ret != 0;
			}
			PC_TileEntityMultiblock tem = PC_Utils.getTileEntity(world, nx, ny, nz, PC_TileEntityMultiblock.class);
			if(tem.noTiles()){
				world.setBlockToAir(nx, ny, nz);
			}
		}
		return false;
	}


	@SuppressWarnings("unused")
	public int handleMultiblockClick(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, PC_Direction side, float xHit,
			float yHit, float zHit, boolean secoundTry) {
		PC_Direction s = side;
		PC_TileEntityMultiblock tem = PC_Utils.getTileEntity(world, x, y, z, PC_TileEntityMultiblock.class);
		switch (getMultiblockType()) {
			case CENTER:
				if (tem.setMultiblockTileEntity(PC_MultiblockIndex.CENTER, getMultiblockObject(itemStack))) {
					itemStack.stackSize--;
					return 1;
				}
				return -1;
			case CORNER:

				break;
			case EDGE:

				break;
			case FACE: {
				PC_Direction[] dirs;
				float hit1;
				float hit2;
				float hit3;
				final float a = 0.5f-2f/16f;
				switch(s){
				case DOWN:
				case UP:
					hit1 = xHit;
					hit2 = zHit;
					hit3 = yHit;
					dirs = new PC_Direction[]{PC_Direction.WEST, PC_Direction.EAST, PC_Direction.NORTH, PC_Direction.SOUTH};
					break;
				case EAST:
				case WEST:
					hit1 = yHit;
					hit2 = zHit;
					hit3 = xHit;
					dirs = new PC_Direction[]{PC_Direction.DOWN, PC_Direction.UP, PC_Direction.NORTH, PC_Direction.SOUTH};
					break;
				case NORTH:
				case SOUTH:
					hit1 = xHit;
					hit2 = yHit;
					hit3 = zHit;
					dirs = new PC_Direction[]{PC_Direction.WEST, PC_Direction.EAST, PC_Direction.DOWN, PC_Direction.UP};
					break;
				default:
					return 0;
				}
				hit1 = 0.5f-hit1;
				hit2 = 0.5f-hit2;
				if(hit3==0 || hit3==1){
					if(!secoundTry)
						return -1;
					s = s.getOpposite();
				}
				if(Math.abs(hit1)>a || Math.abs(hit2)>a){
					if(Math.abs(hit1)>Math.abs(hit2)){
						if(hit1>0){
							s = dirs[0];
						}else{
							s = dirs[1];
						}
					}else{
						if(hit2>0){
							s = dirs[2];
						}else{
							s = dirs[3];
						}
					}
				}
				if (s!=null && tem.setMultiblockTileEntity(PC_MultiblockIndex.FACEINDEXFORDIR[s.ordinal()], getMultiblockObject(itemStack))) {
					itemStack.stackSize--;
					return 1;
				}
				return 0;
			}
			default:
				break;
		}
		return 0;
	}

	@SuppressWarnings("unused")
	public void loadMultiblockIcons(PC_IconRegistry iconRegistry) {
		//
	}
	
}
