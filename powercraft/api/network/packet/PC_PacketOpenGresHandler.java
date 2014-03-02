package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import powercraft.api.gres.PC_Gres;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketServerToClient;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PC_PacketOpenGresHandler extends PC_PacketServerToClient {

	private String guiOpenHandlerName;
	private int windowId;
	private NBTTagCompound nbtTagCompound;
	
	public PC_PacketOpenGresHandler(){
		
	}
	
	public PC_PacketOpenGresHandler(String guiOpenHandlerName, int windowId, NBTTagCompound nbtTagCompound) {
		this.guiOpenHandlerName = guiOpenHandlerName;
		this.windowId = windowId;
		this.nbtTagCompound = nbtTagCompound;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected PC_Packet doAndReply(NetHandlerPlayClient iNetHandler, World world, EntityPlayer player) {
		PC_Gres.openClientGui(player, this.guiOpenHandlerName, this.windowId, this.nbtTagCompound);
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		this.windowId = buf.readInt();
		this.guiOpenHandlerName = readStringFromBuf(buf);
		this.nbtTagCompound = readNBTFromBuf(buf);
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(this.windowId);
		writeStringToBuf(buf, this.guiOpenHandlerName);
		writeNBTToBuf(buf, this.nbtTagCompound);
	}

}
