package powercraft.api.gres;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;

@SideOnly(Side.CLIENT)
public class PC_GresProgressbar extends PC_GresComponent {

	public static final int TYPE_3D = 0, TYPE_RIFFELED = 1, TYPE_PLANE = 2;
	
	private static final String textureName = "Progressbar";
	private static final String[] textureNameContent = {"Progressbar_Content1", "Progressbar_Content2", "Progressbar_Content3"};
	
	private int type;
	private float progress;
	private int steps = 100;
	
	public PC_GresProgressbar(){
		
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
	
	public float getType(){
		return type;
	}
	
	public void setType(int type){
		this.type = type;
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
		drawTexture(textureName, 0, 0, rect.width, rect.height);
		drawTexture(textureNameContent[type], 1, 1, (int) ((rect.width-2)*progress/steps+0.5), rect.height-2);
	}

}
