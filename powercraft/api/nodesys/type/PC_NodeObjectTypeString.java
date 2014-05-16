package powercraft.api.nodesys.type;

import net.minecraft.nbt.NBTTagCompound;



public class PC_NodeObjectTypeString extends PC_NodeObjectType {

	public static final PC_NodeObjectType INSTANCE = new PC_NodeObjectTypeString();
	
	private PC_NodeObjectTypeString(){
		
	}
	
	@Override
	public Object castTo(Object value) {
		return value==null?null:value.toString();
	}

	@Override
	public boolean canCastTo(PC_NodeObjectType to) {
		return true;
	}

	@Override
	public String getName() {
		return "String";
	}

	@Override
	public int getColor() {
		return 0xFFFFFFFF;
	}

	@Override
	public Object loadFrom(NBTTagCompound nbtTagCompound) {
		if(!nbtTagCompound.hasKey("value"))
			return null;
		return nbtTagCompound.getString("value");
	}

	@Override
	public void saveTo(NBTTagCompound nbtTagCompound, Object value) {
		if(value!=null){
			nbtTagCompound.setString("value", (String)value);
		}
	}
	
	@Override
	public Object copy(Object defaultValue) {
		return defaultValue;
	}

}
