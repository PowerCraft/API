package powercraft.api;


public class PC_Vec3I {

	public int x;
	public int y;
	public int z;


	public PC_Vec3I() {

	}


	public PC_Vec3I(int x, int y, int z) {

		this.x = x;
		this.y = y;
		this.z = z;
	}


	@Override
	public boolean equals(Object obj) {

		if (obj instanceof PC_Vec3I) {
			PC_Vec3I vec = (PC_Vec3I) obj;
			return vec.x == this.x && vec.y == this.y && vec.z == this.z;
		}
		return false;
	}


	@Override
	public int hashCode() {

		return this.x ^ 34 + this.y ^ 12 + this.z;
	}


	@Override
	public String toString() {

		return "Vec3I[" + this.x + ", " + this.y + ", " + this.z + "]";
	}


	@SuppressWarnings("hiding")
	public PC_Vec3I offset(int x, int y, int z) {
		return new PC_Vec3I(this.x+x, this.y+y, this.z+z);
	}


}
