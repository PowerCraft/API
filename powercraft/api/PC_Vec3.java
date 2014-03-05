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


}
