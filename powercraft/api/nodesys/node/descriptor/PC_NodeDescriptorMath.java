package powercraft.api.nodesys.node.descriptor;

import powercraft.api.nodesys.PC_GridExecutor;
import powercraft.api.nodesys.PC_NodeComponent;
import powercraft.api.nodesys.PC_NodeDescriptor;
import powercraft.api.nodesys.PC_NodeValueInputDropdown;
import powercraft.api.nodesys.PC_NodeValueInputTextbox;
import powercraft.api.nodesys.PC_NodeValueInputTextbox.InputType;
import powercraft.api.nodesys.node.PC_Node;
import powercraft.api.nodesys.type.PC_NodeObjectTypeNumber;
import powercraft.api.nodesys.type.PC_NodeObjectTypeProgram;


public class PC_NodeDescriptorMath extends PC_NodeDescriptor {

	public static final PC_NodeDescriptor INSTANCE = new PC_NodeDescriptorMath();
	
	private static final PC_NodeComponent[] COMPONENTS = {
		new PC_NodeComponent("Prog Flow", null, PC_NodeObjectTypeProgram.INSTANCE, null, PC_NodeComponent.TYPE_IN | PC_NodeComponent.TYPE_OUT),
		new PC_NodeComponent("Result", null, PC_NodeObjectTypeNumber.INSTANCE, null, PC_NodeComponent.TYPE_OUT),
		new PC_NodeComponent("Type", 0, PC_NodeObjectTypeNumber.INSTANCE, new PC_NodeValueInputDropdown("add", "sub", "mul", "div"), PC_NodeComponent.TYPE_CONFIG),
		new PC_NodeComponent("Value1", 0, PC_NodeObjectTypeNumber.INSTANCE, new PC_NodeValueInputTextbox(InputType.FLOAT), PC_NodeComponent.TYPE_IN),
		new PC_NodeComponent("Value2", 0, PC_NodeObjectTypeNumber.INSTANCE, new PC_NodeValueInputTextbox(InputType.FLOAT), PC_NodeComponent.TYPE_IN),
	};
	
	private PC_NodeDescriptorMath(){
		
	}
	
	@Override
	public String getName() {
		return "Convert.Math";
	}

	@Override
	public PC_NodeComponent[] getComponents() {
		return COMPONENTS;
	}

	@Override
	public Object[] execute(PC_GridExecutor executor, PC_Node node, int nextPin, Object[] in) {
		int i = ((Number)node.getValues()[2]).intValue();
		double d1 = ((Number)in[0]).doubleValue();
		double d2 = ((Number)in[1]).doubleValue();
		double ret;
		switch(i){
		case 0:
			ret = d1+d2;
			break;
		case 1:
			ret = d1-d2;
			break;
		case 2:
			ret = d1*d2;
			break;
		case 3:
			ret = d1/d2;
			break;
		default:
			ret = 0;
			break;
		}
		return new Object[]{ret};
	}

}
