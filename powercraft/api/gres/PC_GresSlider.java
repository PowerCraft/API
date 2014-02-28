package powercraft.api.gres;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.events.PC_GresMouseWheelEvent;
import powercraft.api.gres.history.PC_GresHistory;

@SideOnly(Side.CLIENT)
public class PC_GresSlider extends PC_GresComponent {

	private static final String textureName = "Button";
	private static final int SLIDER_SIZE = 7;
	
	private float progress;
	private int steps = 100;
	
	public PC_GresSlider(){
		
	}
	
	public float getProgress(){
		return progress;
	}
	
	public void setProgress(float progress){
		this.progress = progress;
	}
	
	public float getSteps(){
		return steps;
	}
	
	public void setProgress(int steps){
		this.steps = steps;
	}
	
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		return getTextureMinSize(textureName);
	}

	@Override
	protected PC_Vec2I calculateMaxSize() {
		return new PC_Vec2I(-1, -1);
	}

	@Override
	protected PC_Vec2I calculatePrefSize() {
		return getTextureDefaultSize(textureName);
	}

	@Override
	protected void paint(PC_RectI scissor, double scale, int displayHeight, float timeStamp) {
		drawTexture(textureName, 0, 0, rect.width, rect.height, 3);
		int x = (int) (progress*(rect.width-SLIDER_SIZE)/steps+0.5);
		drawTexture(textureName, x, 0, SLIDER_SIZE, rect.height);
	}
	
	@Override
	protected boolean handleMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton, boolean doubleClick, PC_GresHistory history) {
		int x = (int) (progress*(rect.width-SLIDER_SIZE)/steps+0.5);
		if(mouse.x>=x && mouse.x<=x+SLIDER_SIZE && enabled && parentEnabled){
			mouseDown = true;
			moveBarToMouse(mouse);
		}
		return true;
	}

	@Override
	protected boolean handleMouseMove(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		if(mouseDown){
			moveBarToMouse(mouse);
			return true;
		}
		return false;
	}

	private void moveBarToMouse(PC_Vec2I mouse){
		progress = (mouse.x - SLIDER_SIZE/2)/(float)(rect.width-SLIDER_SIZE)*steps;
		if(progress<0)
			progress = 0;
		if(progress>steps)
			progress = steps;
	}
	
	@Override
	protected boolean handleMouseButtonUp(PC_Vec2I mouse, int buttons, int eventButton, PC_GresHistory history) {
		progress = (int)(progress+0.5);
		return super.handleMouseButtonUp(mouse, buttons, eventButton, history);
	}
	
	@Override
	protected void handleMouseLeave(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		mouseOver = false;
	}

	@Override
	protected void handleMouseWheel(PC_GresMouseWheelEvent event, PC_GresHistory history) {
		float bevore = progress;
		progress += event.getWheel();
		if(progress<0)
			progress = 0;
		if(progress>steps)
			progress = steps;
		if(progress!=bevore)
			event.consume();
	}
	
}
