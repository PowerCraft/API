package powercraft.api;

import java.util.List;

import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;

public abstract class PC_Module {

	public static final String POWERCRAFT = "PowerCraft";
	public static final String POWERCRAFT_URL = "http://powercrafting.net";
	public static final String POWERCRAFT_LOGOFILE = "/powercraft/PowerCraft.png";
	public static final String[] POWERCRAFT_AUTHORS = {"XOR", "Rapus", "Buggi"};
	public static final String POWERCRAFT_CREDITS = "MightyPork, RxD, LOLerul2";
	
	private final PC_CreativeTab creativeTab;
	
	public PC_Module(){
		creativeTab = new PC_CreativeTab(Loader.instance().activeModContainer().getName(), this);
	}
	
	/**
	 * get the {@link ModContainer} for this module
	 * @return the modContainer or null if none
	 */
	public ModContainer getContainer() {
		List<ModContainer> modContainers = Loader.instance().getModList();
		for (ModContainer modContainer : modContainers) {
			if (modContainer.matches(this)) {
				return modContainer;
			}
		}
		return null;
	}

	/**
	 * get the mod metadata
	 * @return the metadata
	 */
	public ModMetadata getMetadata() {
		ModContainer modContainer = getContainer();
		if(modContainer==null)
			return null;
		return modContainer.getMetadata();
	}

	public String getName() {
		return getContainer().getName();
	}
	
	public String getModId() {
		return getContainer().getModId();
	}

	public abstract ItemStack getCreativeTabItemStack();

	public PC_CreativeTab getCreativeTab(){
		return creativeTab;
	}
	
}
