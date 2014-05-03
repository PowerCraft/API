package powercraft.api;


public class PC_Vec3 {

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

	public PC_Vec3 mul(double v) {
		return new PC_Vec3(this.x*v, this.y*v, this.z*v);
	}

	public PC_Vec3 add(PC_Vec3 vec) {
		return new PC_Vec3(this.x+vec.x, this.y+vec.y, this.z+vec.z);
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

}
