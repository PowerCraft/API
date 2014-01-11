package powercraft.api;

import java.util.Random;

import powercraft.api.annotation.Test;

/**
 * @author James
 * Used for testing methods and different ways of doing things
 * without needing to use a seprate project, ect
 */
public class PowerCraft_ClassTest {
	
	/**
	 * The entry point for the tester
	 * @param args The command line arguments
	 */
	public static void main(String[] args){
		/*try{
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
		System.out.println(MathHelper.Q_rsqrt(16));*/
		
		Random rand = new Random();
		
		int r = 6;
   		for (int loop = 0; loop < 10; loop++){
   			int angle = (rand.nextInt(16) + 1);
   			double x = r * Math.cos((Math.PI * 2) / angle);
			//System.out.println((int)Math.cos((float) (Math.PI / angle)));
   			double z = r * Math.sin((Math.PI * 2) / angle);
   			System.out.println(z);
   			//for(int y = 1; y < 256; y++){
   				System.out.println(String.valueOf((int)x + 10) + " " +  String.valueOf(0 + 10) + " " +  String.valueOf((int)z + 10));
   			//}
   		}
	}

	/**
	 * Pointless test method
	 */
	@Test(valueOfAnoteInt = 0)
	public void methodThing() { 
	//
	}
	
	/**
	 * Pointless test method
	 */
	@Test(valueOfAnoteInt = 0, valueOfAnote = "Apple pie")
	public void methodThing2() { 
	//
	}
}
