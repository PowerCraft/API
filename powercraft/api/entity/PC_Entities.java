package powercraft.api.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.Entity;
import powercraft.api.PC_Api;
import powercraft.api.PC_ImmutableList;
import powercraft.api.PC_Logger;
import powercraft.api.PC_Utils;
import powercraft.api.reflect.PC_Security;

public final class PC_Entities {

	private static boolean done;
	private static List<PC_EntityType<?>> entities = new ArrayList<PC_EntityType<?>>();
	private static List<PC_EntityType<?>> immutableEntities = new PC_ImmutableList<PC_EntityType<?>>(entities);
	private static HashMap<Class<?>, PC_EntityType<?>>entitiesByClass = new HashMap<Class<?>, PC_EntityType<?>>();
	
	public static <E extends Entity & PC_IEntity>void register(Class<E> entity, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates){
		PC_EntityTypeImpl<E> type = new PC_EntityTypeImpl<E>(entity);
		type.trackingRange = trackingRange;
		type.updateFrequency = updateFrequency;
		type.sendsVelocityUpdates = sendsVelocityUpdates;
	}

	static void addEntityType(PC_EntityType<?> entityType) {
		if(done){
			PC_Logger.severe("A entity want to register while startup is done");
		}else{
			PC_Logger.info("Entity-ADD: %s", entityType);
			entities.add(entityType);
		}
	}
	
	public static List<PC_EntityType<?>> getEntities(){
		return immutableEntities;
	}

	@SuppressWarnings("unchecked")
	public static <E extends Entity & PC_IEntity> PC_EntityType<E> getEntityType(E entity){
		return (PC_EntityType<E>) entitiesByClass.get(entity.getClass());
	}
	
	public static void construct(){
		PC_Security.allowedCaller("PC_Entities.construct()", PC_Api.class);
		if(!done){
			done = true;
			for(PC_EntityType<?> entity:entities){
				PC_Logger.info("CONSTRUCT: %s", entity);
				entity.construct();
				entitiesByClass.put(entity.getEntity(), entity);
			}
		}
	}
	
	private PC_Entities(){
		PC_Utils.staticClassConstructor();
	}
	
}
