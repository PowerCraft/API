package powercraft.api.script.weasel.source;

public class PC_WeaselConstantValue {
	
	private final Object value;
	
	public PC_WeaselConstantValue(byte value){
		this.value = Byte.valueOf(value);
	}
	
	public PC_WeaselConstantValue(short value){
		this.value = Short.valueOf(value);
	}
	
	public PC_WeaselConstantValue(int value){
		this.value = Integer.valueOf(value);
	}
	
	public PC_WeaselConstantValue(long value){
		this.value = Long.valueOf(value);
	}
	
	public PC_WeaselConstantValue(float value){
		this.value = Float.valueOf(value);
	}
	
	public PC_WeaselConstantValue(double value){
		this.value = Double.valueOf(value);
	}
	
	public PC_WeaselConstantValue(boolean value){
		this.value = Boolean.valueOf(value);
	}
	
	public PC_WeaselConstantValue(String value){
		this.value = value;
	}
	
	public PC_WeaselConstantValue(char value){
		this.value = Character.valueOf(value);
	}
	
	public byte getByte(){
		if(this.value instanceof Byte){
			return ((Byte)this.value).byteValue();
		}else if(this.value instanceof Character){
			return (byte) ((Character)this.value).charValue();
		}
		throw new RuntimeException(getTypeName()+" not compatible with byte");
	}
	
	public short getShort(){
		if(this.value instanceof Byte){
			return ((Byte)this.value).byteValue();
		}else if(this.value instanceof Short){
			return ((Short)this.value).shortValue();
		}else if(this.value instanceof Character){
			return (short) ((Character)this.value).charValue();
		}
		throw new RuntimeException(getTypeName()+" can't be cast to short");
	}
	
	public int getInt(){
		if(this.value instanceof Byte){
			return ((Byte)this.value).byteValue();
		}else if(this.value instanceof Short){
			return ((Short)this.value).shortValue();
		}else if(this.value instanceof Integer){
			return ((Integer)this.value).intValue();
		}else if(this.value instanceof Character){
			return ((Character)this.value).charValue();
		}
		throw new RuntimeException(getTypeName()+" can't be cast to int");
	}
	
	public long getLong(){
		if(this.value instanceof Byte){
			return ((Byte)this.value).byteValue();
		}else if(this.value instanceof Short){
			return ((Short)this.value).shortValue();
		}else if(this.value instanceof Integer){
			return ((Integer)this.value).intValue();
		}else if(this.value instanceof Long){
			return ((Long)this.value).longValue();
		}else if(this.value instanceof Character){
			return ((Character)this.value).charValue();
		}
		throw new RuntimeException(getTypeName()+" can't be cast to long");
	}
	
	public float getFloat(){
		if(this.value instanceof Byte){
			return ((Byte)this.value).byteValue();
		}else if(this.value instanceof Short){
			return ((Short)this.value).shortValue();
		}else if(this.value instanceof Integer){
			return ((Integer)this.value).intValue();
		}else if(this.value instanceof Long){
			return ((Long)this.value).longValue();
		}else if(this.value instanceof Float){
			return ((Float)this.value).floatValue();
		}else if(this.value instanceof Character){
			return ((Character)this.value).charValue();
		}
		throw new RuntimeException(getTypeName()+" can't be cast to float");
	}
	
	public double getDouble(){
		if(this.value instanceof Byte){
			return ((Byte)this.value).byteValue();
		}else if(this.value instanceof Short){
			return ((Short)this.value).shortValue();
		}else if(this.value instanceof Integer){
			return ((Integer)this.value).intValue();
		}else if(this.value instanceof Long){
			return ((Long)this.value).longValue();
		}else if(this.value instanceof Float){
			return ((Float)this.value).floatValue();
		}else if(this.value instanceof Double){
			return ((Double)this.value).doubleValue();
		}else if(this.value instanceof Character){
			return ((Character)this.value).charValue();
		}
		throw new RuntimeException(getTypeName()+" can't be cast to double");
	}
	
