package powercraft.api;

import net.minecraft.item.ItemStack;
import powercraft.api.item.PC_IItem;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public final class PC_FuelHandler implements IFuelHandler {

	public static final PC_FuelHandler INSTANCE = new PC_FuelHandler();
	
	public static void register(){
		PC_Security.allowedCaller("PC_FuelHandler.register()", PC_Api.class);
		GameRegistry.registerFuelHandler(INSTANCE);
	}
	
	private PC_FuelHandler(){
		
	}
	
	@Override
	public int getBurnTime(ItemStack fuel) {
		PC_IItem item = PC_Utils.getItem(fuel, PC_IItem.class);
		if(item!=null){
			return item.getBurnTime(fuel);
		}
		return 0;
	}

}
