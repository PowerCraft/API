package powercraft.api.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.PC_3DRotation;
import powercraft.api.PC_3DRotationY;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Utils;

public abstract class PC_Block extends PC_AbstractBlockBase {

	public PC_Block(Material material) {
		super(material);
	}

	public boolean canRotate() {
		return false;
	}
	
	@Override
	public boolean canRotate(IBlockAccess world, int x, int y, int z) {
		return canRotate();
	}
	
	@Override
	public PC_3DRotation getRotation(IBlockAccess world, int x, int y, int z) {
		if(canRotate(world, x, y, z)){
			return new PC_3DRotationY((PC_Utils.getMetadata(world, x, y, z)>>2) & 3);
		}
		return null;
	}

	@Override
	public int modifiyMetadataPreSet(World world, int x, int y, int z, PC_Direction side, ItemStack stack, EntityPlayer player, float hitX, float hitY, float hitZ, int metadata) {
		if(canRotate(world, x, y, z)){
			return PC_Utils.getRotationMetadata(metadata, player);
		}
		return metadata;
	}

	@Override
	public void onBlockPostSet(World world, int x, int y, int z, PC_Direction side, ItemStack stack, EntityPlayer player, float hitX, float hitY, float hitZ, int metadata) {
		
	}
	
}
