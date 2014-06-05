package powercraft.api.nodesys.type;

import net.minecraft.nbt.NBTTagCompound;


public class PC_NodeObjectVector3 extends PC_NodeObjectType {

	@Override
	public Object castTo(Object object) {
		
		return null;
	}

	@Override
	public boolean canCastTo(PC_NodeObjectType to) {
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getColor() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object loadFrom(NBTTagCompound nbtTagCompound) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveTo(NBTTagCompound nbtTagCompound, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object copy(Object defaultValue) {
		// TODO Auto-generated method stub
		return null;
	}

}
