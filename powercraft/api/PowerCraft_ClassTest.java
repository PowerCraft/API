package powercraft.api;

import java.lang.reflect.Method;

import powercraft.api.annotation.Test;
import powercraft.api.utils.MathHelper;

/**
 * @author James
 * Used for testing methods and different ways of doing things
 * without needing to use a seprate project, ect
 */
public class PowerCraft_ClassTest {
	@SuppressWarnings("javadoc")
	public static void main(String[] args){
		try{
			// I forgot to update the package
			for(Method method : PowerCraft_ClassTest.class.getMethods()){
				if(method.isAnnotationPresent(Test.class)){
					Test methodAnno = method.getAnnotation(Test.class);
					System.out.println(methodAnno.valueOfAnote());
					System.out.println(methodAnno.valueOfAnoteInt());
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		System.out.println(MathHelper.Q_rsqrt(1));
		System.out.println(MathHelper.Q_rsqrt(4));
		System.out.println(MathHelper.Q_rsqrt(9));
		System.out.println(MathHelper.Q_rsqrt(11));
		System.out.println(MathHelper.Q_rsqrt(16));
	}

	@SuppressWarnings("javadoc")
	@Test(valueOfAnoteInt = 0)
	public void methodThing() { 
	//
	}
	
	@SuppressWarnings("javadoc")
	@Test(valueOfAnoteInt = 0, valueOfAnote = "Apple pie")
	public void methodThing2() { 
	//
	}
}
