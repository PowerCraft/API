package powercraft.api.script.weasel;

import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptException;

import powercraft.api.PC_Utils;


public final class PC_WeaselInteraction {
	
	private PC_WeaselInteraction(){
		PC_Utils.staticClassConstructor();
	}
	
	@SuppressWarnings("unchecked")
	public static Map<Object, Object> newInstance(Object vm, String className) throws NoSuchMethodException, ScriptException{
		return (Map<Object, Object>) ((Invocable)vm).invokeFunction(className);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<Object, Object> newArray(Object vm, String baseClassName, int size) throws ScriptException{
		String arrayClassName = "";
		if(baseClassName.equals("bool")){
			arrayClassName = "Bool";
		}else if(baseClassName.equals("byte")){
			arrayClassName = "Byte";
		}else if(baseClassName.equals("short")){
			arrayClassName = "Short";
		}else if(baseClassName.equals("int")){
			arrayClassName = "Int";
		}else if(baseClassName.equals("long")){
			arrayClassName = "Long";
		}else if(baseClassName.equals("float")){
			arrayClassName = "Float";
		}else if(baseClassName.equals("double")){
			arrayClassName = "Double";
		}else if(baseClassName.equals("char")){
			arrayClassName = "Char";
		}else{
			arrayClassName = "<"+baseClassName+">";
		}
		try{
			return (Map<Object, Object>) ((Invocable)vm).invokeFunction("xscript.lang.Array"+arrayClassName, Integer.valueOf(size));
		}catch(NoSuchMethodException e){
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Map<Object, Object> newString(Object vm, String str) throws ScriptException{
		try{
			return (Map<Object, Object>) ((Invocable)vm).invokeFunction("xscript.lang.String", str);
		}catch(NoSuchMethodException e){
			throw new RuntimeException(e);
		}
	}
	
}
