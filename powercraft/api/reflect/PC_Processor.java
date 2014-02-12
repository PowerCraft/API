package powercraft.api.reflect;

import java.lang.reflect.Field;
import java.util.EnumMap;

public interface PC_Processor {

	public void process(Field field, Object value, EnumMap<Result, Object> results);
	
	public static enum Result{
		SET, STOP;
	}
	
}
