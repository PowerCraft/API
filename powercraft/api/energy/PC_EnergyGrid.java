package powercraft.api.energy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import powercraft.api.PC_Api;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Side;
import powercraft.api.PC_TickHandler;
import powercraft.api.PC_TickHandler.PC_IWorldTickHandler;
import powercraft.api.PC_Utils;
import powercraft.api.grid.PC_Grid;
import powercraft.api.multiblock.PC_MultiblockIndex;
import powercraft.api.multiblock.PC_MultiblockObject;
import powercraft.api.multiblock.PC_TileEntityMultiblock;
import powercraft.api.reflect.PC_Security;

public class PC_EnergyGrid extends PC_Grid<PC_EnergyGrid, PC_IEnergyGridTile, PC_EnergyNode<?>, PC_EnergyEdge> {

	private static boolean isEnergyModulePresent;
	private static List<PC_EnergyGrid> grids = new ArrayList<PC_EnergyGrid>();
	private static Ticker ticker;
	
	public static void register(){
		PC_Security.allowedCaller("PC_EnergyGrid.register", PC_Api.class);
		if(ticker==null){
			ticker = new Ticker();
			PC_TickHandler.registerTickHandler(ticker);
		}
	}
	
	private static class Ticker implements PC_IWorldTickHandler{

		@Override
		public void onStartTick(PC_Side side, World world) {
			for(PC_EnergyGrid grid:grids){
				grid.doEnergyTick();
			}
		}

		@Override
		public void onEndTick(PC_Side side, World world) {
			
		}
		
	}
	
	public static void setEnergyModulePresent(){
		PC_Security.allowedCaller("PC_EnergyGrid.setEnergyModulePresent", "powercraft.energy.PCeg_Energy");
		isEnergyModulePresent = true;
	}
	
	public static PC_IEnergyGridTile getGridTile(World world, int x, int y, int z, PC_Direction side){
		TileEntity tileEntity = PC_Utils.getTileEntity(world, x, y, z);
		if(tileEntity instanceof PC_IEnergyGridHolder){
			return ((PC_IEnergyGridHolder)tileEntity).getTile(side);
		}else if(tileEntity instanceof PC_IEnergyGridTile){
			return (PC_IEnergyGridTile)tileEntity;
		}else if(tileEntity instanceof PC_TileEntityMultiblock){
			PC_MultiblockObject obj = ((PC_TileEntityMultiblock)tileEntity).getTile(PC_MultiblockIndex.CENTER);
			if(obj instanceof PC_IEnergyGridHolder){
				return ((PC_IEnergyGridHolder)obj).getTile(side);
			}else if(obj instanceof PC_IEnergyGridTile){
				return (PC_IEnergyGridTile)obj;
			}
		}
		return null;
	}
	
	public static boolean hasGrid(World world, int x, int y, int z, PC_Direction side){
		TileEntity tileEntity = PC_Utils.getTileEntity(world, x, y, z);
		if(tileEntity instanceof PC_IEnergyGridHolder){
			return ((PC_IEnergyGridHolder)tileEntity).getTile(side)!=null;
		}else if(tileEntity instanceof PC_IEnergyGridTile){
			return true;
		}
		return false;
	}
	
	public PC_EnergyGrid(){
		grids.add(this);
	}
	
	@Override
	protected void destroy(){
		grids.remove(this);
	}
	
	public void doEnergyTick(){
		if(isEnergyModulePresent){
			PC_EnergyInfo info = new PC_EnergyInfo();
			List<PC_EnergyNodeBuffer> buffer = new ArrayList<PC_EnergyNodeBuffer>();
			for(PC_EnergyNode<?> node:nodes){
				node.onTickStart();
				node.addToInfo(info);
				if(node instanceof PC_EnergyNodeBuffer){
					buffer.add((PC_EnergyNodeBuffer) node);
				}
			}
			float energy = 0;
			for(PC_EnergyNode<?> node:nodes){
				energy += node.takeEnergy();
			}
			float p = energy/info.energyRequested;
			if(p>1)
				p=1;
			for(PC_EnergyNode<?> node:nodes){
				energy = node.useEnergy(energy, p);
			}
			if(energy>0 && !buffer.isEmpty()){
				PC_EnergyNodeBuffer[] array = buffer.toArray(new PC_EnergyNodeBuffer[buffer.size()]);
				Arrays.sort(array);
				List<PC_EnergyNodeBuffer> bufferNodes = new ArrayList<PC_EnergyNodeBuffer>();
				float lastLevel = 0;
				for(int i=1; i<array.length; i++){
					float nextLevel = array[i].level;
					if(!bufferNodes.isEmpty()){
						float fill = nextLevel-lastLevel;
						if(fill<energy/bufferNodes.size())
							fill=energy/bufferNodes.size();
						Iterator<PC_EnergyNodeBuffer> it = bufferNodes.iterator();
						while(it.hasNext()){
							PC_EnergyNodeBuffer b = it.next();
							energy = b.addEnergy(energy, fill);
							if(b.used==b.maxIn){
								it.remove();
							}
						}
					}
					lastLevel = nextLevel;
					bufferNodes.add(array[i]);
				}
				while(!bufferNodes.isEmpty() && energy>0){
					float fill=energy/bufferNodes.size();
					Iterator<PC_EnergyNodeBuffer> it = bufferNodes.iterator();
					while(it.hasNext()){
						PC_EnergyNodeBuffer b = it.next();
						energy = b.addEnergy(energy, fill);
						if(b.used==b.maxIn){
							it.remove();
						}
					}
				}
			}
			if(energy>0){
				p = energy/info.notProduceNeccecerly;
				if(p>1)
					p=1;
				for(PC_EnergyNode<?> node:nodes){
					energy = node.notUsing(energy, p);
				}
			}
			for(PC_EnergyNode<?> node:nodes){
				node.onTickEnd();
			}
		}else{
			for(PC_EnergyNode<?> node:nodes){
				node.onTickStart();
			}
			for(PC_EnergyNode<?> node:nodes){
				if(node instanceof PC_EnergyNodeConsumer){
					PC_EnergyNodeConsumer consumer = (PC_EnergyNodeConsumer)node;
					consumer.useable = consumer.requested;
				}
			}
			for(PC_EnergyNode<?> node:nodes){
				node.onTickEnd();
			}
		}
	}
	
	@Override
	protected PC_EnergyNode<?> createNode(PC_IEnergyGridTile tile) {
		if(tile instanceof PC_IEnergyGridConduit){
			return new PC_EnergyNodeConduit(this, (PC_IEnergyGridConduit)tile);
		}else if(tile instanceof PC_IEnergyGridBuffer){
			return new PC_EnergyNodeBuffer(this, (PC_IEnergyGridBuffer)tile);
		}else if(tile instanceof PC_IEnergyGridProvider){
			return new PC_EnergyNodeProvider(this, (PC_IEnergyGridProvider)tile);
		}else if(tile instanceof PC_IEnergyGridConsumer){
			return new PC_EnergyNodeConsumer(this, (PC_IEnergyGridConsumer)tile);
		}
		throw new RuntimeException();
	}

	@Override
	protected PC_EnergyEdge createEdge(PC_EnergyNode<?> start, PC_EnergyNode<?> end) {
		return new PC_EnergyEdge(this, start, end);
	}

	@Override
	protected PC_EnergyGrid createGrid() {
		return new PC_EnergyGrid();
	}
	
}
