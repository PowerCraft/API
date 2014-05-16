package powercraft.api.nodesys.node;

import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.nodesys.PC_GridExecutor;
import powercraft.api.nodesys.PC_NodeComponent;
import powercraft.api.nodesys.PC_NodeDescriptor;
import powercraft.api.nodesys.PC_NodeGrid;



public class PC_NodeGroup extends PC_Node {

	private PC_NodeGrid grid;
	
	public PC_NodeGroup(PC_NodeDescriptor descriptor, PC_NodeGrid parent, boolean load) {
		super(descriptor, parent, load);
		if(!load)
			grid = new PC_NodeGrid(parent.getBase());
	}

	@Override
	public void loadFrom(NBTTagCompound nbtTagCompound) {
		super.loadFrom(nbtTagCompound);
		grid = getParent().getBase().getGrid((String)getValues()[0]);
	}

	@Override
	public Object[] execute(PC_GridExecutor executor, int nextPin, Object[] in) {
		executor.enterGroup(grid, executor.getGrid().getIDOf(this), nextPin, in);
		return null;
	}

	@Override
	public PC_NodeComponent[] getComponents() {
		// TODO Auto-generated method stub
		return super.getComponents();
	}
	
}
