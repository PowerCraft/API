package powercraft.api.renderer.model.ms3d;

import java.io.IOException;
import java.util.Arrays;

public class PC_MS3DGroup {

	private byte flags;
	private String name;
	private int[] triangleIndices;
	private byte materialIndex;
	private String comment;
	
	PC_MS3DGroup(PC_MS3DInputStream loaderInputStream) throws IOException {
		this.flags = loaderInputStream.readByte();
		this.name = loaderInputStream.readString(32);
		int numTriangles = loaderInputStream.readUnsignedShort();
		this.triangleIndices = new int[numTriangles];
		for(int i=0; i<numTriangles; i++){
			this.triangleIndices[i] = loaderInputStream.readUnsignedShort();
		}
		this.materialIndex = loaderInputStream.readByte();
	}

	void setComment(String comment) {
		this.comment = comment;
	}
	
	public int getFlags() {
		return this.flags;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int[] getTriangleIndices(){
		return this.triangleIndices;
	}
	
	public int getMaterialIndex(){
		return this.materialIndex;
	}
	
	public String getComment(){
		return this.comment;
	}
	
	@Override
	public String toString() {
		return "Group [flags=" + this.flags + ", name=" + this.name
				+ ", triangleIndices=" + Arrays.toString(this.triangleIndices)
				+ ", materialIndex=" + this.materialIndex + ", comment=" + this.comment
				+ "]";
	}
	
}
