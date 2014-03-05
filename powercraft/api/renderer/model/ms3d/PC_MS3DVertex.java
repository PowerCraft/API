package powercraft.api.renderer.model.ms3d;

import java.io.IOException;

import powercraft.api.PC_Vec3;


public class PC_MS3DVertex {
	
public static final int BONE_COUNT = 4;
	
	private byte flags;
	private PC_Vec3 vertex;
	private byte[] boneIds = {-1, -1, -1, -1};
	private float[] weights = {1.0f, 0.0f, 0.0f, 0.0f};
	private short referenceCount;
	private long extra;
	
	PC_MS3DVertex(PC_MS3DInputStream loaderInputStream) throws IOException {
		this.flags = loaderInputStream.readByte();
		this.vertex = loaderInputStream.readVec3();
		this.boneIds[0] = loaderInputStream.readByte();
		this.referenceCount = loaderInputStream.readUnsignedByte();
	}

	void patch(int vertexSubVersion, PC_MS3DInputStream loaderInputStream) throws IOException {
		for(int i=1; i<BONE_COUNT; i++){
			this.boneIds[i] = loaderInputStream.readByte();
		}
		for(int i=0; i<3; i++){
			this.weights[i] = loaderInputStream.readUnsignedByte();
			if(vertexSubVersion==1){
				this.weights[i]/=255;
			}else{
				this.weights[i]/=100;
			}
		}
		this.weights[3] = 1.0f-this.weights[0]-this.weights[1]-this.weights[2];
		if(vertexSubVersion>1){
			this.extra = loaderInputStream.readUnsignedInt();
		}
	}
	
	public int getFlags() {
		return this.flags;
	}

	public PC_Vec3 getVertex() {
		return this.vertex;
	}

	public int getBoneIds(int index) {
		return this.boneIds[index];
	}

	public float getWeights(int index) {
		return this.weights[index];
	}

	public int getReferenceCount() {
		return this.referenceCount;
	}

	public long getExtra() {
		return this.extra;
	}
	
}
