package powercraft.api.block;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
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
	@SuppressWarnings("unused")
	private String blockName = "";
	
	private int highestTexturedMeta = 0;
	
	private Icon[] iconSidesNorth = new Icon[1];
	private Icon[] iconSidesSouth = new Icon[1];
	private Icon[] iconSidesEast = new Icon[1];
	private Icon[] iconSidesWest = new Icon[1];
	private Icon[] iconBase = new Icon[1];
	private Icon[] iconTop = new Icon[1];
	private String[] iconSidesNorthS = new String[1];
	private String[] iconSidesSouthS = new String[1];
	private String[] iconSidesEastS = new String[1];
	private String[] iconSidesWestS = new String[1];
	private String[] iconBaseS = new String[1];
	private String[] iconTopS = new String[1];
	
	
	/**
	 * @param id The block ID
	 * @param material The material name
	 * @param name The name of the block
	 */
	public Block(int id, String material, String name){
		super(id, MaterialUtil.getMaterialFromName(material));
		this.blockName = name;
	}
	
	/**
	 * Set the stepsound of a block to something
	 * @param step The name of the step sound
	 */
	public void setStep(String step){
		this.setStepSound(StepSoundUtil.getStepSoundFromName(step));
	}
	
	/**
	 * Register a texture to the game
	 * @param meta The metadata that uses the texture
	 * @param path The path of the texture
	 */
	public void setTexture(int meta, String path){
		if(this.highestTexturedMeta < meta){
			this.highestTexturedMeta = meta;
			String[] tmp = new String[this.highestTexturedMeta + 1];
			for(int i = 0; i < this.highestTexturedMeta - 1; i++){
				tmp[i] = this.iconSidesNorthS[i];
			}
			tmp[this.highestTexturedMeta] = path;
			this.iconSidesNorthS = tmp;
			for(int i = 0; i < this.highestTexturedMeta - 1; i++){
				tmp[i] = this.iconSidesSouthS[i];
			}
			tmp[this.highestTexturedMeta] = path;
			this.iconSidesSouthS = tmp;
			for(int i = 0; i < this.highestTexturedMeta - 1; i++){
				tmp[i] = this.iconSidesEastS[i];
			}
			tmp[this.highestTexturedMeta] = path;
			this.iconSidesEastS = tmp;
			for(int i = 0; i < this.highestTexturedMeta - 1; i++){
				tmp[i] = this.iconSidesWestS[i];
			}
			tmp[this.highestTexturedMeta] = path;
			this.iconSidesWestS = tmp;
			for(int i = 0; i < this.highestTexturedMeta - 1; i++){
				tmp[i] = this.iconTopS[i];
			}
			tmp[this.highestTexturedMeta] = path;
			this.iconTopS = tmp;
			for(int i = 0; i < this.highestTexturedMeta - 1; i++){
				tmp[i] = this.iconBaseS[i];
			}
			tmp[this.highestTexturedMeta] = path;
			this.iconBaseS = tmp;
		}
		this.iconSidesNorthS[meta] = path;
		this.iconSidesSouthS[meta] = path;
		this.iconSidesEastS[meta] = path;
		this.iconSidesWestS[meta] = path;
		this.iconTopS[meta] = path;
		this.iconBaseS[meta] = path;
	}
	
	/**
	 * Register a texture to the game
	 * @param meta The metadata that uses the texture
	 * @param path The path of the texture
	 * @param side The side of the block to assign to
	 */
	public void setTexture(int meta, String path, String side){
		switch(side){
		case "north":
			if(this.highestTexturedMeta < meta){
				this.highestTexturedMeta = meta;
				String[] tmp = new String[this.highestTexturedMeta + 1];
				for(int i = 0; i < this.highestTexturedMeta - 1; i++){
					tmp[i] = this.iconSidesNorthS[i];
				}
				tmp[this.highestTexturedMeta] = path;
				this.iconSidesNorthS = tmp;
			}
			this.iconSidesNorthS[meta] = path;
			break;
		case "south":
			if(this.highestTexturedMeta < meta){
				this.highestTexturedMeta = meta;
				String[] tmp = new String[this.highestTexturedMeta + 1];
				for(int i = 0; i < this.highestTexturedMeta - 1; i++){
					tmp[i] = this.iconSidesSouthS[i];
				}
				tmp[this.highestTexturedMeta] = path;
				this.iconSidesSouthS = tmp;
			}
			this.iconSidesSouthS[meta] = path;
			break;
		case "east":
			if(this.highestTexturedMeta < meta){
				this.highestTexturedMeta = meta;
				String[] tmp = new String[this.highestTexturedMeta + 1];
				for(int i = 0; i < this.highestTexturedMeta - 1; i++){
					tmp[i] = this.iconSidesEastS[i];
				}
				tmp[this.highestTexturedMeta] = path;
				this.iconSidesEastS = tmp;
			}
			this.iconSidesEastS[meta] = path;
			break;
		case "west":
			if(this.highestTexturedMeta < meta){
				this.highestTexturedMeta = meta;
				String[] tmp = new String[this.highestTexturedMeta + 1];
				for(int i = 0; i < this.highestTexturedMeta - 1; i++){
					tmp[i] = this.iconSidesWestS[i];
				}
				tmp[this.highestTexturedMeta] = path;
				this.iconSidesWestS = tmp;
			}
			this.iconSidesWestS[meta] = path;
			break;
		case "top":
			if(this.highestTexturedMeta < meta){
				this.highestTexturedMeta = meta;
				String[] tmp = new String[this.highestTexturedMeta + 1];
				for(int i = 0; i < this.highestTexturedMeta - 1; i++){
					tmp[i] = this.iconTopS[i];
				}
				tmp[this.highestTexturedMeta] = path;
				this.iconTopS = tmp;
			}
			this.iconTopS[meta] = path;
			break;
		case "base":
			if(this.highestTexturedMeta < meta){
				this.highestTexturedMeta = meta;
				String[] tmp = new String[this.highestTexturedMeta + 1];
				for(int i = 0; i < this.highestTexturedMeta - 1; i++){
					tmp[i] = this.iconBaseS[i];
				}
				tmp[this.highestTexturedMeta] = path;
				this.iconBaseS = tmp;
			}
			this.iconBaseS[meta] = path;
			break;
		default:
			if(this.highestTexturedMeta < meta){
				this.highestTexturedMeta = meta;
				String[] tmp = new String[this.highestTexturedMeta + 1];
				for(int i = 0; i < this.highestTexturedMeta - 1; i++){
					tmp[i] = this.iconSidesNorthS[i];
				}
				tmp[this.highestTexturedMeta] = path;
				this.iconSidesNorthS = tmp;
			}
			this.iconSidesNorthS[meta] = path;
			break;
		}
	}
}
