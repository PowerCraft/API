package powercraft.api.multiblock;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import powercraft.api.PC_Direction;
import powercraft.api.block.PC_Field;
import powercraft.api.block.PC_Field.Flag;
import powercraft.api.block.PC_TileEntity;

public final class PC_TileEntityMultiblock extends PC_TileEntity {

	@PC_Field(flags={Flag.SAVE, Flag.SYNC})
	private PC_MultiblockObject tiles[] = new PC_MultiblockObject[27];
	
	public PC_MultiblockObject getTile(PC_MultiblockIndex index) {
		return tiles[index.ordinal()];
	}

	public List<ItemStack> removeMultiblockTileEntity(PC_MultiblockIndex index) {
		int i = index.ordinal();
		if (tiles[i] == null) {
			return null;
		}
		List<ItemStack> drop = tiles[i].getDrop();
		PC_MultiblockObject te = tiles[i];
		te.onPreRemove();
		tiles[i] = null;
		te.onRemoved();
		markDirty();
		return drop;
	}

	public boolean setMultiblockTileEntity(PC_MultiblockIndex index, PC_MultiblockObject multiblockObject) {
		int i = index.ordinal();
		for(PC_MultiblockObject tile:tiles){
			if(tile!=null && tile.isUsing(index, multiblockObject))
				return false;
		}
		if (tiles[i] == null) {
			tiles[i] = multiblockObject;
			multiblockObject.setIndexAndMultiblock(PC_MultiblockIndex.values()[i], this);
			if (!multiblockObject.onAdded()) {
				return false;
			}
		} else {
			if (tiles[i].canMixWith(multiblockObject)) {
				tiles[i] = tiles[i].mixWith(multiblockObject);
				tiles[i].setIndexAndMultiblock(PC_MultiblockIndex.values()[i], this);
			} else {
				return false;
			}
		}
		markDirty();
		return true;
	}

	
	public boolean noTiles() {
		for(PC_MultiblockObject tile:tiles){
			if(tile!=null)
				return false;
		}
		return true;
	}

	@Override
	public void onNeighborBlockChange(Block neighbor) {
		for(PC_MultiblockObject tile:tiles){
			if(tile!=null)
				tile.onNeighborBlockChange(neighbor);
		}
	}

	@Override
	public float getPlayerRelativeHardness(EntityPlayer player) {
		PC_MultiblockIndex index = PC_BlockMultiblock.playerSelection.get(player);
		if(index!=null && tiles[index.ordinal()]!=null){
			return tiles[index.ordinal()].getPlayerRelativeHardness(player);
		}
		return -1;
	}

	@Override
	public void onBlockClicked(EntityPlayer player) {
		PC_MultiblockIndex index = PC_BlockMultiblock.playerSelection.get(player);
		if(index!=null && tiles[index.ordinal()]!=null){
			tiles[index.ordinal()].onClicked(player);
		}
	}

	@Override
	public void fillWithRain() {
		for(PC_MultiblockObject tile:tiles){
			if(tile!=null)
				tile.fillWithRain();
		}
	}

	@Override
	public int getLightValue() {
		int lightValue = 0;
		for(PC_MultiblockObject tile:tiles){
			if(tile!=null){
				int value = tile.getLightValue();
				if(value>lightValue){
					lightValue = value;
				}
			}
		}
		return lightValue;
	}

	@Override
	public boolean isLadder(EntityLivingBase entity) {
		for(PC_MultiblockObject tile:tiles){
			if(tile!=null)
				return tile.isLadder(entity);
		}
		return false;
	}

	@Override
	public boolean isBurning() {
		for(PC_MultiblockObject tile:tiles){
			if(tile!=null)
				return tile.isBurning();
		}
		return false;
	}

	@Override
	public float getExplosionResistance(Entity entity, double explosionX, double explosionY, double explosionZ) {
		// TODO Auto-generated method stub
		return super.getExplosionResistance(entity, explosionX, explosionY, explosionZ);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target) {
		PC_MultiblockIndex index = PC_MultiblockIndex.values()[target.subHit];
		if(index!=null && tiles[index.ordinal()]!=null){
			return tiles[index.ordinal()].getPickBlock();
		}
		return null;
	}

	@Override
	public float getEnchantPowerBonus() {
		float bonus = 0;
		for(PC_MultiblockObject tile:tiles){
			if(tile!=null)
				bonus += tile.getEnchantPowerBonus();
		}
		return bonus;
	}

	@Override
	public void onNeighborTEChange(int tileX, int tileY, int tileZ) {
		for(PC_MultiblockObject tile:tiles){
			if(tile!=null)
				tile.onNeighborTEChange(tileX, tileY, tileZ);
		}
	}

	@Override
	public List<AxisAlignedBB> getCollisionBoundingBoxes(Entity entity) {
		List<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
		for(PC_MultiblockObject tile:tiles){
			if(tile!=null){
				List<AxisAlignedBB> l = tile.getCollisionBoundingBoxes();
				if(l!=null)
					list.addAll(l);
			}
		}
		return list;
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, PC_Direction side) {
		PC_MultiblockIndex index = PC_BlockMultiblock.playerSelection.get(player);
		if(index!=null && tiles[index.ordinal()]!=null){
			return tiles[index.ordinal()].onBlockActivated(player);
		}
		return false;
	}

	@Override
	public int getComparatorInput(PC_Direction side) {
		
		return super.getComparatorInput(side);
	}

	@Override
	public boolean isSideSolid(PC_Direction side) {
		PC_MultiblockIndex index = PC_MultiblockIndex.getFromDir(side);
		if(index!=null && tiles[index.ordinal()]!=null){
			return tiles[index.ordinal()].isSolid();
		}
		return false;
	}

	@Override
	public boolean renderWorldBlock(int modelId, RenderBlocks renderer) {
		if(renderer.hasOverrideBlockTexture())
			return true;
		for(PC_MultiblockObject tile:tiles){
			if(tile!=null)
				tile.renderWorldBlock(renderer);
		}
		return true;
	}

	@Override
	public void onTick() {
		for(PC_MultiblockObject tile:tiles){
			if(tile!=null)
				tile.updateObject();
		}
	}

	@Override
	public void onChunkUnload() {
		for(PC_MultiblockObject tile:tiles){
			if(tile!=null)
				tile.onChunkUnload();
		}
	}
	
}
