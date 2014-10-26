package powercraft.api.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercraft.api.PC_ClientRegistry;
import powercraft.api.PC_Direction;
import powercraft.api.PC_IconRegistry;
import powercraft.api.PC_Module;
import powercraft.api.PC_Utils;
import powercraft.api.item.PC_IItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PC_ItemBlock extends ItemBlock implements PC_IItem {

	final static ThreadLocal<EntityPlayer> playerStetting = new ThreadLocal<EntityPlayer>();
	
	public PC_ItemBlock(Block block) {
		super(block);
	}

	@Override
	public CreativeTabs[] getCreativeTabs(){
		if(this.field_150939_a instanceof PC_AbstractBlockBase){
			return ((PC_AbstractBlockBase)this.field_150939_a).getCreativeTabs();
		}
		return super.getCreativeTabs();
	}
	
	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int iside, float hitX, float hitY, float hitZ) {
		Block block = PC_Utils.getBlock(world, x, y, z);
        PC_Direction side = PC_Direction.fromSide(iside);
        int nx = x, ny = y, nz = z;
        if (block == Blocks.snow_layer){
        	side = PC_Direction.UP;
        }else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && !block.isReplaceable(world, x, y, z)){
        	nx += side.offsetX;
        	ny += side.offsetY;
        	nz += side.offsetZ;
        }
        if(itemStack.stackSize==0){
        	return false;
        }else if(!player.canPlayerEdit(nx, ny, nz, iside, itemStack)){
        	return false;
        }else if(ny==255 && this.field_150939_a.getMaterial().isSolid()){
        	return false;
        }
        if(PC_Utils.canPlaceEntityOnSide(world, nx, ny, nz, side, this.field_150939_a, player, itemStack)){
        	int metadata = getMetadata(world, itemStack);
        	metadata = this.field_150939_a.onBlockPlaced(world, nx, ny, nz, iside, hitX, hitY, hitZ, metadata);
        	if (this.field_150939_a instanceof PC_AbstractBlockBase) {
     			metadata = ((PC_AbstractBlockBase) this.field_150939_a).modifiyMetadataPreSet(world, nx, ny, nz, side, itemStack, player, hitX, hitY, hitZ, metadata);
     		}
        	playerStetting.set(player);
        	if (placeBlockAt(itemStack, player, world, nx, ny, nz, iside, hitX, hitY, hitZ, metadata)) {
        		playerStetting.set(null);
        		itemStack.stackSize--;
        		if (this.field_150939_a instanceof PC_AbstractBlockBase) {
        			((PC_AbstractBlockBase) this.field_150939_a).onBlockPostSet(world, nx, ny, nz, side, itemStack, player, hitX, hitY, hitZ, metadata);
        		}
        		world.playSoundEffect(nx+0.5, ny+0.5, nz+0.5, this.field_150939_a.stepSound.func_150496_b(), this.field_150939_a.stepSound.getVolume() + 1 / 2, (float) (this.field_150939_a.stepSound.getPitch() * 0.8));
        	}else{
        		playerStetting.set(null);
        	}
        	return true;
        }
        return false;
	}

	public int getMetadata(World world, ItemStack itemStack) {
		return getMetadata(itemStack);
	}

	public int getMetadata(ItemStack itemStack) {
		return getMetadata(itemStack.getItemDamage());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public boolean func_150936_a(World world, int x, int y, int z, int iside, EntityPlayer player, ItemStack itemStack){
        Block block = PC_Utils.getBlock(world, x, y, z);
        PC_Direction side = PC_Direction.fromSide(iside);
        int nx = x, ny = y, nz = z;
        if (block == Blocks.snow_layer){
        	side = PC_Direction.UP;
        }else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && !block.isReplaceable(world, x, y, z)){
        	nx += side.offsetX;
        	ny += side.offsetY;
        	nz += side.offsetZ;
        }
        return PC_Utils.canPlaceEntityOnSide(world, nx, ny, nz, side, this.field_150939_a, (Entity)null, itemStack);
    }

	@Override
	public void onTick(ItemStack itemStack, World world, IInventory inventory, int slot) {
		//
	}

	@Override
	public float updateDigSpeed(ItemStack itemStack, float speed, int x, int y, int z, EntityPlayer entityPlayer){
		return speed;
	}
	
	@Override
	public int getBurnTime(ItemStack fuel) {
		return 0;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public final void registerIcons(IIconRegister iconRegister) {
		registerIcons(PC_ClientRegistry.getIconRegistry(iconRegister, this));
	}

	public void registerIcons(PC_IconRegistry iconRegistry){
		//
	}

	@Override
	public void construct() {
		//
	}

	@Override
	public PC_Module getModule() {
		return ((PC_AbstractBlockBase)this.field_150939_a).getModule();
	}

	@Override
	public String getRegisterName() {
		return ((PC_AbstractBlockBase)this.field_150939_a).getRegisterName();
	}

	@Override
	public String getTextureFolderName() {
		return ((PC_AbstractBlockBase)this.field_150939_a).getTextureFolderName();
	}

	@Override
	public String[] getOreNames() {
		return ((PC_AbstractBlockBase)this.field_150939_a).getOreNames();
	}

	@Override
	public final void initRecipes() {
		//
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean advancedItemTooltips) {
		((PC_AbstractBlockBase)this.field_150939_a).addInformation(itemStack, player, list, advancedItemTooltips);
	}
	
}
