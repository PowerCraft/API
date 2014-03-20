package powercraft.api;

import net.minecraft.client.renderer.entity.RenderManager;

/**
 * 
 * debug class
 * 
 * @author XOR
 *
 */
public class PC_Debug {

	/**
	 * Debug on/off
	 */
	public static final boolean DEBUG = true;
	
	/**
	 * if on the string will be printed, else not
	 * @param s the string to be printed when you are in debug
	 */
	public static void println(String s){
		if(DEBUG)
			System.out.println(s);
	}
	
	public static void setup(){
		if(DEBUG){
			RenderManager.debugBoundingBox = true;
		}
	}
	
}
