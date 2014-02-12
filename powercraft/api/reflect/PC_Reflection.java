package powercraft.api.reflect;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.EnumMap;

import powercraft.api.PC_Logger;
import powercraft.api.reflect.PC_Processor.Result;


public final class PC_Reflection {

	private static interface PC_ICallerGetter {
		
		public Class<?> getCallerClass(int num);
		
	}
	
	private static class PC_CallerGetterSecMan extends SecurityManager implements PC_ICallerGetter{
		
		private PC_CallerGetterSecMan(){
			
		}
		
		public Class<?> getCallerClass(int num){
			Class<?>[] classes = getClassContext();
			if (classes.length > 3 + num) {
				return classes[num+3];
			}
			PC_Logger.severe("Class %s has no %s callers, only %s", classes[classes.length-1], num, classes.length - 2);
			return null;
		}
		
	}
	
	private static class PC_CallerGetterFallback implements PC_ICallerGetter{

		@Override
		public Class<?> getCallerClass(int num) {
			StackTraceElement[] stackTraceElements = new Exception().getStackTrace();
			if (stackTraceElements.length > 3 + num) {
				try {
					return Class.forName(stackTraceElements[3 + num].getClassName());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			PC_Logger.severe("Class %s has no %s callers, only %s", stackTraceElements[0], num, stackTraceElements.length - 2);
			return null;
		}
		
		
	}
	
	private static final PC_ICallerGetter callerGetter;
	
	static{
		PC_ICallerGetter cGetter;
		try{
			cGetter = new PC_CallerGetterSecMan();
		}catch(Throwable e){
			cGetter = new PC_CallerGetterFallback();
		}
		callerGetter = cGetter;
	}
	
	public static Class<?> getCallerClass() {
		return callerGetter.getCallerClass(0);
	}

	public static Class<?> getCallerClass(int num) {
		return callerGetter.getCallerClass(num);
	}
	
	public static Field findNearestBestField(Class<?> clazz, int index, Class<?> type) {

		Field fields[] = clazz.getDeclaredFields();
		Field f;
		if (index >= 0 && index < fields.length) {
			f = fields[index];
			if (type.isAssignableFrom(f.getType())) {
				return f;
			}
		} else {
			if (index < 0) index = 0;
			if (index >= fields.length) {
				index = fields.length - 1;
			}
		}
		int min = index - 1, max = index + 1;
		while (min >= 0 || max < fields.length) {
			if (max < fields.length) {
				f = fields[max];
				if (type.isAssignableFrom(f.getType())) {
					PC_Logger.warning("Field in %s which should be at index %s not found, now using index %s", clazz, index, max);
					return f;
				}
				max++;
			}
			if (min >= 0) {
				f = fields[min];
				if (type.isAssignableFrom(f.getType())) {
					PC_Logger.warning("Field in %s which should be at index %s not found, now using index %s", clazz, index, min);
					return f;
				}
				min--;
			}
		}
		PC_Logger.severe("Field in %s which should be at index %s not found", clazz, index);
		return null;
	}


	@SuppressWarnings("unchecked")
	public static <T> T getValue(Class<?> clazz, Object object, int index, Class<?> type) {

		Field field = findNearestBestField(clazz, index, type);
		field.setAccessible(true);
		try {
			return (T) field.get(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setValue(Class<?> clazz, Object object, int index, Class<?> type, Object value) {

		try {
			Field field = findNearestBestField(clazz, index, type);
			field.setAccessible(true);
			field.set(object, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setValueAndFinals(Class<?> clazz, Object object, int index, Class<?> type, Object value) {

		try {
			Field field = findNearestBestField(clazz, index, type);
			field.setAccessible(true);
			Field field_modifiers = Field.class.getDeclaredField("modifiers");
			field_modifiers.setAccessible(true);
			int modifier = field_modifiers.getInt(field);

			if ((modifier & Modifier.FINAL) != 0) {
				field_modifiers.setInt(field, modifier & ~Modifier.FINAL);
			}

			field.set(object, value);

			if ((modifier & Modifier.FINAL) != 0) {
				field_modifiers.setInt(field, modifier);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Field[] getDeclaredFields(Class<?> c){
		return c.getDeclaredFields();
	}
	
	public static Object processFields(Object obj, PC_Processor processor){
		Class<?> c = obj.getClass();
		EnumMap<Result, Object> results = new EnumMap<Result, Object>(Result.class);
		while(c!=Object.class){
			Field[] fields = PC_Reflection.getDeclaredFields(c);
			for(Field field:fields){
				results.clear();
				try{
					field.setAccessible(true);
					Object value = field.get(obj);
					processor.process(field, value, results);
					if(results!=null){
						if(results.containsKey(Result.SET)){
							field.set(obj, results.get(Result.SET));
						}
						if(results.containsKey(Result.STOP)){
							return results.get(Result.STOP);
						}
					}
				}catch(IllegalAccessException e){
					PC_Logger.severe("Cannot access field %s.%s", c, field);
				}catch(IllegalArgumentException e){
					PC_Logger.severe("Wrong arguments for field %s.%s", c, field);
				}
			}
		}
		return null;
	}
	
	private PC_Reflection() {
		throw new InstantiationError();
	}
	
}
