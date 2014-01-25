package net.powercrafting.API;

import net.powercrafting.API.lib.GlobalInfo;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = GlobalInfo.modId, name = GlobalInfo.fullName, version = GlobalInfo.version)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class PC_API {

}
