package powercraft.api;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PC_CreativeTab extends CreativeTabs {

	private PC_Module module;
	
	PC_CreativeTab(String name, PC_Module module) {
		super(name);
		this.module = module;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack(){
		return module.getCreativeTabItemStack();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {
		return null;
	}

}
