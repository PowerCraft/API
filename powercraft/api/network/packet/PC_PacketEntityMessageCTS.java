package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
import powercraft.api.PC_Utils;
import powercraft.api.entity.PC_IEntity;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketClientToServer;

public class PC_PacketEntityMessageCTS extends PC_PacketClientToServer {

	private int entityID;
	private NBTTagCompound nbtTagCompound;
	private long session;
	
	public PC_PacketEntityMessageCTS(){
		
	}
	
	public PC_PacketEntityMessageCTS(PC_IEntity entity, NBTTagCompound nbtTagCompound, long session){
		this.entityID = entity.getEntityId();
		this.nbtTagCompound = nbtTagCompound;
		this.session = session;
	}
	
	@Override
	protected PC_Packet doAndReply(NetHandlerPlayServer iNetHandler, World world, EntityPlayerMP player) {
		PC_IEntity entity = PC_Utils.getEntity(world, this.entityID, PC_IEntity.class);
		if(entity!=null){
			entity.onClientMessageCheck(player, this.nbtTagCompound, this.session);
		}
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		this.entityID = buf.readInt();
		this.nbtTagCompound = readNBTFromBuf(buf);
		this.session = buf.readLong();
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(this.entityID);
		writeNBTToBuf(buf, this.nbtTagCompound);
		buf.writeLong(this.session);
	}

}
