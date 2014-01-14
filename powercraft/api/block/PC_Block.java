package powercraft.api.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import powercraft.api.PowerCraft;
import powercraft.api.material.MaterialUtil;
import powercraft.api.stepsound.StepSoundUtil;
import powercraft.api.tileentity.PC_TileEntity;

/**
 * @author James
 * An independent block
 */
public class PC_Block extends net.minecraft.block.BlockContainer{

	/**
	 * The name of the block
	 */
	public String blockName = "";
	
	private short highestTexturedMeta = 0;
	
	// TODO: Use 2D Array
	private Icon[] iconSidesfront = new Icon[1];
	private Icon[] iconSidesback = new Icon[1];
	private Icon[] iconSidesleft = new Icon[1];
	private Icon[] iconSidesright = new Icon[1];
	private Icon[] iconBase = new Icon[1];
	private Icon[] iconTop = new Icon[1];
	private String[] iconSidesfrontS = new String[1];
	private String[] iconSidesbackS = new String[1];
	private String[] iconSidesrightS = new String[1];
	private String[] iconSidesleftS = new String[1];
	private String[] iconBaseS = new String[1];
	private String[] iconTopS = new String[1];
	
	PC_TileEntity thisTileEntity = null;
	
	/**
	 * @param id The block ID
	 * @param material The material name
	 * @param name The name of the block
	 */
	public PC_Block(int id, String material, String name){
		super(id, MaterialUtil.getMaterialFromName(material));
		this.blockName = name;
	}

	/**
	 * @param id The block ID
	 * @param material The material name
	 * @param name The name of the block
	 * @param te The tile entity to use for this block
	 */
	public PC_Block(int id, String material, String name, PC_TileEntity te){
		super(id, MaterialUtil.getMaterialFromName(material));
		this.blockName = name;
		this.thisTileEntity = te;
	}
	
	/**
	 * Set the stepsound of a block to something
	 * @param step The name of the step sound
	 */
	public final void setStep(String step){
		this.setStepSound(StepSoundUtil.getStepSoundFromName(step));
	}
	
