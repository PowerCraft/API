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
	public static World getWorld(int i){
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		return server.worldServers[i];
	}
	
	/**
	 * @param s The dimension name
	 * @return The world with that dimension name
	 */
	public static World getWorld(String s){
		return getWorld(WorldUtils.getWorldIDByName(s));
	}
}
