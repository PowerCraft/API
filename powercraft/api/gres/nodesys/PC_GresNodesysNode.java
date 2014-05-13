package powercraft.api.gres.nodesys;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import powercraft.api.PC_Rect;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresAlign.H;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresContainer;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.history.PC_GresHistory;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import powercraft.api.gres.layout.PC_IGresLayout;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_GresNodesysNode extends PC_GresContainer implements PC_IGresNodesysLineDraw {
	
	private static final String textureName1 = "NodeT";
	private static final String textureName2 = "NodeB";
	private static final String textureName3 = "NodeS";
	
	private static final String arrowRight = "ArrowRight";
	private static final String arrowDown = "ArrowDown";
	
	private boolean isSmall;
	
	private int lastSize;
	
	private boolean canCollaps;
	
	private String rigthButton;
	
	private static final Layout LAYOUT = new Layout();
	
	private static class Layout extends PC_GresLayoutVertical{

		public Layout() {
			
		}
		
		@Override
		public PC_Vec2I getPreferredLayoutSize(PC_GresContainer container) {
			PC_GresNodesysNode node = (PC_GresNodesysNode)container;
			if(node.isSmall()){
				List<PC_GresNodesysConnection> left = new ArrayList<PC_GresNodesysConnection>();
				List<PC_GresNodesysConnection> rigth = new ArrayList<PC_GresNodesysConnection>();
				node.getAllConnections(left, rigth);
				int leftC = left.size();
				int rigthC = rigth.size();
				int max = (leftC>rigthC?leftC:rigthC)*6;
				return node.calculatePrefSize().max(0, max);
			}
			return super.getPreferredLayoutSize(container).max(node.calculatePrefSize());
		}

		@Override
		public PC_Vec2I getMinimumLayoutSize(PC_GresContainer container) {
			PC_GresNodesysNode node = (PC_GresNodesysNode)container;
			if(node.isSmall()){
				List<PC_GresNodesysConnection> left = new ArrayList<PC_GresNodesysConnection>();
				List<PC_GresNodesysConnection> rigth = new ArrayList<PC_GresNodesysConnection>();
				node.getAllConnections(left, rigth);
				int leftC = left.size();
				int rigthC = rigth.size();
				int max = (leftC>rigthC?leftC:rigthC)*6;
				return node.calculatePrefSize().max(0, max);
			}
			return super.getMinimumLayoutSize(container).max(node.calculateMinSize());
		}

		@Override
		public void updateLayout(PC_GresContainer container) {
			PC_GresNodesysNode node = (PC_GresNodesysNode)container;
			if(node.isSmall()){
				PC_RectI rect = node.getChildRect();
				for(PC_GresComponent component : container.getLayoutChildOrder()){
					component.setRect(rect);
				}
				List<PC_GresNodesysConnection> left = new ArrayList<PC_GresNodesysConnection>();
				List<PC_GresNodesysConnection> rigth = new ArrayList<PC_GresNodesysConnection>();
				node.getAllConnections(left, rigth);
				int leftC = left.size();
				int rigthC = rigth.size();
				int max = rect.height;
				if(leftC>0){
					float p = max / (float)leftC;
					float y = p/2;
					for(PC_GresNodesysConnection l:left){
						l.setMidP(4, y);
						y+=p;
					}
				}
				if(rigthC>0){
					float p = max / (float)rigthC;
					float y = p/2;
					for(PC_GresNodesysConnection r:rigth){
						r.setMidP(rect.width-4, y);
						y+=p;
					}
				}
			}else{
				super.updateLayout(container);
			}
		}
	}
	
	public PC_GresNodesysNode(String name){
		this.frame.y = 13;
		super.setLayout(LAYOUT);
		setText(name);
		setSize(calculateMinSize());
		this.canCollaps = true;
		setAlignH(H.LEFT);
	}

	protected PC_GresNodesysNode(String name, boolean canCollaps){
		this.frame.y = 13;
		setText(name);
		setSize(calculateMinSize());
		this.canCollaps = canCollaps;
		setAlignH(H.LEFT);
	}
	
	public void setButtonName(String texName){
		this.rigthButton = texName;
	}
	
	@Override
	public PC_GresNodesysNode setLayout(PC_IGresLayout layout){
		return this;
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		PC_Vec2I size = getTextureDefaultSize(arrowRight);
		PC_Vec2I size2 = getTextureDefaultSize(arrowDown);
		PC_Vec2I max = this.canCollaps?size.max(size2):new PC_Vec2I();
		PC_Vec2I fMax = fontRenderer.getStringSize(this.text);
		max.x += fMax.x+PC_GresNodesysConnection.RADIUS_DETECTION*2+4;
		if(max.y<fMax.y){
			max.y = fMax.y;
		}
		if(this.rigthButton!=null){
			size = getTextureDefaultSize(this.rigthButton);
			max.x += size.x+1;
			if(max.y<size.y){
				max.y = size.y;
			}
		}
		if(this.isSmall){
			max = max.max(getTextureMinSize(textureName3));
		}else{
			max = max.max(getTextureMinSize(textureName1).add(getTextureMinSize(textureName2)));
		}
		return max.add(PC_GresNodesysConnection.RADIUS_DETECTION*2, 0);
	}
	
	@Override
	protected PC_Vec2I calculateMaxSize() {
		return new PC_Vec2I(-1, -1);
	}
	
	@Override
	protected PC_Vec2I calculatePrefSize() {
		PC_Vec2I size = getTextureDefaultSize(arrowRight);
		PC_Vec2I size2 = getTextureDefaultSize(arrowDown);
		PC_Vec2I max = this.canCollaps?size.max(size2):new PC_Vec2I();
		PC_Vec2I fMax = fontRenderer.getStringSize(this.text);
		max.x += fMax.x+PC_GresNodesysConnection.RADIUS_DETECTION*2+4;
		if(max.y<fMax.y){
			max.y = fMax.y;
		}
		if(this.rigthButton!=null){
			size = getTextureDefaultSize(this.rigthButton);
			max.x += size.x+1;
			if(max.y<size.y){
				max.y = size.y;
			}
		}
		if(this.isSmall){
			max = max.max(getTextureDefaultSize(textureName3));
		}else{
			max = max.max(getTextureDefaultSize(textureName1).add(getTextureDefaultSize(textureName2)));
		}
		return max.add(PC_GresNodesysConnection.RADIUS_DETECTION*2, 0);
	}
	
	@Override
	protected void paint(PC_Rect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
		PC_Vec2I size = getTextureDefaultSize(arrowRight);
		PC_Vec2I size2 = getTextureDefaultSize(arrowDown);
		int max = size.x>size2.x?size.x:size2.x;
		if(!this.canCollaps)
			max = 0;
		int h;
		if(this.isSmall){
			h = this.rect.height;
			drawTexture(textureName3, PC_GresNodesysConnection.RADIUS_DETECTION, 0, this.rect.width-PC_GresNodesysConnection.RADIUS_DETECTION*2, this.rect.height);
			if(this.canCollaps)
				drawTexture(arrowRight, PC_GresNodesysConnection.RADIUS_DETECTION*2+(max-size.x)/2, (this.rect.height-size.y)/2, size.x, size.y);
		}else{
			h = this.frame.y;
			drawTexture(textureName1, PC_GresNodesysConnection.RADIUS_DETECTION, 0, this.rect.width-PC_GresNodesysConnection.RADIUS_DETECTION*2, this.frame.y);
			drawTexture(textureName2, PC_GresNodesysConnection.RADIUS_DETECTION, this.frame.y, this.rect.width-PC_GresNodesysConnection.RADIUS_DETECTION*2, this.rect.height-this.frame.y);
			if(this.canCollaps)
				drawTexture(arrowDown, PC_GresNodesysConnection.RADIUS_DETECTION*2+(max-size2.x)/2, (this.frame.y-size2.y)/2, size2.x, size2.y);
		}
		int x = PC_GresNodesysConnection.RADIUS_DETECTION*2+2+max;
		int w = this.rect.width-PC_GresNodesysConnection.RADIUS_DETECTION*2-2-x;
		if(this.rigthButton!=null){
			size = getTextureDefaultSize(this.rigthButton);
			w -= size.x-1;
			drawTexture(this.rigthButton, x+w+1, (h-size.y)/2, size.x, size.y);
		}
		drawString(this.text, x, 0, w, h, this.alignH, this.alignV, false);
	}

	@Override
	public int getCState(){
		List<PC_GresComponent> selected = PC_GresNodesysGrid.gridFor(this).selected;
		return this.enabled && this.parentEnabled ? selected.contains(this) ? selected.get(selected.size()-1)==this ? 1 : 2 : 0 : 3;
	}
	
	@SuppressWarnings("hiding")
	@Override
	protected boolean handleMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton, boolean doubleClick, PC_GresHistory history) {
		PC_Vec2I max = new PC_Vec2I();
		if(this.canCollaps){
			PC_Vec2I size = getTextureDefaultSize(arrowRight);
			PC_Vec2I size2 = getTextureDefaultSize(arrowDown);
			max = size.max(size2);
			PC_RectI rect;
			if(this.isSmall){
				rect = new PC_RectI(PC_GresNodesysConnection.RADIUS_DETECTION*2, (this.rect.height-max.y)/2, max.x, max.y);
			}else{
				rect = new PC_RectI(PC_GresNodesysConnection.RADIUS_DETECTION*2, (this.frame.y-max.y)/2, max.x, max.y);
			}
			if(rect.contains(mouse)){
				setSmall(!this.isSmall);
				return true;
			}
		}
		if(this.rigthButton!=null){
			int h = this.isSmall?this.rect.height:this.frame.y;
			int x = PC_GresNodesysConnection.RADIUS_DETECTION*2+2+max.x;
			int w = this.rect.width-PC_GresNodesysConnection.RADIUS_DETECTION*2-2-x;
			PC_Vec2I size = getTextureDefaultSize(this.rigthButton);
			w -= size.x-1;
			if(new PC_Rect(x+w+1, (h-size.y)/2, size.x, size.y).contains(mouse)){
				buttonPressed();
				return true;
			}
		}
		List<PC_GresComponent> selected = PC_GresNodesysGrid.gridFor(this).selected;
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)){
			selected.remove(this);
		}else{
			selected.clear();
		}
		selected.add(this);
		this.mouseDown = this.enabled && this.parentEnabled;
		PC_GresNodesysGrid.mouseDownForMove(this, mouse.mul(getRecursiveZoom()).add(new PC_Vec2I(getRealLocation())));
		return true;
	}

	protected void buttonPressed(){
		fireEvent(new ButtonPressed(this));
	}
	
	@Override
	protected boolean handleMouseMove(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		PC_GresNodesysGrid.mouseMove(this, mouse.mul(getRecursiveZoom()).add(new PC_Vec2I(getRealLocation())));
		return true;
	}
	
	@Override
	protected boolean handleMouseButtonUp(PC_Vec2I mouse, int buttons, int eventButton, PC_GresHistory history) {
		PC_GresNodesysGrid.mouseUpForMove(this);
		return super.handleMouseButtonUp(mouse, buttons, eventButton, history);
	}
	
	@Override
	public void drawLines(boolean pre) {
		for(PC_GresComponent c:this.children){
			if(c instanceof PC_IGresNodesysLineDraw){
				((PC_IGresNodesysLineDraw)c).drawLines(pre);
		    }
		}
	}
	
	private boolean changing = false;
	
	@Override
	public void notifyChildChange(PC_GresComponent component) {
		if(!this.changing){
			super.notifyChildChange(component);
		}
	}
	
	public void setSmall(boolean isSmall){
		if(this.isSmall!=isSmall){
			this.isSmall = isSmall;
			if(this.isSmall){
				this.frame.y = 0;
			}else{
				this.frame.y = 13;
			}
			int ls = this.lastSize;
			this.lastSize = this.rect.width;
			this.changing = true;
			for(PC_GresComponent c:this.children){
				c.setVisible(!isSmall);
			}
			this.changing = false;
			int lh = this.isSmall?13:this.rect.height;
			setSize(new PC_Vec2I(ls, 0));
			int nh = this.isSmall?this.rect.height:13;
			setLocation(getLocation().add(0, lh/2-nh/2));
			if(!this.isSmall){
				for(PC_GresComponent c:this.children){
					c.setSize(new PC_Vec2I(0, 0));
				}
			}
		}
	}
	
	@Override
	public void add(PC_GresComponent component) {
		int lh = this.rect.height;
		if(this.isSmall){
			this.changing = true;
		}
		super.add(component);
		if(this.isSmall){
			component.setVisible(false);
			setSize(new PC_Vec2I(this.rect.width, 0));
			setLocation(getLocation().add(0, lh/2-this.rect.height/2));
			this.changing = false;
		}
	}

	public boolean isSmall() {
		return this.isSmall;
	}

	@Override
	public PC_GresComponent getComponentAtPosition(PC_Vec2I position) {
		PC_GresComponent c = super.getComponentAtPosition(position);
		if(c==this && (position.x<PC_GresNodesysConnection.RADIUS_DETECTION || position.x>=this.rect.width-PC_GresNodesysConnection.RADIUS_DETECTION)){
			return null;
		}
		return c;
	}

	@Override
	protected void setParent(PC_GresContainer parent) {
		super.setParent(parent);
		if(this.parent==null){
			List<PC_GresNodesysConnection> all = new ArrayList<PC_GresNodesysConnection>();
			getAllConnections(all, all);
			for(PC_GresNodesysConnection c:all){
				c.removeAllConnections();
			}
		}
	}
	
	void getAllConnections(List<PC_GresNodesysConnection> left, List<PC_GresNodesysConnection> rigth){
		for(PC_GresComponent c:getLayoutChildOrder()){
			if(c instanceof PC_GresNodesysEntry){
				PC_GresNodesysEntry entry = (PC_GresNodesysEntry)c;
				if(entry.getLeft()!=null)
					left.add(entry.getLeft());
				if(entry.getRigth()!=null)
					rigth.add(entry.getRigth());
			}
		}
	}
	
	public static class ButtonPressed extends PC_GresEvent{

		ButtonPressed(PC_GresComponent component) {
			super(component);
		}
		
	}
	
}
