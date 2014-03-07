package powercraft.api.gres;

import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;

public class PC_GresMultilineLabel extends PC_GresComponent {

	private String breaked;
	
	public PC_GresMultilineLabel(String label){
		super(label);
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		return calculatePrefSize();
	}

	@Override
	protected PC_Vec2I calculateMaxSize() {
		return new PC_Vec2I(-1, -1);
	}

	@Override
	protected PC_Vec2I calculatePrefSize() {
		autoFormat();
		return fontRenderer.getStringSize(breaked);
	}
	
	@Override
	protected void notifyChange() {
		super.notifyChange();
		autoFormat();
	}

	private void autoFormat(){
		int maxX = getMaxSize().x;
		if(maxX==-1){
			maxX = 100;
		}
		breaked = fontRenderer.warpStringToWidthBl(text, maxX, false);
	}

	@Override
	protected void paint(PC_RectI scissor, double scale, int displayHeight, float timeStamp) {
		drawString(breaked, 0, 0, false);
	}

}
