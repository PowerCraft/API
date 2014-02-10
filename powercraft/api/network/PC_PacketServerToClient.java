package powercraft.api.network;

import net.minecraft.network.INetHandler;
import powercraft.api.PC_Logger;
import powercraft.api.PC_Side;

public abstract class PC_PacketServerToClient extends PC_Packet {
	
	protected abstract PC_Packet doAndReply(INetHandler iNetHandler);
	
	@Override
	protected PC_Packet doAndReply(PC_Side side, INetHandler iNetHandler) {
		if(checkSide(side)){
			return doAndReply(iNetHandler);
		}
		return null;
	}

	private static boolean checkSide(PC_Side side){
		if(side!=PC_Side.CLIENT){
			PC_Logger.severe("A server to client packet can't run on server");
			return false;
		}
		return true;
	}
	
}
