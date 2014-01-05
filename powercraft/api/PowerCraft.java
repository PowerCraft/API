package powercraft.api;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

import powercraft.api.lists.*;

/**
 * @author James
 * The powercraft mod class
 */
@Mod(modid = "PowerCraft", name="PowerCraft", version="4.0.0")
// TODO not needed on the server?
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class PowerCraft {
	
	/**
	 * The powercraft instance used by forge
	 */
	@Instance(value="PowerCraft")
	public static PowerCraft pc;
	
	/**
	 * The list of custom added blocks
	 */
	public CustomBlocksList blocksList = new CustomBlocksList();
	
	/**
	 * The list of custom added items
	 */
	public CustomItemsList itemsList = new CustomItemsList();
	
	/**
	 * The preinit for powercraft
	 * @param evt The event that triggered it
	 */
	@EventHandler
	public void preinit(FMLPreInitializationEvent evt) {
		preinit();
	}
	
	/**
	 * The init for powercraft
	 * @param evt The event that triggered it
	 */
	@EventHandler
	public void init(FMLInitializationEvent evt) {
		init();
	}
	
	/**
	 * The postinit for powercraft
	 * @param evt The event that triggered it
	 */
	@EventHandler
	public void postinit(FMLPostInitializationEvent evt) {
		postinit();
	}
	
	/**
	 * The independent preinit
	 */
	public void preinit(){/* This is meant to be overwritten */}
	
	/**
	 * The independent init
	 */
	public void init(){/* This is meant to be overwritten */}
	
	/**
	 * The independent postinit
	 */
	public void postinit(){/* This is meant to be overwritten */}
}
