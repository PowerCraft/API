package powercraft.api.network;

import net.minecraft.network.INetHandler;
import powercraft.api.PC_Side;
import io.netty.buffer.ByteBuf;

public abstract class PC_Packet {

	protected abstract void fromByteBuffer(ByteBuf buf);

	protected abstract void toByteBuffer(ByteBuf buf);

	protected abstract PC_Packet doAndReply(PC_Side side, INetHandler iNetHandler);

}
