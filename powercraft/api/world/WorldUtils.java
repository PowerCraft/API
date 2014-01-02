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
	
	/**
	 * @param id The world ID
	 * @return the name of the world ID
	 */
	public static String getWorldNameByID(int id){
		switch(id){
		case 0:
			return "world";
		case 1:
			return "end";
		case -1:
			return "nether";
		default:
			return "world";
		}
	}
}
