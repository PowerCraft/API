package powercraft.api;


import net.minecraft.item.ItemStack;
import powercraft.api.block.PC_Blocks;
import powercraft.api.building.PC_CropHarvesting;
import powercraft.api.building.PC_TreeHarvesting;
import powercraft.api.dimension.PC_Dimensions;
import powercraft.api.energy.PC_EnergyGrid;
import powercraft.api.entity.PC_Entities;
import powercraft.api.gres.PC_Gres;
import powercraft.api.item.PC_Items;
import powercraft.api.multiblock.PC_Multiblocks;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.script.miniscript.PC_Miniscript;
import powercraft.api.version.PC_UpdateChecker;
import powercraft.core.PCco_Core;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.InstanceFactory;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;

/**
 * 
 * Mainmodule, all other PowerCraft modules will have this as dependencies
 * 
 * @author XOR
 *
 */
@Mod(modid = PC_Api.NAME, name = PC_Api.NAME, version = PC_Api.VERSION, dependencies=PC_Api.DEPENDENCIES)
public final class PC_Api extends PC_Module {
	
	public static final String NAME = POWERCRAFT+"-Api";
	public static final String VERSION = PC_Build.BUILD_VERSION;
	public static final String DEPENDENCIES = "required-before:"+PCco_Core.NAME+"@"+PCco_Core.VERSION;
	
	static{
		PC_Bootstrap.prepare();
	}
	
	public static final PC_Api INSTANCE = new PC_Api();
	
	@InstanceFactory
	public static PC_Api factory(){
		return INSTANCE;
	}
	
	private PC_Api(){
		if(getConfig().get("options", "checkupdates", true).getBoolean(true)){
			PC_UpdateChecker.check();
		}
	}
	
	@SuppressWarnings({ "static-method", "unused" })
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
		PC_Modules.construct();
		
		PC_PacketHandler.register();
		PC_TickHandler.register();
		PC_ForgeHandler.register();
		PC_Miniscript.register();
		PC_Gres.register();
		PC_Multiblocks.register();
		PC_EnergyGrid.register();
		
		PC_ChunkManager.register();
		
		PC_CropHarvesting.register();
		PC_TreeHarvesting.register();
		
		PC_Blocks.construct();
		PC_Items.construct();
		PC_Multiblocks.construct();
		PC_Dimensions.construct();
		PC_Entities.construct();
		
		PC_Blocks.initRecipes();
		PC_Items.initRecipes();
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
		PC_Modules.saveConfig();
	}
	
	@SuppressWarnings({ "unused", "static-method" })
	@EventHandler
	public void serverStarted(FMLServerAboutToStartEvent start) {
		PC_Utils.markThreadAsServer();
		PC_PacketHandler.setupPackets();
	}

	@SuppressWarnings({ "static-method", "unused" })
	@EventHandler
	public void onServerStopping(FMLServerStoppedEvent serverStoppedEvent){
		PC_WorldSaveData.onServerStopping();
	}
	
	public static ItemStack creativTabItemStack;
	
	@Override
	public ItemStack getCreativeTabItemStack() {
		return creativTabItemStack;
	}

}
