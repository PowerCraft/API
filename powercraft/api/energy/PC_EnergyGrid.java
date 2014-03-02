package powercraft.api.energy;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.minecraft.world.World;
import powercraft.api.PC_Api;
import powercraft.api.PC_Side;
import powercraft.api.PC_TickHandler;
import powercraft.api.PC_TickHandler.PC_IWorldTickHandler;
import powercraft.api.grid.PC_Grid;
import powercraft.api.grid.PC_IGridFactory;
import powercraft.api.reflect.PC_Security;

public class PC_EnergyGrid extends PC_Grid<PC_EnergyGrid, PC_IEnergyGridTile, PC_EnergyNode<?>, PC_EnergyEdge> {

	private static boolean isEnergyModulePresent;
	static List<WeakReference<PC_EnergyGrid>> grids = new ArrayList<WeakReference<PC_EnergyGrid>>();
	private static Ticker ticker;
	
	public static final PC_IGridFactory<PC_EnergyGrid, PC_IEnergyGridTile, PC_EnergyNode<?>, PC_EnergyEdge> factory = new Factory();
	
	public static void register(){
		PC_Security.allowedCaller("PC_EnergyGrid.register", PC_Api.class);
		if(ticker==null){
			ticker = new Ticker();
			PC_TickHandler.registerTickHandler(ticker);
		}
	}
	
	private static class Ticker implements PC_IWorldTickHandler{

		Ticker() {
			//
		}

		@Override
		public void onStartTick(PC_Side side, World world) {
			Iterator<WeakReference<PC_EnergyGrid>> iterator = grids.iterator();
			while(iterator.hasNext()){
				PC_EnergyGrid grid = iterator.next().get();
				if(grid==null){
					iterator.remove();
				}else{
					grid.doEnergyTick();
				}
			}
		}

		@Override
		public void onEndTick(PC_Side side, World world) {
			//
		}
		
	}
	
	private static class Factory implements PC_IGridFactory<PC_EnergyGrid, PC_IEnergyGridTile, PC_EnergyNode<?>, PC_EnergyEdge>{

		Factory() {
			//
		}

		@Override
		public PC_EnergyGrid make(PC_IEnergyGridTile tile) {
			return new PC_EnergyGrid(tile);
		}
		
	}
	
	public static void setEnergyModulePresent(){
		PC_Security.allowedCaller("PC_EnergyGrid.setEnergyModulePresent", "powercraft.energy.PCeg_Energy");
		isEnergyModulePresent = true;
	}
	
	PC_EnergyGrid(){
		grids.add(new WeakReference<PC_EnergyGrid>(this));
	}
	
	PC_EnergyGrid(PC_IEnergyGridTile tile){
		super(tile);
		grids.add(new WeakReference<PC_EnergyGrid>(this));
	}
	
	@Override
	protected void destroy(){
		grids.remove(this);
	}
	
	public void doEnergyTick(){
		if(isEnergyModulePresent){
			PC_EnergyInfo info = new PC_EnergyInfo();
			List<PC_EnergyNodeBuffer> buffer = new ArrayList<PC_EnergyNodeBuffer>();
			for(PC_EnergyNode<?> node:this.nodes){
				node.onTickStart();
				node.addToInfo(info);
				if(node instanceof PC_EnergyNodeBuffer){
					buffer.add((PC_EnergyNodeBuffer) node);
				}
			}
			float energy = 0;
			for(PC_EnergyNode<?> node:this.nodes){
				energy += node.takeEnergy();
			}
			float p = energy/info.energyRequested;
			if(p>1)
				p=1;
			for(PC_EnergyNode<?> node:this.nodes){
				energy = node.useEnergy(energy, p);
			}
			if(energy>0 && !buffer.isEmpty()){
				PC_EnergyNodeBuffer[] array = buffer.toArray(new PC_EnergyNodeBuffer[buffer.size()]);
				Arrays.sort(array);
				List<PC_EnergyNodeBuffer> bufferNodes = new ArrayList<PC_EnergyNodeBuffer>();
				float lastLevel = 0;
				for(int i=0; i<array.length; i++){
					float nextLevel = array[i].level;
					if(!bufferNodes.isEmpty()){
						float fill = nextLevel-lastLevel;
						if(fill>energy/bufferNodes.size())
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
				while(!bufferNodes.isEmpty() && energy>0.00001){
					float fill=energy/bufferNodes.size();
					Iterator<PC_EnergyNodeBuffer> it = bufferNodes.iterator();
					while(it.hasNext()){
						PC_EnergyNodeBuffer b = it.next();
						energy = b.addEnergy(energy, fill);
						if(b.used>=b.maxIn){
							it.remove();
						}
					}
				}
			}
			if(energy>0){
				p = energy/info.notProduceNeccecerly;
				if(p>1)
					p=1;
				for(PC_EnergyNode<?> node:this.nodes){
					energy = node.notUsing(energy, p);
				}
			}
		}else{
			for(PC_EnergyNode<?> node:this.nodes){
				node.onTickStart();
			}
			for(PC_EnergyNode<?> node:this.nodes){
				if(node instanceof PC_EnergyNodeConsumer){
					PC_EnergyNodeConsumer consumer = (PC_EnergyNodeConsumer)node;
					consumer.useable = consumer.requested;
				}
			}
		}
		for(PC_EnergyNode<?> node:this.nodes){
			node.onTickEnd();
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
