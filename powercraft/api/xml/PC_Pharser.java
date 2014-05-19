package powercraft.api.xml;

import powercraft.api.PC_Utils;


public final class PC_Pharser {
	
	private PC_Pharser(){
		PC_Utils.staticClassConstructor();
	}
	
	public static String toString(boolean b){
		return Boolean.toString(b);
	}
	
	public static boolean pharseBoolean(String value){
		if(value==null)
			throw new NumberFormatException();
		String v = value.trim();
		return v.equalsIgnoreCase("true") || v.equalsIgnoreCase("yes");
	}
	
	public static String toString(boolean b[]){
		return toString(b, ", ");
	}
	
	public static String toString(boolean b[], String space){
		String value = "";
		if(b!=null && b.length>0){
			value += toString(b[0]);
			for(int i=1; i<b.length; i++){
				value += space+toString(b[i]);
			}
		}
		return value;
	}
	
	public static boolean[] pharseBooleanArray(String value){
		if(value.trim().isEmpty())
			return new boolean[0];
		String[] tiles = value.split(",");
		boolean[] b = new boolean[tiles.length];
		for(int i=0; i<b.length; i++){
			b[i] = pharseBoolean(tiles[i]);
		}
		return b;
	}
	
	public static String toString(byte b){
		return Byte.toString(b);
	}
	
	public static byte pharseByte(String value){
		if(value==null)
			throw new NumberFormatException();
		String v = value.trim();
		return Byte.parseByte(v);
	}
	
	public static String toString(byte b[]){
		return toString(b, ", ");
	}
	
	public static String toString(byte b[], String space){
		String value = "";
		if(b!=null && b.length>0){
			value += toString(b[0]);
			for(int i=1; i<b.length; i++){
				value += space+toString(b[i]);
			}
		}
		return value;
	}
	
	public static byte[] pharseByteArray(String value){
		if(value.trim().isEmpty())
			return new byte[0];
		String[] tiles = value.split(",");
		byte[] b = new byte[tiles.length];
		for(int i=0; i<b.length; i++){
			b[i] = pharseByte(tiles[i]);
		}
		return b;
	}
	
	public static String toString(short s){
		return Short.toString(s);
	}
	
	public static short pharseShort(String value){
		if(value==null)
			throw new NumberFormatException();
		String v = value.trim();
		return Short.parseShort(v);
	}
	
	public static String toString(short s[]){
		return toString(s, ", ");
	}
	
	public static String toString(short s[], String space){
		String value = "";
		if(s!=null && s.length>0){
			value += toString(s[0]);
			for(int i=1; i<s.length; i++){
				value += space+toString(s[i]);
			}
		}
		return value;
	}
	
	public static short[] pharseShortArray(String value){
		if(value.trim().isEmpty())
			return new short[0];
		String[] tiles = value.split(",");
		short[] s = new short[tiles.length];
		for(int i=0; i<s.length; i++){
			s[i] = pharseShort(tiles[i]);
		}
		return s;
	}
	
	public static String toString(int i){
		return Integer.toString(i);
	}
	
	public static int pharseInt(String value){
		if(value==null)
			throw new NumberFormatException();
		String v = value.trim();
		return Integer.parseInt(v);
	}
	
	public static String toString(int i[]){
		return toString(i, ", ");
	}
	
	public static String toString(int i[], String space){
		String value = "";
		if(i!=null && i.length>0){
			value += toString(i[0]);
			for(int ii=1; ii<i.length; ii++){
				value += space+toString(i[ii]);
			}
		}
		return value;
	}
	
	public static int[] pharseIntArray(String value){
		if(value.trim().isEmpty())
			return new int[0];
		String[] tiles = value.split(",");
		int[] i = new int[tiles.length];
		for(int ii=0; ii<i.length; ii++){
			i[ii] = pharseShort(tiles[ii]);
		}
		return i;
	}
	
	public static String toString(long l){
		return Long.toString(l);
	}
	
	public static long pharseLong(String value){
		if(value==null)
			throw new NumberFormatException();
		String v = value.trim();
		return Long.parseLong(v);
	}
	
	public static String toString(long l[]){
		return toString(l, ", ");
	}
	
	public static String toString(long l[], String space){
		String value = "";
		if(l!=null && l.length>0){
			value += toString(l[0]);
			for(int i=1; i<l.length; i++){
				value += space+toString(l[i]);
			}
		}
		return value;
	}
	
	public static long[] pharseLongArray(String value){
		if(value.trim().isEmpty())
			return new long[0];
		String[] tiles = value.split(",");
		long[] l = new long[tiles.length];
		for(int i=0; i<l.length; i++){
			l[i] = pharseLong(tiles[i]);
		}
		return l;
	}
	
	public static String toString(float f){
		return Float.toString(f);
	}
	
	public static float pharseFloat(String value){
		if(value==null)
			throw new NumberFormatException();
		String v = value.trim();
		return Float.parseFloat(v);
	}
	
	public static String toString(float f[]){
		return toString(f, ", ");
	}
	
	public static String toString(float f[], String space){
		String value = "";
		if(f!=null && f.length>0){
			value += toString(f[0]);
			for(int i=1; i<f.length; i++){
				value += space+toString(f[i]);
			}
		}
		return value;
	}
	
	public static float[] pharseFloatArray(String value){
		if(value.trim().isEmpty())
			return new float[0];
		String[] tiles = value.split(",");
		float[] f = new float[tiles.length];
		for(int i=0; i<f.length; i++){
			f[i] = pharseFloat(tiles[i]);
		}
		return f;
	}
	
	public static String toString(double f){
		return Double.toString(f);
	}
	
	public static double pharseDouble(String value){
		if(value==null)
			throw new NumberFormatException();
		String v = value.trim();
		return Double.parseDouble(v);
	}
	
	public static String toString(double d[]){
		return toString(d, ", ");
	}
	
	public static String toString(double d[], String space){
		String value = "";
		if(d!=null && d.length>0){
			value += toString(d[0]);
			for(int i=1; i<d.length; i++){
				value += space+toString(d[i]);
			}
		}
		return value;
	}
	
	public static double[] pharseDoubleArray(String value){
		if(value.trim().isEmpty())
			return new double[0];
		String[] tiles = value.split(",");
		double[] d = new double[tiles.length];
		for(int i=0; i<d.length; i++){
			d[i] = pharseDouble(tiles[i]);
		}
		return d;
	}
	
}
