package powercraft.api.renderer.model.ms3d;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_Matrix;
import powercraft.api.PC_Module;
import powercraft.api.PC_ResourceReloadListener;
import powercraft.api.PC_Utils;
import powercraft.api.PC_ResourceReloadListener.PC_IResourceReloadListener;
import powercraft.api.PC_Vec2;
import powercraft.api.PC_Vec3;
import powercraft.api.renderer.model.PC_Model;


public class PC_MS3DModel extends PC_Model implements PC_IResourceReloadListener {

	public static final int MAX_VERTICES = 65534;
	public static final int MAX_TRIANGLES = 65534;
	public static final int MAX_GROUPS = 255;
	public static final int MAX_MATERIALS = 128;
	public static final int MAX_JOINTS = 128;

	public static final int SELECTED = 1;
	public static final int HIDDEN = 2;
	public static final int SELECTED2 = 4;
	public static final int DIRTY = 8;
	
	public static final String MAGIC_NUMBER = "MS3D000000";
	
	private ResourceLocation resourceLocation;
	private PC_Module module;
	private String modelName;
	
	private int version;
	private PC_MS3DVertex[] verteces;
	private PC_MS3DTriangle[] triangles;
	private PC_MS3DGroup[] groups;
	private PC_MS3DMaterial[] materials;
	private float animationFPS;
	private float currentTime;
	private int totalFrames;
	private PC_MS3DJoint[] joints;
	private String comment;
	private float jointSize;
	private int transparencyMode;
	private float alphaRef;
	
	public PC_MS3DModel(PC_Module module, String name){
		this.module = module;
		this.modelName = name;
		this.resourceLocation = PC_Utils.getResourceLocation(module, "models/"+name+".ms3d");
		PC_ResourceReloadListener.registerResourceReloadListener(this);
		onResourceReload();
	}

	public int getVersion(){
		return this.version;
	}
	
	public PC_MS3DGroup getGroup(int index) {
		return this.groups[index];
	}

	public PC_MS3DGroup getGroup(String name) {
		for(int i=0; i<this.groups.length; i++){
			if(name.equals(this.groups[i].getName())){
				return this.groups[i];
			}
		}
		return null;
	}
	
	public PC_MS3DVertex getVertex(int index) {
		return this.verteces[index];
	}

	public PC_MS3DTriangle getTrianlge(int index) {
		return this.triangles[index];
	}

	public PC_MS3DMaterial getMaterial(int index) {
		return this.materials[index];
	}
	
	public PC_MS3DMaterial getMaterial(String name) {
		for(int i=0; i<this.materials.length; i++){
			if(name.equals(this.materials[i].getName())){
				return this.materials[i];
			}
		}
		return null;
	}
	
	public PC_MS3DJoint getJoint(int index) {
		return this.joints[index];
	}
	
	public PC_MS3DJoint getJoint(String name) {
		for(int i=0; i<this.joints.length; i++){
			if(name.equals(this.joints[i].getName())){
				return this.joints[i];
			}
		}
		return null;
	}
	
	public int getJointIndex(String name) {
		for(int i=0; i<this.joints.length; i++){
			if(name.equals(this.joints[i].getName())){
				return i;
			}
		}
		return -1;
	}
	
	public PC_MS3DVertex[] getVerteces() {
		return this.verteces;
	}

	public PC_MS3DTriangle[] getTriangles() {
		return this.triangles;
	}

	public PC_MS3DGroup[] getGroups() {
		return this.groups;
	}

	public PC_MS3DMaterial[] getMaterials() {
		return this.materials;
	}

	public float getAnimationFPS() {
		return this.animationFPS;
	}

	public float getCurrentTime() {
		return this.currentTime;
	}

	public int getTotalFrames() {
		return this.totalFrames;
	}

	public float getMaxAnimationTime() {
		return this.totalFrames/this.animationFPS;
	}
	
