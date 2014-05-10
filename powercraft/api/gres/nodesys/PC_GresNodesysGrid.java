package powercraft.api.gres.nodesys;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import powercraft.api.PC_Rect;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresContainer;


public class PC_GresNodesysGrid extends PC_GresContainer {
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		return new PC_Vec2I(1000, 1000);
	}
	
	@Override
	protected PC_Vec2I calculateMaxSize() {
		return new PC_Vec2I(1000, 1000);
	}
	
	@Override
	protected PC_Vec2I calculatePrefSize() {
		return new PC_Vec2I(1000, 1000);
	}
	
	@Override
	protected void paint(PC_Rect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
		Tessellator tessellator = Tessellator.instance;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(1);
        tessellator.startDrawing(GL11.GL_LINES);
        tessellator.setColorRGBA(0, 0, 0, 255);
        for(int i=0; i<=200; i++){
        	if(i%5!=0){
	        	tessellator.addVertex(0, i*5, 0);
	        	tessellator.addVertex(1000, i*5, 0);
	        	tessellator.addVertex(i*5, 0, 0);
	            tessellator.addVertex(i*5, 1000, 0);
        	}
        }
        tessellator.draw();
        GL11.glLineWidth(2);
        tessellator.startDrawing(GL11.GL_LINES);
        tessellator.setColorRGBA(0, 0, 0, 255);
        for(int i=0; i<=200; i+=5){
        	if(i!=100){
	        	tessellator.addVertex(0, i*5, 0);
	        	tessellator.addVertex(1000, i*5, 0);
	        	tessellator.addVertex(i*5, 0, 0);
	            tessellator.addVertex(i*5, 1000, 0);
        	}
        }
        tessellator.draw();
        GL11.glLineWidth(3);
        tessellator.startDrawing(GL11.GL_LINES);
        tessellator.setColorRGBA(0, 0, 0, 255);
        tessellator.addVertex(0, 500, 0);
	    tessellator.addVertex(1000, 500, 0);
	    tessellator.addVertex(500, 0, 0);
	    tessellator.addVertex(500, 1000, 0);
	    tessellator.draw();
	    GL11.glPushMatrix();
	    GL11.glLoadIdentity();
	    GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
	    for(PC_GresComponent c:this.children){
	    	if(c instanceof PC_IGresNodesysLineDraw){
	    		((PC_IGresNodesysLineDraw)c).drawLines();
	    	}
	    }
	    GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(1);
	}
	
}
