package powercraft.api.gres.layout;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresContainer;

@SideOnly(Side.CLIENT)
public class PC_GresLayoutHorizontal implements PC_IGresLayout {

	@Override
	public PC_Vec2I getPreferredLayoutSize(PC_GresContainer container) {

		PC_Vec2I preferredSize = new PC_Vec2I(0, 0);
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
			if (prefSize.x == -1) {
				preferredSize.x += minSize.x + padding.x + padding.width;
			} else {
				preferredSize.x += prefSize.x + padding.x + padding.width;
			}
		}
		return preferredSize;
	}


	@Override
	public PC_Vec2I getMinimumLayoutSize(PC_GresContainer container) {

		PC_Vec2I minimumSize = new PC_Vec2I(0, 0);
		for (PC_GresComponent component : container.getLayoutChildOrder()) {
			component.updateMinSize();
			PC_RectI padding = component.getPadding();
			PC_Vec2I minSize = component.getMinSize();
			minimumSize.x += minSize.x + padding.x + padding.width;
			int height = minSize.y + padding.y + padding.height;
			if (height > minimumSize.y) {
				minimumSize.y= height;
			}
		}
		return minimumSize;
	}


	@Override
	public void updateLayout(PC_GresContainer container) {

		PC_Vec2I preferredSize = getMinimumLayoutSize(container);
		PC_RectI childRect = container.getChildRect();
		int x = (int)(childRect.width / 2.0 - preferredSize.x / 2.0);
		for (PC_GresComponent component : container.getLayoutChildOrder()) {
			PC_RectI padding = component.getPadding();
			int width = -1;//component.getPrefSize().x;
			if(width==-1)
				width = component.getMinSize().x;
			x += padding.x;
			component.putInRect(x, 0, width, childRect.height);
			x += width + padding.width;
		}
	}

}
