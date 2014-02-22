package powercraft.api.multiblock.conduit;

import powercraft.api.multiblock.PC_MultiblockItem;
import powercraft.api.multiblock.PC_MultiblockType;

public abstract class PC_MultiblockItemConduit extends PC_MultiblockItem {

	@Override
	public PC_MultiblockType getMultiblockType() {
		return PC_MultiblockType.CENTER;
	}
	
}
