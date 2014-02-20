package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import powercraft.api.PC_Utils;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketClientToServer;

public class PC_PacketTileEntityMessageCTS extends PC_PacketClientToServer {

	private int x;
	private int y;
	private int z;
	private NBTTagCompound nbtTagCompound;
	private long session;
	
	public PC_PacketTileEntityMessageCTS(){
		
	}
	
	public PC_PacketTileEntityMessageCTS(PC_TileEntity te, NBTTagCompound nbtTagCompound, long session){
		x = te.xCoord;
		y = te.yCoord;
		z = te.zCoord;
		this.nbtTagCompound = nbtTagCompound;
		this.session = session;
	}
	
	@Override
	protected PC_Packet doAndReply(INetHandler iNetHandler) {
		EntityPlayer player = ((NetHandlerPlayServer)iNetHandler).playerEntity;
		PC_TileEntity te = PC_Utils.getTileEntity(player.worldObj, x, y, z, PC_TileEntity.class);
		if(te!=null){
			te.onClientMessage(player, nbtTagCompound, session);
		}
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		nbtTagCompound = readNBTFromBuf(buf);
		session = buf.readLong();
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		writeNBTToBuf(buf, nbtTagCompound);
		buf.writeLong(session);
	}

}
