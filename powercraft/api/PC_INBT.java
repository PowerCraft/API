/**
 * 
 */
package powercraft.api;

import powercraft.api.block.PC_Field.Flag;
import net.minecraft.nbt.NBTTagCompound;

/**
 * 
 * Indicates that this Object can be saved to an NBTTagCompound
 * 
 * <p><b>YOU HAVE TO MAKE THE CONSTRUCTOR(NBTTagCompound)</b> to load the object again
 * 
 * @author XOR
 *
 */
public interface PC_INBT {

	/**
	 * 
	 * function to save this object
	 * 
	 * @param tag save the data into this
	 */
	public void saveToNBT(NBTTagCompound tag, Flag flag);

	
	
}
