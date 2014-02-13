package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import powercraft.api.PC_Utils;
import powercraft.api.multiblock.PC_BlockMultiblock;
import powercraft.api.multiblock.PC_MultiblockIndex;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketClientToServer;
import powercraft.api.network.PC_PacketHandler;

public class PC_PacketSelectMultiblockTile extends PC_PacketClientToServer {

	private int x;
	private int y;
	private int z;
	private int tile;
	
	public PC_PacketSelectMultiblockTile(){
		
	}
	
	public PC_PacketSelectMultiblockTile(int x, int y, int z, PC_MultiblockIndex tile){
		this.x = x;
		this.y = y;
		this.z = z;
		this.tile = tile.ordinal();
	}
	
	@Override
	protected PC_Packet doAndReply(INetHandler iNetHandler) {
		EntityPlayer player = ((NetHandlerPlayServer)iNetHandler).playerEntity;
		PC_BlockMultiblock block = PC_Utils.getBlock(player.worldObj, x, y, z, PC_BlockMultiblock.class);
		if(block!=null){
			PC_MultiblockIndex index = PC_MultiblockIndex.values()[tile];
			PC_BlockMultiblock.playerSelect(player, index);
			PC_PacketHandler.sendToAllAround(new PC_PacketSelectMultiblockTile2(x, y, z, index, player), player.dimension, x, y, z, 32);
		}
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		tile = buf.readInt();
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(tile);
	}

}
