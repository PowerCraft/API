package powercraft.api;

import powercraft.api.block.PC_Field.Flag;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class PC_3DRotationFull implements PC_3DRotation {

	public PC_3DRotationFull(PC_Direction side, Entity entity) {
		// TODO Auto-generated constructor stub
	}

	public PC_3DRotationFull(NBTTagCompound nbtTagCompound, Flag flag){
		
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
		return null;
	}

	@Override
	public void saveToNBT(NBTTagCompound nbtTagCompound, Flag flag) {
		// TODO Auto-generated method stub
		
	}

}
