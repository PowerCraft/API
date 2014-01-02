package powercraft.api.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@SuppressWarnings("javadoc")
public class Test2 {
	public static void main(String[] args){
		try{
			for(Method method : Test2.class.getClassLoader().loadClass("powercraft.api.annotation.Test2").getMethods()){
				if(method.isAnnotationPresent(Test.class)){
					for(@SuppressWarnings("unused") Annotation anno : method.getDeclaredAnnotations()){
						Test methodAnno = method.getAnnotation(Test.class);
						System.out.println(methodAnno.valueOfAnote());
						System.out.println(methodAnno.valueOfAnoteInt());
					}
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	@Test(valueOfAnoteInt = 0)
	public void methodThing() { 
	//
	}
	
	@Test(valueOfAnoteInt = 0, valueOfAnote = "Apple pie")
	public void methodThing2() { 
	//
	}
}
