package powercraft.api.multiblock;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Field;
import powercraft.api.PC_Field.Flag;
import powercraft.api.PC_NBTTagHandler;
import powercraft.api.PC_Utils;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.grid.PC_IGridSided;
import powercraft.api.grid.PC_IGridSidedSide;
import powercraft.api.grid.PC_IGridTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class PC_TileEntityMultiblock extends PC_TileEntity implements PC_IGridSided, PC_IGridSidedSide {

	@PC_Field(flags={Flag.SAVE, Flag.SYNC})
	private PC_MultiblockObject tiles[] = new PC_MultiblockObject[27];
	
	public PC_MultiblockObject getTile(PC_MultiblockIndex index) {
		return this.tiles[index.ordinal()];
	}

	public List<ItemStack> removeMultiblockTileEntity(PC_MultiblockIndex index) {
		int i = index.ordinal();
		if (this.tiles[i] == null) {
			return null;
		}
		List<ItemStack> drop = this.tiles[i].getDrop();
		PC_MultiblockObject te = this.tiles[i];
		te.onPreRemove();
		this.tiles[i] = null;
		te.onRemoved();
		markDirty();
		notifyNeighbors();
		sendReloadTile(i);
		return drop;
	}

	public boolean setMultiblockTileEntity(PC_MultiblockIndex index, PC_MultiblockObject multiblockObject) {
		int i = index.ordinal();
		for(PC_MultiblockObject tile:this.tiles){
			if(tile!=null && tile.isUsing(index, multiblockObject))
				return false;
		}
		if (this.tiles[i] == null) {
			this.tiles[i] = multiblockObject;
			multiblockObject.setIndexAndMultiblock(PC_MultiblockIndex.values()[i], this);
			if (!multiblockObject.onAdded()) {
				return false;
			}
		} else {
			if (this.tiles[i].canMixWith(multiblockObject)) {
				this.tiles[i] = this.tiles[i].mixWith(multiblockObject);
				this.tiles[i].setIndexAndMultiblock(PC_MultiblockIndex.values()[i], this);
			} else {
				return false;
			}
		}
		markDirty();
		notifyNeighbors();
		sendReloadTile(i);
		return true;
	}

	
	public boolean noTiles() {
		for(PC_MultiblockObject tile:this.tiles){
			if(tile!=null)
				return false;
		}
		return true;
	}

	@Override
	public void onNeighborBlockChange(Block neighbor) {
		for(PC_MultiblockObject tile:this.tiles){
			if(tile!=null)
				tile.onNeighborBlockChange(neighbor);
		}
	}

	void onInternalChange(PC_MultiblockObject tile){
		for(PC_MultiblockObject t:this.tiles){
			if(t!=null && t!=tile)
				tile.onInternalChange();
		}
	}
	
	@Override
	public float getPlayerRelativeHardness(EntityPlayer player) {
		PC_MultiblockIndex index = PC_BlockMultiblock.playerSelection.get(player);
		if(index!=null && this.tiles[index.ordinal()]!=null){
			return this.tiles[index.ordinal()].getPlayerRelativeHardness(player);
		}
		return -1;
	}

	@Override
	public void onBlockClicked(EntityPlayer player) {
		PC_MultiblockIndex index = PC_BlockMultiblock.playerSelection.get(player);
		if(index!=null && this.tiles[index.ordinal()]!=null){
			this.tiles[index.ordinal()].onClicked(player);
		}
	}

	@Override
	public void fillWithRain() {
		for(PC_MultiblockObject tile:this.tiles){
			if(tile!=null)
				tile.fillWithRain();
		}
	}

	@Override
	public int getLightValue() {
		int lightValue = 0;
		for(PC_MultiblockObject tile:this.tiles){
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
		for(PC_MultiblockObject tile:this.tiles){
			if(tile!=null)
				return tile.isLadder(entity);
		}
		return false;
	}

	@Override
	public boolean isBurning() {
		for(PC_MultiblockObject tile:this.tiles){
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
		if(index!=null && this.tiles[index.ordinal()]!=null){
			return this.tiles[index.ordinal()].getPickBlock();
		}
		return null;
	}

	@Override
	public float getEnchantPowerBonus() {
		float bonus = 0;
		for(PC_MultiblockObject tile:this.tiles){
			if(tile!=null)
				bonus += tile.getEnchantPowerBonus();
		}
		return bonus;
	}

	@Override
	public void onNeighborTEChange(int tileX, int tileY, int tileZ) {
		for(PC_MultiblockObject tile:this.tiles){
			if(tile!=null)
				tile.onNeighborTEChange(tileX, tileY, tileZ);
		}
	}

	@Override
	public List<AxisAlignedBB> getCollisionBoundingBoxes(Entity entity) {
		List<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
		for(PC_MultiblockObject tile:this.tiles){
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
		if(index!=null && this.tiles[index.ordinal()]!=null){
			return this.tiles[index.ordinal()].onBlockActivated(player);
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
		if(index!=null && this.tiles[index.ordinal()]!=null){
			return this.tiles[index.ordinal()].isSolid();
		}
		return false;
	}

	@Override
	public boolean renderWorldBlock(int modelId, RenderBlocks renderer) {
		if(renderer.hasOverrideBlockTexture())
			return true;
		for(PC_MultiblockObject tile:this.tiles){
			if(tile!=null)
				tile.renderWorldBlock(renderer);
		}
		return true;
	}

	@Override
	public void onTick() {
		for(PC_MultiblockObject tile:this.tiles){
			if(tile!=null)
				tile.updateObject();
		}
	}
	
	@Override
	public void onLoadedFromNBT(Flag flag) {
		for(int i=0; i<this.tiles.length; i++){
			if(this.tiles[i]!=null)
				this.tiles[i].setIndexAndMultiblock(PC_MultiblockIndex.values()[i], this);
		}
	}

	@Override
	public void onChunkUnload() {
		for(PC_MultiblockObject tile:this.tiles){
			if(tile!=null)
				tile.onChunkUnload();
		}
	}

	private void sendReloadTile(int index){
		if(!isClient()){
			NBTTagCompound nbtTagCompound = new NBTTagCompound();
			nbtTagCompound.setInteger("type", 1);
			nbtTagCompound.setInteger("index", index);
			PC_NBTTagHandler.saveToNBT(nbtTagCompound, "tile", this.tiles[index], Flag.SYNC);
			sendMessage(nbtTagCompound);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onClientMessage(EntityPlayer player, NBTTagCompound nbtTagCompound) {
		switch(nbtTagCompound.getInteger("type")){
		case 1:
			int index = nbtTagCompound.getInteger("index");
			this.tiles[index] = PC_NBTTagHandler.loadFromNBT(nbtTagCompound, "tile", PC_MultiblockObject.class, Flag.SYNC);
			if(this.tiles[index]!=null){
				this.tiles[index].setIndexAndMultiblock(PC_MultiblockIndex.values()[index], this);
			}
			renderUpdate();
			break;
		default:
			break;
		}
	}

	public void drop(List<ItemStack> drops) {

		PC_Utils.spawnItems(this.worldObj, this.xCoord, this.yCoord, this.zCoord, drops);
	}

	@Override
	public <T extends PC_IGridTile<?, T, ?, ?>> T getTile(PC_Direction side, int flags, Class<T> tileClass) {
		PC_MultiblockObject tile = this.tiles[0];
		if(tile!=null){
			return tile.getGridTile(flags, tileClass);
		}
		return null;
	}
	
	@Override
	public <T extends PC_IGridTile<?, T, ?, ?>> T getTile(PC_Direction dir, PC_Direction dir2, int flags, Class<T> tileClass) {
		PC_MultiblockObject tile = this.tiles[PC_MultiblockIndex.getFromDir(dir).ordinal()];
		if(tile!=null){
			return tile.getGridTile(flags, tileClass);
		}
		return null;
	}

	@Override
	public boolean canRedstoneConnect(PC_Direction side, int faceSide) {
		PC_MultiblockObject tile = this.tiles[PC_MultiblockIndex.FACEBOTTOM.ordinal()];
		if(tile!=null){
			return tile.canConnectRedstone(side);
		}
		return false;
	}

	@Override
	public int getRedstonePowerValue(PC_Direction side, int faceSide) {
		PC_MultiblockObject tile = this.tiles[PC_MultiblockIndex.FACEBOTTOM.ordinal()];
		if(tile!=null){
			return tile.getRedstonePowerValue(side);
		}
		return 0;
	}

	@Override
	public boolean canProvideStrongPower(PC_Direction side) {
		PC_MultiblockObject tile = this.tiles[PC_MultiblockIndex.FACEBOTTOM.ordinal()];
		if(tile!=null){
			return tile.canProvideStrongPower(side);
		}
		return false;
	}
	
}
