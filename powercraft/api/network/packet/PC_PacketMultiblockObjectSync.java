package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import powercraft.api.PC_ClientUtils;
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
		x = multiblock.xCoord;
		y = multiblock.yCoord;
		z = multiblock.zCoord;
		subTile = multiblockObject.getIndex().ordinal();
		this.nbtTagCompound = nbtTagCompound;
	}
	
	@Override
	protected PC_Packet doAndReply(INetHandler iNetHandler) {
		PC_TileEntityMultiblock tem = PC_Utils.getTileEntity(PC_ClientUtils.mc().theWorld, x, y, z, PC_TileEntityMultiblock.class);
		if(tem!=null){
			PC_MultiblockObject multiblockObject = tem.getTile(PC_MultiblockIndex.values()[subTile]);
			if(multiblockObject!=null){
				multiblockObject.applySync(nbtTagCompound);
			}
		}
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		subTile = buf.readInt();
		nbtTagCompound = readNBTFromBuf(buf);
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(subTile);
		writeNBTToBuf(buf, nbtTagCompound);
	}

}
