package powercraft.api.nodesys.node;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import powercraft.api.nodesys.PC_GridExecutor;
import powercraft.api.nodesys.PC_NodeComponent;
import powercraft.api.nodesys.PC_NodeConnection;
import powercraft.api.nodesys.PC_NodeDescriptor;
import powercraft.api.nodesys.PC_NodeGrid;
import powercraft.api.nodesys.PC_NodeGridHelper;


public class PC_Node {

	private PC_NodeDescriptor descriptor;
	
	private PC_NodeGrid parent;
	
	private Object[] values;
	
	private String label;
	
	private int x = 0;
	
	private int y = 0;
	
	private int width = 100;
	
	private int smallWidth = 100;
	
	private int color = 0x80808080;
	
	private boolean muted = false;
	
	private boolean small = false;
	
	public PC_Node(PC_NodeDescriptor descriptor, PC_NodeGrid parent, boolean load){
		this.descriptor = descriptor;
		this.parent = parent;
		PC_NodeComponent[] c = descriptor.getComponents();
		values = new Object[c.length];
		for(int i=0; i<values.length; i++){
			values[i] = c[i].getDefault();
		}
	}
	
	public void loadFrom(NBTTagCompound nbtTagCompound){
		PC_NodeComponent[] c = descriptor.getComponents();
		NBTTagList list = nbtTagCompound.getTagList("list", NBT.TAG_COMPOUND);
		for(int i=0; i<values.length; i++){
			NBTTagCompound compound = list.getCompoundTagAt(i);
			values[i] = c[i].getType().loadFrom(compound);
			if(compound.hasKey("connectionToNode")){
				PC_NodeConnection connection = new PC_NodeConnection();
				connection.oldValue = values[i];
				connection.node = getParent().getNode(compound.getInteger("connectionToNode"));
				connection.pin = compound.getInteger("connectionToPin");
				values[i] = connection;
			}
		}
		if(nbtTagCompound.hasKey("label"))
			label = nbtTagCompound.getString("label");
		x = nbtTagCompound.getInteger("x");
		y = nbtTagCompound.getInteger("y");
		width = nbtTagCompound.getInteger("width");
		smallWidth = nbtTagCompound.getInteger("smallWidth");
		color = nbtTagCompound.getInteger("color");
		muted = nbtTagCompound.getBoolean("muted");
		small = nbtTagCompound.getBoolean("small");
	}
	
	public void saveTo(NBTTagCompound nbtTagCompound){
		nbtTagCompound.setString("NodeNameID", descriptor.getName());
		PC_NodeComponent[] c = descriptor.getComponents();
		NBTTagList list = new NBTTagList();
		for(int i=0; i<values.length; i++){
			NBTTagCompound compound = new NBTTagCompound();
			if(values[i] instanceof PC_NodeConnection){
				compound.setInteger("connectionToNode", getParent().getIDOf(((PC_NodeConnection)values[i]).node));
				compound.setInteger("connectionToPin", ((PC_NodeConnection)values[i]).pin);
				c[i].getType().saveTo(compound, ((PC_NodeConnection)values[i]).oldValue);
			}else{
				c[i].getType().saveTo(compound, values[i]);
			}
			list.appendTag(compound);
		}
		nbtTagCompound.setTag("list", list);
		nbtTagCompound.setInteger("x", x);
		nbtTagCompound.setInteger("y", y);
		nbtTagCompound.setInteger("width", width);
		nbtTagCompound.setInteger("smallWidth", smallWidth);
		nbtTagCompound.setInteger("color", color);
		nbtTagCompound.setBoolean("muted", muted);
		nbtTagCompound.setBoolean("small", small);
	}
	
	public String getLabel(){
		if(label==null){
			return PC_NodeGridHelper.getNameOnly(descriptor.getName());
		}
		return label;
	}
	
	public Object[] execute(PC_GridExecutor executor, int nextPin, Object[] in){
		return descriptor.execute(executor, this, nextPin, in);
	}
	
	public PC_NodeComponent[] getComponents(){
		return descriptor.getComponents();
	}

	public PC_NodeDescriptor getDescriptor() {
		return descriptor;
	}

	public PC_NodeConnection getConnectedNodeAt(int pin) {
		return (PC_NodeConnection)values[pin];
	}
	
	public PC_NodeGrid getParent(){
		return parent;
	}
	
	public Object[] getValues(){
		return values;
	}
	
}
