package powercraft.api.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import net.minecraftforge.oredict.OreDictionary;
import powercraft.api.PC_ClientRegistry;
import powercraft.api.PC_IconRegistry;
import powercraft.api.PC_Module;
import powercraft.api.PC_Utils;
import powercraft.api.gres.PC_Gres;
import powercraft.api.gres.PC_IGresGuiOpenHandler;
import powercraft.api.inventory.PC_InventoryUtils;

import com.google.common.collect.Sets;

import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class PC_ItemTool extends ItemTool implements PC_IItem {

	private static final CreativeTabs[] NULLCREATIVTABS = {};
	
	private final ModContainer module;
	private CreativeTabs[] creativeTabs = NULLCREATIVTABS;
	private boolean constructed;
	private Set<? extends Material> properMaterials;
	
	protected PC_ItemTool(float damageVsEntity, ToolMaterial toolMaterial, Object[] proper) {
		super(damageVsEntity, toolMaterial, listOf(proper, Block.class));
		this.properMaterials = listOf(proper, Material.class);
		PC_Items.addItem(this);
		this.module = PC_Utils.getActiveMod();
	}

	private static <T> Set<T> listOf(Object[] oa, Class<T> c){
		List<T> list = new ArrayList<T>();
		for(Object o:oa){
			if(c.isAssignableFrom(o.getClass())){
				list.add(c.cast(o));
			}
		}
		return Sets.newHashSet(list);
	}
	
	@Override
	public float func_150893_a(ItemStack itemstack, Block block){
        return block!=null && this.properMaterials.contains(block.getMaterial()) ? this.efficiencyOnProperMaterial : super.func_150893_a(itemstack, block);
    }
	
	@Override
	public final PC_Module getModule() {
		return (PC_Module)this.module.getMod();
	}

	@Override
	public String getRegisterName() {
		return getClass().getSimpleName();
	}
	
	@Override
	public String getTextureFolderName() {
		return getClass().getSimpleName().replaceAll("PC.*_(Item)?", "");
	}

	@Override
	public String[] getOreNames(){
		return null;
	}
	
	@Override
	public Item setCreativeTab(CreativeTabs creativeTab) {
		if(creativeTab==null){
			this.creativeTabs = NULLCREATIVTABS;
			super.setCreativeTab(null);
		}else{
			if(this.constructed){
				this.creativeTabs = PC_Utils.getCreativeTabsFor(creativeTab, getModule());
				super.setCreativeTab(getModule().getCreativeTab());
			}else{
				this.creativeTabs = new CreativeTabs[]{creativeTab};
			}
		}
		return this;
	}
	
	@Override
	@SuppressWarnings("hiding")
	public final void construct() {
		PC_Module module = getModule();
		setUnlocalizedName(getRegisterName());
		GameRegistry.registerItem(this, getRegisterName(), module.getModId());
		String[] oreNames = getOreNames();
		if(oreNames!=null){
			for(String oreName:oreNames){
				OreDictionary.registerOre(oreName, this);
			}
		}
		this.constructed = true;
		if(this.creativeTabs.length>0)
			setCreativeTab(this.creativeTabs[0]);
	}
	
	@Override
	public void initRecipes(){
		//
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
	
	@Override
	public float updateDigSpeed(ItemStack itemStack, float speed, int x, int y, int z, EntityPlayer entityPlayer){
		return speed;
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

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
		if(this instanceof PC_IGresGuiOpenHandler){
			if(!world.isRemote){
				PC_Gres.openGui(entityPlayer, this);
			}
			return itemStack;
		}
		return super.onItemRightClick(itemStack, world, entityPlayer);
	}
	
}
