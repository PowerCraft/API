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
		x = Integer.parseInt(attributes[0].trim());
		y = Integer.parseInt(attributes[1].trim());
		width = Integer.parseInt(attributes[2].trim());
		height = Integer.parseInt(attributes[3].trim());
	}


	public void setTo(PC_RectI rect) {

		x = rect.x;
		y = rect.y;
		width = rect.width;
		height = rect.height;
	}


	public boolean setLocation(PC_Vec2I location) {

		if (x != location.x || y != location.y) {
			x = location.x;
			y = location.y;
			return true;
		}
		return false;
	}


	public PC_Vec2I getLocation() {

		return new PC_Vec2I(x, y);
	}


	public boolean setSize(PC_Vec2I size) {

		if (width != size.x || height != size.y) {
			width = size.x;
			height = size.y;
			return true;
		}
		return false;
	}


	public PC_Vec2I getSize() {

		return new PC_Vec2I(width, height);
	}


	public PC_RectI averageQuantity(PC_RectI rect) {

		PC_RectI fin = new PC_RectI();
		int v1, v2;

		if (x > rect.x) {
			fin.x = x;
		} else {
			fin.x = rect.x;
		}

		if (y > rect.y) {
			fin.y = y;
		} else {
			fin.y = rect.y;
		}

		v1 = x + width;
		v2 = rect.x + rect.width;

		if (v1 > v2) {
			fin.width = v2 - fin.x;
		} else {
			fin.width = v1 - fin.x;
		}

		v1 = y + height;
		v2 = rect.y + rect.height;

		if (v1 > v2) {
			fin.height = v2 - fin.y;
		} else {
			fin.height = v1 - fin.y;
		}

		return fin;
	}


	public boolean contains(PC_Vec2I vec) {

		return x <= vec.x && x + width > vec.x && y <= vec.y && y + height > vec.y;
	}


	@Override
	public String toString() {

		return "PC_RectI [x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + "]";
	}


	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + width;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}


	@Override
	public boolean equals(Object obj) {

		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		PC_RectI other = (PC_RectI) obj;
		if (height != other.height) return false;
		if (width != other.width) return false;
		if (x != other.x) return false;
		if (y != other.y) return false;
		return true;
	}


}
