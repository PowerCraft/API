package powercraft.api.renderer;

import org.lwjgl.opengl.GL11;

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
			GL11.glPushMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslatef((float)x, (float)y, (float)z);
			((PC_ITileEntityRenderer)tileEntity).renderTileEntityAt(this, x, y, z, timeStamp);
			GL11.glPopMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
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
