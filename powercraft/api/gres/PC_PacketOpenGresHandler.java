package powercraft.api.gres;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;
import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_Utils;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketServerToClient;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PC_PacketOpenGresHandler extends PC_PacketServerToClient {

	private String guiOpenHandlerName;
	private int windowId;
	
	public PC_PacketOpenGresHandler(){
		
	}
	
	public PC_PacketOpenGresHandler(String guiOpenHandlerName, int windowId) {
		this.guiOpenHandlerName = guiOpenHandlerName;
		this.windowId = windowId;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected PC_Packet doAndReply(INetHandler iNetHandler) {
		PC_Gres.openClientGui(PC_ClientUtils.mc().thePlayer, guiOpenHandlerName, windowId);
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		windowId = buf.readInt();
		guiOpenHandlerName = PC_Utils.readStringFromBuf(buf);
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(windowId);
		PC_Utils.writeStringToBuf(buf, guiOpenHandlerName);

	}

}
