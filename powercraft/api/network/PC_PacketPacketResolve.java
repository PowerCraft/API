package powercraft.api.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
			packetClasses[i] = readStringFromBuf(buf);
		}
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(packetClasses.length);
		for(int i=0; i<packetClasses.length; i++){
			writeStringToBuf(buf, packetClasses[i]);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected PC_Packet doAndReply(INetHandler iNetHandler) {
		PC_PacketHandler.setPackets(packetClasses);
		return null;
	}

}
