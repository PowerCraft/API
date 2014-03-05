package powercraft.api.renderer.model.ms3d;

import java.io.IOException;
import java.util.Arrays;

import powercraft.api.PC_Vec2;
import powercraft.api.PC_Vec3;

public class PC_MS3DTriangle {

	public static final int VERTEX_COUNT = 3;
	
	private byte flags;
	private TriangleVertex[] verteces = new TriangleVertex[VERTEX_COUNT];
	private byte smoothingGroup;
	private short groupIndex;
	
	PC_MS3DTriangle(PC_MS3DInputStream loaderInputStream) throws IOException {
		this.flags = (byte) loaderInputStream.readUnsignedShort();
		for(int i=0; i<VERTEX_COUNT; i++){
			this.verteces[i] = new TriangleVertex();
		}
		for(int i=0; i<VERTEX_COUNT; i++)
			this.verteces[i].vertexIndex = loaderInputStream.readUnsignedShort();
		for(int i=0; i<VERTEX_COUNT; i++)
			this.verteces[i].vertexNormal = loaderInputStream.readVec3();
		for(int i=0; i<VERTEX_COUNT; i++)
			this.verteces[i].textureCoord.x = loaderInputStream.readFloat();
		for(int i=0; i<VERTEX_COUNT; i++)
			this.verteces[i].textureCoord.y = loaderInputStream.readFloat();
		this.smoothingGroup = loaderInputStream.readByte();
		this.groupIndex = loaderInputStream.readUnsignedByte();
	}
	
	public int getFlags() {
		return this.flags;
	}

	public int getSmoothingGroup() {
		return this.smoothingGroup;
	}

	public int getGroupIndex() {
		return this.groupIndex;
	}

	public int getVertexIndex(int index){
		return this.verteces[index].vertexIndex;
	}
	
	public PC_Vec3 getVertexNormal(int index){
		return this.verteces[index].vertexNormal;
	}
	
	public PC_Vec2 getTextureCoord(int index){
		return this.verteces[index].textureCoord;
	}

	@Override
	public String toString() {
		return "Triangle [flags=" + this.flags + ", verteces="
				+ Arrays.toString(this.verteces) + ", smoothingGroup="
				+ this.smoothingGroup + ", groupIndex=" + this.groupIndex + "]";
	}

	private static class TriangleVertex {

		int vertexIndex;
		PC_Vec3 vertexNormal;
		PC_Vec2 textureCoord;
		
		TriangleVertex() {
			this.textureCoord = new PC_Vec2();
		}

		@Override
		public String toString() {
			return "TriangleVertex [vertexIndex=" + this.vertexIndex
					+ ", vertexNormal=" + this.vertexNormal + ", textureCoord="
					+ this.textureCoord + "]";
		}
		
	}
	
}
