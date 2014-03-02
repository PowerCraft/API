package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import powercraft.api.PC_Utils;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.gres.PC_Gres;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketServerToClient;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PC_PacketOpenGresTileEntity extends PC_PacketServerToClient {

	private int x, y, z;
	private int windowId;
	private long session;
	private NBTTagCompound nbtTagCompound;
	
	public PC_PacketOpenGresTileEntity(){
		
	}
	
	public PC_PacketOpenGresTileEntity(PC_TileEntity tileEntity, int windowId, long session, NBTTagCompound nbtTagCompound){
		this.x = tileEntity.xCoord;
		this.y = tileEntity.yCoord;
		this.z = tileEntity.zCoord;
		this.windowId = windowId;
		this.session = session;
		this.nbtTagCompound = nbtTagCompound;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected PC_Packet doAndReply(NetHandlerPlayClient iNetHandler, World world, EntityPlayer player) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, this.x, this.y, this.z, PC_TileEntity.class);
		if(te!=null){
			te.setSession(this.session);
			PC_Gres.openClientGui(player, te, this.windowId, this.nbtTagCompound);
		}
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		this.windowId = buf.readInt();
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.session = buf.readLong();
		this.nbtTagCompound = readNBTFromBuf(buf);
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(this.windowId);
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeLong(this.session);
		writeNBTToBuf(buf, this.nbtTagCompound);
	}

}
