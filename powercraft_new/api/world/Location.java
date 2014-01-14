package powercraft_new.api.world;

/**
 * @author James
 * Locations for use in the API
 */
public class Location {
	
	/**
	 * The weird consctructor
	 */
	public Location(){}

	/**
	 * Use this constructor if there is no y
	 * (Chunks, rows of blocks)
	 * @param x The x
	 * @param z The z
	 */
	public Location(int x, int z){
		this.x = x;
		this.z = z;
	}
	
	/**
	 * The normal constructor
	 * @param x The x
	 * @param y The y
	 * @param z The z
	 */
	public Location(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * The x position in the location
	 */
	public int x;

	/**
	 * The y position in the location
	 */
	public int y;

	/**
	 * The z position in the location
	 */
	public int z;
}
