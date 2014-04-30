package powercraft.api.gres;

import powercraft.api.PC_Rect;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresAlign.Fill;


public class PC_GresDialogBox extends PC_GresContainer {
	
	private static final String textureName = "DialogBoxBackground";
	
	public PC_GresDialogBox(){
		setFill(Fill.BOTH);
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		return new PC_Vec2I(0, 0);
	}
	
	@Override
	protected PC_Vec2I calculateMaxSize() {
		return new PC_Vec2I(-1, -1);
	}
	
	@Override
	protected PC_Vec2I calculatePrefSize() {
		return new PC_Vec2I(-1, -1);
	}
	
	@Override
	protected void paint(PC_Rect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
		drawTexture(textureName, this.rect.x, this.rect.y, this.rect.width, this.rect.height);
	}
	
}
