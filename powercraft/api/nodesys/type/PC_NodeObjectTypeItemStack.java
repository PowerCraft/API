package powercraft.api.nodesys.type;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;


public class PC_NodeObjectTypeItemStack extends PC_NodeObjectType {

	public static final PC_NodeObjectType INSTANCE = new PC_NodeObjectTypeItemStack();

	private PC_NodeObjectTypeItemStack(){}
	
	@Override
	public Object castTo(Object object) {
		if(object instanceof ItemStack)
			return object;
		return null;
	}

	@Override
	public boolean canCastTo(PC_NodeObjectType to) {
		if(to==PC_NodeObjectTypeItemStack.INSTANCE)
			return true;
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
