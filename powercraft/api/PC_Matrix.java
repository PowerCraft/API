package powercraft.api;

import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * 
 * Matrix
 * 
 * @author Nils
 *
 */
public class PC_Matrix {

	/**
	 * die Identitätsmatrix
	 */
	public static final PC_Matrix identity = new PC_Matrix();
	
	/**
	 * Matrix buffer
	 */
	private final float[] mat = new float[16];

	public PC_Matrix(){
		this(1);
	}
	
	public PC_Matrix(float f){
		for(int i=0; i<4; i++){
			this.mat[i*4+i] = f;
		}
	}
	
	public PC_Matrix(PC_Matrix m){
		System.arraycopy(m.mat, 0, this.mat, 0, 16);
	}
	
	/*public PC_Matrix translate(float x, float y, float z) {
		return multiply(getTranslationMarix(x, y, z));
	}
	
	public PC_Matrix rotate(float a, float x, float y, float z) {
		return multiply(getRotationMarix(a, x, y, z));
	}
	
	public PC_Matrix rotateRad(float a, float x, float y, float z) {
		return multiply(getRotationMarixRad(a, x, y, z));
	}
	
	public PC_Matrix scale(float x, float y, float z) {
		return multiply(getScalationMarix(x, y, z));
	}
	
	public PC_Matrix rotateEuler(float x, float y, float z) {
		return multiply(getEulerRotationMarix(x, y, z));
	}*/
	
	public PC_Matrix add(PC_Matrix m) {
		PC_Matrix m2 = new PC_Matrix();
		for(int i=0; i<16; i++){
			m2.mat[i] = this.mat[i]+m.mat[i];
		}
		return m2;
	}
	
	public PC_Matrix multiply(float f) {
		PC_Matrix m = new PC_Matrix();
		for(int i=0; i<16; i++){
			m.mat[i] = this.mat[i]*f;
		}
		return m;
	}
	
	/*public PC_Matrix inverse(){
		PC_Matrix m = new PC_Matrix();
		android.opengl.PC_Matrix.invertM(m.mat, 0, this.mat, 0);
		return m;
	}*/
	
	public void writeTo(FloatBuffer b) {
		for(int i=0; i<16; i++){
			b.put(this.mat[i]);
		}
	}
	
	public float[] getMatix(){
		return this.mat;
	}

	@Override
	public String toString() {
		return "Matrix [mat=" + Arrays.toString(this.mat) + "]";
	}

	public PC_Matrix setTranslation(double x, double y, double z){
		PC_Matrix m = new PC_Matrix(this);
		m.mat[12] = (float)x;
		m.mat[13] = (float)y;
		m.mat[14] = (float)z;
		return m;
	}
	
	public PC_Matrix setRotationRad(double x, double y, double z){
		PC_Matrix m = new PC_Matrix(this);
		float cr = (float) Math.cos(x);
		float sr = (float) Math.sin(x);
		float cp = (float) Math.cos(y);
		float sp = (float) Math.sin(y);
		float cy = (float) Math.cos(z);
		float sy = (float) Math.sin(z);

		m.mat[0] = cp*cy ;
		m.mat[1] = cp*sy ;
		m.mat[2] = -sp ;

		float srsp = sr*sp;
		float crsp = cr*sp;

		m.mat[4] = srsp*cy-cr*sy;
		m.mat[5] = srsp*sy+cr*cy;
		m.mat[6] = sr*cp;

		m.mat[8] = crsp*cy+sr*sy;
		m.mat[9] = crsp*sy-sr*cy;
		m.mat[10] = cr*cp;
		return m;
	}
	
	public PC_Vec3 inverseRotateVec(PC_Vec3 v){
		PC_Vec3 v2 = new PC_Vec3();
		v2.x = v.x*this.mat[0]+v.y*this.mat[1]+v.z*this.mat[2];
		v2.y = v.x*this.mat[4]+v.y*this.mat[5]+v.z*this.mat[6];
		v2.z = v.x*this.mat[8]+v.y*this.mat[9]+v.z*this.mat[10];
		return v2;
	}
	
	public PC_Vec3 rotateVec(PC_Vec3 v){
		PC_Vec3 v2 = new PC_Vec3();
		v2.x = v.x*this.mat[0]+v.y*this.mat[4]+v.z*this.mat[8];
		v2.y = v.x*this.mat[1]+v.y*this.mat[5]+v.z*this.mat[9];
		v2.z = v.x*this.mat[2]+v.y*this.mat[6]+v.z*this.mat[10];
		return v2;
	}
	
