package powercraft.api.gres.layout;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresAlign.Size;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresContainer;

@SideOnly(Side.CLIENT)
public class PC_GresLayoutHorizontal implements PC_IGresLayout {

	private Size size;
	
	public PC_GresLayoutHorizontal(){
		this.size = Size.SELV;
	}
	
	public PC_GresLayoutHorizontal(Size size){
		this.size = size;
	}
	
	@Override
	public PC_Vec2I getPreferredLayoutSize(PC_GresContainer container) {

		PC_Vec2I preferredSize = new PC_Vec2I(0, 0);
		int paddingX=0;
		for (PC_GresComponent component : container.getLayoutChildOrder()) {
			component.updateMinSize();
			component.updatePrefSize();
			PC_RectI padding = component.getPadding();
			PC_Vec2I minSize = component.getMinSize();
			PC_Vec2I prefSize = component.getPrefSize();
			int height;
			if (prefSize.y == -1) {
				height = minSize.y + padding.y + padding.height;
			} else {
				height = prefSize.y + padding.y + padding.height;
			}
			if (height > preferredSize.y) {
				preferredSize.y = height;
			}
			int w;
			paddingX += padding.x + padding.width;
			if (prefSize.x == -1) {
				w = minSize.x;
			} else {
				w = prefSize.x;
			}
			switch(this.size){
			case BIGGEST:
				if(w>preferredSize.x){
					preferredSize.x = w;
				}
				break;
			case SELV:
				preferredSize.x += w;
				break;
			default:
				break;
			}
		}
		if(this.size==Size.BIGGEST){
			preferredSize.x *= container.getLayoutChildOrder().size();
		}
		preferredSize.x += paddingX;
		return preferredSize;
	}


	@Override
	public PC_Vec2I getMinimumLayoutSize(PC_GresContainer container) {

		PC_Vec2I minimumSize = new PC_Vec2I(0, 0);
		int paddingX=0;
		for (PC_GresComponent component : container.getLayoutChildOrder()) {
			component.updateMinSize();
			PC_RectI padding = component.getPadding();
			PC_Vec2I minSize = component.getMinSize();
			int height = minSize.y + padding.y + padding.height;
			if (height > minimumSize.y) {
				minimumSize.y= height;
			}
			paddingX += padding.x + padding.width;
			switch(this.size){
			case BIGGEST:
				if(minSize.x>minimumSize.x){
					minimumSize.x = minSize.x;
				}
				break;
			case SELV:
				minimumSize.x += minSize.x;
				break;
			default:
				break;
			}
		}
		if(this.size==Size.BIGGEST){
			minimumSize.x *= container.getLayoutChildOrder().size();
		}
		minimumSize.x += paddingX;
		return minimumSize;
	}

	public static int getPadding(PC_GresContainer container){
		int paddingX = 0;
		for (PC_GresComponent component : container.getLayoutChildOrder()) {
			PC_RectI padding = component.getPadding();
			paddingX += padding.x + padding.width;
		}
		return paddingX;
	}

	@Override
	public void updateLayout(PC_GresContainer container) {

		PC_Vec2I preferredSize = getMinimumLayoutSize(container);
		int paddingX = getPadding(container);
		PC_RectI childRect = container.getChildRect();
		float factor = preferredSize.x==0?0:(float)(childRect.width-paddingX)/(preferredSize.x-paddingX);
		if(factor<1)
			factor=1;
		int w = 0;
		if(this.size==Size.BIGGEST && !container.getLayoutChildOrder().isEmpty()){
			w = (preferredSize.x-paddingX)/container.getLayoutChildOrder().size();
		}
		int x = 0;
		for (PC_GresComponent component : container.getLayoutChildOrder()) {
			PC_RectI padding = component.getPadding();
			int width = -1;//component.getPrefSize().x;
			if(width==-1)
				width = component.getMinSize().x;
			if(width<w){
				width=w;
			}
			width *= factor;
			x += padding.x;
			component.putInRect(x, padding.y, width, childRect.height-padding.y-padding.height);
			x += width + padding.width;
		}
	}

}
