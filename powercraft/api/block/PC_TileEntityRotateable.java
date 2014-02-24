package powercraft.api.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import powercraft.api.PC_3DRotation;
import powercraft.api.PC_3DRotationFull;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Field;
import powercraft.api.PC_Field.Flag;

public abstract class PC_TileEntityRotateable extends PC_TileEntity {

	@PC_Field(flags={Flag.SAVE, Flag.SYNC})
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
