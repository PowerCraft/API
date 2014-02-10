package powercraft.api;


import net.minecraft.item.ItemStack;
import powercraft.api.block.PC_Blocks;
import powercraft.api.item.PC_Items;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.script.miniscript.PC_Miniscript;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.InstanceFactory;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;

/**
 * 
 * Mainmodule, all other PowerCraft modules will have this as dependencies
 * 
 * @author XOR
 *
 */
@Mod(modid = PC_Api.NAME, name = PC_Api.NAME, version = PC_Api.VERSION/*, dependencies=PC_Api.DEPENDENCIES*/)
public final class PC_Api extends PC_Module {
	
	public static final String NAME = POWERCRAFT+"-Api";
	public static final String VERSION = "1.7.2";
	//public static final String DEPENDENCIES = "required-before:"+PCco_ModuleCore.NAME+"@"+PCco_ModuleCore.VERSION;
	
	static{
		PC_Bootstrap.prepare();
	}
	
	public static final PC_Api INSTANCE = new PC_Api();
	
	@InstanceFactory
	public static PC_Api factory(){
		return INSTANCE;
	}
	
	private PC_Api(){
		
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		PC_PacketHandler.register();
		PC_TickHandler.register();
		PC_WorldGeneriator.register();
		PC_FuelHandler.register();
		PC_Miniscript.register();
		
	}


	@EventHandler
	public void init(FMLInitializationEvent event) {
		
	}


	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		PC_Blocks.construct();
		PC_Items.construct();
	}
	
	@EventHandler
	public void serverStarted(FMLServerAboutToStartEvent start) {
		PC_Utils.markThreadAsServer();
		PC_PacketHandler.setupPackets();
	}

	public static ItemStack creativTabItemStack;
	
	@Override
	public ItemStack getCreativeTabItemStack() {
		return creativTabItemStack;
	}

}
