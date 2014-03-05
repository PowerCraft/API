package powercraft.api.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import powercraft.api.PC_Api;
import powercraft.api.PC_ImmutableList;
import powercraft.api.PC_Logger;
import powercraft.api.reflect.PC_Security;

public class PC_Entities {

	private static boolean done;
	private static List<PC_EntityType<?>> entities = new ArrayList<PC_EntityType<?>>();
	private static List<PC_EntityType<?>> immutableEntities = new PC_ImmutableList<PC_EntityType<?>>(entities);
	
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
			PC_Logger.info("ADD: %s", entityType);
			entities.add(entityType);
		}
	}
	
	public static List<PC_EntityType<?>> getEntities(){
		return immutableEntities;
	}

	public static void construct(){
		PC_Security.allowedCaller("PC_Entities.construct()", PC_Api.class);
		if(!done){
			done = true;
			for(PC_EntityType<?> entity:entities){
				PC_Logger.info("CONSTRUCT: %s", entity);
				entity.construct();
			}
		}
	}
	
	private PC_Entities(){
		throw new InstantiationError();
	}
	
}
