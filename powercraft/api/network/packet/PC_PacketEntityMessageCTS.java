package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import powercraft.api.PC_Utils;
import powercraft.api.entity.PC_Entity;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketClientToServer;

public class PC_PacketEntityMessageCTS extends PC_PacketClientToServer {

	private int entityID;
	private NBTTagCompound nbtTagCompound;
	private long session;
	
	public PC_PacketEntityMessageCTS(){
		
	}
	
	public PC_PacketEntityMessageCTS(Entity entity, NBTTagCompound nbtTagCompound, long session){
		entityID = entity.getEntityId();
		this.nbtTagCompound = nbtTagCompound;
		this.session = session;
	}
	
	@Override
	protected PC_Packet doAndReply(INetHandler iNetHandler) {
		EntityPlayer player = ((NetHandlerPlayServer)iNetHandler).playerEntity;
		PC_Entity entity = PC_Utils.getEntity(player.worldObj, entityID, PC_Entity.class);
		if(entity!=null){
			entity.onClientMessageCheck(player, nbtTagCompound, session);
		}
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		entityID = buf.readInt();
		nbtTagCompound = readNBTFromBuf(buf);
		session = buf.readLong();
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(entityID);
		writeNBTToBuf(buf, nbtTagCompound);
		buf.writeLong(session);
	}

}
