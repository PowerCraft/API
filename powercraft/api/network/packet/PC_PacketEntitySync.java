package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import powercraft.api.PC_Utils;
import powercraft.api.entity.PC_IEntity;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketServerToClient;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PC_PacketEntitySync extends PC_PacketServerToClient {

	private int entityID;
	private NBTTagCompound nbtTagCompound;
	
	public PC_PacketEntitySync(){
		
	}
	
	public PC_PacketEntitySync(PC_IEntity entity, NBTTagCompound nbtTagCompound){
		this.entityID = entity.getEntityId();
		this.nbtTagCompound = nbtTagCompound;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	protected PC_Packet doAndReply(NetHandlerPlayClient iNetHandler, World world, EntityPlayer player) {
		PC_IEntity entity = PC_Utils.getEntity(world, this.entityID, PC_IEntity.class);
		if(entity!=null){
			entity.applySync(this.nbtTagCompound);
		}
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		this.entityID = buf.readInt();
		this.nbtTagCompound = readNBTFromBuf(buf);
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(this.entityID);
		writeNBTToBuf(buf, this.nbtTagCompound);
	}

}
