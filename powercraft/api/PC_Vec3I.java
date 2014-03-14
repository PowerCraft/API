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
	
	public PC_Vec3I offset(PC_Vec3I other) {
		return new PC_Vec3I(this.x+other.x, this.y+other.y, this.z+other.z);
	}
	
	
	public PC_Vec3I rotate(PC_Direction pcDir, int times){
		int tmpX=0, tmpY=0, tmpZ=0;
		PC_Direction tmp;

		PC_Direction[] sides = new PC_Direction[]{PC_Direction.DOWN, PC_Direction.NORTH, PC_Direction.EAST};
		for(PC_Direction dir:sides){
			tmp=dir.getRotation(pcDir, times);
			if(dir==tmp||dir==tmp.getOpposite()){
				tmpX+=tmp.offsetX*(dir.offsetX*this.x);
				tmpY+=tmp.offsetY*(dir.offsetY*this.y);
				tmpZ+=tmp.offsetZ*(dir.offsetZ*this.z);
			}else{
				tmpX+=tmp.offsetX*(dir.offsetX*this.x+dir.offsetY*this.y+dir.offsetZ*this.z);
				tmpY+=tmp.offsetY*(dir.offsetX*this.x+dir.offsetY*this.y+dir.offsetZ*this.z);
				tmpZ+=tmp.offsetZ*(dir.offsetX*this.x+dir.offsetY*this.y+dir.offsetZ*this.z);
			}
		}
		this.x=tmpX;
		this.y=tmpY;
		this.z=tmpZ;
		return this;
	}


}
