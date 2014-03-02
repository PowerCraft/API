package powercraft.api.entity;

import net.minecraft.entity.Entity;
import powercraft.api.PC_Module;
import powercraft.api.PC_Utils;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.EntityRegistry;

public abstract class PC_EntityType {

	private final ModContainer module;
	
	@SuppressWarnings("unused")
	private boolean constructed;
	
	private int entityTypeID;
	
	public int trackingRange;
	
	public int updateFrequency;
	
	public boolean sendsVelocityUpdates;
	
	public PC_EntityType(){
		PC_Entities.addEntityType(this);
		this.module = PC_Utils.getActiveMod();
	}
	
	public final PC_Module getModule() {
		return (PC_Module)this.module.getMod();
	}
	
	public abstract Class<? extends Entity> getEntity();
	
	public String getRegisterName() {
		return getEntity().getSimpleName();
	}
	
	@SuppressWarnings("hiding")
	void construct() {
		PC_Module module = getModule();
		this.entityTypeID = EntityRegistry.findGlobalUniqueEntityId();
		EntityRegistry.registerModEntity(getEntity(), module.getName()+":"+getRegisterName(), this.entityTypeID, module, this.trackingRange, this.updateFrequency, this.sendsVelocityUpdates);
		this.constructed = true;
	}
	
}
