package powercraft.api.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;
import powercraft.api.PC_Logger;
import powercraft.api.PC_Side;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class PC_PacketPacketResolve extends PC_PacketServerToClientBase {

	private String[] packetClasses;
	
	public PC_PacketPacketResolve(){
		
	}
	
	PC_PacketPacketResolve(String[] packetClasses){
		this.packetClasses = packetClasses;
	}
	
	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		this.packetClasses = new String[buf.readInt()];
		for(int i=0; i<this.packetClasses.length; i++){
			this.packetClasses[i] = readStringFromBuf(buf);
		}
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(this.packetClasses.length);
		for(int i=0; i<this.packetClasses.length; i++){
			writeStringToBuf(buf, this.packetClasses[i]);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected PC_Packet doAndReply(PC_Side side, INetHandler iNetHandler) {
		if(checkSide(side)){
			PC_PacketHandler.setPackets(this.packetClasses);
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
