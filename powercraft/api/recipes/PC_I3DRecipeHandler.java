package powercraft.api.recipes;

import net.minecraft.world.World;
import powercraft.api.recipes.PC_3DRecipe.StructStart;

public interface PC_I3DRecipeHandler {

	public boolean foundStructAt(World world, StructStart structStart);
	
}
