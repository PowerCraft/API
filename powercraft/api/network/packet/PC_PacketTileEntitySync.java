package powercraft.api.network.packet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_Utils;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketServerToClient;

public class PC_PacketTileEntitySync extends PC_PacketServerToClient {

	private int x;
	private int y;
	private int z;
	private NBTTagCompound nbtTagCompound;
	
	public PC_PacketTileEntitySync(){
		
	}
	
	public PC_PacketTileEntitySync(PC_TileEntity te, NBTTagCompound nbtTagCompound){
		x = te.xCoord;
		y = te.yCoord;
		z = te.zCoord;
		this.nbtTagCompound = nbtTagCompound;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	protected PC_Packet doAndReply(INetHandler iNetHandler) {
		PC_TileEntity te = PC_Utils.getTileEntity(PC_ClientUtils.mc().theWorld, x, y, z, PC_TileEntity.class);
		if(te!=null){
			te.applySync(nbtTagCompound);
		}
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		nbtTagCompound = readNBTFromBuf(buf);
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		writeNBTToBuf(buf, nbtTagCompound);
	}

}