package powercraft.api;

import javax.management.InstanceAlreadyExistsException;

import cpw.mods.fml.common.registry.GameRegistry;
import powercraft.api.PC_ResourceReloadListener.PC_IResourceReloadListener;
import powercraft.api.PC_TickHandler.PC_IBaseTickHandler;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketHandler;

public class PC_Registry {

	private static PC_Registry INSTANCE;
	
	PC_Registry() throws InstanceAlreadyExistsException {
		if(INSTANCE!=null){
			throw new InstanceAlreadyExistsException();
		}
		INSTANCE = this;
	}
	
	public static void registerTileEntity(Class<? extends PC_TileEntity> tileEntityClass){
		INSTANCE.iRegisterTileEntity(tileEntityClass);
	}
	
	@SuppressWarnings("static-method")
	void iRegisterTileEntity(Class<? extends PC_TileEntity> tileEntityClass){
		GameRegistry.registerTileEntity(tileEntityClass, tileEntityClass.getName());
	}
	
	public static void registerTickHandler(PC_IBaseTickHandler tickHandler){
		PC_TickHandler.registerTickHandler(tickHandler);
	}
	
	public static void registerResourceReloadListener(PC_IResourceReloadListener listener){
		PC_ResourceReloadListener.registerResourceReloadListener(listener);
	}
	
	public static void registerPacket(Class<? extends PC_Packet> packet){
		PC_PacketHandler.registerPacket(packet);
	}
	
}
