package powercraft.api.renderer.model.ms3d;

import java.io.IOException;
import java.util.Arrays;

import powercraft.api.PC_Vec3;


public class PC_MS3DJoint {

	private byte flags;
	private String name;
	private String parentName;
	private int parentIndex;
	private PC_Vec3 rotation;
	private PC_Vec3 position;
	private PC_MS3DKeyFrame[] keyFrameRotations;
	private PC_MS3DKeyFrame[] keyFrameTranslations;
	private String comment;
	private PC_Vec3 color;
	
	PC_MS3DJoint(PC_MS3DInputStream loaderInputStream) throws IOException {
		this.flags = loaderInputStream.readByte();
		this.name = loaderInputStream.readString(32);
		this.parentName = loaderInputStream.readString(32);
		this.rotation = loaderInputStream.readVec3();
		this.position = loaderInputStream.readVec3();
		int numKeyFramesRotation = loaderInputStream.readUnsignedShort();
		int numKeyFramesTranslation = loaderInputStream.readUnsignedShort();
		this.keyFrameRotations = new PC_MS3DKeyFrame[numKeyFramesRotation];
		this.keyFrameTranslations = new PC_MS3DKeyFrame[numKeyFramesTranslation];
		for(int i=0; i<numKeyFramesRotation; i++){
			this.keyFrameRotations[i] = new PC_MS3DKeyFrame(loaderInputStream);
		}
		for(int i=0; i<numKeyFramesTranslation; i++){
			this.keyFrameTranslations[i] = new PC_MS3DKeyFrame(loaderInputStream);
		}
	}

	void setComment(String comment) {
		this.comment = comment;
	}

	void patch(int jointSubVersion, PC_MS3DInputStream loaderInputStream) throws IOException {
		this.color = loaderInputStream.readVec3();
	}

	void resolve(PC_MS3DModel model) {
		this.parentIndex = model.getJointIndex(this.parentName);
	}
	
	public int getFlags(){
		return this.flags;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getParentIndex() {
		return this.parentIndex;
	}

	public PC_Vec3 getRotation() {
		return this.rotation;
	}

	public PC_Vec3 getPosition() {
		return this.position;
	}

	public String getComment() {
		return this.comment;
	}

	public PC_Vec3 getColor() {
		return this.color;
	}
	
	public PC_Vec3 getKeyFramesRotation(float time) {
		PC_MS3DKeyFrame keyFrameRotation1 = null;
		PC_MS3DKeyFrame keyFrameRotation2 = null;
		for(int i=0; i<this.keyFrameRotations.length; i++){
			PC_MS3DKeyFrame keyFramesRotation = this.keyFrameRotations[i];
			if(keyFramesRotation.time<time && (keyFrameRotation1==null || keyFrameRotation1.time<keyFramesRotation.time)){
				keyFrameRotation1 = keyFramesRotation;
			}
			if(keyFramesRotation.time>=time && (keyFrameRotation2==null || keyFrameRotation2.time>keyFramesRotation.time)){
				keyFrameRotation2 = keyFramesRotation;
			}
		}
		if(keyFrameRotation1==null)
			return keyFrameRotation2==null?null:keyFrameRotation2.value;
		if(keyFrameRotation2==null)
			return keyFrameRotation1.value;
		float p = (time-keyFrameRotation1.time) / (keyFrameRotation2.time-keyFrameRotation1.time);
		PC_Vec3 rot1 = keyFrameRotation1.value;
		PC_Vec3 rot2 = keyFrameRotation2.value;
		return new PC_Vec3(rot1.x * (1.0f-p) + rot2.x * p, rot1.y * (1.0f-p) + rot2.y * p, rot1.z * (1.0f-p) + rot2.z * p);
	}

	public PC_Vec3 getKeyFrameTranslation(float time) {
		PC_MS3DKeyFrame keyFrameTranslation1 = null;
		PC_MS3DKeyFrame keyFrameTranslation2 = null;
		for(int i=0; i<this.keyFrameTranslations.length; i++){
			PC_MS3DKeyFrame keyFrameTranslation = this.keyFrameTranslations[i];
			if(keyFrameTranslation.time<time && (keyFrameTranslation1==null || keyFrameTranslation1.time<keyFrameTranslation.time)){
				keyFrameTranslation1 = keyFrameTranslation;
			}
			if(keyFrameTranslation.time>=time && (keyFrameTranslation2==null || keyFrameTranslation2.time>keyFrameTranslation.time)){
				keyFrameTranslation2 = keyFrameTranslation;
			}
		}
		if(keyFrameTranslation1==null)
			return keyFrameTranslation2==null?null:keyFrameTranslation2.value;
		if(keyFrameTranslation2==null)
			return keyFrameTranslation1.value;
		float p = (time-keyFrameTranslation1.time) / (keyFrameTranslation2.time-keyFrameTranslation1.time);
		PC_Vec3 rot1 = keyFrameTranslation1.value;
		PC_Vec3 rot2 = keyFrameTranslation2.value;
		return new PC_Vec3(rot1.x * (1.0f-p) + rot2.x * p, rot1.y * (1.0f-p) + rot2.y * p, rot1.z * (1.0f-p) + rot2.z * p);
	}

	@Override
	public String toString() {
		return "Joint [flags=" + this.flags + ", name=" + this.name + ", parentName="
				+ this.parentName + ", rotation=" + this.rotation + ", position="
				+ this.position + ", keyFramesRotations="
				+ Arrays.toString(this.keyFrameRotations)
				+ ", keyFrameTranslations="
				+ Arrays.toString(this.keyFrameTranslations) + ", comment="
				+ this.comment + ", color=" + this.color + "]";
	}
	
}
