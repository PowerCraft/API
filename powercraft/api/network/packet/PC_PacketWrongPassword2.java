package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import powercraft.api.PC_Utils;
import powercraft.api.entity.PC_IEntity;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketServerToClient;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PC_PacketWrongPassword2 extends PC_PacketServerToClient {

	private int entityID;
	
	public PC_PacketWrongPassword2(){
		
	}
	
	public PC_PacketWrongPassword2(PC_IEntity entity){
		this.entityID = entity.getEntityId();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	protected PC_Packet doAndReply(NetHandlerPlayClient iNetHandler, World world, EntityPlayer player) {
		PC_IEntity entity = PC_Utils.getEntity(world, this.entityID, PC_IEntity.class);
		if(entity!=null){
			entity.wrongPasswordInput();
		}
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		this.entityID = buf.readInt();
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(this.entityID);
	}

}
