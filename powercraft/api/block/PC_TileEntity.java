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
		
		private final int dimension;
		private final int x;
		private final int y;
		private final int z;
		private final long session;
		
		private Session(PC_TileEntity tileEntity){
			session = sessionRand.nextLong();
			dimension = tileEntity.worldObj.getWorldInfo().getVanillaDimension();
			x = tileEntity.xCoord;
			y = tileEntity.yCoord;
			z = tileEntity.zCoord;
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
			((PC_IGridHolder)this).removeFormGrid();
		}
	}

	public float getHardness() {
		return Float.NaN;
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick() {
		
	}

	public void onNeighborBlockChange(Block neighbor) {
		int newRedstoneValue = PC_Utils.getRedstoneValue(worldObj, xCoord, yCoord, zCoord);
		updateRedstone(newRedstoneValue);
	}
	
	protected void updateRedstone(int newRedstoneValue){
		if(newRedstoneValue!=redstoneValue){
			onRedstoneValueChanging(newRedstoneValue, redstoneValue);
			redstoneValue = newRedstoneValue;
		}
	}

	protected void onRedstoneValueChanging(int newValue, int oldValue){
		if(workWhen==null)
			return;
		switch(workWhen){
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
		
	}
	
	protected void doWork(){
		
	}
	
	protected void stopWorking(){
		
	}
	
	public boolean isWorking(){
		return workWhen == PC_RedstoneWorkType.EVER || (redstoneValue==0 && workWhen == PC_RedstoneWorkType.ON_OFF) || (redstoneValue!=0 && workWhen == PC_RedstoneWorkType.ON_ON);
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
		if (!isClient() && sync) {
			PC_PacketHandler.sendToAllAround(getSyncPacket(), worldObj.getWorldInfo().getVanillaDimension(), xCoord, yCoord, zCoord, 32);
			this.sync = false;
		}
	}

	public void onTick(){
		
	}

	public void sync(){
		if (isClient()) return;
		sync = true;
		markDirty();
	}
	
	public void notifyNeighbors() {
		if(worldObj!=null)
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
	}
	
	public void renderUpdate() {
		if (this.worldObj != null) this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}


	public void lightUpdate() {
		if (this.worldObj != null) this.worldObj.func_147451_t(xCoord, yCoord, zCoord);
	}
	
	public float getPlayerRelativeHardness(EntityPlayer player) {
		return Float.NaN;
	}

	public void onEntityWalking(Entity entity) {
		
	}

	public void onBlockClicked(EntityPlayer player) {
		
	}

	public void velocityToAddToEntity(Entity entity, Vec3 velocity) {
		
	}

	public int getColorMultiplier() {
		return 16777215;
	}

	public void onEntityCollidedWithBlock(Entity entity) {
		
	}

	public void onFallenUpon(Entity entity, float fallDistance) {
		
		
	}

	public int getDamageValue() {
		return getBlockType().damageDropped(blockMetadata);
	}

	public void fillWithRain() {
		
	}

	public int getLightValue() {
		return -1;
	}

	public boolean isLadder(EntityLivingBase entity) {
		return false;
	}

	public boolean isBurning() {
		return false;
	}

	public ArrayList<ItemStack> getDrops(int fortune) {
		return null;
	}
	
	public boolean canCreatureSpawn(EnumCreatureType type) {
		return false;
	}

	public boolean canSustainLeaves() {
		return false;
	}

	public float getExplosionResistance(Entity entity, double explosionX, double explosionY, double explosionZ) {
		return Float.NaN;
	}

	public ItemStack getPickBlock(MovingObjectPosition target) {
		return null;
	}

	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(EffectRenderer effectRenderer) {
		return false;
	}

	public void onPlantGrow(int sourceX, int sourceY, int sourceZ) {
		
	}

	public boolean isFertile() {
		return false;
	}

	public int getLightOpacity() {
		return -1;
	}

	public boolean canEntityDestroy(Entity entity) {
		return true;
	}

	public boolean isBeaconBase(int beaconX, int beaconY, int beaconZ) {
		return false;
	}

	public float getEnchantPowerBonus() {
		return 0;
	}

	public void onNeighborTEChange(int tileX, int tileY, int tileZ) {
		
	}

	public void onBlockPostSet(PC_Direction side, ItemStack stack, EntityPlayer player, float hitX, float hitY, float hitZ) {
		
	}

	public PC_3DRotation get3DRotation() {
		return null;
	}

	public IIcon getIcon(PC_Direction side) {
		return null;
	}

	public List<AxisAlignedBB> getCollisionBoundingBoxes(Entity entity) {
		return null;
	}

	public AxisAlignedBB getMainCollisionBoundingBox() {
		return null;
	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox() {
		return null;
	}

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

	public int getComparatorInput(PC_Direction side) {
		return 0;
	}

	public boolean isSideSolid(PC_Direction side) {
		return getBlockType().isNormalCube(worldObj, xCoord, yCoord, zCoord);
	}

	public int getFlammability(PC_Direction side) {
		return 0;
	}

	public boolean isFlammable(PC_Direction side) {
		return false;
	}

	public int getFireSpreadSpeed(PC_Direction side) {
		return 0;
	}

	public boolean isFireSource(PC_Direction side) {
		return false;
	}

	public boolean canSilkHarvest(EntityPlayer player) {
		return false;
	}

	public boolean canSustainPlant(PC_Direction side, IPlantable plantable) {
		return false;
	}

	public boolean recolourBlock(PC_Direction side, int colour) {
		return false;
	}

	public boolean shouldCheckWeakPower(PC_Direction side) {
		return getBlockType().isNormalCube();
	}

	public boolean getWeakChanges() {
		return false;
	}

	public boolean canRotate() {
		return false;
	}

	public boolean set3DRotation(PC_3DRotation rotation) {
		return false;
	}

	public void openContainer(Container container) {
		
	}

	public void closeContainer(Container container) {
		
	}

	public void sendProgressBarUpdates() {
		
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
		return owner;
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
					value = PC_NBTTagHandler.loadFromNBT(nbtTagCompound, name, type, flag);
					results.put(Result.SET, value);
				}
			}
			
		});
		onLoadedFromNBT();
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
			owner = nbtTagCompound.getString("owner");
			if(nbtTagCompound.hasKey("password")){
				password = nbtTagCompound.getString("password");
			}
		}
		super.readFromNBT(nbtTagCompound);
	}

	@Override
	public final void writeToNBT(NBTTagCompound nbtTagCompound) {
		writeToNBT(nbtTagCompound, Flag.SAVE);
		if(owner!=null){
			nbtTagCompound.setString("owner", owner);
			if(password!=null){
				nbtTagCompound.setString("password", password);
			}
		}
		super.writeToNBT(nbtTagCompound);
	}
	
	public void onLoadedFromNBT(){
		
	}
	
	public final boolean canDoWithoutPassword(EntityPlayer player){
		return owner==null || owner.equals(player.getGameProfile().getName()) || PC_Utils.isOP(player);
	}

	public final boolean canDoWithPassword(EntityPlayer player){
		return owner==null || owner.equals(player.getGameProfile().getName()) || password!=null;
	}
	
	public final boolean checkPassword(EntityPlayer player, String password){
		return canDoWithoutPassword(player) || (this.password!=null && this.password.equals(password));
	}
	
	public final boolean setPassword(EntityPlayer player, String newPassword){
		if(canDoWithoutPassword(player)){
			if(newPassword==null){
				password = null;
			}else{
				password = PC_Utils.getMD5(newPassword);
			}
			owner = PC_Utils.getUsername(player);
			return true;
		}
		return false;
	}
	
	public final boolean guiOpenPasswordReply(EntityPlayer player, String password) {
		password = PC_Utils.getMD5(password);
		if(checkPassword(player, password)){
			PC_Gres.openGui(player, this);
			return true;
		}
		return false;
	}
	
	public long getSession(){
		if(isClient())
			return session;
		return 0;
	}
	
	public void setSession(long session){
		if(isClient())
			this.session = session;
	}

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
		sync = false;
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		makeSync(nbtTagCompound);
		return new PC_PacketTileEntitySync(this, nbtTagCompound);
	}

	public final void makeSync(NBTTagCompound nbtTagCompound) {
		writeToNBT(nbtTagCompound, Flag.SYNC);
	}
	
	public final void applySync(NBTTagCompound nbtTagCompound) {
		if(worldObj.isRemote){
			readFromNBT(nbtTagCompound, Flag.SYNC);
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean renderWorldBlock(int modelId, RenderBlocks renderer) {
		return PC_Renderer.renderBlockInWorld(worldObj, xCoord, yCoord, zCoord, getBlockType(), modelId, renderer);
	}

	public boolean canRedstoneConnect(PC_Direction side, int faceSide) {
		return getBlockType().canProvidePower();
	}

	public int getRedstonePowerValue(PC_Direction side, int faceSide) {
		return 0;
	}

	public void setRedstonePowerValue(PC_Direction side, int faceSide, int value) {
		updateRedstone(value);
	}

	@SideOnly(Side.CLIENT)
	public final void onClientMessageCheck(EntityPlayer player, NBTTagCompound nbtTagCompound, long session, boolean intern) {
		Session pSession = sessions.get(player);
		if(pSession!=null && pSession.dimension == worldObj.getWorldInfo().getVanillaDimension() && pSession.x == xCoord && pSession.y == yCoord && pSession.z == zCoord && pSession.session == session){
			if(intern){
				onInternMessage(player, nbtTagCompound);
			}else{
				onMessage(player, nbtTagCompound);
			}
		}else{
			PC_Logger.warning("Player %s tries to send not signated messaged", session);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void onClientMessage(EntityPlayer player, NBTTagCompound nbtTagCompound) {
		onMessage(player, nbtTagCompound);
	}
	
	public void onInternMessage(EntityPlayer player, NBTTagCompound nbtTagCompound) {
		
	}
	
	public void onMessage(EntityPlayer player, NBTTagCompound nbtTagCompound) {
		
	}
	
	public void sendMessage(NBTTagCompound nbtTagCompound){
		if(isClient()){
			PC_PacketHandler.sendToServer(new PC_PacketTileEntityMessageCTS(this, nbtTagCompound, session));
		}else{
			PC_PacketHandler.sendToAllAround(new PC_PacketTileEntityMessageSTC(this, nbtTagCompound), worldObj.getWorldInfo().getVanillaDimension(), xCoord, yCoord, zCoord, 32);
		}
	}
	
	public void sendInternMessage(NBTTagCompound nbtTagCompound){
		if(isClient()){
			PC_PacketHandler.sendToServer(new PC_PacketTileEntityMessageIntCTS(this, nbtTagCompound, session));
		}
	}

	public void setRedstoneWorkType(PC_RedstoneWorkType rwt) {
		// TODO Auto-generated method stub
		
	}

	public PC_RedstoneWorkType[] getAllowedRedstoneWorkTypes() {
		return new PC_RedstoneWorkType[]{null};
	}
	
	public PC_RedstoneWorkType getRedstoneWorkType() {
		return workWhen;
	}

	public void onAdded(EntityPlayer player) {
		if(this instanceof PC_IGridHolder){
			((PC_IGridHolder)this).getGridIfNull();
		}
	}

	@Override
	public void onChunkUnload() {
		if(this instanceof PC_IGridHolder){
			((PC_IGridHolder)this).removeFormGrid();
		}
	}
	
}
