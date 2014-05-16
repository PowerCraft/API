package powercraft.api.nodesys;


public class PC_NodeGridData {

	private int version;
	
	private Object[][] nodePins;
	
	private PC_NodeGrid grid;
	
	private int groupNode;
	
	public PC_NodeGridData(PC_NodeGrid grid, int size, int version, int groupNode) {
		this.grid = grid;
		nodePins = new Object[size][];
		this.version = version;
		this.groupNode = groupNode;
	}

	public Object getNodeOutputData(int node, int pin) {
		return nodePins[node][pin];
	}

	public void setNodeOutputs(int node, Object[] outputs) {
		nodePins[node] = outputs;
	}

	public int getVersion() {
		return version;
	}

	public int getGroupNode() {
		return groupNode;
	}

	public PC_NodeGrid getGrid() {
		return grid;
	}

}
