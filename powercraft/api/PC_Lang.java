package powercraft.api;

import net.minecraft.util.StatCollector;

/**
 * 
 * language things
 * 
 * @author XOR
 *
 */
public final class PC_Lang {

	/**
	 * translate a key to user language
	 * @param key the key
	 * @param args arguments to put in %s and so
	 * @return the translated and formatted string
	 */
	public static String translate(String key, Object...args){
		return StatCollector.translateToLocalFormatted(key, args);
	}
	
	/**
	 * translate a key to user language
	 * @param key the key
	 * @param args arguments to put in %s and so
	 * @return the translated and formatted string
	 */
	public static String tr(String key, Object...args){
		return StatCollector.translateToLocalFormatted(key, args);
	}
	
	private PC_Lang(){
		PC_Utils.staticClassConstructor();
	}
	
}
