package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
import powercraft.api.PC_Utils;
import powercraft.api.entity.PC_IEntity;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketClientToServer;

public class PC_PacketPasswordReply2 extends PC_PacketClientToServer {

	private int entityID;
	private String password;
	
	public PC_PacketPasswordReply2(){
		
	}
	
	public PC_PacketPasswordReply2(PC_IEntity entity, String password){
		this.entityID = entity.getEntityId();
		this.password = password;
	}
	
	@Override
	protected PC_Packet doAndReply(NetHandlerPlayServer iNetHandler, World world, EntityPlayerMP player) {
		PC_IEntity entity = PC_Utils.getEntity(world, this.entityID, PC_IEntity.class);
		if(entity!=null){
			if(!entity.guiOpenPasswordReply(player, this.password)){
				return new PC_PacketWrongPassword2(entity);
			}
		}
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		this.entityID = buf.readInt();
		this.password = readStringFromBuf(buf);
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(this.entityID);
		writeStringToBuf(buf, this.password);
	}

}
