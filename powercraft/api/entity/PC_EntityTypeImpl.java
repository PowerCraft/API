package powercraft.api.entity;

import net.minecraft.entity.Entity;

final class PC_EntityTypeImpl<E extends Entity & PC_IEntity> extends PC_EntityType<E> {
	
	private final Class<E> entity;
	
	public PC_EntityTypeImpl(Class<E> entity){
		this.entity = entity;
	}
	
	@Override
	public Class<E> getEntity(){
		return this.entity;
	}
	
}
