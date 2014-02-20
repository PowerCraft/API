package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import powercraft.api.PC_ClientUtils;
import powercraft.api.gres.PC_Gres;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketServerToClient;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PC_PacketOpenGresItem extends PC_PacketServerToClient {
	
	private int itemID;
	private int windowId;
	private NBTTagCompound nbtTagCompound;
	
	public PC_PacketOpenGresItem(){
		
	}

	public PC_PacketOpenGresItem(Item item, int windowId, NBTTagCompound nbtTagCompound) {
		itemID = Item.getIdFromItem(item);
		this.windowId = windowId;
		this.nbtTagCompound = nbtTagCompound;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected PC_Packet doAndReply(INetHandler iNetHandler) {
		Item item = Item.getItemById(itemID);
		PC_Gres.openClientGui(PC_ClientUtils.mc().thePlayer, item, windowId, nbtTagCompound);
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		windowId = buf.readInt();
		itemID = buf.readInt();
		nbtTagCompound = readNBTFromBuf(buf);
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(windowId);
		buf.writeInt(itemID);
		writeNBTToBuf(buf, nbtTagCompound);
	}

}
