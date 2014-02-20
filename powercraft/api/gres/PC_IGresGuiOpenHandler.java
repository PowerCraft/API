package powercraft.api.gres;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


/**
 * The guis and container-Objects are requested for opening them by calling those methods
 */
public interface PC_IGresGuiOpenHandler {

	/**
	 * that's the method that is called for opening a GUI on client
	 * @param player the player who tries to open the guy
	 * @param serverData data send from the server by sendOnGuiOpenToClient
	 * @return the GUI Object of the GUI that shall be opened
	 */
	@SideOnly(Side.CLIENT)
	public PC_IGresGui openClientGui(EntityPlayer player, NBTTagCompound serverData);

	/**
	 * that's the method that is called for opening a GUI on server
	 * if there's no inventory in that GUI then this always must return null
	 * else it returns the container object
	 * @param player the player who tries to open the guy
	 * @return the Container Object of the GUI that shall be opened or null if there is no inventory
	 */
	public PC_GresBaseWithInventory openServerGui(EntityPlayer player);

	/**
	 * 
	 * Data to send to client
	 * 
	 * @param player the player who opened the GUI
	 * @return the data to send to the client
	 */
	public NBTTagCompound sendOnGuiOpenToClient(EntityPlayer player);
	
}
