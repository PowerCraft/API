package powercraft.api.nodesys;

import powercraft.api.nodesys.type.PC_NodeObjectType;


public class PC_NodeComponent {

	//Binary combination
	public static final int TYPE_CONFIG = 0;
	public static final int TYPE_IN = 1<<0;
	public static final int TYPE_OUT = 1<<1;
	
	private String name;
	private Object defaultValue;
	private PC_NodeObjectType type;
	private PC_INodeValueInput input;
	private int iotype;
	
	public PC_NodeComponent(String name, Object defaultValue, PC_NodeObjectType type, PC_INodeValueInput input, int iotype){
		this.name = name;
		this.defaultValue = defaultValue;
		this.type = type;
		this.input = input;
		this.iotype = iotype;
	}
	
	public String getName(){
		return name;
	}
	
	public Object getDefault(){
		return getType().copy(defaultValue);
	}

	public PC_NodeObjectType getType(){
		return this.type;
	}

	public PC_INodeValueInput getValueInputType(){
		return this.input;
	}
	
	public int getIOType(){
		return iotype;
	}
	
}