	public boolean getBool(){
		if(this.value instanceof Boolean)
			return ((Boolean)this.value).booleanValue();
		throw new RuntimeException(getTypeName()+" can't be cast to bool");
	}
	
	public String getString(){
		if(this.value instanceof String)
			return (String)this.value;
		throw new RuntimeException(getTypeName()+" can't be cast to xscript.lang.String");
	}
	
	public char getChar(){
		if(this.value instanceof Character)
			return ((Character)this.value).charValue();
		throw new RuntimeException(getTypeName()+" can't be cast to char");
	}
	
	public Class<?> getType(){
		if(this.value==null)
			return null;
		return this.value.getClass();
	}
	
	public PC_WeaselConstantValue add(PC_WeaselConstantValue other){
		if(isString()){
			return new PC_WeaselConstantValue((String)this.value+other);
		}
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new PC_WeaselConstantValue(getInt()+other.getInt());
		}else if(numberid==2){
			return new PC_WeaselConstantValue(getLong()+other.getLong());
		}else if(numberid==3){
			return new PC_WeaselConstantValue(getFloat()+other.getFloat());
		}else if(numberid==4){
			return new PC_WeaselConstantValue(getDouble()+other.getDouble());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public PC_WeaselConstantValue sub(PC_WeaselConstantValue other){
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new PC_WeaselConstantValue(getInt()-other.getInt());
		}else if(numberid==2){
			return new PC_WeaselConstantValue(getLong()-other.getLong());
		}else if(numberid==3){
			return new PC_WeaselConstantValue(getFloat()-other.getFloat());
		}else if(numberid==4){
			return new PC_WeaselConstantValue(getDouble()-other.getDouble());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public PC_WeaselConstantValue mul(PC_WeaselConstantValue other){
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new PC_WeaselConstantValue(getInt()*other.getInt());
		}else if(numberid==2){
			return new PC_WeaselConstantValue(getLong()*other.getLong());
		}else if(numberid==3){
			return new PC_WeaselConstantValue(getFloat()*other.getFloat());
		}else if(numberid==4){
			return new PC_WeaselConstantValue(getDouble()*other.getDouble());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public PC_WeaselConstantValue div(PC_WeaselConstantValue other){
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new PC_WeaselConstantValue(getInt()/other.getInt());
		}else if(numberid==2){
			return new PC_WeaselConstantValue(getLong()/other.getLong());
		}else if(numberid==3){
			return new PC_WeaselConstantValue(getFloat()/other.getFloat());
		}else if(numberid==4){
			return new PC_WeaselConstantValue(getDouble()/other.getDouble());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public PC_WeaselConstantValue mod(PC_WeaselConstantValue other){
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new PC_WeaselConstantValue(getInt()%other.getInt());
		}else if(numberid==2){
			return new PC_WeaselConstantValue(getLong()%other.getLong());
		}else if(numberid==3){
			return new PC_WeaselConstantValue(getFloat()%other.getFloat());
		}else if(numberid==4){
			return new PC_WeaselConstantValue(getDouble()%other.getDouble());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public PC_WeaselConstantValue neg(){
		int numberid = numberid();
		if(numberid==1){
			return new PC_WeaselConstantValue(-getInt());
		}else if(numberid==2){
			return new PC_WeaselConstantValue(-getLong());
		}else if(numberid==3){
			return new PC_WeaselConstantValue(-getFloat());
		}else if(numberid==4){
			return new PC_WeaselConstantValue(-getDouble());
		}
		throw new RuntimeException(getTypeName()+" not compatible");
	}
	
	public PC_WeaselConstantValue shl(PC_WeaselConstantValue other){
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new PC_WeaselConstantValue(getInt()<<other.getInt());
		}else if(numberid==2){
			return new PC_WeaselConstantValue(getLong()<<other.getLong());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public PC_WeaselConstantValue shr(PC_WeaselConstantValue other){
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new PC_WeaselConstantValue(getInt()>>other.getInt());
		}else if(numberid==2){
			return new PC_WeaselConstantValue(getLong()>>other.getLong());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public PC_WeaselConstantValue and(PC_WeaselConstantValue other){
		if(isBool()){
			return new PC_WeaselConstantValue(getBool() && other.getBool());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public PC_WeaselConstantValue band(PC_WeaselConstantValue other){
		if(isBool()){
			return new PC_WeaselConstantValue(getBool() & other.getBool());
		}
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new PC_WeaselConstantValue(getInt() & other.getInt());
		}else if(numberid==2){
			return new PC_WeaselConstantValue(getLong() & other.getLong());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public PC_WeaselConstantValue or(PC_WeaselConstantValue other){
		if(isBool()){
			return new PC_WeaselConstantValue(getBool() || other.getBool());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public PC_WeaselConstantValue bor(PC_WeaselConstantValue other){
		if(isBool()){
			return new PC_WeaselConstantValue(getBool() | other.getBool());
		}
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new PC_WeaselConstantValue(getInt() | other.getInt());
		}else if(numberid==2){
			return new PC_WeaselConstantValue(getLong() | other.getLong());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public PC_WeaselConstantValue xor(PC_WeaselConstantValue other){
		if(isBool()){
			return new PC_WeaselConstantValue(getBool() ^ other.getBool());
		}
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new PC_WeaselConstantValue(getInt() ^ other.getInt());
		}else if(numberid==2){
			return new PC_WeaselConstantValue(getLong() ^ other.getLong());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public PC_WeaselConstantValue not(){
		if(isBool()){
			return new PC_WeaselConstantValue(!getBool());
		}
		throw new RuntimeException(getTypeName()+" not compatible");
	}
	
	public PC_WeaselConstantValue bnot(){
		if(isBool()){
			return new PC_WeaselConstantValue(!getBool());
		}
		int numberid = numberid();
		if(numberid==1){
			return new PC_WeaselConstantValue(~getInt());
		}else if(numberid==2){
			return new PC_WeaselConstantValue(~getLong());
		}
		throw new RuntimeException(getTypeName()+" not compatible");
	}
	
	public PC_WeaselConstantValue pow(PC_WeaselConstantValue other) {
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new PC_WeaselConstantValue((int)Math.pow(getInt(), other.getInt()));
		}else if(numberid==2){
			return new PC_WeaselConstantValue((long)Math.pow(getLong(), other.getLong()));
		}else if(numberid==3){
			return new PC_WeaselConstantValue((float)Math.pow(getFloat(), other.getFloat()));
		}else if(numberid==4){
			return new PC_WeaselConstantValue(Math.pow(getDouble(), other.getDouble()));
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public PC_WeaselConstantValue eq(PC_WeaselConstantValue other) {
		if(isBool()){
			return new PC_WeaselConstantValue(getBool() == other.getBool());
		}else if(isString()){
			return new PC_WeaselConstantValue(getString().equals(other.getString()));
		}else if(isChar()){
			return new PC_WeaselConstantValue(getChar() == other.getChar());
		}
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new PC_WeaselConstantValue(getInt()==other.getInt());
		}else if(numberid==2){
			return new PC_WeaselConstantValue(getLong()==other.getLong());
		}else if(numberid==3){
			return new PC_WeaselConstantValue(getFloat()==other.getFloat());
		}else if(numberid==4){
			return new PC_WeaselConstantValue(getDouble()==other.getDouble());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public PC_WeaselConstantValue comp(PC_WeaselConstantValue other) {
		if(isChar()){
			return new PC_WeaselConstantValue(getChar() == other.getChar()?0:getChar()>other.getChar()?1:-1);
		}
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new PC_WeaselConstantValue(getInt()==other.getInt()?0:getInt()>other.getInt()?1:-1);
		}else if(numberid==2){
			return new PC_WeaselConstantValue(getLong()==other.getLong()?0:getLong()>other.getLong()?1:-1);
		}else if(numberid==3){
			return new PC_WeaselConstantValue(getFloat()==other.getFloat()?0:getFloat()>other.getFloat()?1:-1);
		}else if(numberid==4){
			return new PC_WeaselConstantValue(getDouble()==other.getDouble()?0:getDouble()>other.getDouble()?1:-1);
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public PC_WeaselConstantValue castTo(Class<?> c) {
		Class<?> t = getType();
		if(c==Boolean.class){
			return new PC_WeaselConstantValue(getBool());
		}else if(c==Character.class){
			if(t==Integer.class){
				return new PC_WeaselConstantValue((char)((Integer)this.value).intValue());
			}
			return new PC_WeaselConstantValue(getChar());
		}else if(c==Byte.class){
			return new PC_WeaselConstantValue(getInt());
		}else if(c==Short.class){
			return new PC_WeaselConstantValue(getInt());
		}else if(c==Integer.class){
			if(t==Long.class){
				return new PC_WeaselConstantValue((int)((Long)this.value).longValue());
			}else if(t==Float.class){
				return new PC_WeaselConstantValue((int)((Float)this.value).floatValue());
			}else if(t==Double.class){
				return new PC_WeaselConstantValue((int)((Double)this.value).doubleValue());
			}else if(t==Character.class){
				return new PC_WeaselConstantValue((int)((Character)this.value).charValue());
			}
			return new PC_WeaselConstantValue(getInt());
		}else if(c==Long.class){
			if(t==Float.class){
				return new PC_WeaselConstantValue((long)((Float)this.value).floatValue());
			}else if(t==Double.class){
				return new PC_WeaselConstantValue((long)((Double)this.value).doubleValue());
			}
			return new PC_WeaselConstantValue(getLong());
		}else if(c==Float.class){
			if(t==Double.class){
				return new PC_WeaselConstantValue((long)((Double)this.value).doubleValue());
			}
			return new PC_WeaselConstantValue(getFloat());
		}else if(c==Double.class){
			return new PC_WeaselConstantValue(getDouble());
		}
		return null;
	}
	
	private int numberid(){
		if(this.value instanceof Character){
			return 0;
		}else if(this.value instanceof Byte){
			return 1;
		}else if(this.value instanceof Short){
			return 1;
		}else if(this.value instanceof Integer){
			return 1;
		}else if(this.value instanceof Long){
			return 2;
		}else if(this.value instanceof Float){
			return 3;
		}else if(this.value instanceof Double){
			return 4;
		}
		return -1;
	}
	
	private static int compNID(int nid1, int nid2){
		if(nid1>0 && nid2>0){
			if(nid1<nid2)
				return nid2;
			return nid1;
		}else if(nid1>0 && nid2==0){
			return nid1;
		}else if(nid1==0 && nid2>0){
			return nid2;
		}
		return 0;
	}
	
	public boolean isBool(){
		return this.value instanceof Boolean;
	}
	
	public boolean isString(){
		return this.value instanceof String;
	}
	
	public boolean isChar(){
		return this.value instanceof Character;
	}

	@Override
	public String toString() {
		return this.value==null?"null":this.value.toString();
	}
	
	public String getTypeName(){
		Class<?> c = getType();
		if(c==null){
			return "null";
		}else if(c==Boolean.class){
			return "bool";
		}else if(c==Character.class){
			return "char";
		}else if(c==Integer.class){
			return "int";
		}else if(c==Long.class){
			return "long";
		}else if(c==Float.class){
			return "float";
		}else if(c==Double.class){
			return "double";
		}else if(c==String.class){
			return "xscript.lang.String";
		}else{
			throw new AssertionError();
		}
	}
	
}
