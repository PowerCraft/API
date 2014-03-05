package powercraft.api.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.renderer.PC_EntityRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface PC_IEntity {

	@SideOnly(Side.CLIENT)
	public void onClientMessage(EntityPlayer player, NBTTagCompound nbtTagCompound);
	
	public void onClientMessageCheck(EntityPlayer player, NBTTagCompound nbtTagCompound, long session);
	
	public boolean guiOpenPasswordReply(EntityPlayer player, String password);
	
	public void setSession(long session);
	
	public long getNewSession(EntityPlayer player);
	
	@SideOnly(Side.CLIENT)
	public void openPasswordGui();
	
	@SideOnly(Side.CLIENT)
	public void wrongPasswordInput();
	
	public void applySync(NBTTagCompound nbtTagCompound);
	
	public void openContainer(PC_GresBaseWithInventory container);
	
	public void closeContainer(PC_GresBaseWithInventory container);
	
	public void sendProgressBarUpdates();

	public int getEntityId();
	
	@SideOnly(Side.CLIENT)
	public String getEntityTextureName(PC_EntityRenderer<?> renderer);
	
	@SideOnly(Side.CLIENT)
	public void doRender(PC_EntityRenderer<?> renderer, double x, double y, double z, float rotYaw, float timeStamp);
	
}
