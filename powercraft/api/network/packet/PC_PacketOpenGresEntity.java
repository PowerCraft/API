package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_Utils;
import powercraft.api.entity.PC_Entity;
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
	
	public PC_PacketOpenGresEntity(Entity entity, int windowId, long session, NBTTagCompound nbtTagCompound){
		entityID = entity.getEntityId();
		this.windowId = windowId;
		this.session = session;
		this.nbtTagCompound = nbtTagCompound;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected PC_Packet doAndReply(INetHandler iNetHandler) {
		PC_Entity entity = PC_Utils.getEntity(PC_ClientUtils.mc().theWorld, entityID, PC_Entity.class);
		if(entity!=null){
			entity.setSession(session);
			PC_Gres.openClientGui(PC_ClientUtils.mc().thePlayer, entity, windowId, nbtTagCompound);
		}
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		windowId = buf.readInt();
		entityID = buf.readInt();
		session = buf.readLong();
		nbtTagCompound = readNBTFromBuf(buf);
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(windowId);
		buf.writeInt(entityID);
		buf.writeLong(session);
		writeNBTToBuf(buf, nbtTagCompound);
	}

}
