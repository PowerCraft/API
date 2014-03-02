package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import powercraft.api.PC_Utils;
import powercraft.api.multiblock.PC_MultiblockIndex;
import powercraft.api.multiblock.PC_MultiblockObject;
import powercraft.api.multiblock.PC_TileEntityMultiblock;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketServerToClient;

public class PC_PacketMultiblockObjectSync extends PC_PacketServerToClient {

	private int x;
	private int y;
	private int z;
	private int subTile;
	private NBTTagCompound nbtTagCompound;
	
	public PC_PacketMultiblockObjectSync(){
		
	}
	
	public PC_PacketMultiblockObjectSync(PC_MultiblockObject multiblockObject, NBTTagCompound nbtTagCompound){
		PC_TileEntityMultiblock multiblock = multiblockObject.getTileEntity();
		this.x = multiblock.xCoord;
		this.y = multiblock.yCoord;
		this.z = multiblock.zCoord;
		this.subTile = multiblockObject.getIndex().ordinal();
		this.nbtTagCompound = nbtTagCompound;
	}
	
	@Override
	protected PC_Packet doAndReply(NetHandlerPlayClient iNetHandler, World world, EntityPlayer player) {
		PC_TileEntityMultiblock tem = PC_Utils.getTileEntity(world, this.x, this.y, this.z, PC_TileEntityMultiblock.class);
		if(tem!=null){
			PC_MultiblockObject multiblockObject = tem.getTile(PC_MultiblockIndex.values()[this.subTile]);
			if(multiblockObject!=null){
				multiblockObject.applySync(this.nbtTagCompound);
			}
		}
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.subTile = buf.readInt();
		this.nbtTagCompound = readNBTFromBuf(buf);
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeInt(this.subTile);
		writeNBTToBuf(buf, this.nbtTagCompound);
	}

}
