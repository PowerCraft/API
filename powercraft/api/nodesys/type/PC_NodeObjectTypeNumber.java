package powercraft.api.nodesys.type;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;



public class PC_NodeObjectTypeNumber extends PC_NodeObjectType {

	public static final PC_NodeObjectType INSTANCE = new PC_NodeObjectTypeNumber();
	
	private PC_NodeObjectTypeNumber(){
		
	}

	@Override
	public Object castTo(Object value) {
		if(value instanceof Number){
			return value;
		}else if(value instanceof String){
			try{
				return Integer.parseInt((String)value);
			}catch(NumberFormatException e){
				try{
					return Float.parseFloat((String)value);
				}catch(NumberFormatException e1){
					try{
						return Double.parseDouble((String)value);
					}catch(NumberFormatException e2){
						
					}
				}
			}
		}
		throw new RuntimeException("Datatypes not compatible");
	}

	@Override
	public boolean canCastTo(PC_NodeObjectType to) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		return "Number";
	}

	@Override
	public int getColor() {
		return 0xFFFFFFFF;
	}

	@Override
	public Object loadFrom(NBTTagCompound nbtTagCompound) {
		if(!nbtTagCompound.hasKey("value"))
			return null;
		NBTBase base = nbtTagCompound.getTag("value");
		if(base instanceof NBTTagByte){
			return ((NBTTagByte)base).func_150290_f();
		}else if(base instanceof NBTTagShort){
			return ((NBTTagShort)base).func_150289_e();
		}else if(base instanceof NBTTagInt){
			return ((NBTTagInt)base).func_150287_d();
		}else if(base instanceof NBTTagLong){
			return ((NBTTagLong)base).func_150291_c();
		}else if(base instanceof NBTTagFloat){
			return ((NBTTagFloat)base).func_150288_h();
		}else if(base instanceof NBTTagDouble){
			return ((NBTTagDouble)base).func_150286_g();
		}
		return null;
	}

	@Override
	public void saveTo(NBTTagCompound nbtTagCompound, Object value) {
		if(value instanceof Byte){
			nbtTagCompound.setByte("value", (Byte) value);
		}else if(value instanceof Short){
			nbtTagCompound.setShort("value", (Short) value);
		}else if(value instanceof Integer){
			nbtTagCompound.setInteger("value", (Integer) value);
		}else if(value instanceof Long){
			nbtTagCompound.setLong("value", (Long) value);
		}else if(value instanceof Float){
			nbtTagCompound.setFloat("value", (Float) value);
		}else if(value instanceof Double){
			nbtTagCompound.setDouble("value", (Double) value);
		}
	}

	@Override
	public Object copy(Object defaultValue) {
		return defaultValue;
	}

}
