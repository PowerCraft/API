package powercraft.api.renderer.model.ms3d;

import java.io.IOException;

import powercraft.api.PC_Vec3;

class PC_MS3DKeyFrame {

	float time;
	PC_Vec3 value;
	
	PC_MS3DKeyFrame(PC_MS3DInputStream loaderInputStream) throws IOException {
		this.time = loaderInputStream.readFloat();
		this.value = loaderInputStream.readVec3();
	}

	@Override
	public String toString() {
		return "PC_MS3DKeyFrame [time=" + this.time + ", value=" + this.value
				+ "]";
	}
	
}
