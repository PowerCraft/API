package powercraft.api.network;

import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import powercraft.api.PC_Logger;
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
	
	protected static void writeNBTToBuf(ByteBuf buf, NBTTagCompound nbtTagCompound) {
		try {
			byte[] bytes = CompressedStreamTools.compress(nbtTagCompound);
			buf.writeInt(bytes.length);
			buf.writeBytes(bytes);
		} catch (IOException e) {
			e.printStackTrace();
			PC_Logger.severe("Error while compressing NBTTag");
		}
	}
	
	protected static NBTTagCompound readNBTFromBuf(ByteBuf buf) {
		byte[] bytes = new byte[buf.readInt()];
		buf.readBytes(bytes);
		try {
			return CompressedStreamTools.decompress(bytes);
		} catch (IOException e) {
			e.printStackTrace();
			PC_Logger.severe("Error while decompressing NBTTag");
		}
		return new NBTTagCompound();
	}
	
}
