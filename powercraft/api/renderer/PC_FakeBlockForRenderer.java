package powercraft.api.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import powercraft.api.PC_Utils;

final class PC_FakeBlockForRenderer extends Block {

	PC_FakeBlockForRenderer() {
		super(Material.ground);
	}

	IIcon[] icons;
	int colorMultiplier;
	
	@Override
	public int getMixedBrightnessForBlock(IBlockAccess world, int x, int y, int z) {
		return PC_Utils.getBlock(world, x, y, z).getMixedBrightnessForBlock(world, x, y, z);
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		return getIcon(side, 0) != null;
	}

	@Override
	public IIcon getIcon(int side, int metadata) {
		if (this.icons == null) return null;
		if (side >= this.icons.length) return this.icons[this.icons.length - 1];
		return this.icons[side];
	}

	@Override
	public int getRenderColor(int metadata) {
		return this.colorMultiplier;
	}

	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		return this.colorMultiplier;
	}
	
}
