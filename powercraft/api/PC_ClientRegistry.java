package powercraft.api;

import javax.management.InstanceAlreadyExistsException;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import powercraft.api.block.PC_AbstractBlockBase;
import powercraft.api.block.PC_ItemBlock;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.entity.PC_EntityType;
import powercraft.api.entity.PC_IEntity;
import powercraft.api.item.PC_IItem;
import powercraft.api.item.PC_Item;
import powercraft.api.item.PC_ItemArmor;
import powercraft.api.multiblock.PC_MultiblockItem;
import powercraft.api.multiblock.PC_Multiblocks;
import powercraft.api.reflect.PC_Security;
import powercraft.api.renderer.PC_EntityRenderer;
import powercraft.api.renderer.PC_ITileEntityRenderer;
import powercraft.api.renderer.PC_TileEntitySpecialRenderer;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class PC_ClientRegistry extends PC_Registry {

	private PC_TileEntitySpecialRenderer specialRenderer;
	
	public PC_ClientRegistry()  throws InstanceAlreadyExistsException{
		this.specialRenderer = new PC_TileEntitySpecialRenderer();
	}
	
	@Override
	void iRegisterTileEntity(Class<? extends PC_TileEntity> tileEntityClass){
		if(PC_ITileEntityRenderer.class.isAssignableFrom(tileEntityClass)){
			ClientRegistry.registerTileEntity(tileEntityClass, tileEntityClass.getName(), this.specialRenderer);
		}else{
			GameRegistry.registerTileEntity(tileEntityClass, tileEntityClass.getName());
		}
	}
	
	public static PC_IconRegistry getIconRegistry(IIconRegister iconRegister, PC_AbstractBlockBase block) {
		PC_Security.allowedCaller("PC_IconRegistryImpl(IIconRegister, PC_AbstractBlockBase)", PC_AbstractBlockBase.class);
		return new PC_IconRegistryImpl(iconRegister, block.getModule(), block.getTextureFolderName());
	}
	
	public static PC_IconRegistry getIconRegistry(IIconRegister iconRegister, PC_IItem item) {
		PC_Security.allowedCaller("PC_IconRegistryImpl(IIconRegister, PC_IItem)", PC_Item.class, PC_ItemArmor.class);
		return new PC_IconRegistryImpl(iconRegister, item.getModule(), item.getTextureFolderName());
	}

	public static PC_IconRegistry getIconRegistry(IIconRegister iconRegister, PC_ItemBlock itemBlock) {
		PC_Security.allowedCaller("PC_IconRegistryImpl(IIconRegister, PC_ItemBlock)", PC_ItemBlock.class);
		PC_AbstractBlockBase block = (PC_AbstractBlockBase)itemBlock.field_150939_a;
		return new PC_IconRegistryImpl(iconRegister, block.getModule(), block.getTextureFolderName());
	}

	public static PC_IconRegistry getIconRegistry(PC_IconRegistry iconRegister, PC_MultiblockItem multiblockItem) {
		PC_Security.allowedCaller("PC_IconRegistryImpl(PC_IconRegistry, PC_MultiblockItem)", PC_Multiblocks.class);
		return new PC_IconRegistryImpl(((PC_IconRegistryImpl)iconRegister).iconRegister, multiblockItem.getModule(), multiblockItem.getTextureFolderName());
	}

	public static PC_IconRegistry getIconRegistry(IIconRegister iconRegister, PC_EntityType<?> type) {
		PC_Security.allowedCaller("PC_IconRegistryImpl(PC_IconRegistry, PC_EntityType)", PC_EntityRenderer.class);
		return new PC_IconRegistryImpl(iconRegister, type.getModule(), type.getTextureFolderName());
	}
	
	@Override
	<E extends Entity & PC_IEntity>void iRegisterEntity(Class<? extends Entity> entity, String name, int entityTypeID, PC_Module module, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, PC_EntityType<E> type){
		super.iRegisterEntity(entity, name, entityTypeID, module, trackingRange, updateFrequency, sendsVelocityUpdates, type);
		RenderingRegistry.registerEntityRenderingHandler(entity, new PC_EntityRenderer<E>(type));
	}
	
	@Override
	void iPlaySound(double x, double y, double z, String sound, float soundVolume, float pitch) {
		World world = PC_ClientUtils.mc().theWorld;
		if (world != null && PC_ClientUtils.mc().renderViewEntity != null) {
			world.playSound(x, y, z, sound, soundVolume, pitch, false);
		}
	}
	
}
