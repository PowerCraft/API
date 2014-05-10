package powercraft.api.gres.nodesys;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import powercraft.api.PC_Rect;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresContainer;
import powercraft.api.gres.history.PC_GresHistory;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import powercraft.api.gres.layout.PC_IGresLayout;


public class PC_GresNodesysNode extends PC_GresContainer implements PC_IGresNodesysLineDraw {
	
	private static final String textureName = "Node";
	
	private static final List<PC_GresComponent> selected = new ArrayList<PC_GresComponent>();
	
	private static final PC_Vec2I lastMousePos = new PC_Vec2I(0, 0);
	
	public PC_GresNodesysNode(String name){
		this.frame.y = 13;
		super.setLayout(new PC_GresLayoutVertical());
		setText(name);
		setSize(getTextureMinSize(textureName).max(fontRenderer.getStringSize(this.text).add(4, 0)).add(PC_GresNodesysConnection.RADIUS_DETECTION*2, 0));
	}
	
	@Override
	public PC_GresNodesysNode setLayout(PC_IGresLayout layout){
		return this;
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		return getTextureMinSize(textureName).max(fontRenderer.getStringSize(this.text).add(4, 0)).add(PC_GresNodesysConnection.RADIUS_DETECTION*2, 0);
	}
	
	@Override
	protected PC_Vec2I calculateMaxSize() {
		return new PC_Vec2I(-1, -1);
	}
	
	@Override
	protected PC_Vec2I calculatePrefSize() {
		return getTextureDefaultSize(textureName).max(fontRenderer.getStringSize(this.text).add(4, 0)).add(PC_GresNodesysConnection.RADIUS_DETECTION*2, 0);
	}
	
	@Override
	protected void paint(PC_Rect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
		drawTexture(textureName, PC_GresNodesysConnection.RADIUS_DETECTION, 0, this.rect.width-PC_GresNodesysConnection.RADIUS_DETECTION*2, this.rect.height);
		drawString(this.text, PC_GresNodesysConnection.RADIUS_DETECTION+2, 0, false);
	}

	@Override
	public int getCState(){
		return this.enabled && this.parentEnabled ? selected.contains(this) ? selected.get(selected.size()-1)==this ? 1 : 2 : 0 : 3;
	}
	
	@Override
	protected boolean handleMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton, boolean doubleClick, PC_GresHistory history) {
		if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)){
			selected.remove(this);
		}else{
			selected.clear();
		}
		selected.add(this);
		this.mouseDown = this.enabled && this.parentEnabled;
		lastMousePos.setTo(mouse.mul(getRecursiveZoom()).add(new PC_Vec2I(getRealLocation())));
		return true;
	}

	@Override
	protected boolean handleMouseMove(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		if((buttons&1)!=0){
			PC_Vec2I m = mouse.mul(getRecursiveZoom()).add(new PC_Vec2I(getRealLocation()));
			PC_Vec2I move = m.sub(lastMousePos);
			lastMousePos.setTo(m);
			for(PC_GresComponent c:selected){
				c.setLocation(c.getLocation().add(move.div(c.getRecursiveZoom())));
			}
		}
		return true;
	}

	@Override
	public void drawLines() {
		for(PC_GresComponent c:this.children){
			if(c instanceof PC_IGresNodesysLineDraw){
				((PC_IGresNodesysLineDraw)c).drawLines();
		    }
		}
	}
	
}
