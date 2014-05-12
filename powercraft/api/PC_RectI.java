package powercraft.api;


public class PC_RectI {

	public int x;
	public int y;
	public int width;
	public int height;


	public PC_RectI() {

	}


	public PC_RectI(int x, int y, int width, int height) {

		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}


	public PC_RectI(PC_RectI rect) {

		this(rect.x, rect.y, rect.width, rect.height);
	}


	public PC_RectI(String attribute) {

		String[] attributes = attribute.split(",");
		if (attributes.length != 4) throw new NumberFormatException();
		this.x = Integer.parseInt(attributes[0].trim());
		this.y = Integer.parseInt(attributes[1].trim());
		this.width = Integer.parseInt(attributes[2].trim());
		this.height = Integer.parseInt(attributes[3].trim());
	}


	public void setTo(PC_RectI rect) {

		this.x = rect.x;
		this.y = rect.y;
		this.width = rect.width;
		this.height = rect.height;
	}


	public boolean setLocation(PC_Vec2I location) {

		if (this.x != location.x || this.y != location.y) {
			this.x = location.x;
			this.y = location.y;
			return true;
		}
		return false;
	}


	public PC_Vec2I getLocation() {

		return new PC_Vec2I(this.x, this.y);
	}
	
	public PC_Vec2 getLocationF() {

		return new PC_Vec2(this.x, this.y);
	}


	public boolean setSize(PC_Vec2I size) {

		if (this.width != size.x || this.height != size.y) {
			this.width = size.x;
			this.height = size.y;
			return true;
		}
		return false;
	}


	public PC_Vec2I getSize() {

		return new PC_Vec2I(this.width, this.height);
	}


	public PC_RectI averageQuantity(PC_RectI rect) {

		PC_RectI fin = new PC_RectI();
		int v1, v2;

		if (this.x > rect.x) {
			fin.x = this.x;
		} else {
			fin.x = rect.x;
		}

		if (this.y > rect.y) {
			fin.y = this.y;
		} else {
			fin.y = rect.y;
		}

		v1 = this.x + this.width;
		v2 = rect.x + rect.width;

		if (v1 > v2) {
			fin.width = v2 - fin.x;
		} else {
			fin.width = v1 - fin.x;
		}

		v1 = this.y + this.height;
		v2 = rect.y + rect.height;

		if (v1 > v2) {
			fin.height = v2 - fin.y;
		} else {
			fin.height = v1 - fin.y;
		}

		return fin;
	}


	public boolean contains(PC_Vec2I vec) {

		return this.x <= vec.x && this.x + this.width > vec.x && this.y <= vec.y && this.y + this.height > vec.y;
	}


	@Override
	public String toString() {

		return "PC_RectI [x=" + this.x + ", y=" + this.y + ", width=" + this.width + ", height=" + this.height + "]";
	}


	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + this.height;
		result = prime * result + this.width;
		result = prime * result + this.x;
		result = prime * result + this.y;
		return result;
	}


	@Override
	public boolean equals(Object obj) {

		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		PC_RectI other = (PC_RectI) obj;
		if (this.height != other.height) return false;
		if (this.width != other.width) return false;
		if (this.x != other.x) return false;
		if (this.y != other.y) return false;
		return true;
	}

	public PC_RectI enclosing(PC_RectI rect) {
		int xx1 = x<rect.x?x:rect.x;
		int yy1 = y<rect.y?y:rect.y;
		int c1 = x+width;
		int c2 = rect.x+rect.width;
		int xx2 = c1>c2?c1:c2;
		c1 = y+height;
		c2 = rect.y+rect.height;
		int yy2 = c1>c2?c1:c2;
		return new PC_RectI(xx1, yy1, xx2-xx1, yy2-yy1);
	}

}
