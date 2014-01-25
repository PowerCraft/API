package net.powercrafting.API.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;

public abstract class PC_InventoryBlockGeneric extends BlockContainer {

	public PC_InventoryBlockGeneric(int id, Material material,
			String textureName) {
		super(id, material);
	}

}