	/**
	 * Register a texture to the game
	 * @param meta The metadata that uses the texture
	 * @param path The path of the texture
	 */
	public final void setTexture(short meta, String path){
		if(this.highestTexturedMeta < meta){
			this.highestTexturedMeta = meta;
			String[] tmp = new String[this.highestTexturedMeta + 1];
			for(int i = 0; i < this.highestTexturedMeta - 1; i++){
				tmp[i] = this.iconSidesfrontS[i];
			}
			tmp[this.highestTexturedMeta] = path;
			this.iconSidesfrontS = tmp;
			for(int i = 0; i < this.highestTexturedMeta - 1; i++){
				tmp[i] = this.iconSidesbackS[i];
			}
			tmp[this.highestTexturedMeta] = path;
			this.iconSidesbackS = tmp;
			for(int i = 0; i < this.highestTexturedMeta - 1; i++){
				tmp[i] = this.iconSidesleftS[i];
			}
			tmp[this.highestTexturedMeta] = path;
			this.iconSidesleftS = tmp;
			for(int i = 0; i < this.highestTexturedMeta - 1; i++){
				tmp[i] = this.iconSidesrightS[i];
			}
			tmp[this.highestTexturedMeta] = path;
			this.iconSidesrightS = tmp;
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
		this.iconSidesfrontS[meta] = path;
		this.iconSidesbackS[meta] = path;
		this.iconSidesleftS[meta] = path;
		this.iconSidesrightS[meta] = path;
		this.iconTopS[meta] = path;
		this.iconBaseS[meta] = path;
	}
	
	/**
	 * Register a texture to the game
	 * @param meta The metadata that uses the texture
	 * @param path The path of the texture
	 * @param side The side of the block to assign to
	 */
	public final void setTexture(short meta, String path, String side){
		switch(side){
		case "front":
			if(this.highestTexturedMeta < meta){
				this.highestTexturedMeta = meta;
				String[] tmp = new String[this.highestTexturedMeta + 1];
				for(int i = 0; i < this.highestTexturedMeta - 1; i++){
					tmp[i] = this.iconSidesfrontS[i];
				}
				tmp[this.highestTexturedMeta] = path;
				this.iconSidesfrontS = tmp;
			}
			this.iconSidesfrontS[meta] = path;
			break;
		case "back":
			if(this.highestTexturedMeta < meta){
				this.highestTexturedMeta = meta;
				String[] tmp = new String[this.highestTexturedMeta + 1];
				for(int i = 0; i < this.highestTexturedMeta - 1; i++){
					tmp[i] = this.iconSidesbackS[i];
				}
				tmp[this.highestTexturedMeta] = path;
				this.iconSidesbackS = tmp;
			}
			this.iconSidesbackS[meta] = path;
			break;
		case "left":
			if(this.highestTexturedMeta < meta){
				this.highestTexturedMeta = meta;
				String[] tmp = new String[this.highestTexturedMeta + 1];
				for(int i = 0; i < this.highestTexturedMeta - 1; i++){
					tmp[i] = this.iconSidesleftS[i];
				}
				tmp[this.highestTexturedMeta] = path;
				this.iconSidesleftS = tmp;
			}
			this.iconSidesleftS[meta] = path;
			break;
		case "right":
			if(this.highestTexturedMeta < meta){
				this.highestTexturedMeta = meta;
				String[] tmp = new String[this.highestTexturedMeta + 1];
				for(int i = 0; i < this.highestTexturedMeta - 1; i++){
					tmp[i] = this.iconSidesrightS[i];
				}
				tmp[this.highestTexturedMeta] = path;
				this.iconSidesrightS = tmp;
			}
			this.iconSidesrightS[meta] = path;
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
					tmp[i] = this.iconSidesfrontS[i];
				}
				tmp[this.highestTexturedMeta] = path;
				this.iconSidesfrontS = tmp;
			}
			this.iconSidesfrontS[meta] = path;
			break;
		}
	}
	
	@Override 
	public final void registerIcons(IconRegister ir){
		for(int i = 0; this.highestTexturedMeta < i + 1; i++){
			this.iconBase[i] = ir.registerIcon(this.iconBaseS[i]);
			this.iconTop[i] = ir.registerIcon(this.iconTopS[i]);
			this.iconSidesfront[i] = ir.registerIcon(this.iconSidesfrontS[i]);
			this.iconSidesback[i] = ir.registerIcon(this.iconSidesbackS[i]);
			this.iconSidesleft[i] = ir.registerIcon(this.iconSidesleftS[i]);
			this.iconSidesright[i] = ir.registerIcon(this.iconSidesrightS[i]);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public final Icon getIcon(int side, int meta){
		// I dont know what the left, right, and back
		// are represented by, so I guessed.
		// If stuff is screwy, blame it on this
		
		switch(side){
		case 0:
			return this.iconBase[meta];
		case 1:
			return this.iconTop[meta];
		case 2:
			return this.iconSidesback[meta];
		case 3:
			// I accidentally screwed up the naming, so left = right, right = left
			return this.iconSidesleft[meta];
		case 4:
			return this.iconSidesfront[meta];
		case 5:
			return this.iconSidesright[meta];
		default:
			return this.iconSidesfront[meta];
		}
	}

	@Override
	public final TileEntity createNewTileEntity(World world) {
		return createNewTileEntity(world.getWorldInfo().getVanillaDimension());
	}
	
	/**
	 * @param dim Dimension ID
	 * @return The tile entity to use
	 */
	public PC_TileEntity createNewTileEntity(int dim){
		return this.thisTileEntity;
	}
	
	/**
	 * Call this, after the block is COMPLETE.
	 */
	public final void register(){
		PowerCraft.pc.blocksList.addBlock(this);
		GameRegistry.registerBlock(this, this.blockName);
		LanguageRegistry.addName(this, this.blockName);
	}
}
