package powercraft.api.block;

import powercraft.api.material.MaterialUtil;
import powercraft.api.stepsound.StepSoundUtil;

/**
 * @author James
 * An independant block
 */
public class Block {

	/**
	 * The name of the block
	 */
	public final String blockName;
	
	private net.minecraft.block.Block block;
	
	/**
	 * @param id The block ID
	 * @param material The material name
	 * @param name The name of the block
	 */
	public Block(int id, String material, String name){
		this.blockName = name;
		this.block = new net.minecraft.block.Block(id, MaterialUtil.getMaterialFromName(material));
	}
	
	/**
	 * @param hardness The hardness to set the block hardness to
	 */
	@SuppressWarnings("boxing")
	public void setHardness(Float hardness){
		this.block.setHardness(hardness);
	}
	
	/**
	 * @param resist The resistance to set the block hardness to
	 */
	@SuppressWarnings("boxing")
	public void setResistance(Float resist){
		this.block.setHardness(resist);
	}

	/**
	 * @param step The name of the step sound
	 */
	public void setStep(String step){
		this.block.setStepSound(StepSoundUtil.getStepSoundFromName(step));
	}
}
