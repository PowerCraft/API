package powercraft.api.block.multiblock;

import net.minecraft.block.material.Material;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_TileEntity;

public final class PC_BlockMultiblock extends PC_BlockTileEntity {

	public PC_BlockMultiblock(Material material) {
		super(material);
	}
	
	@Override
	public Class<? extends PC_TileEntity> getTileEntityClass() {
		return PC_TileEntityMultiblock.class;
	}
	
}
