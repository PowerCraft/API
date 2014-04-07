package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
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
	protected PC_Packet doAndReply(NetHandlerPlayServer iNetHandler, World world, EntityPlayerMP player) {
		PC_BlockMultiblock block = PC_Utils.getBlock(world, this.x, this.y, this.z, PC_BlockMultiblock.class);
		if(block!=null){
			PC_MultiblockIndex index = PC_MultiblockIndex.values()[this.tile];
			PC_BlockMultiblock.playerSelect(player, index);
			PC_PacketHandler.sendToAllAround(new PC_PacketSelectMultiblockTile2(this.x, this.y, this.z, index, player), player.dimension, this.x, this.y, this.z, 32);
		}
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.tile = buf.readInt();
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeInt(this.tile);
	}

}
