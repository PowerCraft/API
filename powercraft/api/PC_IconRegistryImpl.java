package powercraft.api;

import powercraft.api.block.PC_AbstractBlockBase;
import powercraft.api.item.PC_Item;
import powercraft.api.reflect.PC_Security;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class PC_IconRegistryImpl implements PC_IconRegistry {

	private IIconRegister iconRegister;
	private PC_Module module;
	private String namePrefix;
	
	public PC_IconRegistryImpl(IIconRegister iconRegister, PC_AbstractBlockBase block) {
		PC_Security.allowedCaller("PC_IconRegistryImpl(IIconRegister, PC_AbstractBlockBase)", PC_AbstractBlockBase.class);
		this.iconRegister = iconRegister;
		module = block.getModule();
		namePrefix = block.getRegisterName();
	}
	
	public PC_IconRegistryImpl(IIconRegister iconRegister, PC_Item item) {
		PC_Security.allowedCaller("PC_IconRegistryImpl(IIconRegister, PC_Item)", PC_Item.class);
		this.iconRegister = iconRegister;
		module = item.getModule();
		namePrefix = item.getRegisterName();
	}

	@Override
	public IIcon registerIcon(String name) {
		return iconRegister.registerIcon("powercraft-"+module.getName()+"/textures/:"+namePrefix+"_"+name);
	}
	
}
