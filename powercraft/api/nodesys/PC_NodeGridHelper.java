package powercraft.api.nodesys;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec3I;
import powercraft.api.nodesys.node.PC_Node;
import powercraft.api.nodesys.node.descriptor.PC_NodeDescriptorGroup;
import powercraft.api.nodesys.node.descriptor.PC_NodeDescriptorGroupInput;
import powercraft.api.nodesys.node.descriptor.PC_NodeDescriptorGroupOutput;
import powercraft.api.nodesys.node.descriptor.PC_NodeDescriptorMath;
import powercraft.api.nodesys.type.PC_NodeObjectType;
import powercraft.api.nodesys.type.PC_NodeObjectTypeNumber;
import powercraft.api.nodesys.type.PC_NodeObjectTypeProgram;
import powercraft.api.nodesys.type.PC_NodeObjectTypeString;


public final class PC_NodeGridHelper {

	private static final HashMap<String, PC_NodeDescriptor> nodes = new HashMap<String, PC_NodeDescriptor>();
	
	private static final HashMap<String, PC_NodeObjectType> types = new HashMap<String, PC_NodeObjectType>();
	
	private PC_NodeGridHelper(){
		PC_Utils.staticClassConstructor();
	}
	
	public static PC_NodeGridBase getNodeGridAt(World world, PC_Vec3I newPos) {
		PC_INodeGridContainer container = PC_Utils.getTileEntity(world, newPos, PC_INodeGridContainer.class);
		if(container==null)
			return null;
		return container.getNodeGrid();
	}

	public static void registerNode(PC_NodeDescriptor descriptor){
		nodes.put(descriptor.getName(), descriptor);
	}
	
	public static PC_Node makeEmptyNode(PC_NodeGrid parent, String name){
		return nodes.get(name).create(parent, false);
	}
	
	public static PC_Node loadFrom(PC_NodeGrid parent, NBTTagCompound nbtTagCompound){
		return nodes.get(nbtTagCompound.getString("NodeNameID")).create(parent, true);
	}
	
	public static void registerType(PC_NodeObjectType type){
		types.put(type.getName(), type);
	}
	
	public static PC_NodeObjectType getType(String name){
		return types.get(name);
	}
	
	static{
		registerNode(PC_NodeDescriptorGroup.INSTANCE);
		registerNode(PC_NodeDescriptorGroupInput.INSTANCE);
		registerNode(PC_NodeDescriptorGroupOutput.INSTANCE);
		registerNode(PC_NodeDescriptorMath.INSTANCE);
		
		registerType(PC_NodeObjectTypeNumber.INSTANCE);
		registerType(PC_NodeObjectTypeProgram.INSTANCE);
		registerType(PC_NodeObjectTypeString.INSTANCE);
	}

	public static String getNameOnly(String name) {
		int index = name.lastIndexOf('.');
		if(index==-1)
			return name;
		return name.substring(index+1);
	}
	
}
