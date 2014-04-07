package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.world.World;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketServerToClient;


public class PC_PacketSetSlot extends PC_PacketServerToClient {
	
	private int windowId;
	
	private int slot;
	
	private ItemStack itemStack;
	
	public PC_PacketSetSlot(){
		
	}
	
	public PC_PacketSetSlot(int windowId, int slot, ItemStack itemStack){
		this.windowId = windowId;
		this.slot = slot;
		this.itemStack = itemStack;
	}
	
	@Override
	protected PC_Packet doAndReply(NetHandlerPlayClient playClient, World world, EntityPlayer player) {
		PC_GresBaseWithInventory.SETTING_OK = true;
		playClient.handleSetSlot(new S2FPacketSetSlot(this.windowId, this.slot, this.itemStack));
		PC_GresBaseWithInventory.SETTING_OK = false;
		return null;
	}
	
	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		this.windowId = buf.readInt();
		this.slot = buf.readInt();
		this.itemStack = readItemStackFromBuf(buf);
	}
	
	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(this.windowId);
		buf.writeInt(this.slot);
		writeItemStackToBuf(buf, this.itemStack);
	}
	
}
