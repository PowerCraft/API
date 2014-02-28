package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import powercraft.api.PC_Utils;
import powercraft.api.entity.PC_Entity;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketClientToServer;

public class PC_PacketPasswordReply2 extends PC_PacketClientToServer {

	private int entityID;
	private String password;
	
	public PC_PacketPasswordReply2(){
		
	}
	
	public PC_PacketPasswordReply2(Entity entity, String password){
		entityID = entity.getEntityId();
		this.password = password;
	}
	
	@Override
	protected PC_Packet doAndReply(INetHandler iNetHandler) {
		EntityPlayer player = ((NetHandlerPlayServer)iNetHandler).playerEntity;
		PC_Entity entity = PC_Utils.getEntity(player.worldObj, entityID, PC_Entity.class);
		if(entity!=null){
			if(!entity.guiOpenPasswordReply(player, password)){
				return new PC_PacketWrongPassword2(entity);
			}
		}
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		entityID = buf.readInt();
		password = readStringFromBuf(buf);
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(entityID);
		writeStringToBuf(buf, password);
	}

}
