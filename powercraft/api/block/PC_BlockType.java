package powercraft.api.block;

import powercraft.api.PC_Material;
import net.minecraft.block.material.Material;


public enum PC_BlockType {
	
	MACHINE(PC_Material.MACHINES),
	OTHER(Material.ground);
	
	public final Material material;
	
	PC_BlockType(Material material){
		this.material = material;
	}
	
}
