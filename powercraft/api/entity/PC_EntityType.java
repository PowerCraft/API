package powercraft.api.entity;

import net.minecraft.entity.Entity;
import powercraft.api.PC_IconRegistry;
import powercraft.api.PC_Module;
import powercraft.api.PC_Registry;
import powercraft.api.PC_Utils;
import powercraft.api.renderer.PC_EntityRenderer;
import powercraft.api.renderer.model.PC_Model;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class PC_EntityType<E extends Entity & PC_IEntity> {

	private final ModContainer module;

	private boolean constructed;
	
	private int entityTypeID;
	
	public int trackingRange;
	
	public int updateFrequency;
	
	public boolean sendsVelocityUpdates;
	
	protected PC_Model model;
	
	public PC_EntityType(){
		PC_Entities.addEntityType(this);
		this.module = PC_Utils.getActiveMod();
	}
	
	public final PC_Module getModule() {
		return (PC_Module)this.module.getMod();
	}
	
	public abstract Class<E> getEntity();
	
	public String getRegisterName() {
		return getEntity().getSimpleName();
	}
	
	void construct() {
		PC_Module module = getModule();
		this.entityTypeID = EntityRegistry.findGlobalUniqueEntityId();
		PC_Registry.registerEntity(getEntity(), module.getName()+":"+getRegisterName(), this.entityTypeID, module, this.trackingRange, this.updateFrequency, this.sendsVelocityUpdates, this);
		if(PC_Utils.isClient())
			this.model = PC_Model.loadModel(module, getTextureFolderName(), "ms3d");
		this.constructed = true;
	}

	@SuppressWarnings("static-method")
	public boolean isStaticEntity() {
		return false;
	}

	public String getTextureFolderName() {
		return getEntity().getSimpleName().replaceAll("PC.*_(Entity)?", "");
	}

	public void registerIcons(PC_IconRegistry iconRegistry) {
		//
	}

	@SideOnly(Side.CLIENT)
	public String getEntityTextureName(PC_EntityRenderer<E> renderer, E entity) {
		return entity.getEntityTextureName(renderer);
	}
	
	@SideOnly(Side.CLIENT)
	public void doRender(PC_EntityRenderer<E> renderer, E entity, double x, double y, double z, float rotYaw, float timeStamp) {
		entity.doRender(renderer, x, y, z, rotYaw, timeStamp);
	}
	
	public PC_Model getModel(){
		return this.model;
	}
	
}
