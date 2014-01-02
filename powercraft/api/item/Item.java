package powercraft.api.item;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import powercraft.api.PowerCraft;

/**
 * @author James
 * Powercraft's item
 */
public class Item extends net.minecraft.item.Item{
		
	private Icon icon;
	private String iconS;
	
	/**
	 * @param par1 Item ID
	 */
	public Item(int par1) {
		super(par1);
	}

	// MASSIVE todo
	
	/**
	 * The name of the item
	 */
	public String itemName = "";
	
	/**
	 * Call this, after the block is COMPLETE.
	 */
	public final void register(){
		PowerCraft.pc.itemsList.addItem(this);
	}
	
	/**
	 * Set the texture path
	 * @param path The texture path
	 */
	public final void setTexture(String path){
		this.iconS = path;
	}
	
	/**
	 * @param ir The icon register
	 */
	@Override
	public final void registerIcons(IconRegister ir){
		this.icon = ir.registerIcon(this.iconS);
	}
}
