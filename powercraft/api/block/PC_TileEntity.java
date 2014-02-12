package powercraft.api.block;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.IPlantable;
import powercraft.api.PC_3DRotation;
import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_Direction;
import powercraft.api.PC_NBTTagHandler;
import powercraft.api.PC_Utils;
import powercraft.api.block.PC_Field.Flag;
import powercraft.api.gres.PC_Gres;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.PC_IGresGuiOpenHandler;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.network.packet.PC_PacketPasswordRequest;
import powercraft.api.reflect.PC_Processor;
import powercraft.api.reflect.PC_Reflection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PC_TileEntity extends TileEntity {

	private static Random sessionRand = new Random();
	
	private String owner;
	private String password;
	
	private long session;
	
	public boolean isClient() {

		if (this.worldObj == null) return PC_Utils.isClient();
		return this.worldObj.isRemote;
	}
	
	public void onBreak() {
		
	}

	public float getHardness() {
		return Float.NaN;
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick() {
		
	}

	public void onNeighborBlockChange(Block neighbor) {
		
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
			
		}
		
		return false;
	}

	public int getWeakRedstonePower(PC_Direction side) {
		return 0;
	}

	public int getStrongRedstonePower(PC_Direction side) {
		return 0;
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

	public boolean canConnectRedstone(PC_Direction side) {
		return getBlockType().canProvidePower();
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
					value = PC_NBTTagHandler.loadFromNBT(nbtTagCompound, name, type);
					results.put(Result.SET, value);
				}
			}
			
		});
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
					PC_NBTTagHandler.saveToNBT(nbtTagCompound, name, value);
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
		onLoadedFromNBT();
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
	
	public final boolean guiOpenPasswordReply(EntityPlayer player, String password) {
		password = PC_Utils.getMD5(password);
		if(checkPassword(player, password)){
			PC_Gres.openGui(player, this);
			return true;
		}
		return false;
	}
	
	public long getSession(){
		return session;
	}
	
	public void setSession(long session){
		this.session = session;
	}

	public long getNewSession() {
		session = sessionRand.nextLong();
		return session;
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
	
}
