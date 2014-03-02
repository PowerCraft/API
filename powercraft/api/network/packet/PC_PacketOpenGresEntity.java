package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import powercraft.api.PC_Utils;
import powercraft.api.entity.PC_IEntity;
import powercraft.api.gres.PC_Gres;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketServerToClient;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PC_PacketOpenGresEntity extends PC_PacketServerToClient {

	private int entityID;
	private int windowId;
	private long session;
	private NBTTagCompound nbtTagCompound;
	
	public PC_PacketOpenGresEntity(){
		
	}
	
	public PC_PacketOpenGresEntity(PC_IEntity entity, int windowId, long session, NBTTagCompound nbtTagCompound){
		this.entityID = entity.getEntityId();
		this.windowId = windowId;
		this.session = session;
		this.nbtTagCompound = nbtTagCompound;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected PC_Packet doAndReply(NetHandlerPlayClient iNetHandler, World world, EntityPlayer player) {
		PC_IEntity entity = PC_Utils.getEntity(world, this.entityID, PC_IEntity.class);
		if(entity!=null){
			entity.setSession(this.session);
			PC_Gres.openClientGui(player, entity, this.windowId, this.nbtTagCompound);
		}
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		this.windowId = buf.readInt();
		this.entityID = buf.readInt();
		this.session = buf.readLong();
		this.nbtTagCompound = readNBTFromBuf(buf);
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(this.windowId);
		buf.writeInt(this.entityID);
		buf.writeLong(this.session);
		writeNBTToBuf(buf, this.nbtTagCompound);
	}

}
