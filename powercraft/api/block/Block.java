package powercraft.api.block;

import powercraft.api.material.MaterialUtil;
import powercraft.api.stepsound.StepSoundUtil;

/**
 * @author James
 * An independant block
 */
public class Block extends net.minecraft.block.Block{

	/**
	 * The name of the block
	 */
	public final String blockName;
	
	/**
	 * @param id The block ID
	 * @param material The material name
	 * @param name The name of the block
	 */
	public Block(int id, String material, String name){
		super(id, MaterialUtil.getMaterialFromName(material));
		this.blockName = name;
	}
}
