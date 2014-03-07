package powercraft.api;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import powercraft.api.PC_Field.Flag;

public class PC_3DRotationY implements PC_3DRotation {

	private int rotation;
	
	public PC_3DRotationY(int rotation){
		this.rotation = (rotation%4+4)%4;
	}
	
	public PC_3DRotationY(Entity entity){
		this.rotation = PC_Utils.getRotation(entity);
	}
	
	@SuppressWarnings("unused")
	public PC_3DRotationY(NBTTagCompound nbtTagCompound, Flag flag){
		this.rotation = nbtTagCompound.getByte("rotation");
	}
	
	@Override
	public PC_Direction getSidePosition(PC_Direction side) {
		return side.getRotation(PC_Direction.UP, this.rotation);
	}
	
	@Override
	public PC_Direction getSidePositionInv(PC_Direction side) {
		return side.getRotation(PC_Direction.DOWN, this.rotation);
	}

	@Override
	public int getSideRotation(PC_Direction side){
		if(side==PC_Direction.UP){
			return this.rotation;
		}else if(side==PC_Direction.DOWN){
			return (4-this.rotation)%4;
		}
		return 0;
	}
	
	@Override
	public PC_3DRotation rotateAround(PC_Direction axis) {
		if(axis==PC_Direction.UP){
			return new PC_3DRotationY(this.rotation+1%4);
		}else if(axis==PC_Direction.DOWN){
			return new PC_3DRotationY(this.rotation+3%4);
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
		return box;
	}

	@Override
	public void saveToNBT(NBTTagCompound nbtTagCompound, Flag flag) {
		nbtTagCompound.setByte("rotation", (byte) this.rotation);
	}

	@Override
	public String toString() {
		return "PC_3DRotationY [rotation=" + this.rotation + "]";
	}
	
}
