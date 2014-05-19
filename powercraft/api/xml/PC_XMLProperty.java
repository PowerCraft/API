package powercraft.api.xml;



public class PC_XMLProperty {

	private String key;
	
	private String value;
	
	public PC_XMLProperty(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return this.key;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public void setValue(boolean b){
		setValue(PC_Pharser.toString(b));
	}
	
	public boolean getValueBoolean(){
		return PC_Pharser.pharseBoolean(getValue());
	}
	
	public void setValue(boolean[] b){
		setValue(PC_Pharser.toString(b));
	}
	
	public boolean[] getValueBooleanArray(){
		return PC_Pharser.pharseBooleanArray(getValue());
	}
	
	public void setValue(byte b){
		setValue(PC_Pharser.toString(b));
	}
	
	public byte getValueByte(){
		return PC_Pharser.pharseByte(getValue());
	}
	
	public void setValue(byte[] b){
		setValue(PC_Pharser.toString(b));
	}
	
	public byte[] getValueByteArray(){
		return PC_Pharser.pharseByteArray(getValue());
	}
	
	public void setValue(short s){
		setValue(PC_Pharser.toString(s));
	}
	
	public short getValueShort(){
		return PC_Pharser.pharseShort(getValue());
	}
	
	public void setValue(short[] s){
		setValue(PC_Pharser.toString(s));
	}
	
	public short[] getValueShortArray(){
		return PC_Pharser.pharseShortArray(getValue());
	}
	
	public void setValue(int i){
		setValue(PC_Pharser.toString(i));
	}
	
	public int getValueInt(){
		return PC_Pharser.pharseInt(getValue());
	}
	
	public void setValue(int[] i){
		setValue(PC_Pharser.toString(i));
	}
	
	public int[] getValueIntArray(){
		return PC_Pharser.pharseIntArray(getValue());
	}
	
	public void setValue(long l){
		setValue(PC_Pharser.toString(l));
	}
	
	public long getValueLong(){
		return PC_Pharser.pharseLong(getValue());
	}
	
	public void setValue(long[] l){
		setValue(PC_Pharser.toString(l));
	}
	
	public long[] getValueLongArray(){
		return PC_Pharser.pharseLongArray(getValue());
	}
	
	public void setValue(float f){
		setValue(PC_Pharser.toString(f));
	}
	
	public float getValueFloat(){
		return PC_Pharser.pharseFloat(getValue());
	}
	
	public void setValue(float[] f){
		setValue(PC_Pharser.toString(f));
	}
	
	public float[] getValueFloatArray(){
		return PC_Pharser.pharseFloatArray(getValue());
	}
	
	public void setValue(double d){
		setValue(PC_Pharser.toString(d));
	}
	
	public double getValueDouble(){
		return PC_Pharser.pharseDouble(getValue());
	}
	
	public void setValue(double[] d){
		setValue(PC_Pharser.toString(d));
	}
	
	public double[] getValueDoubleArray(){
		return PC_Pharser.pharseDoubleArray(getValue());
	}
	
}
