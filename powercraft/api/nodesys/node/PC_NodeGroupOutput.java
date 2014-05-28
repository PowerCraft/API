package powercraft.api.nodesys.node;

import powercraft.api.nodesys.PC_GridExecutor;
import powercraft.api.nodesys.PC_NodeComponent;
import powercraft.api.nodesys.PC_NodeDescriptor;
import powercraft.api.nodesys.PC_NodeGrid;

public class PC_NodeGroupOutput extends PC_Node {

	public PC_NodeGroupOutput(PC_NodeDescriptor descriptor, PC_NodeGrid parent, boolean load) {
		super(descriptor, parent, load);
	}

	@Override
	public PC_NodeComponent[] getComponents() {
		return getParent().getInputs();
	}
	
	@Override
	public Object[] execute(PC_GridExecutor executor, int nextPin, Object[] in) {
		executor.exitGroup(in, nextPin);
		return null;
	}
	
}
