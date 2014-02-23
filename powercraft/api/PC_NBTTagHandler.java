package powercraft.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTBase.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

import org.apache.commons.io.output.ByteArrayOutputStream;

import powercraft.api.block.PC_Field.Flag;

/**
 * 
 * NBTTag handler for saving and loading objects
 * 
 * @author Aaron
 *
 */
public final class PC_NBTTagHandler {

	/**
	 * saves a object to an nbttag
	 * @param nbtTagCompound the nbttag
	 * @param name the key
	 * @param value the object
	 */
	public static void saveToNBT(NBTTagCompound nbtTagCompound, String name, Object value, Flag flag){
		NBTBase base = getObjectNBT(value, flag);
		if(base!=null)
			nbtTagCompound.setTag(name, base);
	}
	
	/**
	 * create a nbt for a object<b>
	 * can save base types, arrays, PC_INBT instances and Enum values
	 * 
	 * @param value the object
	 * @return the generated nbt
	 */
	public static NBTBase getObjectNBT(Object value, Flag flag){
		if(value==null)
			return null;
		Class<?> c = value.getClass();
		if(c==Boolean.class){
			return new NBTTagByte((byte)((Boolean)value?1:0));
		}else if(c==Byte.class){
			return new NBTTagByte((Byte)value);
		}else if(c==Short.class){
			return new NBTTagShort((Short)value);
		}else if(c==Integer.class){
			return new NBTTagInt((Integer)value);
		}else if(c==Long.class){
			return new NBTTagLong((Long)value);
		}else if(c==Float.class){
			return new NBTTagFloat((Float)value);
		}else if(c==Double.class){
			return new NBTTagDouble((Double)value);
		}else if(c==String.class){
			return new NBTTagString((String)value);
		}else if(c==int[].class){
			return new NBTTagIntArray((int[])value);
		}else if(c==byte[].class){
			return new NBTTagByteArray((byte[])value);
		}else if(c==String[].class){
			String[] array = (String[])value;
			NBTTagList list = new NBTTagList();
			for(int i=0; i<array.length; i++){
				list.appendTag(new NBTTagString(array[i]));
			}
			return list;
		}else if(c==int[][].class){
			int[][] array = (int[][])value;
			NBTTagList list = new NBTTagList();
			for(int i=0; i<array.length; i++){
				list.appendTag(new NBTTagIntArray(array[i]));
			}
			return list;
		}else if(c==double[].class){
			double[] array = (double[])value;
			NBTTagList list = new NBTTagList();
			for(int i=0; i<array.length; i++){
				list.appendTag(new NBTTagDouble(array[i]));
			}
			return list;
		}else if(c==float[].class){
			float[] array = (float[])value;
			NBTTagList list = new NBTTagList();
			for(int i=0; i<array.length; i++){
				list.appendTag(new NBTTagFloat(array[i]));
			}
			return list;
		}else if(c==ItemStack[].class){
			ItemStack[] array = (ItemStack[])value;
			NBTTagList list = new NBTTagList();
			for (int i = 0; i < array.length; i++) {
				ItemStack itemStack = array[i];
				NBTTagCompound nbtTagCompound = new NBTTagCompound();
				if (itemStack != null) {
					itemStack.writeToNBT(nbtTagCompound);
				}
				list.appendTag(nbtTagCompound);
			}
			return list;
		}else if(c.isArray()){
			NBTTagList list = new NBTTagList();
			int size = Array.getLength(value);
			for(int i=0; i<size; i++){
				Object obj = Array.get(value, i);
				NBTBase base = getObjectNBT(obj, flag);
				if(base==null){
					base = new NBTTagCompound();
				}
				list.appendTag(base);
			}
			return list;
		}else if(PC_INBT.class.isAssignableFrom(c)){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("Class", c.getName());
			((PC_INBT)value).saveToNBT(tag, flag);
			return tag;
		}else if(Enum.class.isAssignableFrom(c)){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("Enum", c.getName());
			tag.setString("value", ((Enum<?>)value).name());
			return tag;
		}else if(Serializable.class.isAssignableFrom(c)){
			try {
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				ObjectOutputStream objOut = new ObjectOutputStream(output);
				objOut.writeObject(value);
				objOut.close();
				return new NBTTagByteArray(output.toByteArray());
			} catch (IOException e) {
				e.printStackTrace();
				PC_Logger.severe("Error while try to save object %s", value);
			}
			
		}
		PC_Logger.severe("Can't save object %s form type %s", value, c);
		return null;
	}
	
	/**
	 * loads a object from a nbttag
	 * @param nbtTagCompound the nbttag
	 * @param name the key
	 * @param c the expected object class
	 * @return the object
	 */
	public static <T> T loadFromNBT(NBTTagCompound nbtTagCompound, String name, Class<T> c, Flag flag){
		NBTBase base = nbtTagCompound.getTag(name);
		if(base==null)
			return null;
		return getObjectFromNBT(base, c, flag);
	}

