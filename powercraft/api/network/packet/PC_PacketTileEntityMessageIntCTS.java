package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
import powercraft.api.PC_Utils;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketClientToServer;

public class PC_PacketTileEntityMessageIntCTS extends PC_PacketClientToServer {

	private int x;
	private int y;
	private int z;
	private NBTTagCompound nbtTagCompound;
	private long session;
	
	public PC_PacketTileEntityMessageIntCTS(){
		
	}
	
	public PC_PacketTileEntityMessageIntCTS(PC_TileEntity te, NBTTagCompound nbtTagCompound, long session){
		this.x = te.xCoord;
		this.y = te.yCoord;
		this.z = te.zCoord;
		this.nbtTagCompound = nbtTagCompound;
		this.session = session;
	}
	
	@Override
	protected PC_Packet doAndReply(NetHandlerPlayServer iNetHandler, World world, EntityPlayer player) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, this.x, this.y, this.z, PC_TileEntity.class);
		if(te!=null){
			te.onClientMessageCheck(player, this.nbtTagCompound, this.session, true);
		}
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.nbtTagCompound = readNBTFromBuf(buf);
		this.session = buf.readLong();
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		writeNBTToBuf(buf, this.nbtTagCompound);
		buf.writeLong(this.session);
	}

}
