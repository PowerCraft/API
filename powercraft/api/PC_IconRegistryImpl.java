package powercraft.api;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
final class PC_IconRegistryImpl implements PC_IconRegistry {

	IIconRegister iconRegister;
	private PC_Module module;
	private String tileName;
	
	PC_IconRegistryImpl(IIconRegister iconRegister, PC_Module module, String tileName) {
		this.iconRegister = iconRegister;
		this.module = module;
		this.tileName = tileName;
	}
	
	@Override
	public IIcon registerIcon(String name) {
		return this.iconRegister.registerIcon(this.module.getName()+":"+this.tileName+"/"+name);
	}
	
}
