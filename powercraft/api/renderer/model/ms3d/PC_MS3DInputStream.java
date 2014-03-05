package powercraft.api.renderer.model.ms3d;

import java.io.IOException;
import java.io.InputStream;

import powercraft.api.PC_Vec2;
import powercraft.api.PC_Vec3;
import powercraft.api.PC_Vec4;


class PC_MS3DInputStream {
	
	private InputStream inputStream;
	
	PC_MS3DInputStream(InputStream inputStream){
		this.inputStream = inputStream;
	}

	byte readByte() throws IOException{
		return (byte) this.inputStream.read();
	}
	
	short readUnsignedByte() throws IOException{
		return (short) this.inputStream.read();
	}
	
	short readShort() throws IOException{
		return (short) (this.inputStream.read() | this.inputStream.read()<<8);
	}
	
	int readUnsignedShort() throws IOException{
		return this.inputStream.read() | this.inputStream.read()<<8;
	}
	
	int readInt() throws IOException{
		return this.inputStream.read() | this.inputStream.read()<<8 | this.inputStream.read()<<16 | this.inputStream.read()<<24;
	}
	
	long readUnsignedInt() throws IOException {
		return this.inputStream.read() | (long)this.inputStream.read()<<8 | (long)this.inputStream.read()<<16 | (long)this.inputStream.read()<<24;
	}
	
	float readFloat() throws IOException{
		return Float.intBitsToFloat(readInt());
	}
	
	String readString(int size) throws IOException{
		byte[] buffer = new byte[size];
		this.inputStream.read(buffer);
		return new String(buffer).trim();
	}

	PC_Vec2 readVec2() throws IOException {
		return new PC_Vec2(readFloat(), readFloat());
	}
	
	PC_Vec3 readVec3() throws IOException {
		return new PC_Vec3(readFloat(), readFloat(), readFloat());
	}
	
	PC_Vec4 readVec4() throws IOException {
		return new PC_Vec4(readFloat(), readFloat(), readFloat(), readFloat());
	}
	
}
