package powercraft.api.gres;

import powercraft.api.PC_Rect;
import powercraft.api.PC_Vec2I;


public class PC_GresInfoWindow extends PC_GresNeedFocusFrame {
	
	private static final String textureName = "InfoFrame";
	
	public PC_GresInfoWindow(PC_Vec2I realPos){
		super(realPos);
	}

	@Override
	protected void paint(PC_Rect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
		drawTexture(textureName, 0, 0, this.rect.width, this.rect.height);
	}
	
}
