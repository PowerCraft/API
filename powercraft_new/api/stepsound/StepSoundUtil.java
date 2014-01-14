package powercraft_new.api.stepsound;

/**
 * @author James
 */
public class StepSoundUtil {
	
	/**
	 * @param name The name of the stepsound
	 * @return The stepsound that is being gotten
	 */
	public static net.minecraft.block.StepSound getStepSoundFromName(String name){
		switch(name){
		case "anvil":
			return net.minecraft.block.Block.soundAnvilFootstep;
		case "cloth":
			return net.minecraft.block.Block.soundClothFootstep;
		case "glass":
			return net.minecraft.block.Block.soundGlassFootstep;
		case "grass":
			return net.minecraft.block.Block.soundGrassFootstep;
		case "gravel":
			return net.minecraft.block.Block.soundGravelFootstep;
		case "ladder":
			return net.minecraft.block.Block.soundLadderFootstep;
		case "metal":
			return net.minecraft.block.Block.soundMetalFootstep;
		case "powder":
			return net.minecraft.block.Block.soundPowderFootstep;
		case "sand":
			return net.minecraft.block.Block.soundSandFootstep;
		case "snow":
			return net.minecraft.block.Block.soundSnowFootstep;
		case "stone":
			return net.minecraft.block.Block.soundStoneFootstep;
		case "wood":
			return net.minecraft.block.Block.soundWoodFootstep;
		default:
			return null;
		}
	}
}
