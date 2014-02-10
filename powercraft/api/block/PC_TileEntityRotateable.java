package powercraft.api.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import powercraft.api.PC_3DRotation;
import powercraft.api.PC_3DRotationFull;
import powercraft.api.PC_Direction;

public abstract class PC_TileEntityRotateable extends PC_TileEntity {

	protected PC_3DRotation rotation;
	
	@Override
	public void onBlockPostSet(PC_Direction side, ItemStack stack, EntityPlayer player, float hitX, float hitY, float hitZ) {
		set3DRotation(new PC_3DRotationFull(side, player));
	}

	@Override
	public PC_3DRotation get3DRotation() {
		return rotation;
	}

	@Override
	public boolean set3DRotation(PC_3DRotation rotation) {
		this.rotation = rotation;
		return true;
	}

	@Override
	public boolean canRotate() {
		return true;
	}
	
}
