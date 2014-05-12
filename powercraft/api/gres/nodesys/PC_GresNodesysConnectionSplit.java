package powercraft.api.gres.nodesys;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import powercraft.api.PC_Rect;
import powercraft.api.PC_Vec2;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresContainer;
import powercraft.api.gres.PC_GresRenderer;
import powercraft.api.gres.history.PC_GresHistory;


public class PC_GresNodesysConnectionSplit extends PC_GresComponent implements PC_IGresNodesysConnection, PC_IGresNodesysLineDraw {
	
	public static final int RADIUS_DETECTION = 4;
	
	private static final PC_Vec2 mousePos = new PC_Vec2();
	
	private PC_IGresNodesysConnection input;
	
	private boolean makeConnection;
	
	private List<PC_IGresNodesysConnection> outputs = new ArrayList<PC_IGresNodesysConnection>();
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		return new PC_Vec2I(PC_GresNodesysConnection.RADIUS_DETECTION*2, PC_GresNodesysConnection.RADIUS_DETECTION*2);
	}
	
	@Override
	protected PC_Vec2I calculateMaxSize() {
		return new PC_Vec2I(PC_GresNodesysConnection.RADIUS_DETECTION*2, PC_GresNodesysConnection.RADIUS_DETECTION*2);
	}
	
	@Override
	protected PC_Vec2I calculatePrefSize() {
		return new PC_Vec2I(PC_GresNodesysConnection.RADIUS_DETECTION*2, PC_GresNodesysConnection.RADIUS_DETECTION*2);
	}
	
	@Override
	protected void paint(PC_Rect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
		PC_GresRenderer.drawRect(2, 2, 6, 6, 0xFFFFFFFF);
	}

	@Override
	public int getCompGroup() {
		return this.input == null ? this.outputs.isEmpty() ? -1 : this.outputs.get(0).getCompGroup() : this.input.getCompGroup();
	}
	
	@Override
	public PC_GresNodesysNode getNode() {
		return null;
	}
	
	@Override
	public int getType(boolean fromThis) {
		return fromThis?2:3;
	}

	@Override
	public PC_Vec2 getPosOnScreen() {
		PC_Vec2 rl = getRealLocation();
	    float zoom = getRecursiveZoom();
	    return rl.add(RADIUS_DETECTION*zoom);
	}
	
	@Override
	public void removeConnection(PC_IGresNodesysConnection con) {
		if(this.input==con){
			this.input = null;
			con.removeConnection(this);
		}else if(this.outputs.remove(con)){
			con.removeConnection(this);
		}
	}

	@Override
	public void addConnection(PC_IGresNodesysConnection con, boolean asInput) {
		if(asInput){
			if(this.input==con)
				return;
			if(this.input!=null){
				PC_IGresNodesysConnection in = this.input;
				this.input = null;
				in.removeConnection(this);
			}
			this.input = con;
			con.addConnection(this, false);
		}else if(!this.outputs.contains(con)){
			this.outputs.add(con);
			con.addConnection(this, true);
		}
	}

	@Override
	protected boolean handleMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton, boolean doubleClick, PC_GresHistory history) {
		if(eventButton==0){
			this.makeConnection = true;
		}else if(eventButton==1){
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)){
				PC_GresNodesysNode.selected.remove(this);
			}else{
				PC_GresNodesysNode.selected.clear();
			}
			PC_GresNodesysNode.selected.add(this);
			PC_GresNodesysNode.mouseDownForMove(this, mouse.mul(getRecursiveZoom()).add(new PC_Vec2I(getRealLocation())));
		}
		return super.handleMouseButtonDown(mouse, buttons, eventButton, doubleClick, history);
	}

	@Override
	protected boolean handleMouseMove(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		if(this.makeConnection){
			mousePos.setTo(new PC_Vec2(mouse).mul(getRecursiveZoom()).add(getRealLocation()));
		}
		PC_GresNodesysNode.mouseMove(this, mouse.mul(getRecursiveZoom()).add(new PC_Vec2I(getRealLocation())));
		return super.handleMouseMove(mouse, buttons, history);
	}
	
	@Override
	protected boolean handleMouseButtonUp(PC_Vec2I mouse, int buttons, int eventButton, PC_GresHistory history) {
		if(eventButton==0){
			PC_Vec2I pos = mouse.mul(getRecursiveZoom()).add(new PC_Vec2I(getRealLocation()));
			PC_GresComponent c = getGuiHandler().getComponentAtPosition(pos);
			PC_IGresNodesysConnection nc = PC_GresNodesysHelper.getConnection(this, c);
			if(nc!=null){
				this.outputs.add(nc);
				nc.addConnection(this, true);
			}
			this.makeConnection = false;
		}else{
			PC_GresNodesysNode.mouseUpForMove(this);
		}
		return super.handleMouseButtonUp(mouse, buttons, eventButton, history);
	}
	
	@Override
	protected void handleMouseLeave(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		this.mouseOver = false;
	}
	
	@Override
	public void drawLines(boolean pre) {
		if((this.mouseDown && this.makeConnection) || this.input!=null){
			GL11.glLineWidth(3);
		    Tessellator tessellator = Tessellator.instance;
		    tessellator.startDrawing(GL11.GL_LINES);
	        tessellator.setColorRGBA(0, 0, 0, 255);
	        if(this.input!=null && pre){
	        	PC_Vec2 rl = getPosOnScreen();
		        tessellator.addVertex(rl.x, rl.y, 0);
		        rl = this.input.getPosOnScreen();
		        tessellator.addVertex(rl.x, rl.y, 0);
	        }
	        if(this.mouseDown && this.makeConnection && !pre){
		        PC_GresComponent c = getGuiHandler().getComponentAtPosition(new PC_Vec2I(mousePos));
				PC_IGresNodesysConnection nc = PC_GresNodesysHelper.getConnection(this, c);
				if(nc==null){
					tessellator.addVertex(mousePos.x, mousePos.y, 0);
				}else{
					PC_Vec2 rl = nc.getPosOnScreen();
				    tessellator.addVertex(rl.x, rl.y, 0);
				}
			    PC_Vec2 rl = getPosOnScreen();
			    tessellator.addVertex(rl.x, rl.y, 0);
	        }
			tessellator.draw();
		}
	}

	public void removeAllConnections(){
		while(!this.outputs.isEmpty()){
			removeConnection(this.outputs.get(0));
		}
		if(this.input!=null)
			removeConnection(this.input);
	}
	
	@Override
	protected void setParent(PC_GresContainer parent) {
		super.setParent(parent);
		if(this.parent==null){
			removeAllConnections();
		}
	}
	
}
