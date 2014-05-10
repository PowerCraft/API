package powercraft.api.gres.nodesys;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_Rect;
import powercraft.api.PC_Vec2;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresRenderer;
import powercraft.api.gres.history.PC_GresHistory;

@SideOnly(Side.CLIENT)
public class PC_GresNodesysConnection extends PC_GresComponent implements PC_IGresNodesysLineDraw {
	
	public static final int RADIUS_DETECTION = 4;
	
	private static final PC_Vec2 mousePos = new PC_Vec2();
	
	private boolean isInput;
	private boolean left;
	private int color;
	private int compGroup;
	private List<PC_GresNodesysConnection> connection = new ArrayList<PC_GresNodesysConnection>();
	
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
	protected boolean onMouseButtonUp(PC_Vec2I mouse, int buttons, int eventButton, PC_GresHistory history) {
		PC_Vec2I pos = mouse.mul(getRecursiveZoom()).add(new PC_Vec2I(getRealLocation()));
		PC_GresNodesysConnection nc;
		if(!this.connection.isEmpty() && this.isInput){
			nc = getConnectionAt(pos, this.connection.get(0));
		}else{
			nc = getConnectionAt(pos, this);
		}
		if(nc==null){
			if(!this.connection.isEmpty() && this.isInput){
				removeConnection(this.connection.get(0));
			}
		}else{
			if(this.connection.isEmpty() || !this.isInput){
				this.connection.add(nc);
				nc.addConnection(this);
			}else{
				PC_GresNodesysConnection to = this.connection.get(0);
				removeConnection(to);
				to.connection.add(nc);
				nc.addConnection(to);
			}
		}
		return super.onMouseButtonUp(mouse, buttons, eventButton, history);
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

	private void addConnection(PC_GresNodesysConnection con) {
		if(this.isInput && !this.connection.isEmpty()){
			removeConnection(this.connection.get(0));
		}
		this.connection.add(con);
	}

	private void removeConnection(PC_GresNodesysConnection con){
		this.connection.remove(con);
		con.connection.remove(this);
	}
	
	private static PC_GresNodesysConnection getConnectionAt(PC_Vec2I pos, PC_GresNodesysConnection forConnection){
		PC_GresComponent c = forConnection.getGuiHandler().getComponentAtPosition(pos);
		if(c instanceof PC_GresNodesysConnection){
			PC_GresNodesysConnection connection = (PC_GresNodesysConnection)c;
			if(connection.compGroup==forConnection.compGroup && connection.isInput!=forConnection.isInput && connection.getParent().getParent()!=forConnection.getParent().getParent()){
				return connection;
			}
		}
		return null;
	}

	public int getRadius(){
		return this.rect.height/2;
	}
	
	@Override
	public void drawLines() {
		GL11.glLineWidth(3);
	    Tessellator tessellator = Tessellator.instance;
	    tessellator.startDrawing(GL11.GL_LINES);
        tessellator.setColorRGBA(0, 0, 0, 255);
		if(this.isInput && !this.connection.isEmpty()){
			PC_GresNodesysConnection con = this.connection.get(0);
			if(this.mouseDown){
				PC_GresNodesysConnection nc = getConnectionAt(new PC_Vec2I(mousePos), con);
				if(nc==null){
					tessellator.addVertex(mousePos.x, mousePos.y, 0);
				}else{
					PC_Vec2 rl = nc.getRealLocation();
			        float zoom = nc.getRecursiveZoom();
			        tessellator.addVertex(rl.x+RADIUS_DETECTION*zoom, rl.y+nc.getRadius()*zoom, 0);
				}
		        PC_Vec2 rl = con.getRealLocation();
		        float zoom = con.getRecursiveZoom();
		        tessellator.addVertex(rl.x+RADIUS_DETECTION*zoom, rl.y+con.getRadius()*zoom, 0);
			}else{
		        PC_Vec2 rl = getRealLocation();
		        float zoom = getRecursiveZoom();
		        tessellator.addVertex(rl.x+RADIUS_DETECTION*zoom, rl.y+getRadius()*zoom, 0);
		        rl = con.getRealLocation();
		        zoom = con.getRecursiveZoom();
		        tessellator.addVertex(rl.x+RADIUS_DETECTION*zoom, rl.y+con.getRadius()*zoom, 0);
			}
		}else if(this.mouseDown){
			PC_GresNodesysConnection nc = getConnectionAt(new PC_Vec2I(mousePos), this);
			if(nc==null){
				tessellator.addVertex(mousePos.x, mousePos.y, 0);
			}else{
				PC_Vec2 rl = nc.getRealLocation();
		        float zoom = nc.getRecursiveZoom();
		        tessellator.addVertex(rl.x+RADIUS_DETECTION*zoom, rl.y+nc.getRadius()*zoom, 0);
			}
	        PC_Vec2 rl = getRealLocation();
	        float zoom = getRecursiveZoom();
	        tessellator.addVertex(rl.x+RADIUS_DETECTION*zoom, rl.y+getRadius()*zoom, 0);
		}
		tessellator.draw();
	}

	public boolean isInput() {
		return this.isInput;
	}

	public boolean isConnected() {
		return !this.connection.isEmpty();
	}

	public void setMidP(int x, float y) {
		this.rect.setLocation(new PC_Vec2I(x-4, (int) (y-3)));
		this.rect.setSize(new PC_Vec2I(8, 6));
	}
	
}
