package powercraft.api.redstone.multiblock;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import powercraft.api.PC_IconRegistry;
import powercraft.api.multiblock.PC_MultiblockObject;
import powercraft.api.multiblock.cable.PC_MultiblockItemCable;


public class PC_MultiblockItemRedstoneCable extends PC_MultiblockItemCable {
	
	static IIcon icon;
	
	public PC_MultiblockItemRedstoneCable(){
		setCreativeTab(CreativeTabs.tabRedstone);
	}
	
	@Override
	public Class<? extends PC_MultiblockObject> getMultiblockObjectClass() {
		return PC_MultiblockObjectRedstoneCable.class;
	}

	@Override
	public void loadMultiblockIcons(PC_IconRegistry iconRegistry) {
		icon = iconRegistry.registerIcon("redstone");
	}

	@Override
	public void registerIcons(PC_IconRegistry iconRegistry) {
		this.itemIcon = iconRegistry.registerIcon("item");
	}
	
}
