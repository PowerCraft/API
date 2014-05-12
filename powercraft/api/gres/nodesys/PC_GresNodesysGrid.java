package powercraft.api.gres.nodesys;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_Rect;
import powercraft.api.PC_Vec2;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresContainer;
import powercraft.api.gres.history.PC_GresHistory;

@SideOnly(Side.CLIENT)
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
        tessellator.setColorRGBA(0, 0, 0, 128);
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
        tessellator.setColorRGBA(0, 0, 0, 128);
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
        tessellator.setColorRGBA(0, 0, 0, 128);
        tessellator.addVertex(0, 500, 0);
	    tessellator.addVertex(1000, 500, 0);
	    tessellator.addVertex(500, 0, 0);
	    tessellator.addVertex(500, 1000, 0);
	    tessellator.draw();
	    GL11.glPushMatrix();
	    GL11.glLoadIdentity();
	    GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	    for(PC_GresComponent c:this.children){
	    	if(c instanceof PC_IGresNodesysBackgroundDraw){
	    		((PC_IGresNodesysBackgroundDraw)c).drawBackground();
	    	}
	    }
	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	    for(PC_GresComponent c:this.children){
	    	if(c instanceof PC_IGresNodesysLineDraw){
	    		((PC_IGresNodesysLineDraw)c).drawLines();
	    	}
	    }
	    GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(1);
	}

	@Override
	protected boolean handleMouseMove(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		PC_GresNodesysNode.mouseMove(this, mouse.mul(getRecursiveZoom()).add(new PC_Vec2I(getRealLocation())));
		return super.handleMouseMove(mouse, buttons, history);
	}

	@Override
	protected boolean handleMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton, boolean doubleClick, PC_GresHistory history) {
		PC_GresNodesysNode.mouseDownForMove(this, mouse.mul(getRecursiveZoom()).add(new PC_Vec2I(getRealLocation())));
		return super.handleMouseButtonDown(mouse, buttons, eventButton, doubleClick, history);
	}

	@Override
	protected boolean handleMouseButtonUp(PC_Vec2I mouse, int buttons, int eventButton, PC_GresHistory history) {
		PC_GresNodesysNode.mouseUpForMove(this);
		return super.handleMouseButtonUp(mouse, buttons, eventButton, history);
	}
	
	@Override
	protected void tryActionOnKeyTyped(char key, int keyCode, boolean repeat, PC_GresHistory history) {
		if(keyCode == Keyboard.KEY_DELETE){
			for(PC_GresComponent s:PC_GresNodesysNode.selected){
				s.getParent().remove(s);
			}
			PC_GresNodesysNode.selected.clear();
		}else if(keyCode == Keyboard.KEY_P && Keyboard.isKeyDown(Keyboard.KEY_LMENU)){
			for(PC_GresComponent c:PC_GresNodesysNode.selected){
				PC_GresNodesysGrid grid = null;
				PC_GresComponent cc = c;
				while((!(cc instanceof PC_GresNodesysGrid)) && cc!=null){
					cc = cc.getParent();
				}
				grid = (PC_GresNodesysGrid)cc;
				if(grid!=null){
					PC_Vec2 move = c.getRealLocation().sub(grid.getRealLocation());
					c.getParent().removeOnly(c);
					c.setLocation(new PC_Vec2I(move));
					grid.add(c);
					c.moveToTop();
				}
			}
		}
		super.tryActionOnKeyTyped(key, keyCode, repeat, history);
	}
	
}
