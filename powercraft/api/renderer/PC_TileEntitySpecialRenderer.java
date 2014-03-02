package powercraft.api.renderer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class PC_TileEntitySpecialRenderer extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float timeStamp) {
		if(tileEntity instanceof PC_ITileEntityRenderer){
			((PC_ITileEntityRenderer)tileEntity).renderTielEntityAt(this, x, y, z, timeStamp);
		}
	}

	@Override
	public void bindTexture(ResourceLocation resourceLocation){
		super.bindTexture(resourceLocation);
	}
	
	public FontRenderer getFontRenderer(){
		return func_147498_b();
	}
	
	public TileEntityRendererDispatcher getRenderDispatcher(){
		return this.field_147501_a;
	}
	
}
