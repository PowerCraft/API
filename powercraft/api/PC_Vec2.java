package powercraft.api;


public class PC_Vec2 {

	public double x;
	public double y;


	public PC_Vec2() {

	}


	public PC_Vec2(double x, double y) {

		this.x = x;
		this.y = y;
	}


	@Override
	public boolean equals(Object obj) {

		if (obj instanceof PC_Vec2) {
			PC_Vec2 vec = (PC_Vec2) obj;
			return vec.x == this.x && vec.y == this.y;
		}
		return false;
	}	

	@Override
	public int hashCode() {

		return ((int)this.x) ^ 12 + (int)this.y;
	}


	@Override
	public String toString() {

		return "Vec2[" + this.x + ", " + this.y + "]";
	}


	public void setTo(PC_Vec2 vec) {
		this.x = vec.x;
		this.y = vec.y;
	}


}
