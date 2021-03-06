package powercraft.api;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;

/**
 * The Chunk Manager for forcing chunks to be loaded all the time
 * @author XOR
 *
 */
public final class PC_ChunkManager implements LoadingCallback {
	/**
	 * The instance
	 */
	private static PC_ChunkManager INSTANCE = new PC_ChunkManager();
	/**
	 * Chunk infos for all the worlds
	 */
	private static WeakHashMap<World, WorldInfo> worldInfos = new WeakHashMap<World, WorldInfo>();

	/**
	 * Info for a World
	 * @author XOR
	 *
	 */
	private static class WorldInfo{
		/**
		 * The World
		 */
		private World world;
		/**
		 * All the Forge chunk loader tickets
		 */
		List<Ticket> tickets = new ArrayList<Ticket>();
		
		WorldInfo(World world) {
			this.world = world;
		}

		private Ticket getTicketForChunk(ChunkCoordIntPair pair){
			String key = pair.toString();
			for(Ticket ticket:this.tickets){
				if(ticket.getModData().hasKey(key)){
					return ticket;
				}
			}
			return null;
		}
		
		void forceChunk(int x, int z) {
			ChunkCoordIntPair pair = new ChunkCoordIntPair(x>>4, z>>4);
			Ticket ticket = getTicketForChunk(pair);
			if(ticket==null){
				ticket = getFreeTicket();
				ForgeChunkManager.forceChunk(ticket, pair);
				ticket.getModData().setTag(pair.toString(), new NBTTagCompound());
			}
			NBTTagCompound nbtTagCompound = ticket.getModData().getCompoundTag(pair.toString());
			nbtTagCompound.setInteger("num", nbtTagCompound.getInteger("num")+1);
		}

		void unforceChunk(int x, int z) {
			ChunkCoordIntPair pair = new ChunkCoordIntPair(x>>4, z>>4);
			Ticket ticket = getTicketForChunk(pair);
			if(ticket!=null){
				NBTTagCompound nbtTagCompound = ticket.getModData().getCompoundTag(pair.toString());
				int num;
				nbtTagCompound.setInteger("num", num = nbtTagCompound.getInteger("num")-1);
				if(num<=0){
					ForgeChunkManager.unforceChunk(ticket, pair);
					ticket.getModData().removeTag(pair.toString());
				}
			}
		}
		
		private Ticket getFreeTicket(){
			for(Ticket ticket:this.tickets){
				if(ticket.getChunkListDepth()<ticket.getMaxChunkListDepth())
					ticket.setChunkListDepth(ticket.getMaxChunkListDepth());
				if(ticket.getChunkList().size()<ticket.getChunkListDepth()){
					return ticket;
				}
			}
			return askForNewTicket();
		}
		
		private Ticket askForNewTicket(){
			List<PC_Module> modules = PC_Modules.getModules();
			for(PC_Module module:modules){
				int ticketCount = ForgeChunkManager.ticketCountAvailableFor(module, this.world);
				if(ticketCount>0){
					Ticket ticket = ForgeChunkManager.requestTicket(module, this.world, Type.NORMAL);
					this.tickets.add(ticket);
					return ticket;
				}
			}
			PC_Logger.severe("No more Chunkloader Tickets :/");
			return null;
		}
		
	}
	
	private PC_ChunkManager(){
		
	}
	
	static void register(){
		List<PC_Module> modules = PC_Modules.getModules();
		for(PC_Module module:modules){
			ForgeChunkManager.setForcedChunkLoadingCallback(module, INSTANCE);
		}
	}
	/**
	 * force a chunk to be loaded, increase the ref count
	 * @param world the World
	 * @param x world x coord
	 * @param z world y coord
	 */
	public static void forceChunk(World world, int x, int z){
		getWorldInfo(world).forceChunk(x, z);
	}
	/**
	 * unforce a chunk to be loaded, decrease the ref count and free the chunk if refs==0
	 * @param world the World
	 * @param x world x coord
	 * @param z world y coord
	 */
	public static void unforceChunk(World world, int x, int z){
		getWorldInfo(world).unforceChunk(x, z);
	}
	
	private static WorldInfo getWorldInfo(World world){
		WorldInfo worldInfo = worldInfos.get(world);
		if(worldInfo==null){
			worldInfos.put(world, worldInfo = new WorldInfo(world));
		}
		return worldInfo;
	}
	
	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world) {
		getWorldInfo(world).tickets.addAll(tickets);
	}
}
