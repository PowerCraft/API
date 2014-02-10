package powercraft.api.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.IPlantable;
import powercraft.api.PC_3DRotation;
import powercraft.api.PC_Direction;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PC_TileEntity extends TileEntity {

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

}
