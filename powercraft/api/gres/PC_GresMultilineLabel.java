package powercraft.api.gres;

import powercraft.api.PC_Rect;
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
		return fontRenderer.getStringSize(this.breaked);
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
		this.breaked = fontRenderer.warpStringToWidthBl(this.text, maxX, false);
	}

	@Override
	protected void paint(PC_Rect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
		drawString(this.breaked, 0, 0, false);
	}

}
