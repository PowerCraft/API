package powercraft.api;


import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import powercraft.api.block.PC_Blocks;
import powercraft.api.energy.PC_EnergyGrid;
import powercraft.api.gres.PC_Gres;
import powercraft.api.item.PC_Items;
import powercraft.api.multiblock.PC_BlockMultiblock;
import powercraft.api.multiblock.PC_Multiblocks;
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
	
	public static final PC_BlockMultiblock MULTIBLOCK = PC_Multiblocks.getMultiblock();
	
	@InstanceFactory
	public static PC_Api factory(){
		return INSTANCE;
	}
	
	private PC_Api(){
		
	}
	
	@SuppressWarnings({ "static-method", "unused" })
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		PC_PacketHandler.register();
		PC_TickHandler.register();
		PC_ForgeHandler.register();
		PC_Miniscript.register();
		PC_Gres.register();
		PC_Multiblocks.register();
		PC_EnergyGrid.register();
		
		PC_Blocks.construct();
		PC_Items.construct();
		PC_Multiblocks.construct();
		
		MinecraftForge.EVENT_BUS.register(new PC_EventHandler());
		
	}


	@SuppressWarnings("unused")
	@EventHandler
	public void init(FMLInitializationEvent event) {
		//
	}


	@SuppressWarnings({ "unused", "static-method" })
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		PC_Miniscript.loadDefaultReplacements();
	}
	
	@SuppressWarnings({ "unused", "static-method" })
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
