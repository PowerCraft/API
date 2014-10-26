package powercraft.api;


import java.io.File;

import javax.management.InstanceAlreadyExistsException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldSettings.GameType;
import powercraft.api.reflect.PC_Fields;
import powercraft.api.reflect.PC_Reflection;
import powercraft.api.renderer.PC_Renderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 
 * Utils for client side
 * 
 * @author XOR
 *
 */
@SideOnly(Side.CLIENT)
public final class PC_ClientUtils extends PC_Utils {

	private static InheritableThreadLocal<Boolean> isClient = new InheritableThreadLocal<Boolean>();
	
	static{
		try {
			new PC_ClientUtils();
		} catch (InstanceAlreadyExistsException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Will be called from Forge
	 */
	public PC_ClientUtils() throws InstanceAlreadyExistsException {

		PC_Renderer.getInstance();
		isClient.set(Boolean.TRUE);
		PC_ResourceReloadListener.register();
		PC_Keyboard.register();
		PC_CtrlPressed.register();
		
	}

	/**
	 * get the Minecraft instance
	 * @return the Minecraft instance
	 */
	public static Minecraft mc() {

		return Minecraft.getMinecraft();
	}

	/**
	 * get the PowerCraft file in the same folder as the mod folder is
	 * @return the file
	 */
	@Override
	File iGetPowerCraftFile() {

		return new File(mc().mcDataDir, "PowerCraft");
	}

	/**
	 * get the game type for a specific player
	 * @param player the player
	 * @return the game type
	 */
	@Override
	GameType iGetGameTypeFor(EntityPlayer player) {
		return PC_Fields.Client.PlayerControllerMP_currentGameType.getValue(mc().playerController);
	}

	/**
	 * is this game running on client
	 * @return always yes
	 */
	@Override
	PC_Side iGetSide(){
		Boolean isClient = PC_ClientUtils.isClient.get();
		if(isClient==null){
			return PC_Side.CLIENT;
		}else if(isClient.booleanValue()){
			return PC_Side.CLIENT;
		}
		return PC_Side.SERVER;
	}
	
	@Override
	void iMarkThreadAsServer(){
		isClient.set(Boolean.FALSE);
	}
	
	@Override
	EntityPlayer iGetClientPlayer() {
		return mc().thePlayer;
	}
	
	@Override
	boolean iIsEntityFX(Entity entity) {
		return entity instanceof EntityFX;
	}

	public static void spawnParicle(EntityFX entityFX) {
		mc().effectRenderer.addEffect(entityFX);
	}
	
}
