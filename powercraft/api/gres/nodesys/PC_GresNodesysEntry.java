package powercraft.api.gres.nodesys;

import powercraft.api.PC_Rect;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresAlign.Fill;
import powercraft.api.gres.PC_GresAlign.H;
import powercraft.api.gres.PC_GresAlign.V;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresContainer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_GresNodesysEntry extends PC_GresContainer implements PC_IGresNodesysLineDraw {
	
	private PC_GresNodesysConnection left;
	private PC_GresNodesysConnection rigth;
	private PC_GresComponent mid;
	
	private boolean rv = true;
	
	public PC_GresNodesysEntry(String name){
		setFill(Fill.HORIZONTAL);
		setText(name);
	}

	@Override
	public void add(PC_GresComponent component){
		if(component instanceof PC_GresNodesysConnection){
			if(((PC_GresNodesysConnection)component).isLeft()){
				if(this.left!=null){
					remove(this.left);
				}
				this.left = (PC_GresNodesysConnection) component;
				super.add(this.left);
			}else{
				if(this.rigth!=null){
					remove(this.rigth);
				}
				this.rigth = (PC_GresNodesysConnection) component;
				super.add(this.rigth);
			}
		}else{
			if(this.mid!=null){
				remove(this.mid);
			}
			this.mid = component;
			super.add(this.mid);
		}
	}
	
	@SuppressWarnings("hiding")
	@Override
	protected PC_Vec2I calculateMinSize() {
		PC_Vec2I min = new PC_Vec2I(PC_GresNodesysConnection.RADIUS_DETECTION*4, PC_GresNodesysConnection.RADIUS_DETECTION*2);
		PC_Vec2I midSize = PC_GresComponent.fontRenderer.getStringSize(this.text);
		if(this.mid!=null){
			this.mid.updateMinSize();
			this.mid.updatePrefSize();
			PC_RectI padding = this.mid.getPadding();
			PC_Vec2I minSize = this.mid.getMinSize();
			PC_Vec2I prefSize = this.mid.getPrefSize();
			int height;
			if (prefSize.y == -1) {
				height = minSize.y + padding.y + padding.height;
			} else {
				height = prefSize.y + padding.y + padding.height;
			}
			if (height > midSize.y) {
				midSize.y = height;
			}
			int w = padding.x + padding.width;
			if (prefSize.x == -1) {
				w += minSize.x;
			} else {
				w += prefSize.x;
			}
			if (w > midSize.x) {
				midSize.x = w;
			}
		}
		min.x += midSize.x;
		if(min.y<midSize.y){
			min.y = midSize.y;
		}
		return min;
	}
	
	@Override
	protected PC_Vec2I calculateMaxSize() {
		return new PC_Vec2I(-1, -1);
	}
	
	@Override
	protected PC_Vec2I calculatePrefSize() {
		return calculateMinSize();
	}
	
	@SuppressWarnings("hiding")
	@Override
	public void updateLayout() {
		if(this.rv){
			PC_RectI childRect = getChildRect();
			if(this.left!=null){
				this.left.setLocation(new PC_Vec2I(childRect.x, childRect.y + childRect.height/2-PC_GresNodesysConnection.RADIUS_DETECTION));
			}
			if(this.rigth!=null){
				this.rigth.setLocation(new PC_Vec2I(childRect.x+childRect.width-PC_GresNodesysConnection.RADIUS_DETECTION*2, childRect.y + childRect.height/2-PC_GresNodesysConnection.RADIUS_DETECTION));
			}
			if(this.mid!=null){
				PC_RectI padding = this.mid.getPadding();
				this.mid.putInRect(childRect.x+PC_GresNodesysConnection.RADIUS_DETECTION*2+padding.x, childRect.y+padding.y, childRect.width-PC_GresNodesysConnection.RADIUS_DETECTION*4-padding.x-padding.width, childRect.height-padding.y-padding.height);
			}
		}
	}

	@SuppressWarnings("hiding")
	@Override
	protected void paint(PC_Rect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
		if(!this.rv)
			return;
		boolean hasConnection = (this.left!=null && this.left.isInput() && this.left.isConnected()) || (this.rigth!=null && this.rigth.isInput() && this.rigth.isConnected());
		if(this.mid!=null){
			this.mid.setVisible(!hasConnection);
		}
		if(hasConnection || this.mid==null){
			if(this.mid==null){
				PC_RectI childRect = getChildRect();
				drawString(this.text, childRect.x+PC_GresNodesysConnection.RADIUS_DETECTION*2, childRect.y, childRect.width-PC_GresNodesysConnection.RADIUS_DETECTION*4, childRect.height, this.left!=null && this.rigth!=null? this.alignH:this.left!=null || this.rigth==null?H.LEFT:H.RIGHT, V.CENTER, false);
			}else{
				PC_RectI rect = this.mid.getRect();
				drawString(this.text, rect.x, rect.y, rect.width, rect.height, this.mid.getAlignH(), V.CENTER, false);
			}
		}
	}
	
	@Override
	public void drawLines(boolean pre) {
		if(this.left!=null){
			this.left.drawLines(pre);
		}
		if(this.rigth!=null){
			this.rigth.drawLines(pre);
		}
	}

	public PC_GresNodesysConnection getLeft() {
		return this.left;
	}

	public PC_GresNodesysConnection getRigth() {
		return this.rigth;
	}

	@Override
	protected void doPaint(PC_Vec2 offset, PC_Rect scissorOld, double scale, int displayHeight, float timeStamp, float zoom) {
		this.visible = true;
		super.doPaint(offset, scissorOld, scale, displayHeight, timeStamp, zoom);
		this.visible = this.rv;
	}

	@Override
	public void setVisible(boolean visible) {
		this.rv = visible;
		if(this.mid!=null)
			this.mid.setVisible(visible);
		super.setVisible(visible);
	}

	@Override
	public PC_GresComponent getComponentAtPosition(PC_Vec2I position) {
		this.visible = true;
		PC_GresComponent c = super.getComponentAtPosition(position);
		this.visible = this.rv;
		return c==this?null:c;
	}
	
	@Override
	protected void notifyChange() {
		if(this.rv){
			super.notifyChange();
		}
	}
	
}
