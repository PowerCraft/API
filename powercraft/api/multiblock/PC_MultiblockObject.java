package powercraft.api.multiblock;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import powercraft.api.PC_Direction;

public abstract class PC_MultiblockObject {

	protected PC_MultiblockIndex index;
	protected PC_TileEntityMultiblock tileEntityMultiblock;
	
	public List<AxisAlignedBB> getCollisionBoundingBoxes() {
		return null;
	}

	public AxisAlignedBB getSelectedBoundingBox() {
		return null;
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

	public void setIndexAndMultiblock(PC_MultiblockIndex index, PC_TileEntityMultiblock tileEntityMultiblock) {
		this.index = index;
		this.tileEntityMultiblock = tileEntityMultiblock;
	}

	public boolean onAdded() {
		return true;
	}

	public void updateObject() {
		
	}

	public void onChunkUnload() {
		
	}
	
}