	/**
	 * loads a object from a nbt
	 * @param base the nbt
	 * @param c the expected object class
	 * @return the object
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T getObjectFromNBT(NBTBase base, Class<T> c, Flag flag) {
		if(base==null)
			return null;
		if(base instanceof NBTTagCompound){
			if(((NBTTagCompound)base).hasNoTags()){
				return null;
			}
		}
		if(c==Boolean.class || c==boolean.class){
			return (T)(Boolean)(((NBTPrimitive)base).func_150290_f()!=0);
		}else if(c==Byte.class || c==byte.class){
			return (T)(Byte)((NBTPrimitive)base).func_150290_f();
		}else if(c==Short.class || c==short.class){
			return (T)(Short)((NBTPrimitive)base).func_150289_e();
		}else if(c==Integer.class || c==int.class){
			return (T)(Integer)((NBTPrimitive)base).func_150287_d();
		}else if(c==Long.class || c==long.class){
			return (T)(Long)((NBTPrimitive)base).func_150291_c();
		}else if(c==Float.class || c==float.class){
			return (T)(Float)((NBTPrimitive)base).func_150288_h();
		}else if(c==Double.class || c==double.class){
			return (T)(Double)((NBTPrimitive)base).func_150286_g();
		}else if(c==String.class){
			return c.cast(((NBTTagString)base).func_150285_a_());
		}else if(c==int[].class){
			return c.cast(((NBTTagIntArray)base).func_150302_c());
		}else if(c==byte[].class){
			return c.cast(((NBTTagByteArray)base).func_150292_c());
		}else if(c==String[].class){
			NBTTagList list = (NBTTagList) base;
			int size = list.tagCount();
			String[] array = new String[size];
			for(int i=0; i<size; i++){
				array[i] = list.getStringTagAt(i);
			}
			return c.cast(array);
		}else if(c==int[][].class){
			NBTTagList list = new NBTTagList();
			int size = list.tagCount();
			int[][] array = new int[size][];
			for(int i=0; i<size; i++){
				array[i] = list.func_150306_c(i);
			}
			return c.cast(array);
		}else if(c==double[].class){
			NBTTagList list = new NBTTagList();
			int size = list.tagCount();
			double[] array = new double[size];
			for(int i=0; i<size; i++){
				array[i] = list.func_150309_d(i);
			}
			return c.cast(array);
		}else if(c==float[].class){
			NBTTagList list = new NBTTagList();
			int size = list.tagCount();
			float[] array = new float[size];
			for(int i=0; i<size; i++){
				array[i] = list.func_150308_e(i);
			}
			return c.cast(array);
		}else if(c==ItemStack[].class){
			NBTTagList list = (NBTTagList) base;
			int size = list.tagCount();
			ItemStack[] array = new ItemStack[size];
			for (int i = 0; i < array.length; i++) {
				NBTTagCompound nbtTagCompound = list.getCompoundTagAt(i);
				if(nbtTagCompound.hasKey("id")){
					array[i] = ItemStack.loadItemStackFromNBT(nbtTagCompound);
				}
			}
			return c.cast(array);
		}else if(c.isArray()){
			NBTTagList list = (NBTTagList) base;
			int size = list.tagCount();
			Class<?> ac = c.getComponentType();
			Object array = Array.newInstance(ac, size);
			for(int i=0; i<size; i++){
				NBTBase obj = list.removeTag(0);
				list.appendTag(obj);
				Array.set(array, i, getObjectFromNBT(obj, ac, flag));
			}
			return c.cast(array);
		}else if(PC_INBT.class.isAssignableFrom(c)){
			NBTTagCompound tag = (NBTTagCompound) base;
			String cName = tag.getString("Class");
			try {
				Class<?> cc = Class.forName(cName);
				try{
					Constructor<?> constr = cc.getConstructor(NBTTagCompound.class, Flag.class);
					return c.cast(constr.newInstance(tag, flag));
				}catch(NoSuchMethodException e){}
				Constructor<?> constr = cc.getConstructor(NBTTagCompound.class);
				return c.cast(constr.newInstance(tag));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				PC_Logger.severe("Can't find class %s form NBT save", cName);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				PC_Logger.severe("Class %s need constructor %s(NBTTagCompound)", cName, cName);
			} catch (SecurityException e) {
				e.printStackTrace();
				PC_Logger.severe("No Permissions :(");
			} catch (InstantiationException e) {
				e.printStackTrace();
				PC_Logger.severe("Class %s can't be instantionated", cName);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				PC_Logger.severe("No access to constructor %s(NBTTagCompound)", cName);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				PC_Logger.severe("Class %s can't get NBTTagCompound as argument", cName);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				PC_Logger.severe("Error while initialize class %s", cName);
			}
			return null;
		}else if(Enum.class.isAssignableFrom(c)){
			NBTTagCompound tag = (NBTTagCompound) base;
			String eName = tag.getString("Enum");
			try {
				Class<?> ec = (Class<?>) Class.forName(eName);
				if(Enum.class.isAssignableFrom(ec)){
					return c.cast(Enum.valueOf((Class<? extends Enum>)ec, tag.getString("value")));
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} 
			PC_Logger.severe("Can't find enum %s form NBT save", eName);
			return null;
		}else if(Serializable.class.isAssignableFrom(c)){
			try {
				ByteArrayInputStream input = new ByteArrayInputStream(((NBTTagByteArray)base).func_150292_c());
				ObjectInputStream objInp = new ObjectInputStream(input);
				Object value = objInp.readObject();
				objInp.close();
				return c.cast(value);
			} catch (IOException e) {
				e.printStackTrace();
				PC_Logger.severe("Error while try to load object");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				PC_Logger.severe("Error while try to load object, class not found");
			}
			
		}
		PC_Logger.severe("Can't load an unknown object");
		return null;
	}
	
	private PC_NBTTagHandler() {
		throw new InstantiationError();
	}
	
}
