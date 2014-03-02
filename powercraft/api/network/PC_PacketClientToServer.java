package powercraft.api.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
import powercraft.api.PC_Logger;
import powercraft.api.PC_Side;

public abstract class PC_PacketClientToServer extends PC_Packet {

	protected abstract PC_Packet doAndReply(NetHandlerPlayServer playServer, World world, EntityPlayer player);
	
	@Override
	protected PC_Packet doAndReply(PC_Side side, INetHandler iNetHandler) {
		if(checkSide(side) && iNetHandler instanceof NetHandlerPlayServer){
			NetHandlerPlayServer playServer = (NetHandlerPlayServer)iNetHandler;
			return doAndReply(playServer, playServer.playerEntity.worldObj, playServer.playerEntity);
		}
		return null;
	}


	private static boolean checkSide(PC_Side side){
		if(side!=PC_Side.SERVER){
			PC_Logger.severe("A client to server packet can't run on client");
			return false;
		}
		return true;
	}
	
}
