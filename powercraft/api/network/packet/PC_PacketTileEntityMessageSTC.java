package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import powercraft.api.PC_Utils;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketServerToClient;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PC_PacketTileEntityMessageSTC extends PC_PacketServerToClient {

	private int x;
	private int y;
	private int z;
	private NBTTagCompound nbtTagCompound;
	
	public PC_PacketTileEntityMessageSTC(){
		
	}
	
	public PC_PacketTileEntityMessageSTC(PC_TileEntity te, NBTTagCompound nbtTagCompound){
		this.x = te.xCoord;
		this.y = te.yCoord;
		this.z = te.zCoord;
		this.nbtTagCompound = nbtTagCompound;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	protected PC_Packet doAndReply(NetHandlerPlayClient iNetHandler, World world, EntityPlayer player) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, this.x, this.y, this.z, PC_TileEntity.class);
		if(te!=null){
			te.onClientMessage(player, this.nbtTagCompound);
		}
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.nbtTagCompound = readNBTFromBuf(buf);
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		writeNBTToBuf(buf, this.nbtTagCompound);
	}

}
