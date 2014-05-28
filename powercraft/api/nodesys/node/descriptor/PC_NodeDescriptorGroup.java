package powercraft.api.nodesys.node.descriptor;

import powercraft.api.nodesys.PC_GridExecutor;
import powercraft.api.nodesys.PC_NodeComponent;
import powercraft.api.nodesys.PC_NodeDescriptor;
import powercraft.api.nodesys.PC_NodeGrid;
import powercraft.api.nodesys.node.PC_Node;
import powercraft.api.nodesys.node.PC_NodeGroup;


public class PC_NodeDescriptorGroup extends PC_NodeDescriptor {

	public static final PC_NodeDescriptor INSTANCE = new PC_NodeDescriptorGroup();
	
	private PC_NodeDescriptorGroup(){
		
	}
	
	@Override
	public String getName() {
		return "Group.Group";
	}

	@Override
	public PC_Node create(PC_NodeGrid parent, boolean load) {
		return new PC_NodeGroup(this, parent, load);
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
