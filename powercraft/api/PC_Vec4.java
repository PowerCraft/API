package powercraft.api;

public class PC_Vec4 {

	public double x;
	public double y;
	public double z;
	public double w;

	public PC_Vec4() {
		this.w = 1;
	}

	public PC_Vec4(double x, double y, double z, double w) {

		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof PC_Vec4) {
			PC_Vec4 vec = (PC_Vec4) obj;
			return vec.x == this.x && vec.y == this.y && vec.z == this.z && vec.w == this.w;
		}
		return false;
	}

	@Override
	public int hashCode() {

		return ((int) this.x) ^ 78 + ((int) this.x) ^ 34 + ((int) this.y) ^ 12 + ((int) this.z);
	}

	@Override
	public String toString() {

		return "Vec4[" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "]";
	}

	public void setTo(PC_Vec4 vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
		this.w = vec.w;
	}

	public PC_Vec4I roundToInt() {
		return new PC_Vec4I((int) Math.round(this.x), (int) Math.round(this.y), (int) Math.round(this.z), (int) Math.round(this.w));
	}

}
