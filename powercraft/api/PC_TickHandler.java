package powercraft.api;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import powercraft.api.reflect.PC_Security;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;

public class PC_TickHandler {

	private static final PC_TickHandler INSTANCE = new PC_TickHandler();
	
	private static int count = 0;
	private static final List<PC_IBaseTickHandler> toAdd = new ArrayList<PC_IBaseTickHandler>();
	private static final List<PC_IBaseTickHandler> toDelete = new ArrayList<PC_IBaseTickHandler>();
	private static final List<PC_ITickHandler> tickHandlers = new ArrayList<PC_ITickHandler>();
	private static final List<PC_IWorldTickHandler> worldTickHandlers = new ArrayList<PC_IWorldTickHandler>();
	private static final List<PC_IPlayerTickHandler> playerTickHandlers = new ArrayList<PC_IPlayerTickHandler>();
	private static final List<PC_IRenderTickHandler> renderTickHandlers = new ArrayList<PC_IRenderTickHandler>();

	static void register(){
		PC_Security.allowedCaller("PC_TickHandler.register()", PC_Api.class);
		FMLCommonHandler.instance().bus().register(INSTANCE);
	}
	
	private PC_TickHandler(){}
	
	public static void registerTickHandler(PC_IBaseTickHandler tickHandler){
		if(count==0){
			if(tickHandler instanceof PC_ITickHandler){
				tickHandlers.add((PC_ITickHandler) tickHandler);
			}
			if(tickHandler instanceof PC_IWorldTickHandler){
				worldTickHandlers.add((PC_IWorldTickHandler) tickHandler);
			}
			if(tickHandler instanceof PC_IPlayerTickHandler){
				playerTickHandlers.add((PC_IPlayerTickHandler) tickHandler);
			}
			if(tickHandler instanceof PC_IRenderTickHandler){
				renderTickHandlers.add((PC_IRenderTickHandler) tickHandler);
			}
		}else{
			toAdd.add(tickHandler);
		}
	}
	
	public static void removeTickHander(PC_IBaseTickHandler tickHandler) {
		if(count==0){
			tickHandlers.remove(tickHandler);
			worldTickHandlers.remove(tickHandler);
			playerTickHandlers.remove(tickHandler);
			renderTickHandlers.remove(tickHandler);
		}else{
			toDelete.add(tickHandler);
		}
	}
	
	private static void startIteration(){
		synchronized(INSTANCE){
			count++;
		}
	}
	
	private static void endIteration(){
		synchronized(INSTANCE){
			count--;
			if(count==0){
				for(PC_IBaseTickHandler tickHandler:toAdd){
					registerTickHandler(tickHandler);
				}
				toAdd.clear();
				for(PC_IBaseTickHandler tickHandler:toDelete){
					removeTickHander(tickHandler);
				}
				toDelete.clear();
			}
		}
	}
	
	@SuppressWarnings("static-method")
	@SubscribeEvent
	public void tickEvent(TickEvent event){
		switch(event.phase){
		case END:
			onEndTickEvent(event);
			break;
		case START:
			onStartTickEvent(event);
			break;
		default:
			break;
		}
	}
	
	private static void onStartTickEvent(TickEvent event){
		PC_Side side = PC_Side.from(event.side);
		startIteration();
		switch(event.type){
		case CLIENT:
		case SERVER:
			for(PC_ITickHandler tickHandler:tickHandlers){
				tickHandler.onStartTick(side);
			}
			break;
		case PLAYER:
			EntityPlayer player = ((PlayerTickEvent)event).player;
			for(PC_IPlayerTickHandler playerTickHandler:playerTickHandlers){
				playerTickHandler.onStartTick(side, player);
			}
			break;
		case RENDER:
			float renderTickTime = ((RenderTickEvent)event).renderTickTime;
			for(PC_IRenderTickHandler renderTickHandler:renderTickHandlers){
				renderTickHandler.onStartTick(renderTickTime);
			}
			break;
		case WORLD:
			World world = ((WorldTickEvent)event).world;
			for(PC_IWorldTickHandler worldTickHandler:worldTickHandlers){
				worldTickHandler.onStartTick(side, world);
			}
			break;
		default:
			break;
		}
		endIteration();
	}
	
	private static void onEndTickEvent(TickEvent event){
		PC_Side side = PC_Side.from(event.side);
		startIteration();
		switch(event.type){
		case CLIENT:
		case SERVER:
			for(PC_ITickHandler tickHandler:tickHandlers){
				tickHandler.onEndTick(side);
			}
			break;
		case PLAYER:
			EntityPlayer player = ((PlayerTickEvent)event).player;
			for(PC_IPlayerTickHandler playerTickHandler:playerTickHandlers){
				playerTickHandler.onEndTick(side, player);
			}
			break;
		case RENDER:
			float renderTickTime = ((RenderTickEvent)event).renderTickTime;
			for(PC_IRenderTickHandler renderTickHandler:renderTickHandlers){
				renderTickHandler.onEndTick(renderTickTime);
			}
			break;
		case WORLD:
			World world = ((WorldTickEvent)event).world;
			for(PC_IWorldTickHandler worldTickHandler:worldTickHandlers){
				worldTickHandler.onEndTick(side, world);
			}
			break;
		default:
			break;
		}
		endIteration();
	}
	
	static interface PC_IBaseTickHandler{
		//
	}
	
	public static interface PC_ITickHandler extends PC_IBaseTickHandler{
		
		public void onStartTick(PC_Side side);
		
		public void onEndTick(PC_Side side);
		
	}

	public static interface PC_IWorldTickHandler extends PC_IBaseTickHandler{
			
		public void onStartTick(PC_Side side, World world);
			
		public void onEndTick(PC_Side side, World world);
			
	}
	
	public static interface PC_IPlayerTickHandler extends PC_IBaseTickHandler{
		
		public void onStartTick(PC_Side side, EntityPlayer player);
		
		public void onEndTick(PC_Side side, EntityPlayer player);
		
	}
	
	public static interface PC_IRenderTickHandler extends PC_IBaseTickHandler{
		
		public void onStartTick(float renderTickTime);
		
		public void onEndTick(float renderTickTime);
		
	}
	
}
