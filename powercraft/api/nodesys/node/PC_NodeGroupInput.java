package powercraft.api.nodesys.node;

import powercraft.api.nodesys.PC_NodeComponent;
import powercraft.api.nodesys.PC_NodeDescriptor;
import powercraft.api.nodesys.PC_NodeGrid;

public class PC_NodeGroupInput extends PC_Node {

	public PC_NodeGroupInput(PC_NodeDescriptor descriptor, PC_NodeGrid parent, boolean load) {
		super(descriptor, parent, load);
	}

	@Override
	public PC_NodeComponent[] getComponents() {
		return getParent().getOutputs();
	}
	
}
