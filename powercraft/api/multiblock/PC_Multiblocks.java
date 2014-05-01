package powercraft.api.multiblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import powercraft.api.PC_Api;
import powercraft.api.PC_ClientRegistry;
import powercraft.api.PC_IconRegistry;
import powercraft.api.PC_ImmutableList;
import powercraft.api.PC_Logger;
import powercraft.api.PC_Utils;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.network.packet.PC_PacketMultiblockObjectSync;
import powercraft.api.network.packet.PC_PacketSelectMultiblockTile;
import powercraft.api.network.packet.PC_PacketSelectMultiblockTile2;
import powercraft.api.reflect.PC_Security;
import powercraft.core.PCco_Core;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class PC_Multiblocks {

	private static boolean done;
	private static List<PC_MultiblockItem> multiblockItems = new ArrayList<PC_MultiblockItem>();
	private static List<PC_MultiblockItem> immutableMultiblockItems = new PC_ImmutableList<PC_MultiblockItem>(multiblockItems);
	private static HashMap<PC_MultiblockItem, Class<? extends PC_MultiblockObject>> itemMapper = new HashMap<PC_MultiblockItem, Class<? extends PC_MultiblockObject>>();
	private static HashMap<Class<? extends PC_MultiblockObject>, PC_MultiblockItem> itemMapperRev = new HashMap<Class<? extends PC_MultiblockObject>, PC_MultiblockItem>();
	
	public static void register(){
		PC_Security.allowedCaller("PC_Multiblocks.register()", PC_Api.class);
		PC_PacketHandler.registerPacket(PC_PacketMultiblockObjectSync.class);
		PC_PacketHandler.registerPacket(PC_PacketSelectMultiblockTile.class);
		PC_PacketHandler.registerPacket(PC_PacketSelectMultiblockTile2.class);
	}
	
	public static PC_BlockMultiblock getMultiblock(){
		if(PCco_Core.MULTIBLOCK==null){
			PC_Security.allowedCaller("PC_Multiblocks.getMultiblock()", PCco_Core.class);
			return new PC_BlockMultiblock();
		}
		return PCco_Core.MULTIBLOCK;
	}
	
	static void addMultiblock(PC_MultiblockItem multiblockItem, Class<? extends PC_MultiblockObject> multiblockObjectClass) {
		if(done){
			PC_Logger.severe("A Multiblock want to register while startup is done");
		}else{
			PC_Logger.info("Multiblock-ADD: %s", multiblockItem);
			multiblockItems.add(multiblockItem);
			itemMapper.put(multiblockItem, multiblockObjectClass);
			itemMapperRev.put(multiblockObjectClass, multiblockItem);
		}
	}
	
	public static List<PC_MultiblockItem> getBlocks(){
		return immutableMultiblockItems;
	}

	public static void construct(){
		PC_Security.allowedCaller("PC_Multiblocks.construct()", PC_Api.class);
		if(!done){
			done = true;
		}
	}
	
	private PC_Multiblocks(){
		PC_Utils.staticClassConstructor();
	}

	public static PC_MultiblockItem getItem(PC_MultiblockObject multiblockObject) {
		return itemMapperRev.get(multiblockObject.getClass());
	}

	@SideOnly(Side.CLIENT)
	static void loadMultiblockIcons(PC_IconRegistry iconRegistry) {
		for(PC_MultiblockItem multiblockItem:multiblockItems){
			multiblockItem.loadMultiblockIcons(PC_ClientRegistry.getIconRegistry(iconRegistry, multiblockItem));
		}
	}
	
}
