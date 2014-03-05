package powercraft.api;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import powercraft.api.reflect.PC_Security;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class PC_ResourceReloadListener {
	
	private static List<PC_IResourceReloadListener> listeners = new ArrayList<PC_IResourceReloadListener>();
	
	@SideOnly(Side.CLIENT)
	static void register(){
		PC_Security.allowedCaller("PC_ResourceListener.register()", PC_ClientUtils.class);
		((IReloadableResourceManager)PC_ClientUtils.mc().getResourceManager()).registerReloadListener(PC_ResourceListener.INSTANCE);
	}
	
	private PC_ResourceReloadListener(){}

	static void onResourceReload(){
		for(PC_IResourceReloadListener listener:listeners){
			listener.onResourceReload();
		}
	}
	
	public static void registerResourceReloadListener(PC_IResourceReloadListener listener){
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	
	@SideOnly(Side.CLIENT)
	private static final class PC_ResourceListener implements IResourceManagerReloadListener {

		public static final PC_ResourceListener INSTANCE = new PC_ResourceListener();
		
		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) {
			onResourceReload();
		}
		
	}
	
	public static interface PC_IResourceReloadListener {

		public void onResourceReload();
		
	}
	
}
