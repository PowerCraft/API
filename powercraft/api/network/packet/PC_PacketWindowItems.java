package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.world.World;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketServerToClient;


public class PC_PacketWindowItems extends PC_PacketServerToClient {
	
	private int windowId;
	
	private List<ItemStack> itemStacks;
	
	public PC_PacketWindowItems(){
		
	}
	
	public PC_PacketWindowItems(int windowId, List<ItemStack> itemStacks){
		this.windowId = windowId;
		this.itemStacks = itemStacks;
	}
	
	@Override
	protected PC_Packet doAndReply(NetHandlerPlayClient playClient, World world, EntityPlayer player) {
		PC_GresBaseWithInventory.SETTING_OK = true;
		playClient.handleWindowItems(new S30PacketWindowItems(this.windowId, this.itemStacks));
		PC_GresBaseWithInventory.SETTING_OK = false;
		return null;
	}
	
	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		this.windowId = buf.readInt();
		int size = buf.readInt();
		this.itemStacks = new ArrayList<ItemStack>(size);
		for(int i=0; i<size; i++){
			this.itemStacks.add(readItemStackFromBuf(buf));
		}
	}
	
	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(this.windowId);
		buf.writeInt(this.itemStacks.size());
		for(ItemStack itemStack:this.itemStacks){
			writeItemStackToBuf(buf, itemStack);
		}
	}
	
}
