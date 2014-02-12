package powercraft.api;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import powercraft.api.block.PC_AbstractBlockBase;
import powercraft.api.block.PC_ItemBlock;
import powercraft.api.item.PC_Item;
import powercraft.api.reflect.PC_Security;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class PC_IconRegistryImpl implements PC_IconRegistry {

	private IIconRegister iconRegister;
	private PC_Module module;
	private String tile;
	private String tileName;
	
	public PC_IconRegistryImpl(IIconRegister iconRegister, PC_AbstractBlockBase block) {
		PC_Security.allowedCaller("PC_IconRegistryImpl(IIconRegister, PC_AbstractBlockBase)", PC_AbstractBlockBase.class);
		this.iconRegister = iconRegister;
		module = block.getModule();
		tile = "bolcks";
		tileName = block.getTextureFolderName();
		System.out.println("tileName:"+tileName);
	}
	
	public PC_IconRegistryImpl(IIconRegister iconRegister, PC_Item item) {
		PC_Security.allowedCaller("PC_IconRegistryImpl(IIconRegister, PC_Item)", PC_Item.class);
		this.iconRegister = iconRegister;
		module = item.getModule();
		tile = "items";
		tileName = item.getTextureFolderName();
	}

	public PC_IconRegistryImpl(IIconRegister iconRegister, PC_ItemBlock itemBlock) {
		PC_Security.allowedCaller("PC_IconRegistryImpl(IIconRegister, PC_ItemBlock)", PC_ItemBlock.class);
		this.iconRegister = iconRegister;
		PC_AbstractBlockBase block = (PC_AbstractBlockBase)itemBlock.field_150939_a;
		module = block.getModule();
		tile = "items";
		tileName = block.getTextureFolderName();
	}

	@Override
	public IIcon registerIcon(String name) {
		return iconRegister.registerIcon("powercraft-"+module.getName()+"/textures/"+tile+"/"+tileName+":"+name);
	}
	
}
