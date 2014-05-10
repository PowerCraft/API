package powercraft.api;


public class PC_Vec2I {

	public int x;
	public int y;


	public PC_Vec2I() {

	}


	public PC_Vec2I(int x, int y) {

		this.x = x;
		this.y = y;
	}


	public PC_Vec2I(PC_Vec2I vec) {

		this(vec.x, vec.y);
	}


	public PC_Vec2I(String attribute) {

		String[] attributes = attribute.split(",");
		if (attributes.length != 2) throw new NumberFormatException();
		this.x = Integer.parseInt(attributes[0].trim());
		this.y = Integer.parseInt(attributes[1].trim());
	}


	public PC_Vec2I(PC_Vec2 vec) {
		this.x = (int) (vec.x+0.5);
		this.y = (int) (vec.y+0.5);
	}


	public void setTo(PC_Vec2I vec) {

		this.x = vec.x;
		this.y = vec.y;
	}


	@Override
	public boolean equals(Object obj) {

		if (obj instanceof PC_Vec2I) {
			PC_Vec2I vec = (PC_Vec2I) obj;
			return vec.x == this.x && vec.y == this.y;
		}
		return false;
	}


	@Override
	public int hashCode() {

		return this.x ^ 34 + this.y;
	}


	@Override
	public String toString() {

		return "Vec2I[" + this.x + ", " + this.y + "]";
	}


	public PC_Vec2I add(int n) {

		return new PC_Vec2I(this.x + n, this.y + n);
	}


	@SuppressWarnings("hiding")
	public PC_Vec2I add(int x, int y) {

		return new PC_Vec2I(this.x + x, this.y + y);
	}


	public PC_Vec2I add(PC_Vec2I vec) {

		return new PC_Vec2I(this.x + vec.x, this.y + vec.y);
	}


	public PC_Vec2I sub(int n) {

		return new PC_Vec2I(this.x - n, this.y - n);
	}


	@SuppressWarnings("hiding")
	public PC_Vec2I sub(int x, int y) {

		return new PC_Vec2I(this.x - x, this.y - y);
	}


	public PC_Vec2I sub(PC_Vec2I vec) {

		return new PC_Vec2I(this.x - vec.x, this.y - vec.y);
	}


	public PC_Vec2I max(int n) {

		return new PC_Vec2I(this.x > n ? this.x : n, this.y > n ? this.y : n);
	}


	@SuppressWarnings("hiding")
	public PC_Vec2I max(int x, int y) {

		return new PC_Vec2I(this.x > x ? this.x : x, this.y > y ? this.y : y);
	}


	public PC_Vec2I max(PC_Vec2I vec) {

		return new PC_Vec2I(this.x > vec.x ? this.x : vec.x, this.y > vec.y ? this.y : vec.y);
	}

	public PC_Vec2I min(int n) {

		return new PC_Vec2I(this.x < n ? this.x : n, this.y < n ? this.y : n);
	}


	@SuppressWarnings("hiding")
	public PC_Vec2I min(int x, int y) {

		return new PC_Vec2I(this.x < x ? this.x : x, this.y < y ? this.y : y);
	}


	public PC_Vec2I min(PC_Vec2I vec) {

		return new PC_Vec2I(this.x < vec.x ? this.x : vec.x, this.y < vec.y ? this.y : vec.y);
	}


	public PC_Vec2I mul(float v) {
		return new PC_Vec2I((int)(this.x * v), (int)(this.y * v));
	}
	
	public PC_Vec2I div(float v) {
		return new PC_Vec2I((int)(this.x / v), (int)(this.y / v));
	}
	
}
