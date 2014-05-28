package powercraft.api.gres.nodesys;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import powercraft.api.PC_Rect;
import powercraft.api.PC_Vec2;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresContainer;
import powercraft.api.gres.history.PC_GresHistory;
import powercraft.api.nodesys.PC_NodeGrid;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_GresNodesysGrid extends PC_GresContainer {

	public final List<PC_GresComponent> selected = new ArrayList<PC_GresComponent>();
	
	private PC_GresComponent moveHandler;
	
	private final PC_Vec2I lastMousePos = new PC_Vec2I(0, 0);
	
	private PC_NodeGrid grid;
	
	public PC_GresNodesysGrid(PC_NodeGrid grid) {
		this.grid = grid;
	}

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
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(50, 50, 50, 200);
        tessellator.addVertex(0, 1000, 0);
    	tessellator.addVertex(1000, 1000, 0);
    	tessellator.addVertex(1000, 0, 0);
        tessellator.addVertex(0, 0, 0);
        tessellator.draw();
        if(getParent().getChildren().get(0)==this){
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
        }
	    GL11.glPushMatrix();
	    GL11.glLoadIdentity();
	    GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	    ListIterator<PC_GresComponent> li = this.children.listIterator(this.children.size());
		while(li.hasPrevious()){
			PC_GresComponent c = li.previous();
			if(c instanceof PC_IGresNodesysBackgroundDraw){
	    		((PC_IGresNodesysBackgroundDraw)c).drawBackground();
	    	}
		}
	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	    for(PC_GresComponent c:this.children){
	    	if(c instanceof PC_IGresNodesysLineDraw){
	    		((PC_IGresNodesysLineDraw)c).drawLines(true);
	    	}
	    }
	    GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(1);
	}
	
	@Override
	protected void postPaint(PC_Rect scissor, double scale, int displayHeight, float timeStamp, float zoom){
		GL11.glPushMatrix();
	    GL11.glLoadIdentity();
	    GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	    for(PC_GresComponent c:this.children){
	    	if(c instanceof PC_IGresNodesysLineDraw){
	    		((PC_IGresNodesysLineDraw)c).drawLines(false);
	    	}
	    }
	    GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(1);
	}

	@Override
	protected boolean handleMouseMove(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		mouseMove(this, mouse.mul(getRecursiveZoom()).add(new PC_Vec2I(getRealLocation())));
		return super.handleMouseMove(mouse, buttons, history);
	}

	@Override
	protected boolean handleMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton, boolean doubleClick, PC_GresHistory history) {
		mouseDownForMove(this, mouse.mul(getRecursiveZoom()).add(new PC_Vec2I(getRealLocation())));
		return super.handleMouseButtonDown(mouse, buttons, eventButton, doubleClick, history);
	}

	@Override
	protected boolean handleMouseButtonUp(PC_Vec2I mouse, int buttons, int eventButton, PC_GresHistory history) {
		mouseUpForMove(this);
		return super.handleMouseButtonUp(mouse, buttons, eventButton, history);
	}
	
	@Override
	protected boolean handleKeyTyped(char key, int keyCode, boolean repeat, PC_GresHistory history) {
		if(keyCode == Keyboard.KEY_DELETE){
			for(PC_GresComponent s:this.selected){
				s.getParent().remove(s);
			}
			this.selected.clear();
			return true;
		}else if(keyCode == Keyboard.KEY_P && Keyboard.isKeyDown(Keyboard.KEY_LMENU)){
			for(PC_GresComponent c:this.selected){
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
			return true;
		}else if(keyCode == Keyboard.KEY_TAB){
			if(getParent()!=null && getParent().getChildren().size()>1){
				PC_GresContainer p = getParent();
				p.remove(this);
				PC_GresNodesysGrid g = (PC_GresNodesysGrid)p.getChildren().get(0);
				if(g.selected.isEmpty()){
					g.takeFocus();
				}else{
					g.selected.get(g.selected.size()-1).takeFocus();
				}
			}
		}
		return super.handleKeyTyped(key, keyCode, repeat, history);
	}

	@Override
	public float getZoom() {
		return getParent() instanceof PC_GresNodesysGridView?((PC_GresNodesysGridView)getParent()).getComponentZoom():1;
	}
	
	public static void mouseDownForMove(PC_GresComponent mh, PC_Vec2I mouse){
		PC_GresNodesysGrid grid = PC_GresNodesysGrid.gridFor(mh);
		if(grid != null && grid.moveHandler==null){
			grid.moveHandler = mh;
			grid.lastMousePos.setTo(mouse);
		}
	}
	
	public static void mouseUpForMove(PC_GresComponent mh){
		PC_GresNodesysGrid grid = PC_GresNodesysGrid.gridFor(mh);
		if(grid!=null && grid.moveHandler == mh){
			grid.moveHandler = null;
			if(mh.getGuiHandler()!=null){
				List<PC_GresComponent> list = new ArrayList<PC_GresComponent>();
				grid.getComponentsAtPosition(grid.lastMousePos.sub(new PC_Vec2I(grid.getRealLocation())), list);
				for(PC_GresComponent c:list){
					if(c instanceof PC_GresNodesysNodeFrame && !grid.selected.contains(c)){
						PC_GresNodesysNodeFrame f = (PC_GresNodesysNodeFrame) c;
						for(PC_GresComponent cc:grid.selected){
							if(cc.canAddTo(f)){
								PC_Vec2 move = cc.getRealLocation().sub(f.getRealLocation());
								cc.getParent().removeOnly(cc);
								cc.setLocation(new PC_Vec2I(move).sub(f.getFrame().getLocation()));
								f.add(cc);
								cc.moveToTop();
							}
						}
						break;
					}
				}
			}
		}
	}
	
	public static void mouseMove(PC_GresComponent mh, PC_Vec2I mouse){
		PC_GresNodesysGrid grid = PC_GresNodesysGrid.gridFor(mh);
		if(grid.moveHandler==mh){
			PC_Vec2I move = mouse.sub(grid.lastMousePos);
			grid.lastMousePos.setTo(mouse);
			for(PC_GresComponent c:grid.selected){
				boolean allOk = true;
				for(PC_GresComponent cc:grid.selected){
					if(!c.canAddTo(cc) && c!=cc){
						allOk = false;
						break;
					}
				}
				if(allOk){
					c.setLocation(c.getLocation().add(move.div(c.getRecursiveZoom())));
				}
			}
		}
	}
	
	public static PC_GresNodesysGrid gridFor(PC_GresComponent c){
		PC_GresComponent cc = c;
		while(!(cc instanceof PC_GresNodesysGrid) && cc!=null){
			cc = cc.getParent();
		}
		return (PC_GresNodesysGrid) cc;
	}

	@Override
	public PC_GresComponent getComponentAtPosition(PC_Vec2I position) {
		List<PC_GresComponent> list = new ArrayList<PC_GresComponent>();
		getComponentsAtPosition(position, list);
		if(list.isEmpty()){
			return this;
		}
		for(PC_GresComponent c:list){
			if(!(c instanceof PC_GresNodesysNodeFrame || c instanceof PC_GresNodesysGrid)){
				return c;
			}
		}
		return list.get(0);
	}

	public PC_NodeGrid getGrid() {
		return grid;
	}
	
}
