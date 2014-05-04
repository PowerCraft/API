package powercraft.api;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import powercraft.api.PC_Field.Flag;

public class PC_3DRotationFull implements PC_3DRotation {

	@SuppressWarnings("unused")
	public PC_3DRotationFull(PC_Direction side, Entity entity) {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unused")
	public PC_3DRotationFull(NBTTagCompound nbtTagCompound, Flag flag){
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public PC_Direction getSidePosition(PC_Direction side) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSideRotation(PC_Direction side){
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public PC_3DRotation rotateAround(PC_Direction axis) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ForgeDirection[] getValidRotations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AxisAlignedBB rotateBox(AxisAlignedBB box) {
		// TODO Auto-generated method stub
		return box;
	}

	@Override
	public void saveToNBT(NBTTagCompound nbtTagCompound, Flag flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PC_Direction getSidePositionInv(PC_Direction side) {
		// TODO Auto-generated method stub
		return null;
	}

}
