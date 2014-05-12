package powercraft.api.gres.nodesys;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import powercraft.api.PC_Rect;
import powercraft.api.PC_Vec2;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresRenderer;
import powercraft.api.gres.history.PC_GresHistory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_GresNodesysConnection extends PC_GresComponent implements PC_IGresNodesysLineDraw, PC_IGresNodesysConnection {
	
	public static final int RADIUS_DETECTION = 4;
	
	private static final PC_Vec2 mousePos = new PC_Vec2();
	
	private boolean isInput;
	private boolean left;
	private int color;
	private int compGroup;
	private List<PC_IGresNodesysConnection> connections = new ArrayList<PC_IGresNodesysConnection>();
	
	public PC_GresNodesysConnection(boolean isInput, boolean left, int color, int compGroup){
		this.isInput = isInput;
		this.left = left;
		this.color = color;
		this.compGroup = compGroup;
	}
	
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
		int radius = getRadius();
		PC_GresRenderer.drawRect(2, radius-2, 6, radius+2, this.color);
	}

	public boolean isLeft() {
		return this.left;
	}

	@Override
	protected boolean handleMouseButtonUp(PC_Vec2I mouse, int buttons, int eventButton, PC_GresHistory history) {
		PC_Vec2I pos = mouse.mul(getRecursiveZoom()).add(new PC_Vec2I(getRealLocation()));
		PC_GresComponent c = getGuiHandler().getComponentAtPosition(pos);
		PC_IGresNodesysConnection nc;
		if(!this.connections.isEmpty() && this.isInput){
			nc = PC_GresNodesysHelper.getConnection(this.connections.get(0), c);
		}else{
			nc = PC_GresNodesysHelper.getConnection(this, c);
		}
		if(nc==null){
			if(!this.connections.isEmpty() && this.isInput){
				removeConnection(this.connections.get(0));
			}
		}else{
			if(this.connections.isEmpty() || !this.isInput){
				this.connections.add(nc);
				nc.addConnection(this, !this.isInput);
			}else{
				PC_IGresNodesysConnection to = this.connections.get(0);
				removeConnection(to);
				to.addConnection(nc, false);
			}
		}
		return super.handleMouseButtonUp(mouse, buttons, eventButton, history);
	}
	
	@Override
	protected void handleMouseLeave(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		this.mouseOver = false;
	}

	@Override
	protected boolean handleMouseMove(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		mousePos.setTo(new PC_Vec2(mouse).mul(getRecursiveZoom()).add(getRealLocation()));
		return super.handleMouseMove(mouse, buttons, history);
	}

	@Override
	public void addConnection(PC_IGresNodesysConnection con, boolean asInput) {
		if(this.connections.contains(con))
			return;
		if(this.isInput && !this.connections.isEmpty()){
			removeConnection(this.connections.get(0));
		}
		this.connections.add(con);
		con.addConnection(this, !asInput);
	}

	@Override
	public void removeConnection(PC_IGresNodesysConnection con){
		if(this.connections.remove(con)){
			con.removeConnection(this);
		}
	}
	
	public void removeAllConnections(){
		while(!this.connections.isEmpty()){
			removeConnection(this.connections.get(0));
		}
	}

	public int getRadius(){
		return this.rect.height/2;
	}
	
	@Override
	public void drawLines(boolean pre) {
		GL11.glLineWidth(3);
	    Tessellator tessellator = Tessellator.instance;
	    tessellator.startDrawing(GL11.GL_LINES);
        tessellator.setColorRGBA(0, 0, 0, 255);
		if(this.isInput && !this.connections.isEmpty()){
			PC_IGresNodesysConnection con = this.connections.get(0);
			if(this.mouseDown){
				if(!pre){
					PC_GresComponent c = getGuiHandler().getComponentAtPosition(new PC_Vec2I(mousePos));
					PC_IGresNodesysConnection nc = PC_GresNodesysHelper.getConnection(con, c);
					if(nc==null){
						tessellator.addVertex(mousePos.x, mousePos.y, 0);
					}else{
						PC_Vec2 rl = nc.getPosOnScreen();
				        tessellator.addVertex(rl.x, rl.y, 0);
					}
			        PC_Vec2 rl = con.getPosOnScreen();
			        tessellator.addVertex(rl.x, rl.y, 0);
				}
			}else if(pre){
		        PC_Vec2 rl = getPosOnScreen();
		        tessellator.addVertex(rl.x, rl.y, 0);
		        rl = con.getPosOnScreen();
		        tessellator.addVertex(rl.x, rl.y, 0);
			}
		}else if(this.mouseDown && !pre){
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

	public boolean isInput() {
		return this.isInput;
	}

	public boolean isConnected() {
		return !this.connections.isEmpty();
	}

	public void setMidP(int x, float y) {
		this.rect.setLocation(new PC_Vec2I(x-4, (int) (y-3)));
		this.rect.setSize(new PC_Vec2I(8, 6));
	}

	@Override
	public int getCompGroup() {
		return this.compGroup;
	}

	@Override
	public PC_GresNodesysNode getNode() {
		return (PC_GresNodesysNode)getParent().getParent();
	}

	@Override
	public int getType(boolean fromThis) {
		return this.isInput?1:2;
	}

	@Override
	public PC_Vec2 getPosOnScreen() {
		PC_Vec2 rl = getRealLocation();
	    float zoom = getRecursiveZoom();
	    rl.x += RADIUS_DETECTION*zoom;
	    rl.y += getRadius()*zoom;
	    return rl;
	}
	
}
