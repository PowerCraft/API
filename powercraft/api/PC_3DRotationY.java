package powercraft.api;

import powercraft.api.block.PC_Field.Flag;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class PC_3DRotationY implements PC_3DRotation {

	private int rotation;
	
	public PC_3DRotationY(int rotation){
		this.rotation = (rotation%4+4)%4;
	}
	
	public PC_3DRotationY(NBTTagCompound nbtTagCompound, Flag flag){
		rotation = nbtTagCompound.getByte("rotation");
	}
	
	@Override
	public PC_Direction getSidePosition(PC_Direction side) {
		return side.getRotation(PC_Direction.UP, rotation);
	}

	@Override
	public int getSideRotation(PC_Direction side){
		if(side==PC_Direction.UP){
			return rotation;
		}else if(side==PC_Direction.DOWN){
			return (4-rotation)%4;
		}
		return 0;
	}
	
	@Override
	public PC_3DRotation rotateAround(PC_Direction axis) {
		if(axis==PC_Direction.UP){
			return new PC_3DRotationY(rotation+1%4);
		}else if(axis==PC_Direction.DOWN){
			return new PC_3DRotationY(rotation+3%4);
		}
		return null;
	}

	@Override
	public ForgeDirection[] getValidRotations() {
		return new ForgeDirection[]{ForgeDirection.UP, ForgeDirection.DOWN};
	}

	@Override
	public AxisAlignedBB rotateBox(AxisAlignedBB box) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveToNBT(NBTTagCompound nbtTagCompound, Flag flag) {
		nbtTagCompound.setByte("rotation", (byte) rotation);
	}

}
