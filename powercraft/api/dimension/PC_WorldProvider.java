package powercraft.api.dimension;

import net.minecraft.world.WorldProvider;


public abstract class PC_WorldProvider extends WorldProvider {
	
	protected final PC_Dimension dimension;
	
	public PC_WorldProvider(){
		this.dimension = PC_Dimensions.getDimenstionForProvider(getClass());
	}
	
	public PC_Dimension getDimension(){
		return this.dimension;
	}
	
}
