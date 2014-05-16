package powercraft.api.nodesys;

import powercraft.api.nodesys.node.PC_Node;


public abstract class PC_NodeDescriptor {

	public abstract String getName();
	
	public PC_Node create(PC_NodeGrid parent, boolean load){
		return new PC_Node(this, parent, load);
	}

	public abstract PC_NodeComponent[] getComponents();

	public abstract Object[] execute(PC_GridExecutor executor, PC_Node node, int nextPin, Object[] in);
	
}
