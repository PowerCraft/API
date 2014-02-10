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


	@Override
	public boolean equals(Object obj) {

		if (obj instanceof PC_Vec3) {
			PC_Vec3 vec = (PC_Vec3) obj;
			return vec.x == x && vec.y == y && vec.z == z;
		}
		return false;
	}	

	@Override
	public int hashCode() {

		return ((int)x) ^ 34 + ((int)y) ^ 12 + ((int)z);
	}


	@Override
	public String toString() {

		return "Vec3I[" + x + ", " + y + ", " + z + "]";
	}


	public void setTo(PC_Vec3 vec) {
		x = vec.x;
		y = vec.y;
		z = vec.z;
	}


}
