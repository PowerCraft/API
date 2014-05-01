package powercraft.api.recipes;

import java.util.ArrayList;
import java.util.List;

import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec3I;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;

public final class PC_Recipes {
	
	private static List<PC_3DRecipe> recipes3d = new ArrayList<PC_3DRecipe>();
	
	public static void addShapedRecipe(ItemStack out, Object...params){
		GameRegistry.addShapedRecipe(out, params);
	}
	
	public static void addShapelessRecipe(ItemStack out, Object...in){
		GameRegistry.addShapelessRecipe(out, in);
	}
	
	public static void addSmelting(ItemStack out, Object in){
		addSmelting(out, in, 0);
	}
	
	public static void addSmelting(ItemStack out, Object in, float xp){
		if(in instanceof Block){
			GameRegistry.addSmelting((Block)in, out, xp);
		}else if(in instanceof Item){
			GameRegistry.addSmelting((Item)in, out, xp);
		}else if(in instanceof ItemStack){
			GameRegistry.addSmelting((ItemStack)in, out, xp);
		}else{
			throw new RuntimeException();
		}
	}
	
	public static void add3DRecipe(PC_I3DRecipeHandler obj, Object...o){
		PC_3DRecipe recipe3d = new PC_3DRecipe(obj, o);
		recipes3d.add(recipe3d);
	}
	
	public static boolean tryToDo3DRecipe(World world, int x, int y, int z) {
		PC_Vec3I pos = new PC_Vec3I(x, y, z);
		for(PC_3DRecipe recipe3d:recipes3d){
			if(recipe3d.isStruct(world, pos)){
				return true;
			}
		}
		return false;
	}
	
	private PC_Recipes(){
		PC_Utils.staticClassConstructor();
	}
	
}
