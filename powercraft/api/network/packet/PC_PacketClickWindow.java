package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.world.World;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketClientToServer;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.reflect.PC_Fields;


public class PC_PacketClickWindow extends PC_PacketClientToServer {
	
	private int windowId;
	private int slotNumber;
	private int mouseButton;
	private int transfer;
	private int transactionID;
	private ItemStack clientDone;
	
	public PC_PacketClickWindow(){
		
	}
	
	public PC_PacketClickWindow(int windowId, int slotNumber, int mouseButton, int transfer, int transactionID, ItemStack clientDone){
		this.windowId = windowId;
		this.slotNumber = slotNumber;
		this.mouseButton = mouseButton;
		this.transfer = transfer;
		this.transactionID = transactionID;
		this.clientDone = clientDone;
	}
	
	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		this.windowId = buf.readInt();
		this.slotNumber = buf.readInt();
		this.mouseButton = buf.readInt();
		this.transfer = buf.readInt();
		this.transactionID = buf.readInt();
		this.clientDone = readItemStackFromBuf(buf);
	}
	
	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(this.windowId);
		buf.writeInt(this.slotNumber);
		buf.writeInt(this.mouseButton);
		buf.writeInt(this.transfer);
		buf.writeInt(this.transactionID);
		writeItemStackToBuf(buf, this.clientDone);
	}

	@Override
	protected PC_Packet doAndReply(NetHandlerPlayServer playServer, World world, EntityPlayerMP player) {
		player.func_143004_u();

        if (player.openContainer.windowId == this.windowId && player.openContainer.isPlayerNotUsingContainer(player)){
            ItemStack itemstack = player.openContainer.slotClick(this.slotNumber, this.mouseButton, this.transfer, player);

            if (ItemStack.areItemStacksEqual(this.clientDone, itemstack)){
            	player.playerNetServerHandler.sendPacket(new S32PacketConfirmTransaction(this.windowId, (short) this.transactionID, true));
            	player.isChangingQuantityOnly = true;
            	player.openContainer.detectAndSendChanges();
            	player.updateHeldItem();
            	player.isChangingQuantityOnly = false;
            }else{
            	PC_Fields.NetHandlerPlayServer_field_147372_n.getValue(playServer).addKey(player.openContainer.windowId, Short.valueOf((short)this.transactionID));
            	player.playerNetServerHandler.sendPacket(new S32PacketConfirmTransaction(this.windowId, (short) this.transactionID, false));
                player.openContainer.setPlayerIsPresent(player, false);
                ArrayList<ItemStack> itemStacks = new ArrayList<ItemStack>();

                for (int i = 0; i < player.openContainer.inventorySlots.size(); ++i)
                {
                	itemStacks.add(((Slot)player.openContainer.inventorySlots.get(i)).getStack());
                }

                PC_PacketHandler.sendTo(new PC_PacketWindowItems(this.windowId, itemStacks), player);
                PC_PacketHandler.sendTo(new PC_PacketSetSlot(-1, -1, player.inventory.getItemStack()), player);
            }
        }
		return null;
	}
	
}
