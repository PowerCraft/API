package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_Utils;
import powercraft.api.entity.PC_Entity;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketServerToClient;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PC_PacketEntitySync extends PC_PacketServerToClient {

	private int entityID;
	private NBTTagCompound nbtTagCompound;
	
	public PC_PacketEntitySync(){
		
	}
	
	public PC_PacketEntitySync(Entity entity, NBTTagCompound nbtTagCompound){
		entityID = entity.getEntityId();
		this.nbtTagCompound = nbtTagCompound;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	protected PC_Packet doAndReply(INetHandler iNetHandler) {
		PC_Entity entity = PC_Utils.getEntity(PC_ClientUtils.mc().theWorld, entityID, PC_Entity.class);
		if(entity!=null){
			entity.applySync(nbtTagCompound);
		}
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		entityID = buf.readInt();
		nbtTagCompound = readNBTFromBuf(buf);
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(entityID);
		writeNBTToBuf(buf, nbtTagCompound);
	}

}
