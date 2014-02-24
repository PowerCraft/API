package powercraft.api;

import net.minecraft.util.StatCollector;

public class PC_Lang {

	public static String translate(String key, Object...args){
		return StatCollector.translateToLocalFormatted(key, args);
	}
	
}
