package powercraft.api.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import powercraft.api.PC_Api;
import powercraft.api.PC_ClientRegistry;
import powercraft.api.PC_IconRegistry;
import powercraft.api.PC_Module;
import powercraft.api.PC_Utils;
import powercraft.api.inventory.PC_InventoryUtils;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class PC_Item extends Item implements PC_IItem{

	private static final CreativeTabs[] NULLCREATIVTABS = {};
	
	private final ModContainer module;
	private CreativeTabs[] creativeTabs = NULLCREATIVTABS;
	private boolean constructed;
	
	public PC_Item(){
		PC_Items.addItem(this);
		this.module = PC_Utils.getActiveMod();
	}
	
	public final PC_Module getModule() {
		return (PC_Module)this.module.getMod();
	}

	public String getRegisterName() {
		return getClass().getSimpleName();
	}
	
	public String getTextureFolderName() {
		return getClass().getSimpleName().replaceAll("PC.*_(Item)?", "");
	}
	
	@Override
	public Item setCreativeTab(CreativeTabs creativeTab) {
		if(creativeTab==null){
			this.creativeTabs = NULLCREATIVTABS;
		}else{
			if(this.constructed){
				List<CreativeTabs> creativeTabList = new ArrayList<CreativeTabs>();
				creativeTabList.add(creativeTab);
				if(!creativeTabList.contains(getModule().getCreativeTab()))
					creativeTabList.add(getModule().getCreativeTab());
				if(!creativeTabList.contains(PC_Api.INSTANCE.getCreativeTab()))
					creativeTabList.add(PC_Api.INSTANCE.getCreativeTab());
				this.creativeTabs = creativeTabList.toArray(new CreativeTabs[creativeTabList.size()]);
			}else{
				this.creativeTabs = new CreativeTabs[]{creativeTab};
			}
		}
		return this;
	}
	
	@SuppressWarnings("hiding")
	final void construct() {
		PC_Module module = getModule();
		setUnlocalizedName(getRegisterName());
		GameRegistry.registerItem(this, module.getName()+":"+getRegisterName(), module.getModId());
		this.constructed = true;
		if(this.creativeTabs.length>0)
			setCreativeTab(this.creativeTabs[0]);
	}
	
	@Override
	public CreativeTabs[] getCreativeTabs(){
		return this.creativeTabs;
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

	@Override
	public int getBurnTime(ItemStack fuel) {
		return 0;
	}
	
	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int i, boolean currentItem) {
		onTick(itemStack, world, PC_InventoryUtils.getInventoryFrom(entity), i);
	}

	@Override
	public void onTick(ItemStack itemStack, World world, IInventory inventory, int slot) {
		//
	}
	
	@SuppressWarnings({ "static-method", "unused" })
	@SideOnly(Side.CLIENT)
	public boolean handleRenderType(ItemStack itemStack, ItemRenderType type) {
		return false;
	}

	@SuppressWarnings({ "static-method", "unused" })
	@SideOnly(Side.CLIENT)
	public boolean shouldUseRenderHelper(ItemStack itemStack, ItemRenderType type, ItemRendererHelper helper) {
		return false;
	}

	@SuppressWarnings("unused")
	@SideOnly(Side.CLIENT)
	public void renderItem(ItemStack itemStack, ItemRenderType type, Object[] data) {
		//
	}
	
}