	public PC_Vec3 translateVec(PC_Vec3 v){
		PC_Vec3 v2 = new PC_Vec3();
		v2.x = v.x+this.mat[12];
		v2.y = v.y+this.mat[13];
		v2.z = v.z+this.mat[14];
		return v2;
	}
	
	public PC_Vec3 inverseTranslateVec(PC_Vec3 v){
		PC_Vec3 v2 = new PC_Vec3();
		v2.x = v.x-this.mat[12];
		v2.y = v.y-this.mat[13];
		v2.z = v.z-this.mat[14];
		return v2;
	}
	
	public PC_Matrix postMultiply(PC_Matrix m){
		PC_Matrix m2 = new PC_Matrix();
		m2.mat[0] = this.mat[0]*m.mat[0] + this.mat[4]*m.mat[1] + this.mat[8]*m.mat[2];
		m2.mat[1] = this.mat[1]*m.mat[0] + this.mat[5]*m.mat[1] + this.mat[9]*m.mat[2];
		m2.mat[2] = this.mat[2]*m.mat[0] + this.mat[6]*m.mat[1] + this.mat[10]*m.mat[2];

		m2.mat[4] = this.mat[0]*m.mat[4] + this.mat[4]*m.mat[5] + this.mat[8]*m.mat[6];
		m2.mat[5] = this.mat[1]*m.mat[4] + this.mat[5]*m.mat[5] + this.mat[9]*m.mat[6];
		m2.mat[6] = this.mat[2]*m.mat[4] + this.mat[6]*m.mat[5] + this.mat[10]*m.mat[6];

		m2.mat[8] = this.mat[0]*m.mat[8] + this.mat[4]*m.mat[9] + this.mat[8]*m.mat[10];
		m2.mat[9] = this.mat[1]*m.mat[8] + this.mat[5]*m.mat[9] + this.mat[9]*m.mat[10];
		m2.mat[10] = this.mat[2]*m.mat[8] + this.mat[6]*m.mat[9] + this.mat[10]*m.mat[10];

		m2.mat[12] = this.mat[0]*m.mat[12] + this.mat[4]*m.mat[13] + this.mat[8]*m.mat[14] + this.mat[12];
		m2.mat[13] = this.mat[1]*m.mat[12] + this.mat[5]*m.mat[13] + this.mat[9]*m.mat[14] + this.mat[13];
		m2.mat[14] = this.mat[2]*m.mat[12] + this.mat[6]*m.mat[13] + this.mat[10]*m.mat[14] + this.mat[14];
		return m2;
	}
	
	public PC_Vec4 multiply(PC_Vec4 v){
		PC_Vec4 v2 = new PC_Vec4();
		v2.x = this.mat[0]*v.x + this.mat[4]*v.y + this.mat[8]*v.z + this.mat[12]*v.w;
		v2.y = this.mat[1]*v.x + this.mat[5]*v.y + this.mat[9]*v.z + this.mat[13]*v.w;
		v2.z = this.mat[2]*v.x + this.mat[6]*v.y + this.mat[10]*v.z + this.mat[14]*v.w;
		v2.w = this.mat[3]*v.x + this.mat[7]*v.y + this.mat[11]*v.z + this.mat[15]*v.w;
		return v2;
	}
	
