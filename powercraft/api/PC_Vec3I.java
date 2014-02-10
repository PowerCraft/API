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
			return vec.x == x && vec.y == y && vec.z == z;
		}
		return false;
	}


	@Override
	public int hashCode() {

		return x ^ 34 + y ^ 12 + z;
	}


	@Override
	public String toString() {

		return "Vec3I[" + x + ", " + y + ", " + z + "]";
	}


}
