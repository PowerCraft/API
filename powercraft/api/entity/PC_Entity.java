package powercraft.api.entity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;
import java.util.WeakHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_Field;
import powercraft.api.PC_Field.Flag;
import powercraft.api.PC_Logger;
import powercraft.api.PC_NBTTagHandler;
import powercraft.api.PC_Utils;
import powercraft.api.gres.PC_Gres;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.PC_IGresGuiOpenHandler;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.network.packet.PC_PacketEntityMessageCTS;
import powercraft.api.network.packet.PC_PacketEntityMessageSTC;
import powercraft.api.network.packet.PC_PacketEntitySync;
import powercraft.api.network.packet.PC_PacketPasswordRequest2;
import powercraft.api.reflect.PC_Processor;
import powercraft.api.reflect.PC_Reflection;
import powercraft.api.renderer.PC_EntityRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class PC_Entity extends Entity implements PC_IEntity {

	private static WeakHashMap<EntityPlayer, Session> sessions = new WeakHashMap<EntityPlayer, Session>();
	private static WeakHashMap<PC_Entity, List<PC_GresBaseWithInventory>> containers = new WeakHashMap<PC_Entity, List<PC_GresBaseWithInventory>>();

	private static class Session {

		private static Random sessionRand = new Random();

		final int dimension;
		final int entityID;
		final long session;

		Session(PC_Entity entity) {
			this.session = sessionRand.nextLong();
			this.dimension = PC_Utils.getDimensionID(entity.worldObj);
			this.entityID = entity.getEntityId();
		}

	}

	private long session;

	private String owner;
	private String password;

	private boolean sync;

	public PC_Entity(World world) {
		super(world);
	}

	private final void readFromNBT(final NBTTagCompound nbtTagCompound,
			final Flag flag) {
		PC_Reflection.processFields(this, new PC_Processor() {

			@Override
			public void process(Field field, Object value,
					EnumMap<Result, Object> results) {
				PC_Field info = field.getAnnotation(PC_Field.class);
				if (info != null && flag.isIn(info)) {
					String name = info.name();
					if (name.isEmpty()) {
						name = field.getName();
					}
					Class<?> type = field.getType();
					Object nvalue = PC_NBTTagHandler.loadFromNBT(nbtTagCompound, name,
							type, flag);
					results.put(Result.SET, nvalue);
				}
			}

		});
		onLoadedFromNBT(flag);
	}

	private final void writeToNBT(final NBTTagCompound nbtTagCompound,
			final Flag flag) {
		PC_Reflection.processFields(this, new PC_Processor() {

			@Override
			public void process(Field field, Object value,
					EnumMap<Result, Object> results) {
				if (value == null)
					return;
				PC_Field info = field.getAnnotation(PC_Field.class);
				if (info != null && flag.isIn(info)) {
					String name = info.name();
					if (name.isEmpty()) {
						name = field.getName();
					}
					PC_NBTTagHandler.saveToNBT(nbtTagCompound, name, value,
							flag);
				}
			}

		});
	}

	@SuppressWarnings("unused")
	protected void onLoadedFromNBT(Flag flag) {
		//
	}

	@Override
	protected final void readEntityFromNBT(NBTTagCompound tag) {
		readFromNBT(tag, Flag.SAVE);
		if (tag.hasKey("owner")) {
			this.owner = tag.getString("owner");
			if (tag.hasKey("password")) {
				this.password = tag.getString("password");
			}
		}
	}

	@Override
	protected final void writeEntityToNBT(NBTTagCompound tag) {
		writeToNBT(tag, Flag.SAVE);
		if (this.owner != null) {
			tag.setString("owner", this.owner);
			if (this.password != null) {
				tag.setString("password", this.password);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onClientMessage(EntityPlayer player,
			NBTTagCompound nbtTagCompound) {
		onMessage(player, nbtTagCompound);
	}

	@SuppressWarnings("unused")
	public void onMessage(EntityPlayer player, NBTTagCompound nbtTagCompound) {
		//
	}

	public void sendMessage(NBTTagCompound nbtTagCompound) {
		if (isClient()) {
			PC_PacketHandler.sendToServer(new PC_PacketEntityMessageCTS(this,
					nbtTagCompound, this.session));
		} else {
			PC_PacketHandler.sendToAllAround(new PC_PacketEntityMessageSTC(
					this, nbtTagCompound), this.worldObj, this.posX, this.posY, this.posZ, 32);
		}
	}

	public boolean isClient() {

		if (this.worldObj == null)
			return PC_Utils.isClient();
		return this.worldObj.isRemote;
	}

	@SuppressWarnings("hiding")
	@Override
	public void onClientMessageCheck(EntityPlayer player,
			NBTTagCompound nbtTagCompound, long session) {
		Session pSession = sessions.get(player);
		if (pSession != null
				&& pSession.dimension == PC_Utils.getDimensionID(this.worldObj)
				&& pSession.entityID == getEntityId()
				&& pSession.session == session) {
			onMessage(player, nbtTagCompound);
		} else {
			PC_Logger.warning("Player %s tries to send not signated messaged",
					Long.valueOf(session));
		}
	}

	public final boolean canDoWithoutPassword(EntityPlayer player) {
		return this.owner == null || this.owner.equals(player.getGameProfile().getName())
				|| PC_Utils.isOP(player);
	}

	public final boolean canDoWithPassword(EntityPlayer player) {
		return this.owner == null || this.owner.equals(player.getGameProfile().getName())
				|| this.password != null;
	}

	@SuppressWarnings("hiding")
	public final boolean checkPassword(EntityPlayer player, String password) {
		return canDoWithoutPassword(player)
				|| (this.password != null && this.password.equals(password));
	}

	public final boolean setPassword(EntityPlayer player, String newPassword) {
		if (canDoWithoutPassword(player)) {
			if (newPassword == null) {
				this.password = null;
			} else {
				this.password = PC_Utils.getMD5(newPassword);
			}
			this.owner = PC_Utils.getUsername(player);
			return true;
		}
		return false;
	}

	@SuppressWarnings("hiding")
	@Override
	public final boolean guiOpenPasswordReply(EntityPlayer player,
			String password) {
		String md5password = PC_Utils.getMD5(password);
		if (checkPassword(player, md5password)) {
			PC_Gres.openGui(player, this);
			return true;
		}
		return false;
	}

	public long getSession() {
		if (isClient())
			return this.session;
		return 0;
	}

	@Override
	public void setSession(long session) {
		if (isClient())
			this.session = session;
	}

	@SuppressWarnings("hiding")
	@Override
	public long getNewSession(EntityPlayer player) {
		if (isClient())
			return 0;
		Session session = new Session(this);
		sessions.put(player, session);
		return session.session;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void openPasswordGui() {
		PC_Gres.openClientGui(PC_ClientUtils.mc().thePlayer,
				new PC_GuiPasswordInput(this), -1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void wrongPasswordInput() {
		PC_IGresGui gui = PC_Gres.getCurrentClientGui();
		if (gui instanceof PC_GuiPasswordInput) {
			((PC_GuiPasswordInput) gui).wrongPassword(this);
		}
	}

	@Override
	public boolean interactFirst(EntityPlayer player) {
		if (this instanceof PC_IGresGuiOpenHandler) {

			if (!isClient()) {

				if (canDoWithoutPassword(player)) {

					PC_Gres.openGui(player, this);

				} else if (canDoWithPassword(player)) {

					PC_PacketHandler.sendTo(
							new PC_PacketPasswordRequest2(this),
							(EntityPlayerMP) player);

				}

			}

			return true;

		}

		return false;
	}

	public final PC_Packet getSyncPacket() {
		this.sync = false;
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		makeSync(nbtTagCompound);
		return new PC_PacketEntitySync(this, nbtTagCompound);
	}

	public final void makeSync(NBTTagCompound nbtTagCompound) {
		writeToNBT(nbtTagCompound, Flag.SYNC);
	}

	@Override
	public final void applySync(NBTTagCompound nbtTagCompound) {
		if (this.worldObj.isRemote) {
			readFromNBT(nbtTagCompound, Flag.SYNC);
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!isClient() && this.sync) {
			PC_PacketHandler
					.sendToAllAround(getSyncPacket(), this.worldObj, this.posX, this.posY, this.posZ, 32);
			this.sync = false;
		}
	}

	public void sendProgressBarUpdate(int key, int value) {
		List<PC_GresBaseWithInventory> list = containers.get(this);
		if (list == null)
			return;
		for (PC_GresBaseWithInventory container : list) {
			container.sendProgressBarUpdate(key, value);
		}
	}

	@Override
	public void openContainer(PC_GresBaseWithInventory container) {
		List<PC_GresBaseWithInventory> list = containers.get(this);
		if (list == null) {
			containers.put(this,
					list = new ArrayList<PC_GresBaseWithInventory>());
		}
		if (!list.contains(container)) {
			list.add(container);
		}
	}

	@Override
	public void closeContainer(PC_GresBaseWithInventory container) {
		List<PC_GresBaseWithInventory> list = containers.get(this);
		if (list == null)
			return;
		list.remove(container);
		if (list.isEmpty())
			containers.remove(this);
	}

	public void detectAndSendChanges() {
		List<PC_GresBaseWithInventory> list = containers.get(this);
		if (list == null)
			return;
		for (PC_GresBaseWithInventory container : list) {
			container.detectAndSendChanges();
		}
	}

	@Override
	public void sendProgressBarUpdates() {
		//
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getEntityTextureName(PC_EntityRenderer<?> renderer) {
		return "texture";
	}
	
}
