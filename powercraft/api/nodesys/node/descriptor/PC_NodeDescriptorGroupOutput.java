package powercraft.api.nodesys.node.descriptor;

import powercraft.api.nodesys.PC_GridExecutor;
import powercraft.api.nodesys.PC_NodeComponent;
import powercraft.api.nodesys.PC_NodeDescriptor;
import powercraft.api.nodesys.PC_NodeGrid;
import powercraft.api.nodesys.node.PC_Node;
import powercraft.api.nodesys.node.PC_NodeGroupOutput;


public class PC_NodeDescriptorGroupOutput extends PC_NodeDescriptor {

	public static final PC_NodeDescriptor INSTANCE = new PC_NodeDescriptorGroupOutput();

	private PC_NodeDescriptorGroupOutput(){
		
	}
	
	@Override
	public String getName() {
		return "Output.GroupOutput";
	}

	@Override
	public PC_Node create(PC_NodeGrid parent, boolean load) {
		return new PC_NodeGroupOutput(this, parent, load);
	}
	
	@Override
	public PC_NodeComponent[] getComponents() {
		return new PC_NodeComponent[0];
	}

	@Override
	public Object[] execute(PC_GridExecutor executor, PC_Node node, int nextPin, Object[] in) {
		return null;
	}

}
