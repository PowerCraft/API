package powercraft.api.world;

/**
 * @author James
 * Same as all the other utils
 */
public class WorldUtils {
	
	/**
	 * Gets the ID of a world by it's name
	 * @param name The world's name
	 * @return the world's id
	 */
	public static int getWorldIDByName(String name){
		switch(name){
		case "world":
			return 0;
		case "nether":
			return -1;
		case "end":
			return 1;
		default:
			return 0;
		}
	}
	
	/**
	 * Gets the world with that name
	 * @param name The name of the world
	 * @return The world you wanted
	 */
	public static net.minecraft.world.World getWorldByName(String name){
		return _World.getWorld(name);
	}
}
