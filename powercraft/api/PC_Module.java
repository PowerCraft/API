package powercraft.api;

import java.io.File;
import java.util.Arrays;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import powercraft.api.PC_ResourceReloadListener.PC_IResourceReloadListener;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;

public abstract class PC_Module implements PC_IResourceReloadListener {
	
	public static final String POWERCRAFT = "PowerCraft";
	public static final String POWERCRAFT_URL = "http://powercrafting.net";
	public static final String POWERCRAFT_LOGOFILE = "/powercraft/PowerCraft.png";
	public static final String[] POWERCRAFT_AUTHORS = { "XOR", "Rapus", "Buggi", "zcraftler" };
	public static final String POWERCRAFT_CREDITS = "MightyPork, RxD, LOLerul2";

	private final PC_CreativeTab creativeTab;
	private final ModContainer mod;
	private final Configuration config;

	public PC_Module() {
		PC_Modules.addModule(this);
		this.creativeTab = new PC_CreativeTab(Loader.instance().activeModContainer().getName(), this);
		this.mod = PC_Utils.getActiveMod();
		ModMetadata metadata = getMetadata();
		metadata.autogenerated = false;
		metadata.url = POWERCRAFT_URL;
		metadata.logoFile = POWERCRAFT_LOGOFILE;
		metadata.description = PC_Lang.translate("desk."+this.mod.getName());
		metadata.authorList = Arrays.asList(POWERCRAFT_AUTHORS);
		metadata.credits = POWERCRAFT_CREDITS;
		if (PC_Api.INSTANCE != null)
			PC_Api.INSTANCE.getMetadata().childMods.add(PC_Utils.getActiveMod());
		this.config = new Configuration(new File(Loader.instance().getConfigDir(), this.mod.getName()
				+ ".cfg"));
		this.config.load();
		moduleBootstrap();
		PC_ResourceReloadListener.registerResourceReloadListener(this);
	}

	protected void moduleBootstrap() {
		//
	}

	public void saveConfig() {
		this.config.save();
	}

	public Configuration getConfig() {
		return this.config;
	}

	/**
	 * get the {@link ModContainer} for this module
	 * 
	 * @return the modContainer or null if none
	 */
	public ModContainer getContainer() {
		return this.mod;
	}

	/**
	 * get the mod metadata
	 * 
	 * @return the metadata
	 */
	public ModMetadata getMetadata() {
		return this.mod.getMetadata();
	}

	public String getName() {
		return getContainer().getName();
	}

	public String getModId() {
		return getContainer().getModId();
	}

	public abstract ItemStack getCreativeTabItemStack();

	public PC_CreativeTab getCreativeTab() {
		return this.creativeTab;
	}

	@Override
	public void onResourceReload() {
		getMetadata().description = PC_Lang.translate("desk."+this.mod.getName());
	}
	
}
