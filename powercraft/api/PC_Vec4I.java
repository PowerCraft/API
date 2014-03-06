package powercraft.api;

public class PC_Vec4I {

	public int x;
	public int y;
	public int z;
	public int w;

	public PC_Vec4I() {

	}

	public PC_Vec4I(int _x, int _y, int _z, int _w) {

		this.x = _x;
		this.y = _y;
		this.z = _z;
		this.w = _w;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof PC_Vec4I) {
			PC_Vec4I vec = (PC_Vec4I) obj;
			return vec.x == this.x && vec.y == this.y && vec.z == this.z && vec.w == this.w;
		}
		return false;
	}

	@Override
	public int hashCode() {

		return (this.x) ^ 78 + (this.x) ^ 34 + (this.y) ^ 12 + (this.z);
	}

	@Override
	public String toString() {

		return "Vec4I[" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "]";
	}

	public void setTo(PC_Vec4I vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
		this.w = vec.w;
	}

	public void add(PC_Vec4I vec) throws NullPointerException {
		x += vec.x;
		y += vec.y;
		z += vec.z;
		w += vec.w;
	}

	public static PC_Vec4I sum(PC_Vec4I... vec) {
		PC_Vec4I result = new PC_Vec4I(0, 0, 0, 0);
		for (PC_Vec4I cvec : vec) {
			try {
				result.add(cvec);
			} catch (NullPointerException e) {
			}
		}
		return result;
	}

	public PC_Vec4 divide(double divident) {
		return new PC_Vec4(x / divident, y / divident, z / divident, w / divident);
	}

	public boolean isZero() {
		return x == 0 && y == 0 && z == 0 && w == 0;
	}

}
