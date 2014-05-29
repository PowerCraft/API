package powercraft.api;

import powercraft.api.PC_Field.Flag;
import net.minecraft.nbt.NBTTagCompound;


public class PC_Vec3 implements PC_INBT {

	public double x;
	public double y;
	public double z;


	public PC_Vec3() {

	}


	public PC_Vec3(double x, double y, double z) {

		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public PC_Vec3(PC_Vec3 vec) {

		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
	}

	@SuppressWarnings("unused")
	public PC_Vec3(NBTTagCompound nbtTagCompound, Flag flag) {
		this.x = nbtTagCompound.getDouble("x");
		this.y = nbtTagCompound.getDouble("y");
		this.z = nbtTagCompound.getDouble("z");
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof PC_Vec3) {
			PC_Vec3 vec = (PC_Vec3) obj;
			return vec.x == this.x && vec.y == this.y && vec.z == this.z;
		}
		return false;
	}	

	@Override
	public int hashCode() {

		return ((int)this.x) ^ 34 + ((int)this.y) ^ 12 + ((int)this.z);
	}


	@Override
	public String toString() {

		return "Vec3[" + this.x + ", " + this.y + ", " + this.z + "]";
	}

	public void setTo(PC_Vec3 vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
	}
	
	public PC_Vec3 neg(){
		return mul(-1);
	}

	public PC_Vec3 mul(double v) {
		return new PC_Vec3(this.x*v, this.y*v, this.z*v);
	}

	public PC_Vec3 add(PC_Vec3 vec) {
		return new PC_Vec3(this.x+vec.x, this.y+vec.y, this.z+vec.z);
	}

	public PC_Vec3 add(double value) {
		return new PC_Vec3(this.x+value, this.y+value, this.z+value);
	}
	
	public PC_Vec3 sub(PC_Vec3 vec) {
		return new PC_Vec3(this.x-vec.x, this.y-vec.y, this.z-vec.z);
	}
	
	public double distanceTo(PC_Vec3 pos) {
		return sub(pos).length();
	}

	public double length() {
		return PC_MathHelper.sqrt_double(this.x*this.x+this.y*this.y+this.z*this.z);
	}


	public PC_Vec3 normalize() {
		double l = length();
		return new PC_Vec3(this.x/l, this.y/l, this.z/l);
	}


	public double dot(PC_Vec3 vec) {
		return this.x*vec.x+this.y*vec.y+this.z*vec.z;
	}


	@Override
	public void saveToNBT(NBTTagCompound tag, Flag flag) {
		tag.setDouble("x", this.x);
		tag.setDouble("y", this.y);
		tag.setDouble("z", this.z);
	}


	public PC_Vec3 cross(PC_Vec3 other) {
		return new PC_Vec3(this.y*other.z-this.z*other.y, this.z*other.x-this.x*other.z, this.x*other.y-this.y*other.x);
	}

}
