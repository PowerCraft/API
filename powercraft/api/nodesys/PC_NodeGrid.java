package powercraft.api.nodesys;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import powercraft.api.PC_Direction;
import powercraft.api.nodesys.node.PC_Node;
import powercraft.api.nodesys.node.descriptor.PC_NodeDescriptorGroupInput;


public class PC_NodeGrid {

	private List<PC_Node> list = new ArrayList<PC_Node>();
	private String name;
	private List<PC_NodeComponent> outputs = new ArrayList<PC_NodeComponent>();
	private List<PC_NodeComponent> inputs = new ArrayList<PC_NodeComponent>();
	private PC_NodeGridBase base;
	
	public PC_NodeGrid(PC_NodeGridBase base){
		base.add(this);
		this.base = base;
	}
	
	protected PC_NodeGrid(){
		base = (PC_NodeGridBase) this;
	}
	
	protected PC_NodeGrid(NBTTagCompound nbtTagCompound){
		NBTTagList list = nbtTagCompound.getTagList("list", NBT.TAG_COMPOUND);
		for(int i=0; i<list.tagCount(); i++){
			this.list.add(PC_NodeGridHelper.loadFrom(this, list.getCompoundTagAt(i)));
		}
		for(int i=0; i<list.tagCount(); i++){
			this.list.get(i).loadFrom(list.getCompoundTagAt(i));
		}
	}
	
	protected void saveTo(NBTTagCompound nbtTagCompound){
		NBTTagList list = new NBTTagList();
		for(int i=0; i<this.list.size(); i++){
			NBTTagCompound compound = new NBTTagCompound();
			this.list.get(i).saveTo(compound);
			list.appendTag(compound);
		}
		nbtTagCompound.setTag("list", list);
	}
	
	public PC_Node getNode(int index) {
		return list.get(index);
	}

	public PC_NodeGridData makeEmptyData() {
		return new PC_NodeGridData(this, list.size(), getVersion(), -1);
	}

	public int getVersion() {
		return base.getVersion();
	}

	public int getStartNodeFor(PC_Direction dir) {
		return -1;
	}

	public PC_NodeGridData makeDataFromIn(PC_GridExecutor executor, int groupNode, int pin, Object[] in) {
		int i=0;
		PC_NodeGridData gridData = new PC_NodeGridData(this, list.size(), getVersion(), groupNode);
		int index = -1;
		for(PC_Node node:list){
			if(node.getDescriptor()==PC_NodeDescriptorGroupInput.INSTANCE){
				if(index==-1){
					index = i;
				}
				gridData.setNodeOutputs(i, in);
			}
			i++;
		}
		if(index==-1){
			executor.setNextNode(-1, -1);
		}else{
			PC_NodeConnection connection = getNode(index).getConnectedNodeAt(pin);
			executor.setNextNode(getIDOf(connection.node), connection.pin);
		}
		return gridData;
	}

	public PC_NodeConnection getNodeConnection(int node, int pin) {
		return getNode(node).getConnectedNodeAt(pin);
	}

	public int getIDOf(PC_Node node){
		return this.list.indexOf(node);
	}
	
	public void add(PC_Node node){
		this.list.add(0, node);
	}
	
	public void moveToTop(PC_Node node){
		this.list.remove(node);
		this.list.add(0, node);
	}

	public PC_NodeGridBase getBase() {
		return base;
	}

	public String getName() {
		return name;
	}

	public PC_NodeComponent[] getOutputs() {
		return outputs.toArray(new PC_NodeComponent[outputs.size()]);
	}
	
	public PC_NodeComponent[] getInputs() {
		return inputs.toArray(new PC_NodeComponent[inputs.size()]);
	}
	
}
