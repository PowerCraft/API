package powercraft.api.block;

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
        	int metadata = getMetadata(itemStack.getItemDamage());
        	metadata = this.field_150939_a.onBlockPlaced(world, nx, ny, nz, iside, hitX, hitY, hitZ, metadata);
        	if (this.field_150939_a instanceof PC_AbstractBlockBase) {
     			metadata = ((PC_AbstractBlockBase) this.field_150939_a).modifiyMetadataPreSet(world, nx, ny, nz, side, itemStack, player, hitX, hitY, hitZ, metadata);
     		}
        	playerStetting.set(player);
        	if (placeBlockAt(itemStack, player, world, nx, ny, nz, iside, hitX, hitY, hitZ, metadata)) {
        		playerStetting.set(null);
        		if (this.field_150939_a instanceof PC_AbstractBlockBase) {
        			((PC_AbstractBlockBase) this.field_150939_a).onBlockPostSet(world, nx, ny, nz, side, itemStack, player, hitX, hitY, hitZ, metadata);
        		}
        		world.playSoundEffect(nx+0.5, ny+0.5, nz+0.5, this.field_150939_a.stepSound.func_150496_b(), this.field_150939_a.stepSound.getVolume() + 1 / 2, (float) (this.field_150939_a.stepSound.getPitch() * 0.8));
        		itemStack.stackSize--;
        	}else{
        		playerStetting.set(null);
        	}
        	return true;
        }
        return false;
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
	public int getBurnTime(ItemStack fuel) {
		return 0;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public final void registerIcons(IIconRegister iconRegister) {
		registerIcons(PC_ClientRegistry.getIconRegistry(iconRegister, this));
	}

	@SuppressWarnings("unused")
	public void registerIcons(PC_IconRegistry iconRegistry){
		//
	}

}