	public PC_MS3DJoint[] getJoints() {
		return this.joints;
	}

	public String getComment() {
		return this.comment;
	}

	public float getJointSize() {
		return this.jointSize;
	}

	public int getTransparencyMode() {
		return this.transparencyMode;
	}

	public float getAlphaRef() {
		return this.alphaRef;
	}

	@Override
	public String toString() {
		String s="Model\n";
		s += "verteces:"+this.verteces.length+"\n";
		for(int i=0; i<this.verteces.length; i++){
			s += i+":"+this.verteces[i]+"\n";
		}
		s += "triangles:"+this.triangles.length+"\n";
		for(int i=0; i<this.triangles.length; i++){
			s += i+":"+this.triangles[i]+"\n";
		}
		s += "groups:"+this.groups.length+"\n";
		for(int i=0; i<this.groups.length; i++){
			s += i+":"+this.groups[i]+"\n";
		}
		s += "materials:"+this.materials.length+"\n";
		for(int i=0; i<this.materials.length; i++){
			s += i+":"+this.materials[i]+"\n";
		}
		s += "animationFPS:"+this.animationFPS+"\n";
		s += "currentTime:"+this.currentTime+"\n";
		s += "totalFrames:"+this.totalFrames+"\n";
		s += "joints:"+this.joints.length+"\n";
		for(int i=0; i<this.joints.length; i++){
			s += i+":"+this.joints[i]+"\n";
		}
		s += "comment:"+this.comment+"\n";
		s += "jointSize:"+this.jointSize+"\n";
		s += "transparencyMode:"+this.transparencyMode+"\n";
		s += "alphaRef:"+this.alphaRef;
		return s;
	}

