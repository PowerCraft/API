package powercraft.api.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;
import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_Utils;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketServerToClient;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PC_PacketWrongPassword extends PC_PacketServerToClient {

	private int x;
	private int y;
	private int z;
	
	public PC_PacketWrongPassword(){
		
	}
	
	public PC_PacketWrongPassword(PC_TileEntity te){
		x = te.xCoord;
		y = te.yCoord;
		z = te.zCoord;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	protected PC_Packet doAndReply(INetHandler iNetHandler) {
		PC_TileEntity te = PC_Utils.getTileEntity(PC_ClientUtils.mc().theWorld, x, y, z, PC_TileEntity.class);
		if(te!=null){
			te.wrongPasswordInput();
		}
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}

}
