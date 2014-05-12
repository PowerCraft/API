package powercraft.api.gres.nodesys;

import org.lwjgl.opengl.GL11;

import powercraft.api.PC_Rect;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresContainer;


public class PC_GresNodesysNodeFrame extends PC_GresNodesysNode implements PC_IGresNodesysBackgroundDraw {

	public PC_GresNodesysNodeFrame() {
		super("Frame", false);
	}

	private boolean updating;
	
	public void makeToRightSize(){
		if(this.updating){
			return;
		}
		this.updating = true;
		PC_RectI rect = null;
		for(PC_GresComponent c:this.children){
			if(rect==null){
				rect=c.getRect();
			}else{
				rect = rect.enclosing(c.getRect());
			}
		}
		if(rect==null){
			rect = new PC_RectI(0, 0, 100, 100);
		}else{
			rect.x -= 5;
			rect.y -= 5;
			rect.width += 10 + this.frame.x + this.frame.width;
			rect.height += 10 + this.frame.y + this.frame.height;
		}
		for(PC_GresComponent c:this.children){
			c.setLocation(c.getLocation().sub(rect.getLocation()));
		}
		setLocation(getLocation().add(rect.getLocation()));
		setSize(rect.getSize());
		this.updating = false;
	}
	
	@Override
	protected void notifyChange() {
		super.notifyChange();
		makeToRightSize();
	}

	@Override
	protected void setParent(PC_GresContainer parent) {
		PC_GresContainer oldParent = this.parent;
		super.setParent(parent);
		if(parent==null){
			this.updating = true;
			while(!this.children.isEmpty()){
				PC_GresComponent c = this.children.get(0);
				c.setLocation(c.getLocation().add(getLocation()).add(this.frame.getLocation()));
				oldParent.add(c);
				remove(c);
			}
			this.updating = false;
		}
	}
	
	@Override
	protected void paint(PC_Rect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
		//
	}

	@Override
	public void drawBackground() {
		GL11.glPushMatrix();
		PC_Vec2 pos = getRealLocation();
		GL11.glTranslated(pos.x, pos.y, 0);
		float zoom = getRecursiveZoom();
		GL11.glScaled(zoom, zoom, 1);
		super.paint(null, 0, 0, 0, zoom);
		GL11.glPopMatrix();
		for(PC_GresComponent c:this.children){
			if(c instanceof PC_IGresNodesysBackgroundDraw){
				((PC_IGresNodesysBackgroundDraw) c).drawBackground();
			}
		}
	}
	
}
