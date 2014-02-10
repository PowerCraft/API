package powercraft.api;

import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public interface PC_3DRotation {

	public PC_Direction getSidePosition(PC_Direction side);

	public int getSideRotation(PC_Direction side);
	
	public PC_3DRotation rotateAround(PC_Direction axis);

	public ForgeDirection[] getValidRotations();

	public AxisAlignedBB rotateBox(AxisAlignedBB box);
	
}
