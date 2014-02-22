package powercraft.api.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import powercraft.api.block.PC_AbstractBlockBase;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

// Use insted the normal PowerCraft renderer
@Deprecated
public abstract class PC_ISimpleBlockRenderingHandler implements ISimpleBlockRenderingHandler {

	@Override
	public final int getRenderId() {
		return getRenderID();
	}

	public abstract int getRenderID();

	@Override
	public final void renderInventoryBlock(Block block, int metadata, int modelId,
			RenderBlocks renderer) {
		renderInventoryPC_Block((PC_AbstractBlockBase) block, metadata, modelId, renderer);
	}

	public abstract void renderInventoryPC_Block(PC_AbstractBlockBase block, int metadata,
			int modelId, RenderBlocks renderer);

	@Override
	public final boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block,
			int modelId, RenderBlocks renderer) {
		return renderWorldPC_Block(world, x, y, z, (PC_AbstractBlockBase) block, modelId, renderer);
	}

	public abstract boolean renderWorldPC_Block(IBlockAccess world, int x, int y, int z,
			PC_AbstractBlockBase block, int modelId, RenderBlocks renderer);

	@Override
	public final boolean shouldRender3DInInventory(int modelId) {
		return shouldRender3DInPC_Inventory(modelId);
	}

	public abstract boolean shouldRender3DInPC_Inventory(int modelId);
}
