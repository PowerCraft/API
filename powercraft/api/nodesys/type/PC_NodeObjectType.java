package powercraft.api.nodesys.type;

import net.minecraft.nbt.NBTTagCompound;



public abstract class PC_NodeObjectType {

	public abstract Object castTo(Object object);

	public abstract boolean canCastTo(PC_NodeObjectType to);

	public abstract String getName();
	
	public abstract int getColor();

	public abstract Object loadFrom(NBTTagCompound nbtTagCompound);
	
	public abstract void saveTo(NBTTagCompound nbtTagCompound, Object value);

	public abstract Object copy(Object defaultValue);
	
	public boolean swap(){
		return false;
	}
	
	public int group(){
		return swap()?0:1;
	}
	
}
