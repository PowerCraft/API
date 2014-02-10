package powercraft.api.gres;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;
import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_Utils;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketServerToClient;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PC_PacketOpenGresTileEntity extends PC_PacketServerToClient {

	private int x, y, z;
	private int windowId;
	
	public PC_PacketOpenGresTileEntity(){
		
	}
	
	public PC_PacketOpenGresTileEntity(PC_TileEntity tileEntity, int windowId){
		x = tileEntity.xCoord;
		y = tileEntity.yCoord;
		z = tileEntity.zCoord;
		this.windowId = windowId;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected PC_Packet doAndReply(INetHandler iNetHandler) {
		PC_TileEntity te = PC_Utils.getTileEntity(PC_ClientUtils.mc().theWorld, x, y, z, PC_TileEntity.class);
		if(te!=null){
			PC_Gres.openClientGui(PC_ClientUtils.mc().thePlayer, te, windowId);
		}
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		windowId = buf.readInt();
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(windowId);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}

}
