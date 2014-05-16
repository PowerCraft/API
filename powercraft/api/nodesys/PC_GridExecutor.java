package powercraft.api.nodesys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.world.World;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Vec3I;


public class PC_GridExecutor {

	private HashMap<PC_Vec3I, PC_NodeGridData> blockData = new HashMap<PC_Vec3I, PC_NodeGridData>();
	
	private List<PC_NodeGridData> groupDepth = new ArrayList<PC_NodeGridData>();
	
	private PC_NodeGridData actualData;
	
	private PC_NodeGrid grid;
	
	private int nextNode = -1;
	
	private int nextPin = -1;
	
	public Object getNodeOutputData(int node, int pin){
		return actualData.getNodeOutputData(node, pin);
	}
	
	public boolean executeNext(){
		if(nextNode==-1)
			return false;
		int thisNode = nextNode;
		nextNode = -1;
		Object[] in = null;
		Object[] outputs = grid.getNode(thisNode).execute(this, nextPin, in);
		actualData.setNodeOutputs(thisNode, outputs);
		return nextNode!=-1;
	}
	
	public void setNextNode(int nextNode, int nextPin){
		this.nextNode = nextNode;
		this.nextPin = nextPin;
	}
	
	public void enterGroup(PC_NodeGrid grid, int groupNode, int nextPin, Object[] in){
		groupDepth.add(actualData);
		this.grid = grid;
		actualData = grid.makeDataFromIn(this, groupNode, nextPin, in);
	}
	
	public void exitGroup(Object[] output, int pin){
		int node = actualData.getGroupNode();
		actualData = groupDepth.remove(groupDepth.size()-1);
		this.grid = actualData.getGrid();
		actualData.setNodeOutputs(node, output);
		PC_NodeConnection connection = this.grid.getNodeConnection(node, pin);
		this.nextNode = this.grid.getIDOf(connection.node);
		this.nextPin = connection.pin;
	}
	
	public void changePos(World world, PC_Vec3I newPos, PC_Direction dir){
		grid = PC_NodeGridHelper.getNodeGridAt(world, newPos);
		actualData = blockData.get(newPos);
		if(actualData==null){
			blockData.put(newPos, actualData = grid.makeEmptyData());
		}else{
			if(grid.getVersion()!=actualData.getVersion()){
				nextNode = -1;
				return;
			}
		}
		nextNode = grid.getStartNodeFor(dir);
	}
	
	public boolean hasNext(){
		return this.nextNode!=-1;
	}

	public PC_NodeGrid getGrid() {
		return grid;
	}
	
}
