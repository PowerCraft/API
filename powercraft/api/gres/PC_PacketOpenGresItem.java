package powercraft.api.gres;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.network.INetHandler;
import powercraft.api.PC_ClientUtils;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketServerToClient;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PC_PacketOpenGresItem extends PC_PacketServerToClient {
	
	private int itemID;
	private int windowId;
	
	public PC_PacketOpenGresItem(){
		
	}

	public PC_PacketOpenGresItem(Item item, int windowId) {
		itemID = Item.getIdFromItem(item);
		this.windowId = windowId;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected PC_Packet doAndReply(INetHandler iNetHandler) {
		Item item = Item.getItemById(itemID);
		PC_Gres.openClientGui(PC_ClientUtils.mc().thePlayer, item, windowId);
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		windowId = buf.readInt();
		itemID = buf.readInt();
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(windowId);
		buf.writeInt(itemID);
	}

}
