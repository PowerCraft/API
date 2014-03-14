package powercraft.api.dimension;

import net.minecraftforge.common.DimensionManager;



public class PC_Dimension {
	
	protected int id;
	protected Class<? extends PC_WorldProvider> worldProvider;
	protected boolean keepLoaded; 
	
	public PC_Dimension(Class<? extends PC_WorldProvider> worldProvider){
		this.worldProvider = worldProvider;
		PC_Dimensions.addDimensions(this);
		this.id = worldProvider.getClass().getName().hashCode();
	}
	
	public PC_Dimension(Class<? extends PC_WorldProvider> worldProvider, int id){
		this.worldProvider = worldProvider;
		PC_Dimensions.addDimensions(this);
		this.id = id;
	}

	public int getID(){
		return this.id;
	}
	
	public final Class<? extends PC_WorldProvider> getWorldProvider(){
		return this.worldProvider;
	}
	
	public boolean keepLoaded(){
		return this.keepLoaded;
	}
	
	void construct() {
		DimensionManager.registerProviderType(getID(), getWorldProvider(), keepLoaded());
	}
	
}
