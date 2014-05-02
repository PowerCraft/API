package powercraft.api.gres.layout;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresContainer;
import powercraft.api.gres.PC_GresAlign.Size;

@SideOnly(Side.CLIENT)
public class PC_GresLayoutVertical implements PC_IGresLayout {

	private Size size;
	
	public PC_GresLayoutVertical(){
		this.size = Size.SELV;
	}
	
	public PC_GresLayoutVertical(Size size){
		this.size = size;
	}
	
	@Override
	public PC_Vec2I getPreferredLayoutSize(PC_GresContainer container) {

		PC_Vec2I preferredSize = new PC_Vec2I(0, 0);
		int paddingY=0;
		for (PC_GresComponent component : container.getLayoutChildOrder()) {
			component.updateMinSize();
			component.updatePrefSize();
			PC_RectI padding = component.getPadding();
			PC_Vec2I minSize = component.getMinSize();
			PC_Vec2I prefSize = component.getPrefSize();
			int width;
			if (prefSize.x == -1) {
				width = minSize.x + padding.x + padding.width;
			} else {
				width = prefSize.x + padding.x + padding.width;
			}
			if (width > preferredSize.x) {
				preferredSize.x = width;
			}
			int h;
			paddingY += padding.y + padding.height;
			if (prefSize.y == -1) {
				h = minSize.y;
			} else {
				h = prefSize.y;
			}
			switch(this.size){
			case BIGGEST:
				if(h>preferredSize.y){
					preferredSize.y = h;
				}
				break;
			case SELV:
				preferredSize.y += h;
				break;
			default:
				break;
			}
		}
		if(this.size==Size.BIGGEST){
			preferredSize.y *= container.getLayoutChildOrder().size();
		}
		preferredSize.y += paddingY;
		return preferredSize;
	}


	@Override
	public PC_Vec2I getMinimumLayoutSize(PC_GresContainer container) {

		PC_Vec2I minimumSize = new PC_Vec2I(0, 0);
		int paddingY=0;
		for (PC_GresComponent component : container.getLayoutChildOrder()) {
			component.updateMinSize();
			PC_RectI padding = component.getPadding();
			PC_Vec2I minSize = component.getMinSize();
			int width = minSize.x + padding.x + padding.width;
			if (width > minimumSize.x) {
				minimumSize.x = width;
			}
			paddingY += padding.y + padding.height;
			switch(this.size){
			case BIGGEST:
				if(minSize.y>minimumSize.y){
					minimumSize.y = minSize.y;
				}
				break;
			case SELV:
				minimumSize.y += minSize.y;
				break;
			default:
				break;
			}
		}
		if(this.size==Size.BIGGEST){
			minimumSize.y *= container.getLayoutChildOrder().size();
		}
		minimumSize.y += paddingY;
		return minimumSize;
	}
	
	public static int getPadding(PC_GresContainer container){
		int paddingY = 0;
		for (PC_GresComponent component : container.getLayoutChildOrder()) {
			PC_RectI padding = component.getPadding();
			paddingY += padding.y + padding.height;
		}
		return paddingY;
	}
	
	@Override
	public void updateLayout(PC_GresContainer container) {

		PC_Vec2I preferredSize = getMinimumLayoutSize(container);
		int paddingY = getPadding(container);
		PC_RectI childRect = container.getChildRect();
		float factor = preferredSize.y==0?0:(float)(childRect.height-paddingY)/(preferredSize.y-paddingY);
		if(factor<1)
			factor=1;
		int h = 0;
		if(this.size==Size.BIGGEST && !container.getLayoutChildOrder().isEmpty()){
			h = (preferredSize.y-paddingY)/container.getLayoutChildOrder().size();
		}
		int y = 0;
		for (PC_GresComponent component : container.getLayoutChildOrder()) {
			PC_RectI padding = component.getPadding();
			int height = -1;//component.getPrefSize().y;
			if(height==-1)
				height = component.getMinSize().y;
			if(height<h){
				height=h;
			}
			height *= factor;
			y += padding.y;
			component.putInRect(padding.x, y, childRect.width-padding.x-padding.width, height);
			y += height + padding.height;
		}
	}

}
