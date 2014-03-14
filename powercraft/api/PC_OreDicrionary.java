package powercraft.api;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;


public class PC_OreDicrionary {

    public static int getOreID(String name){
    	return OreDictionary.getOreID(name);
    }

    public static String getOreName(int id){
        return OreDictionary.getOreName(id);
    }
    
    public static int getOreID(ItemStack itemStack){
        return OreDictionary.getOreID(itemStack);
    }

    public static ArrayList<ItemStack> getOres(String name){
        return OreDictionary.getOres(name);
    }

    public static String[] getOreNames(){
        return OreDictionary.getOreNames();
    }

    public static ArrayList<ItemStack> getOres(int id){
        return OreDictionary.getOres(Integer.valueOf(id));
    }

    public static boolean itemMatches(ItemStack target, ItemStack input, boolean strict){
        return OreDictionary.itemMatches(target, input, strict);
    }
	
}
