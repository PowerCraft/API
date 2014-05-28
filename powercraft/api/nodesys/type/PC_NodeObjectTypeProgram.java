package powercraft.api.nodesys.type;

import net.minecraft.nbt.NBTTagCompound;


public class PC_NodeObjectTypeProgram extends PC_NodeObjectType {

	public static final PC_NodeObjectType INSTANCE = new PC_NodeObjectTypeProgram();
	
	private PC_NodeObjectTypeProgram(){
		
	}
	
	@Override
	public Object castTo(Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean canCastTo(PC_NodeObjectType to) {
		return false;
	}

	@Override
	public String getName() {
		return "Program";
	}

	@Override
	public int getColor() {
		return 0x80FF0000;
	}

	@Override
	public Object loadFrom(NBTTagCompound nbtTagCompound) {
		return null;
	}

	@Override
	public void saveTo(NBTTagCompound nbtTagCompound, Object value) {
		
	}

	@Override
	public Object copy(Object defaultValue) {
		return null;
	}
	
	@Override
	public boolean swap(){
		return true;
	}

}
