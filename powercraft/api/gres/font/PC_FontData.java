package powercraft.api.gres.font;

class PC_FontData {
	
	String name;
	float size;
	int style;
	boolean antiAlias = true;
	
	public PC_FontData(String name, float size, int style) {
		this.name = name;
		this.size = size;
		this.style = style;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result + Float.floatToIntBits(this.size);
		result = prime * result + this.style;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		PC_FontData other = (PC_FontData) obj;
		if (this.name == null) {
			if (other.name != null) return false;
		} else if (!this.name.equals(other.name)) return false;
		if (Float.floatToIntBits(this.size) != Float.floatToIntBits(other.size)) return false;
		if (this.style != other.style) return false;
		return true;
	}
	
	
	
}