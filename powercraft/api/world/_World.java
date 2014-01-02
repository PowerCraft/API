package powercraft.api.world;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

/**
 * @author James
 * The world wrapper
 */
public class _World {
	
	/**
	 * @param i The dimension ID
	 * @return  The world with that dimension ID
	 */
	protected static World getWorld(int i){
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		return server.worldServers[i];
	}
	
	/**
	 * @param s The dimension name
	 * @return The world with that dimension name
	 */
	protected static World getWorld(String s){
		return getWorld(WorldUtils.getWorldIDByName(s));
	}
	
	/**
	 * @param world The name of the world you want to edit
	 * @param x The block x
	 * @param y The block y
	 * @param z The block z
	 * @param blockID The block ID to set to
	 */
	public static void setBlockAt(String world, int x, int y, int z, int blockID){
		getWorld(world).setBlock(x, y, z, blockID);
	}
	
	/**
	 * @param world The id of the world you want to edit
	 * @param x The block x
	 * @param y The block y
	 * @param z The block z
	 * @param blockID The block ID to set to
	 */
	public static void setBlockAt(int world, int x, int y, int z, int blockID){
		getWorld(world).setBlock(x, y, z, blockID);
	}
	
	/**
	 * @param world The name of the world you want to edit
	 * @param x The block x
	 * @param y The block y
	 * @param z The block z
	 * @param blockID The block ID to set to
	 * @param meta The block metadata
	 */
	public static void setBlockAtWithMeta(String world, int x, int y, int z, int blockID, int meta){
		getWorld(world).setBlock(x, y, z, blockID);
		getWorld(world).setBlockMetadataWithNotify(x, y, z, meta, 0);
	}
	
	/**
	 * @param world The id of the world you want to edit
	 * @param x The block x
	 * @param y The block y
	 * @param z The block z
	 * @param blockID The block ID to set to
	 * @param meta The metadata to set to
	 */
	public static void setBlockAtWithMetadata(int world, int x, int y, int z, int blockID, int meta){
		getWorld(world).setBlock(x, y, z, blockID);
		getWorld(world).setBlockMetadataWithNotify(x, y, z, meta, 0);
	}
	
	/**
	 * @param world The world
	 * @param x The block x
	 * @param y The block y
	 * @param z The block z
	 * @return The block ID and metadata
	 */
	public static int[] getBlockAt(String world, int x, int y, int z){
		return new int[]{ getWorld(world).getBlockId(x, y, z), getWorld(world).getBlockMetadata(x, y, z) };
	}
	
	/**
	 * @param world The world
	 * @param x The block x
	 * @param y The block y
	 * @param z The block z
	 * @return The block ID and metadata
	 */
	public static int[] getBlockAt(int world, int x, int y, int z){
		return new int[]{ getWorld(world).getBlockId(x, y, z), getWorld(world).getBlockMetadata(x, y, z) };
	}
}
