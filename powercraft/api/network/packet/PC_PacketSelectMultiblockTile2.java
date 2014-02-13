package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import powercraft.api.PC_ClientUtils;
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
	
	@Override
	@SideOnly(Side.CLIENT)
	protected PC_Packet doAndReply(INetHandler iNetHandler) {
		
		Entity e = PC_ClientUtils.mc().theWorld.getEntityByID(player);
		PC_BlockMultiblock block = PC_Utils.getBlock(PC_ClientUtils.mc().theWorld, x, y, z, PC_BlockMultiblock.class);
		if(block!=null && e instanceof EntityPlayer && e!=PC_ClientUtils.mc().thePlayer){
			PC_BlockMultiblock.playerSelect((EntityPlayer)e, PC_MultiblockIndex.values()[tile]);
		}
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		tile = buf.readInt();
		player = buf.readInt();
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(tile);
		buf.writeInt(player);
	}

}
