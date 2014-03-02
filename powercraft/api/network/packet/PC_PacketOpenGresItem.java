package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
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
		this.itemID = Item.getIdFromItem(item);
		this.windowId = windowId;
		this.nbtTagCompound = nbtTagCompound;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected PC_Packet doAndReply(NetHandlerPlayClient iNetHandler, World world, EntityPlayer player) {
		Item item = Item.getItemById(this.itemID);
		PC_Gres.openClientGui(player, item, this.windowId, this.nbtTagCompound);
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		this.windowId = buf.readInt();
		this.itemID = buf.readInt();
		this.nbtTagCompound = readNBTFromBuf(buf);
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(this.windowId);
		buf.writeInt(this.itemID);
		writeNBTToBuf(buf, this.nbtTagCompound);
	}

}
