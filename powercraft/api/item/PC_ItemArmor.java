package powercraft.api.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;

public class PC_ItemArmor extends PC_Item implements ISpecialArmor {

	public static final int HEAD=0, TORSO=1, LEGS=2, FEET=3;
	
	protected int armorType;
	protected PC_ArmorProperties properties;
	protected String textureName = "armor";
	
	public PC_ItemArmor(int armorType, int priority, double ratio, int max){
		this.armorType = armorType;
		properties = new PC_ArmorProperties(priority, ratio, max);
	}
	
	@Override
	public PC_ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
		return properties;
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		return properties.AbsorbMax;
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
		
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return getModule().getName()+":textures/items/"+getTextureFolderName()+"/"+textureName+".png";
	}

	@Override
	public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
		return this.armorType==armorType;
	}
	
}
