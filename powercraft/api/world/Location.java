package powercraft.api.world;

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
	
	@SuppressWarnings("javadoc")
	public int x;
	@SuppressWarnings("javadoc")
	public int y;
	@SuppressWarnings("javadoc")
	public int z;
	
	@SuppressWarnings("javadoc")
	public int getX() {
		return this.x;
	}
	
	@SuppressWarnings("javadoc")
	public void setX(int x) {
		this.x = x;
	}
	
	@SuppressWarnings("javadoc")
	public int getY() {
		return this.y;
	}
	
	@SuppressWarnings("javadoc")
	public void setY(int y) {
		this.y = y;
	}
	
	@SuppressWarnings("javadoc")
	public int getZ() {
		return this.z;
	}
	
	@SuppressWarnings("javadoc")
	public void setZ(int z) {
		this.z = z;
	}
}
