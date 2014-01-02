package powercraft.api.creativetab;

/**
 * @author James
 */
public class CreativeTabUtil {
	
	/**
	 * @param name The name of the creative tab
	 * @return The creative tab by the given name
	 */
	public static net.minecraft.creativetab.CreativeTabs getCreativeTabFromName(String name)
	{
		switch(name){
		case "blocks":
			return net.minecraft.creativetab.CreativeTabs.tabBlock;
		case "brewing":
			return net.minecraft.creativetab.CreativeTabs.tabBrewing;
		case "combat":
			return net.minecraft.creativetab.CreativeTabs.tabCombat;
		case "decorations":
			return net.minecraft.creativetab.CreativeTabs.tabDecorations;
		case "food":
			return net.minecraft.creativetab.CreativeTabs.tabFood;
		case "materials":
			return net.minecraft.creativetab.CreativeTabs.tabMaterials;
		case "misc":
			return net.minecraft.creativetab.CreativeTabs.tabMisc;
		case "redstone":
			return net.minecraft.creativetab.CreativeTabs.tabRedstone;
		case "tools":
			return net.minecraft.creativetab.CreativeTabs.tabTools;
		case "transport":
			return net.minecraft.creativetab.CreativeTabs.tabTransport;
		default:
			return null;
		}
	}
}