	public PC_Matrix multiply(PC_Matrix m){
		PC_Matrix m2 = new PC_Matrix();
		m2.mat[0] = this.mat[0]*m.mat[0] + this.mat[4]*m.mat[1] + this.mat[8]*m.mat[2] + this.mat[12]*m.mat[3];
		m2.mat[1] = this.mat[1]*m.mat[0] + this.mat[5]*m.mat[1] + this.mat[9]*m.mat[2] + this.mat[13]*m.mat[3];
		m2.mat[2] = this.mat[2]*m.mat[0] + this.mat[6]*m.mat[1] + this.mat[10]*m.mat[2] + this.mat[14]*m.mat[3];
		m2.mat[3] = this.mat[3]*m.mat[0] + this.mat[7]*m.mat[1] + this.mat[11]*m.mat[2] + this.mat[15]*m.mat[3];
		
		m2.mat[4] = this.mat[0]*m.mat[4] + this.mat[4]*m.mat[5] + this.mat[8]*m.mat[6] + this.mat[12]*m.mat[7];
		m2.mat[5] = this.mat[1]*m.mat[4] + this.mat[5]*m.mat[5] + this.mat[9]*m.mat[6] + this.mat[13]*m.mat[7];
		m2.mat[6] = this.mat[2]*m.mat[4] + this.mat[6]*m.mat[5] + this.mat[10]*m.mat[6] + this.mat[14]*m.mat[7];
		m2.mat[7] = this.mat[3]*m.mat[4] + this.mat[7]*m.mat[5] + this.mat[11]*m.mat[6] + this.mat[15]*m.mat[7];
		
		m2.mat[8] = this.mat[0]*m.mat[8] + this.mat[4]*m.mat[9] + this.mat[8]*m.mat[10] + this.mat[12]*m.mat[11];
		m2.mat[9] = this.mat[1]*m.mat[8] + this.mat[5]*m.mat[9] + this.mat[9]*m.mat[10] + this.mat[13]*m.mat[11];
		m2.mat[10] = this.mat[2]*m.mat[8] + this.mat[6]*m.mat[9] + this.mat[10]*m.mat[10] + this.mat[14]*m.mat[11];
		m2.mat[11] = this.mat[3]*m.mat[8] + this.mat[7]*m.mat[9] + this.mat[11]*m.mat[10] + this.mat[15]*m.mat[11];
		
		m2.mat[12] = this.mat[0]*m.mat[12] + this.mat[4]*m.mat[13] + this.mat[8]*m.mat[14] + this.mat[12]*m.mat[15];
		m2.mat[13] = this.mat[1]*m.mat[12] + this.mat[5]*m.mat[13] + this.mat[9]*m.mat[14] + this.mat[13]*m.mat[15];
		m2.mat[14] = this.mat[2]*m.mat[12] + this.mat[6]*m.mat[13] + this.mat[10]*m.mat[14] + this.mat[14]*m.mat[15];
		m2.mat[15] = this.mat[3]*m.mat[12] + this.mat[7]*m.mat[13] + this.mat[11]*m.mat[14] + this.mat[15]*m.mat[15];
		return m2;
	}
	
	/*public static PC_Matrix getTranslationMarix(float x, float y, float z){
		PC_Matrix m = new PC_Matrix();
		android.opengl.PC_Matrix.translateM(m.mat, 0, x, y, z);
		return m;
	}
	
	public static PC_Matrix getRotationMarix(float a, float x, float y, float z){
		PC_Matrix m = new PC_Matrix();
		android.opengl.PC_Matrix.rotateM(m.mat, 0, a, x, y, z);
		return m;
	}
	
	public static PC_Matrix getRotationMarixRad(float a, float x, float y, float z){
		PC_Matrix m = new PC_Matrix();
		android.opengl.PC_Matrix.rotateM(m.mat, 0, (float)Math.toDegrees(a), x, y, z);
		return m;
	}
	
	public static PC_Matrix getScalationMarix(float x, float y, float z){
		PC_Matrix m = new PC_Matrix();
		android.opengl.PC_Matrix.scaleM(m.mat, 0, x, y, z);
		return m;
	}
	
	public static PC_Matrix getEulerRotationMarix(float x, float y, float z){
		PC_Matrix m = new PC_Matrix();
		android.opengl.PC_Matrix.setRotateEulerM(m.mat, 0, x, y, z);
		return m;
	}
	
	public static PC_Matrix getEulerRotationMarixRad(float x, float y, float z){
		PC_Matrix m = new PC_Matrix();
		android.opengl.PC_Matrix.setRotateEulerM(m.mat, 0, (float)Math.toDegrees(x), (float)Math.toDegrees(y), (float)Math.toDegrees(z));
		return m;
	}
	
	public static PC_Matrix getPerspectiveMarix(float fovy, float aspect, float zNear, float zFar){
		PC_Matrix m = new PC_Matrix();
		android.opengl.PC_Matrix.perspectiveM(m.mat, 0, fovy, aspect, zNear, zFar);
		return m;
	}
	
	public static PC_Matrix getOrthoMarix(float left, float right, float bottom, float top, float near, float far){
		PC_Matrix m = new PC_Matrix();
		android.opengl.PC_Matrix.orthoM(m.mat, 0, left, right, bottom, top, near, far);
		return m;
	}
	
	public static PC_Matrix getFrustumMarix(float left, float right, float bottom, float top, float near, float far){
		PC_Matrix m = new PC_Matrix();
		android.opengl.PC_Matrix.frustumM(m.mat, 0, left, right, bottom, top, near, far);
		return m;
	}*/
	
}
