package powercraft.api;

import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PC_EventHandler {

	@SuppressWarnings("static-method")
	@SubscribeEvent
	public void worldLoadEvent(WorldEvent.Load load){
		if(!load.world.isRemote)
			load.world.addWorldAccess(new PC_WorldAccess(load.world));
	}
	
}
