package powercraft.api.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import powercraft.api.PC_ClientRegistry;
import powercraft.api.PC_Utils;
import powercraft.api.entity.PC_EntityType;
import powercraft.api.entity.PC_IEntity;


public class PC_EntityRenderer<E extends Entity & PC_IEntity> extends Render {
	
	private PC_EntityType<E> type;
	
	public PC_EntityRenderer(PC_EntityType<E> type){
		this.type = type;
	}
	
	@Override
	public void doRender(Entity entity, double x, double y, double z, float rotYaw, float timeStamp) {
		GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(180f-rotYaw, 0, 1, 0);
		this.type.doRender(this, this.type.getEntity().cast(entity), x, y, z, rotYaw, timeStamp);
		GL11.glPopMatrix();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		String textureFolder = this.type.getTextureFolderName();
		String textureName = this.type.getEntityTextureName(this, this.type.getEntity().cast(entity));
		return PC_Utils.getResourceLocation(this.type.getModule(), "textures/entities/"+textureFolder+"/"+textureName+".png");
	}
	
	@Override
	public boolean isStaticEntity(){
		return this.type.isStaticEntity();
	}
	
	@Override
	public void updateIcons(IIconRegister iconRegister) {
		this.type.registerIcons(PC_ClientRegistry.getIconRegistry(iconRegister, this.type));
	}
	
}
