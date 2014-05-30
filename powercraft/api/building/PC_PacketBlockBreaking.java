package powercraft.api.building;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketServerToClient;


public class PC_PacketBlockBreaking extends PC_PacketServerToClient {

	private int x;
	private int y;
	private int z;
	private int damage;
	
	public PC_PacketBlockBreaking(){
		
	}
	
	PC_PacketBlockBreaking(int x, int y, int z, int damage) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.damage = damage;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.damage = buf.readInt();
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeInt(this.damage);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	protected PC_Packet doAndReply(NetHandlerPlayClient playClient, World world, EntityPlayer player) {
		PC_BlockDamage.setClientDamage(this.x, this.y, this.z, this.damage);
		return null;
	}
	
}
