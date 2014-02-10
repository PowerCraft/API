package powercraft.api.gres.layout;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresContainer;

@SideOnly(Side.CLIENT)
public class PC_GresLayoutVertical implements PC_IGresLayout {

	@Override
	public PC_Vec2I getPreferredLayoutSize(PC_GresContainer container) {

		PC_Vec2I preferredSize = new PC_Vec2I(0, 0);
		for (PC_GresComponent component : container.getLayoutChildOrder()) {
			PC_RectI padding = component.getPadding();
			PC_Vec2I minSize = component.getMinSize();
			PC_Vec2I prefSize = component.getPrefSize();
			if (prefSize.y == -1) {
				preferredSize.y += minSize.y + padding.y + padding.height;
			} else {
				preferredSize.y += prefSize.y + padding.y + padding.height;
			}
			int width;
			if (prefSize.x == -1) {
				width = minSize.x + padding.x + padding.width;
			} else {
				width = prefSize.x + padding.x + padding.width;
			}
			if (width > preferredSize.x) {
				preferredSize.x = width;
			}
		}
		return preferredSize;
	}


	@Override
	public PC_Vec2I getMinimumLayoutSize(PC_GresContainer container) {

		PC_Vec2I minimumSize = new PC_Vec2I(0, 0);
		for (PC_GresComponent component : container.getLayoutChildOrder()) {
			PC_RectI padding = component.getPadding();
			PC_Vec2I minSize = component.getMinSize();
			int width = minSize.x + padding.x + padding.width;
			if (width > minimumSize.x) {
				minimumSize.x = width;
			}
			minimumSize.y += minSize.y + padding.y + padding.height;
		}
		return minimumSize;
	}


	@Override
	public void updateLayout(PC_GresContainer container) {

		PC_Vec2I preferredSize = getMinimumLayoutSize(container);
		PC_RectI childRect = container.getChildRect();
		int y = (int)(childRect.height / 2.0 - preferredSize.y / 2.0);
		for (PC_GresComponent component : container.getLayoutChildOrder()) {
			PC_RectI padding = component.getPadding();
			int height = -1;//component.getPrefSize().y;
			if(height==-1)
				height = component.getMinSize().y;
			y += padding.y;
			component.putInRect(0, y, childRect.width, height);
			y += height + padding.height;
		}
	}

}
