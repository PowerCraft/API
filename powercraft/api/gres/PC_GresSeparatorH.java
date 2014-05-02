package powercraft.api.gres;

import powercraft.api.PC_Rect;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresAlign.Fill;


public class PC_GresSeparatorH extends PC_GresComponent {
	
	private static final String textureName = "LineColor";
	
	public PC_GresSeparatorH(){
		setFill(Fill.HORIZONTAL);
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		return new PC_Vec2I(1, 1);
	}
	
	@Override
	protected PC_Vec2I calculateMaxSize() {
		return new PC_Vec2I(-1, -1);
	}
	
	@Override
	protected PC_Vec2I calculatePrefSize() {
		return new PC_Vec2I(1, 1);
	}
	
	@Override
	protected void paint(PC_Rect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
		drawTexture(textureName, 0, 0, this.rect.width, this.rect.height);
	}
	
}
