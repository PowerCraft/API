package net.powercrafting.API;

import net.powercrafting.API.lib.GlobalInfo;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = GlobalInfo.modId, name = GlobalInfo.fullName, version = GlobalInfo.version)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class PC_API {
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// TODO Init Blocks
		// TODO Init Items
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		// TODO register crafting
	}
}
