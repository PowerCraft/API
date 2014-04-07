package powercraft.api.item;

import java.util.ArrayList;
import java.util.List;

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
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.oredict.OreDictionary;

public class PC_ItemArmor extends ItemArmor implements PC_IItem, ISpecialArmor {

	public static final int HEAD=0, TORSO=1, LEGS=2, FEET=3;
	
	protected PC_ArmorProperties properties;
	protected String textureName = "armor";
	
	private static final CreativeTabs[] NULLCREATIVTABS = {};
	
	private final ModContainer module;
	private CreativeTabs[] creativeTabs = NULLCREATIVTABS;
	private boolean constructed;
	
	public PC_ItemArmor(int armorType, int priority, double ratio, int max){
		super(ArmorMaterial.IRON, 0, armorType);
		PC_Items.addItem(this);
		this.module = PC_Utils.getActiveMod();
		this.properties = new PC_ArmorProperties(priority, ratio, max);
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
	
	@Override
	public PC_ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
		return this.properties;
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		return this.properties.AbsorbMax;
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
		//
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return getModule().getName()+":textures/items/"+getTextureFolderName()+"/"+this.textureName+".png";
	}

	@SuppressWarnings("hiding")
	@Override
	public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
		return this.armorType==armorType;
	}

	@Override
	public void initRecipes() {
		//
	}
	
}