	@Override
	public void onResourceReload() {
		IResourceManager resourceManager = PC_ClientUtils.mc().getResourceManager();
		InputStream inputStream = null;
		try{
			IResource resource = resourceManager.getResource(this.resourceLocation);
			inputStream = resource.getInputStream();
			PC_MS3DInputStream loaderInputStream = new PC_MS3DInputStream(inputStream);
			load(loaderInputStream);
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(inputStream!=null){
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void clear(){
		this.version = 0;
		this.verteces = null;
		this.triangles = null;
		this.groups = null;
		this.materials = null;
		this.animationFPS = 0;
		this.currentTime = 0;
		this.totalFrames = 0;
		this.joints = null;
		this.comment = null;
		this.jointSize = 0;
		this.transparencyMode = 0;
		this.alphaRef = 0;
	}
	
	private void load(PC_MS3DInputStream loaderInputStream) throws IOException{
		clear();
		String magicNumber = loaderInputStream.readString(10);
		if(!magicNumber.equals(MAGIC_NUMBER))
			throw new RuntimeException("Wrong magic number");
		this.version = loaderInputStream.readInt();
		int numVertices = loaderInputStream.readUnsignedShort();
		this.verteces = new PC_MS3DVertex[numVertices];
		for(int i=0; i<numVertices; i++){
			this.verteces[i] = new PC_MS3DVertex(loaderInputStream);
		}
		int numTriangles = loaderInputStream.readUnsignedShort();
		this.triangles = new PC_MS3DTriangle[numTriangles];
		for(int i=0; i<numTriangles; i++){
			this.triangles[i] = new PC_MS3DTriangle(loaderInputStream);
		}
		int numGroups = loaderInputStream.readUnsignedShort();
		this.groups = new PC_MS3DGroup[numGroups];
		for(int i=0; i<numGroups; i++){
			this.groups[i] = new PC_MS3DGroup(loaderInputStream);
		}
		int numMaterials = loaderInputStream.readUnsignedShort();
		this.materials = new PC_MS3DMaterial[numMaterials];
		for(int i=0; i<numMaterials; i++){
			this.materials[i] = new PC_MS3DMaterial(loaderInputStream);
		}
		this.animationFPS = loaderInputStream.readFloat();
		this.currentTime = loaderInputStream.readFloat();
		this.totalFrames = loaderInputStream.readInt();
		int numJoints = loaderInputStream.readUnsignedShort();
		this.joints = new PC_MS3DJoint[numJoints];
		for(int i=0; i<numJoints; i++){
			this.joints[i] = new PC_MS3DJoint(loaderInputStream);
		}
		int commentsSubVersion = 0;
		try{
			commentsSubVersion = loaderInputStream.readInt();
		}catch(EOFException e){/**/}
		if(commentsSubVersion>0){
			long numGroupComments = loaderInputStream.readUnsignedInt();
			for(int i=0; i<numGroupComments; i++){
				int index = loaderInputStream.readInt();
				this.groups[index].setComment(loaderInputStream.readString(loaderInputStream.readInt()));
			}
			long numMaterialComments = loaderInputStream.readUnsignedInt();
			for(int i=0; i<numMaterialComments; i++){
				int index = loaderInputStream.readInt();
				this.materials[index].setComment(loaderInputStream.readString(loaderInputStream.readInt()));
			}
			long numJointComments = loaderInputStream.readUnsignedInt();
			for(int i=0; i<numJointComments; i++){
				int index = loaderInputStream.readInt();
				this.joints[index].setComment(loaderInputStream.readString(loaderInputStream.readInt()));
			}
			int hasModelComment = loaderInputStream.readInt();
			if(hasModelComment==1){
				this.comment = loaderInputStream.readString(loaderInputStream.readInt());
			}
		}
		int vertexSubVersion = 0;
		try{
			vertexSubVersion = loaderInputStream.readInt();
		}catch(EOFException e){/**/}
		if(vertexSubVersion>0){
			for(int i=0; i<numVertices; i++){
				this.verteces[i].patch(vertexSubVersion, loaderInputStream);
			}
		}
		int jointSubVersion = 0;
		try{
			jointSubVersion = loaderInputStream.readInt();
		}catch(EOFException e){/**/}
		if(jointSubVersion>0){
			for(int i=0; i<numJoints; i++){
				this.joints[i].patch(jointSubVersion, loaderInputStream);
			}
		}
		int modelSubVersion = 0;
		try{
			modelSubVersion = loaderInputStream.readInt();
		}catch(EOFException e){/**/}
		if(modelSubVersion>0){
			this.jointSize = loaderInputStream.readFloat();
			this.transparencyMode = loaderInputStream.readInt();
			this.alphaRef = loaderInputStream.readFloat();
		}
		for(int i=0; i<numJoints; i++){
			this.joints[i].resolve(this);
		}
		calcAbsMat();
	}
	
	private PC_Matrix[] absPositions;
	
	@Override
	public void render(Entity entity, float time, float par3, float par4, float par5, float par6, float par7) {
		PC_Matrix[] matrixes = new PC_Matrix[this.joints.length];
		for(int i=0; i<matrixes.length; i++){
			if(matrixes[i]==null)
				matrixes[i] = calcJointMat(this.joints[i], time, matrixes);
		}
		for(int i=0; i<this.groups.length; i++){
			renderGroup(i, matrixes);
		}
	}
	
	@SuppressWarnings("hiding")
	private void renderGroup(int groupID, PC_Matrix[] matrixes){
		PC_MS3DGroup group = this.groups[groupID];
		PC_MS3DMaterial material;
		String texture;
		if(group.getMaterialIndex()==-1){
			material = null;
			texture = "default";
		}else{
			material = this.materials[group.getMaterialIndex()];
			texture = material.getTexture();
		}
		ResourceLocation resourceLocation = PC_Utils.getResourceLocation(this.module, "textures/model/"+this.modelName+"/"+texture+".png");
		PC_ClientUtils.mc().renderEngine.bindTexture(resourceLocation);
		int[] triangles = group.getTriangleIndices();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		Tessellator.instance.startDrawing(GL11.GL_TRIANGLES);
		for(int i=0; i<triangles.length; i++){
			PC_MS3DTriangle triangle = this.triangles[triangles[i]];
			for(int j=0; j<3; j++){
				renderVertex(triangle.getVertexIndex(j), triangle.getTextureCoord(j), triangle.getVertexNormal(j), matrixes);
			}
		}
		Tessellator.instance.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	private void renderVertex(int vertexID, PC_Vec2 textureCoord, PC_Vec3 normal, PC_Matrix[] matrixes){
		PC_MS3DVertex vertex = this.verteces[vertexID];
		PC_Vec3 pos = new PC_Vec3(vertex.getVertex());
		PC_Vec3 n = new PC_Vec3(normal);
		for(int i=0; i<4; i++){
			transform(vertex.getBoneIds(i), vertex.getWeights(i), pos, n, matrixes);
		}
		Tessellator.instance.setNormal((float)n.x, (float)n.y, (float)n.z);
		Tessellator.instance.addVertexWithUV(pos.x, pos.y, pos.z, textureCoord.x, textureCoord.y);
	}
	
	private void transform(int boneID, float weight, PC_Vec3 pos, PC_Vec3 normal, PC_Matrix[] matrixes){
		if(boneID<0 || weight<=0)
			return;
		PC_Matrix p44 = matrixes[boneID];
		PC_Matrix a44 = this.absPositions[boneID];
		PC_Vec3 tmp = a44.inverseTranslateVec(pos);
		tmp = a44.inverseRotateVec(tmp);
		tmp = p44.rotateVec(tmp);
		tmp = p44.translateVec(tmp);
		pos.x += tmp.x*weight;
		pos.y += tmp.y*weight;
		pos.z += tmp.z*weight;
		tmp = a44.inverseRotateVec(normal);
		tmp = p44.rotateVec(tmp);
		normal.x += tmp.x*weight;
		normal.y += tmp.y*weight;
		normal.z += tmp.z*weight;
	}
	
	private PC_Matrix calcJointMat(PC_MS3DJoint joint, float time, PC_Matrix[] matrixes){
		PC_Vec3 pos = joint.getKeyFrameTranslation(time);
		PC_Vec3 rot = joint.getKeyFramesRotation(time);
		PC_Matrix matrix = PC_Matrix.identity.setRotationRad(rot.x, rot.y, rot.z).setTranslation(pos.x, pos.y, pos.z);
		pos = joint.getPosition();
		rot = joint.getRotation();
		PC_Matrix relative = PC_Matrix.identity.setRotationRad(rot.x, rot.y, rot.z).setTranslation(pos.x, pos.y, pos.z);
		matrix = relative.postMultiply(matrix);
		int parent = joint.getParentIndex();
		if(parent!=-1){
			if(matrixes[parent]==null){
				matrixes[parent] = calcJointMat(this.joints[parent], time, matrixes);
			}
			matrix = matrixes[parent].postMultiply(matrix);
		}
		return matrix;
	}
	
	private void calcAbsMat(){
		this.absPositions = new PC_Matrix[this.joints.length];
		for(int i=0; i<this.joints.length; i++){
			if(this.absPositions[i]==null)
				this.absPositions[i] = calcAbsMat(this.joints[i]);
		}
	}
	
	private PC_Matrix calcAbsMat(PC_MS3DJoint joint){
		PC_Vec3 pos = joint.getPosition();
		PC_Vec3 rot = joint.getRotation();
		PC_Matrix matrix = PC_Matrix.identity.setRotationRad(rot.x, rot.y, rot.z).setTranslation(pos.x, pos.y, pos.z);
		int parent = joint.getParentIndex();
		if(parent!=-1){
			if(this.absPositions[parent]==null){
				this.absPositions[parent] = calcAbsMat(this.joints[parent]);
			}
			matrix = this.absPositions[parent].postMultiply(matrix);
		}
		return matrix;
	}
	
}
