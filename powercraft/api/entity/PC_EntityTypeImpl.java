package powercraft.api.entity;

import net.minecraft.entity.Entity;

final class PC_EntityTypeImpl extends PC_EntityType {
	
	private final Class<? extends Entity> entity;
	
	public PC_EntityTypeImpl(Class<? extends Entity> entity){
		this.entity = entity;
	}
	
	@Override
	public Class<? extends Entity> getEntity(){
		return this.entity;
	}
	
}
