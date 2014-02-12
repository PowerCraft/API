package powercraft.api.network;

import net.minecraft.network.INetHandler;
import powercraft.api.PC_Side;
import io.netty.buffer.ByteBuf;

public abstract class PC_Packet {

	protected abstract void fromByteBuffer(ByteBuf buf);

	protected abstract void toByteBuffer(ByteBuf buf);

	protected abstract PC_Packet doAndReply(PC_Side side, INetHandler iNetHandler);

	protected static void writeStringToBuf(ByteBuf buf, String string) {
		buf.writeShort(string.length());
		for(int j=0; j<string.length(); j++){
			buf.writeChar(string.charAt(j));
		}
	}
	
	protected static String readStringFromBuf(ByteBuf buf) {
		char[] chars = new char[buf.readUnsignedShort()];
		for(int i=0; i<chars.length; i++){
			chars[i] = buf.readChar();
		}
		return new String(chars);
	}
	
}
