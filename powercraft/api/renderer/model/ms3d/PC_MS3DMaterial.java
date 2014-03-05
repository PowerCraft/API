package powercraft.api.renderer.model.ms3d;

import java.io.IOException;

import powercraft.api.PC_Vec4;

public class PC_MS3DMaterial {

	private String name;
	public PC_Vec4 ambient;
	public PC_Vec4 diffuse;
	public PC_Vec4 specular;
	public PC_Vec4 emissive;
	public float shininess;
	public float transparency;
	public byte mode;
	public String texture;
	public String alphamap;
	public String comment;
	
	PC_MS3DMaterial(PC_MS3DInputStream loaderInputStream) throws IOException {
		this.name = loaderInputStream.readString(32);
		this.ambient = loaderInputStream.readVec4();
		this.diffuse = loaderInputStream.readVec4();
		this.specular = loaderInputStream.readVec4();
		this.emissive = loaderInputStream.readVec4();
		this.shininess = loaderInputStream.readFloat();
		this.transparency = loaderInputStream.readFloat();
		this.mode = loaderInputStream.readByte();
		this.texture = loaderInputStream.readString(128);
		this.alphamap = loaderInputStream.readString(128);
	}

	void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getName() {
		return this.name;
	}
	
	public PC_Vec4 getAmbient() {
		return this.ambient;
	}

	public PC_Vec4 getDiffuse() {
		return this.diffuse;
	}

	public PC_Vec4 getSpecular() {
		return this.specular;
	}

	public PC_Vec4 getEmissive() {
		return this.emissive;
	}

	public float getShininess() {
		return this.shininess;
	}

	public float getTransparency() {
		return this.transparency;
	}

	public byte getMode() {
		return this.mode;
	}

	public String getTexture() {
		return this.texture;
	}

	public String getAlphamap() {
		return this.alphamap;
	}

	public String getComment() {
		return this.comment;
	}

	@Override
	public String toString() {
		return "Material [name=" + this.name + ", ambient=" + this.ambient + ", diffuse="
				+ this.diffuse + ", specular=" + this.specular + ", emissive=" + this.emissive
				+ ", shininess=" + this.shininess + ", transparency=" + this.transparency
				+ ", mode=" + this.mode + ", texture=" + this.texture + ", alphamap="
				+ this.alphamap + ", comment=" + this.comment + "]";
	}
	
}
