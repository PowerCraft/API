package powercraft.api.network;

import net.minecraft.network.INetHandler;
import io.netty.buffer.ByteBuf;

public final class PC_PacketPacketResolve extends PC_PacketServerToClient {

	private String[] packetClasses;
	
	public PC_PacketPacketResolve(){
		
	}
	
	PC_PacketPacketResolve(String[] packetClasses){
		this.packetClasses = packetClasses;
	}
	
	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		packetClasses = new String[buf.readInt()];
		for(int i=0; i<packetClasses.length; i++){
			char[] chars = new char[buf.readUnsignedShort()];
			for(int j=0; j<chars.length; j++){
				chars[i] = buf.readChar();
			}
			packetClasses[i] = new String(chars);
		}
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(packetClasses.length);
		for(int i=0; i<packetClasses.length; i++){
			buf.writeShort(packetClasses[i].length());
			for(int j=0; j<packetClasses.length; j++){
				buf.writeChar(packetClasses[i].charAt(j));
			}
		}
	}

	@Override
	protected PC_Packet doAndReply(INetHandler iNetHandler) {
		PC_PacketHandler.setPackets(packetClasses);
		return null;
	}

}
