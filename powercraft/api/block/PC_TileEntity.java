package powercraft.api.block;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;
import java.util.WeakHashMap;

import net.minecraft.block.Block;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.IPlantable;
import powercraft.api.PC_3DRotation;
import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Field;
import powercraft.api.PC_Logger;
import powercraft.api.PC_NBTTagHandler;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Field.Flag;
import powercraft.api.gres.PC_Gres;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.PC_IGresGuiOpenHandler;
import powercraft.api.grid.PC_IGridHolder;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.network.packet.PC_PacketPasswordRequest;
import powercraft.api.network.packet.PC_PacketTileEntityMessageCTS;
import powercraft.api.network.packet.PC_PacketTileEntityMessageIntCTS;
import powercraft.api.network.packet.PC_PacketTileEntityMessageSTC;
import powercraft.api.network.packet.PC_PacketTileEntitySync;
import powercraft.api.redstone.PC_RedstoneWorkType;
import powercraft.api.reflect.PC_Processor;
import powercraft.api.reflect.PC_Reflection;
import powercraft.api.renderer.PC_Renderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PC_TileEntity extends TileEntity {

	private static WeakHashMap<EntityPlayer, Session> sessions = new WeakHashMap<EntityPlayer, Session>();
	private static WeakHashMap<PC_TileEntity, List<PC_GresBaseWithInventory>> containers = new  WeakHashMap<PC_TileEntity, List<PC_GresBaseWithInventory>>();
	
	private static class Session{
		
		private static Random sessionRand = new Random();
		
		final int dimension;
		final int x;
		final int y;
		final int z;
		final long session;
		
		Session(PC_TileEntity tileEntity){
			this.session = sessionRand.nextLong();
			this.dimension = tileEntity.getWorldObj().getWorldInfo().getVanillaDimension();
			this.x = tileEntity.xCoord;
			this.y = tileEntity.yCoord;
			this.z = tileEntity.zCoord;
		}
		
	}
	
	private String owner;
	private String password;
	
	private long session;
	
	protected boolean sync = false;
	
	@PC_Field(flags={Flag.SAVE, Flag.SYNC})
	private int redstoneValue;
	
	@PC_Field(flags={Flag.SAVE})
	protected PC_RedstoneWorkType workWhen;
	
	public boolean isClient() {

		if (this.worldObj == null) return PC_Utils.isClient();
		return this.worldObj.isRemote;
	}
	
	public void onBreak() {
		if(this instanceof PC_IGridHolder){
			((PC_IGridHolder)this).removeFromGrid();
		}
	}

	@SuppressWarnings("static-method")
	public float getHardness() {
		return Float.NaN;
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick() {
		//
	}

	@SuppressWarnings("unused")
	public void onNeighborBlockChange(Block neighbor) {
		int newRedstoneValue = PC_Utils.getRedstoneValue(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		updateRedstone(newRedstoneValue);
	}
	
	protected void updateRedstone(int newRedstoneValue){
		if(newRedstoneValue!=this.redstoneValue){
			onRedstoneValueChanging(newRedstoneValue, this.redstoneValue);
			this.redstoneValue = newRedstoneValue;
		}
	}

	protected void onRedstoneValueChanging(int newValue, int oldValue){
		if(this.workWhen==null)
			return;
		switch(this.workWhen){
		case ON_FLANK:
			if((newValue==0 && oldValue!=0) || (newValue!=0 && oldValue==0)){
				startWorking();
				doWork();
				stopWorking();
			}
			break;
		case ON_HI_FLANK:
			if(newValue!=0 && oldValue==0){
				startWorking();
				doWork();
				stopWorking();
			}
			break;
		case ON_LOW_FLANK:
			if(newValue==0 && oldValue!=0){
				startWorking();
				doWork();
				stopWorking();
			}
			break;
		case ON_OFF:
			if(newValue==0 && oldValue!=0){
				startWorking();
			}else if(newValue!=0 && oldValue==0){
				stopWorking();
			}
			break;
		case ON_ON:
			if(newValue!=0 && oldValue==0){
				startWorking();
			}else if(newValue==0 && oldValue!=0){
				stopWorking();
			}
			break;
		default:
			break;
		}
	}
	
	protected void startWorking(){
		//
	}
	
	protected void doWork(){
		//
	}
	
	protected void stopWorking(){
		//
	}
	
	public boolean isWorking(){
		return this.workWhen == PC_RedstoneWorkType.EVER || (this.redstoneValue==0 && this.workWhen == PC_RedstoneWorkType.ON_OFF) || (this.redstoneValue!=0 && this.workWhen == PC_RedstoneWorkType.ON_ON);
	}
	
	@Override
	public final void updateEntity() {
		if(isInvalid())
			return;
		if(isWorking()){
			doWork();
		}
		if(this instanceof PC_IGridHolder){
			((PC_IGridHolder)this).getGridIfNull();
		}
		onTick();
		if (!isClient() && this.sync) {
			PC_PacketHandler.sendToAllAround(getSyncPacket(), this.worldObj.getWorldInfo().getVanillaDimension(), this.xCoord, this.yCoord, this.zCoord, 32);
			this.sync = false;
		}
	}

	public void onTick(){
		//
	}

	public void sync(){
		if (isClient()) return;
		this.sync = true;
		markDirty();
	}
	
	public void notifyNeighbors() {
		if(this.worldObj!=null)
			this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, getBlockType());
	}
	
	public void renderUpdate() {
		if (this.worldObj != null) this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	}


	public void lightUpdate() {
		if (this.worldObj != null) this.worldObj.func_147451_t(this.xCoord, this.yCoord, this.zCoord);
	}
	
	@SuppressWarnings({ "static-method", "unused" })
	public float getPlayerRelativeHardness(EntityPlayer player) {
		return Float.NaN;
	}

	@SuppressWarnings("unused")
	public void onEntityWalking(Entity entity) {
		//
	}

	@SuppressWarnings("unused")
	public void onBlockClicked(EntityPlayer player) {
		//
	}

	@SuppressWarnings("unused")
	public void velocityToAddToEntity(Entity entity, Vec3 velocity) {
		//
	}

	@SuppressWarnings("static-method")
	public int getColorMultiplier() {
		return 16777215;
	}

	@SuppressWarnings("unused")
	public void onEntityCollidedWithBlock(Entity entity) {
		//
	}

	@SuppressWarnings("unused")
	public void onFallenUpon(Entity entity, float fallDistance) {
		//
	}

	public int getDamageValue() {
		return getBlockType().damageDropped(this.blockMetadata);
	}

	public void fillWithRain() {
		//
	}

	@SuppressWarnings("static-method")
	public int getLightValue() {
		return -1;
	}

	@SuppressWarnings({ "static-method", "unused" })
	public boolean isLadder(EntityLivingBase entity) {
		return false;
	}

	@SuppressWarnings("static-method")
	public boolean isBurning() {
		return false;
	}

	@SuppressWarnings({ "static-method", "unused" })
	public ArrayList<ItemStack> getDrops(int fortune) {
		return null;
	}
	
	@SuppressWarnings({ "static-method", "unused" })
	public boolean canCreatureSpawn(EnumCreatureType type) {
		return false;
	}

	@SuppressWarnings("static-method")
	public boolean canSustainLeaves() {
		return false;
	}

	@SuppressWarnings({ "static-method", "unused" })
	public float getExplosionResistance(Entity entity, double explosionX, double explosionY, double explosionZ) {
		return Float.NaN;
	}

	@SuppressWarnings({ "static-method", "unused" })
	public ItemStack getPickBlock(MovingObjectPosition target) {
		return null;
	}

	@SuppressWarnings({ "static-method", "unused" })
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(EffectRenderer effectRenderer) {
		return false;
	}

	@SuppressWarnings("unused")
	public void onPlantGrow(int sourceX, int sourceY, int sourceZ) {
		//
	}

	@SuppressWarnings("static-method")
	public boolean isFertile() {
		return false;
	}

	@SuppressWarnings("static-method")
	public int getLightOpacity() {
		return -1;
	}

	@SuppressWarnings({ "static-method", "unused" })
	public boolean canEntityDestroy(Entity entity) {
		return true;
	}

	@SuppressWarnings({ "static-method", "unused" })
	public boolean isBeaconBase(int beaconX, int beaconY, int beaconZ) {
		return false;
	}

	@SuppressWarnings("static-method")
	public float getEnchantPowerBonus() {
		return 0;
	}

	@SuppressWarnings("unused")
	public void onNeighborTEChange(int tileX, int tileY, int tileZ) {
		//
	}

	@SuppressWarnings("unused")
	public void onBlockPostSet(PC_Direction side, ItemStack stack, EntityPlayer player, float hitX, float hitY, float hitZ) {
		//
	}

	@SuppressWarnings("static-method")
	public PC_3DRotation get3DRotation() {
		return null;
	}

	@SuppressWarnings({ "static-method", "unused" })
	public IIcon getIcon(PC_Direction side) {
		return null;
	}

	@SuppressWarnings({ "static-method", "unused" })
	public List<AxisAlignedBB> getCollisionBoundingBoxes(Entity entity) {
		return null;
	}

	@SuppressWarnings("static-method")
	public AxisAlignedBB getMainCollisionBoundingBox() {
		return null;
	}

	@SuppressWarnings("static-method")
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox() {
		return null;
	}

	@SuppressWarnings("unused")
	public boolean onBlockActivated(EntityPlayer player, PC_Direction side) {
		
		if(this instanceof PC_IGresGuiOpenHandler){
			
			if(!isClient()){
				
				if(canDoWithoutPassword(player)){
					
					PC_Gres.openGui(player, this);
					
				}else if(canDoWithPassword(player)){
					
					PC_PacketHandler.sendTo(new PC_PacketPasswordRequest(this), (EntityPlayerMP)player);
					
				}
				
			}
			
			return true;
			
		}
		
		return false;
	}

	@SuppressWarnings({ "static-method", "unused" })
	public int getComparatorInput(PC_Direction side) {
		return 0;
	}

	@SuppressWarnings("unused")
	public boolean isSideSolid(PC_Direction side) {
		return getBlockType().isNormalCube(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
	}

	@SuppressWarnings({ "static-method", "unused" })
	public int getFlammability(PC_Direction side) {
		return 0;
	}

	@SuppressWarnings({ "static-method", "unused" })
	public boolean isFlammable(PC_Direction side) {
		return false;
	}

	@SuppressWarnings({ "static-method", "unused" })
	public int getFireSpreadSpeed(PC_Direction side) {
		return 0;
	}

	@SuppressWarnings({ "static-method", "unused" })
	public boolean isFireSource(PC_Direction side) {
		return false;
	}

	@SuppressWarnings({ "static-method", "unused" })
	public boolean canSilkHarvest(EntityPlayer player) {
		return false;
	}

	@SuppressWarnings({ "static-method", "unused" })
	public boolean canSustainPlant(PC_Direction side, IPlantable plantable) {
		return false;
	}

	@SuppressWarnings({ "static-method", "unused" })
	public boolean recolourBlock(PC_Direction side, int colour) {
		return false;
	}

	@SuppressWarnings("unused")
	public boolean shouldCheckWeakPower(PC_Direction side) {
		return getBlockType().isNormalCube();
	}

	@SuppressWarnings("static-method")
	public boolean getWeakChanges() {
		return false;
	}

	@SuppressWarnings("static-method")
	public boolean canRotate() {
		return false;
	}

	@SuppressWarnings({ "static-method", "unused" })
	public boolean set3DRotation(PC_3DRotation rotation) {
		return false;
	}

	@SuppressWarnings("unused")
	public void openContainer(Container container) {
		//		
	}

	@SuppressWarnings("unused")
	public void closeContainer(Container container) {
		//
	}

	public void sendProgressBarUpdates() {
		//
	}

	public void sendProgressBarUpdate(int key, int value) {
		List<PC_GresBaseWithInventory> list = containers.get(this);
		if(list==null)
			return;
		for (PC_GresBaseWithInventory container : list) {
			container.sendProgressBarUpdate(key, value);
		}
	}
	
	public void openContainer(PC_GresBaseWithInventory container) {
		List<PC_GresBaseWithInventory> list = containers.get(this);
		if(list==null){
			containers.put(this, list = new ArrayList<PC_GresBaseWithInventory>());
		}
		if (!list.contains(container)) {
			list.add(container);
		}
	}


	public void closeContainer(PC_GresBaseWithInventory container) {
		List<PC_GresBaseWithInventory> list = containers.get(this);
		if(list==null)
			return;
		list.remove(container);
		if(list.isEmpty())
			containers.remove(this);
	}
	
	public void detectAndSendChanges() {
		List<PC_GresBaseWithInventory> list = containers.get(this);
		if(list==null)
			return;
		for (PC_GresBaseWithInventory container : list) {
			container.detectAndSendChanges();
		}
	}
	
	public final String getOwner(){
		return this.owner;
	}
	
	private final void readFromNBT(final NBTTagCompound nbtTagCompound, final Flag flag){
		PC_Reflection.processFields(this, new PC_Processor(){

			@Override
			public void process(Field field, Object value, EnumMap<Result, Object> results) {
				PC_Field info = field.getAnnotation(PC_Field.class);
				if(info!=null && flag.isIn(info)){
					String name = info.name();
					if(name.isEmpty()){
						name = field.getName();
					}
					Class<?> type = field.getType();
					Object nvalue = PC_NBTTagHandler.loadFromNBT(nbtTagCompound, name, type, flag);
					results.put(Result.SET, nvalue);
				}
			}
			
		});
		onLoadedFromNBT(flag);
	}
	
	private final void writeToNBT(final NBTTagCompound nbtTagCompound, final Flag flag){
		PC_Reflection.processFields(this, new PC_Processor(){

			@Override
			public void process(Field field, Object value, EnumMap<Result, Object> results) {
				if(value==null)
					return;
				PC_Field info = field.getAnnotation(PC_Field.class);
				if(info!=null && flag.isIn(info)){
					String name = info.name();
					if(name.isEmpty()){
						name = field.getName();
					}
					PC_NBTTagHandler.saveToNBT(nbtTagCompound, name, value, flag);
				}
			}
			
		});
	}
	
	@Override
	public final void readFromNBT(NBTTagCompound nbtTagCompound) {
		readFromNBT(nbtTagCompound, Flag.SAVE);
		if(nbtTagCompound.hasKey("owner")){
			this.owner = nbtTagCompound.getString("owner");
			if(nbtTagCompound.hasKey("password")){
				this.password = nbtTagCompound.getString("password");
			}
		}
		super.readFromNBT(nbtTagCompound);
	}

	@Override
	public final void writeToNBT(NBTTagCompound nbtTagCompound) {
		writeToNBT(nbtTagCompound, Flag.SAVE);
		if(this.owner!=null){
			nbtTagCompound.setString("owner", this.owner);
			if(this.password!=null){
				nbtTagCompound.setString("password", this.password);
			}
		}
		super.writeToNBT(nbtTagCompound);
	}
	
	@SuppressWarnings("unused")
	public void onLoadedFromNBT(Flag flag){
		//
	}
	
	public final boolean canDoWithoutPassword(EntityPlayer player){
		return this.owner==null || this.owner.equals(player.getGameProfile().getName()) || PC_Utils.isOP(player);
	}

	public final boolean canDoWithPassword(EntityPlayer player){
		return this.owner==null || this.owner.equals(player.getGameProfile().getName()) || this.password!=null;
	}
	
	@SuppressWarnings("hiding")
	public final boolean checkPassword(EntityPlayer player, String password){
		return canDoWithoutPassword(player) || (this.password!=null && this.password.equals(password));
	}
	
	public final boolean setPassword(EntityPlayer player, String newPassword){
		if(canDoWithoutPassword(player)){
			if(newPassword==null){
				this.password = null;
			}else{
				this.password = PC_Utils.getMD5(newPassword);
			}
			this.owner = PC_Utils.getUsername(player);
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("hiding")
	public final boolean guiOpenPasswordReply(EntityPlayer player, String password) {
		String md5password = PC_Utils.getMD5(password);
		if(checkPassword(player, md5password)){
			PC_Gres.openGui(player, this);
			return true;
		}
		return false;
	}
	
	public long getSession(){
		if(isClient())
			return this.session;
		return 0;
	}
	
	public void setSession(long session){
		if(isClient())
			this.session = session;
	}

	@SuppressWarnings("hiding")
	public long getNewSession(EntityPlayer player) {
		if(isClient())
			return 0;
		Session session = new Session(this);
		sessions.put(player, session);
		return session.session;
	}
	
	@SideOnly(Side.CLIENT)
	public void openPasswordGui() {
		PC_Gres.openClientGui(PC_ClientUtils.mc().thePlayer, new PC_GuiPasswordInput(this), -1);
	}

	@SideOnly(Side.CLIENT)
	public void wrongPasswordInput() {
		PC_IGresGui gui = PC_Gres.getCurrentClientGui();
		if(gui instanceof PC_GuiPasswordInput){
			((PC_GuiPasswordInput)gui).wrongPassword(this);
		}
	}

	@Override
	public final Packet getDescriptionPacket() {
		return PC_PacketHandler.getPacketFrom(getSyncPacket());
	}
	
	public final PC_Packet getSyncPacket(){
		this.sync = false;
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		makeSync(nbtTagCompound);
		return new PC_PacketTileEntitySync(this, nbtTagCompound);
	}

	public final void makeSync(NBTTagCompound nbtTagCompound) {
		writeToNBT(nbtTagCompound, Flag.SYNC);
	}
	
	public final void applySync(NBTTagCompound nbtTagCompound) {
		if(this.worldObj.isRemote){
			readFromNBT(nbtTagCompound, Flag.SYNC);
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean renderWorldBlock(int modelId, RenderBlocks renderer) {
		return PC_Renderer.renderBlockInWorld(this.worldObj, this.xCoord, this.yCoord, this.zCoord, getBlockType(), modelId, renderer);
	}

	@SuppressWarnings("unused")
	public boolean canRedstoneConnect(PC_Direction side, int faceSide) {
		return getBlockType().canProvidePower();
	}

	@SuppressWarnings({ "unused", "static-method" })
	public int getRedstonePowerValue(PC_Direction side, int faceSide) {
		return 0;
	}

	@SuppressWarnings("unused")
	public void setRedstonePowerValue(PC_Direction side, int faceSide, int value) {
		updateRedstone(value);
	}

	@SuppressWarnings("hiding")
	public final void onClientMessageCheck(EntityPlayer player, NBTTagCompound nbtTagCompound, long session, boolean intern) {
		Session pSession = sessions.get(player);
		if(pSession!=null && pSession.dimension == this.worldObj.getWorldInfo().getVanillaDimension() && pSession.x == this.xCoord && pSession.y == this.yCoord && pSession.z == this.zCoord && pSession.session == session){
			if(intern){
				onInternMessage(player, nbtTagCompound);
			}else{
				onMessage(player, nbtTagCompound);
			}
		}else{
			PC_Logger.warning("Player %s tries to send not signated messaged", Long.valueOf(session));
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void onClientMessage(EntityPlayer player, NBTTagCompound nbtTagCompound) {
		onMessage(player, nbtTagCompound);
	}
	
	@SuppressWarnings("unused")
	public void onInternMessage(EntityPlayer player, NBTTagCompound nbtTagCompound) {
		if(nbtTagCompound.getInteger("type")==0){
			if(nbtTagCompound.hasKey("workWhen")){
				setRedstoneWorkType(PC_RedstoneWorkType.values()[nbtTagCompound.getInteger("workWhen")]);
			}else{
				setRedstoneWorkType(null);
			}
		}
	}
	
	@SuppressWarnings("unused")
	public void onMessage(EntityPlayer player, NBTTagCompound nbtTagCompound) {
		//
	}
	
	public void sendMessage(NBTTagCompound nbtTagCompound){
		if(isClient()){
			PC_PacketHandler.sendToServer(new PC_PacketTileEntityMessageCTS(this, nbtTagCompound, this.session));
		}else{
			PC_PacketHandler.sendToAllAround(new PC_PacketTileEntityMessageSTC(this, nbtTagCompound), this.worldObj.getWorldInfo().getVanillaDimension(), this.xCoord, this.yCoord, this.zCoord, 32);
		}
	}
	
	public void sendInternMessage(NBTTagCompound nbtTagCompound){
		if(isClient()){
			PC_PacketHandler.sendToServer(new PC_PacketTileEntityMessageIntCTS(this, nbtTagCompound, this.session));
		}
	}

	public void setRedstoneWorkType(PC_RedstoneWorkType rwt) {
		PC_RedstoneWorkType allowed[] = getAllowedRedstoneWorkTypes();
		for(int i=0; i<allowed.length; i++){
			if(allowed[i]== rwt){
				this.workWhen = rwt;
				sync();
				markDirty();
				NBTTagCompound tagCompound = new NBTTagCompound();
				tagCompound.setInteger("type", 0);
				if(rwt!=null)
					tagCompound.setInteger("workWhen", rwt.ordinal());
				sendInternMessage(tagCompound);
			}
		}
	}

	@SuppressWarnings("static-method")
	public PC_RedstoneWorkType[] getAllowedRedstoneWorkTypes() {
		return new PC_RedstoneWorkType[]{null};
	}
	
	public PC_RedstoneWorkType getRedstoneWorkType() {
		return this.workWhen;
	}

	@SuppressWarnings("unused")
	public void onAdded(EntityPlayer player) {
		if(this instanceof PC_IGridHolder){
			((PC_IGridHolder)this).getGridIfNull();
		}
	}

	@Override
	public void onChunkUnload() {
		if(this instanceof PC_IGridHolder){
			((PC_IGridHolder)this).removeFromGrid();
		}
	}
	
}
