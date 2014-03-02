package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import powercraft.api.PC_Utils;
import powercraft.api.multiblock.PC_BlockMultiblock;
import powercraft.api.multiblock.PC_MultiblockIndex;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketServerToClient;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PC_PacketSelectMultiblockTile2 extends PC_PacketServerToClient {

	private int x;
	private int y;
	private int z;
	private int tile;
	private int player;
	
	public PC_PacketSelectMultiblockTile2(){
		
	}
	
	public PC_PacketSelectMultiblockTile2(int x, int y, int z, PC_MultiblockIndex tile, EntityPlayer player){
		this.x = x;
		this.y = y;
		this.z = z;
		this.tile = tile.ordinal();
		this.player = player.getEntityId();
	}
	
	@SuppressWarnings("hiding")
	@Override
	@SideOnly(Side.CLIENT)
	protected PC_Packet doAndReply(NetHandlerPlayClient iNetHandler, World world, EntityPlayer player) {
		
		Entity e = world.getEntityByID(this.player);
		PC_BlockMultiblock block = PC_Utils.getBlock(world, this.x, this.y, this.z, PC_BlockMultiblock.class);
		if(block!=null && e instanceof EntityPlayer && e!=player){
			PC_BlockMultiblock.playerSelect((EntityPlayer)e, PC_MultiblockIndex.values()[this.tile]);
		}
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.tile = buf.readInt();
		this.player = buf.readInt();
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeInt(this.tile);
		buf.writeInt(this.player);
	}

}
