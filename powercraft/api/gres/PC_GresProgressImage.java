package powercraft.api.gres;


import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_GresProgressImage extends PC_GresComponent {

	private final String textureNameShadow;
	private final String textureNameOn;
	private float progress;


	public PC_GresProgressImage(String textureNameShadow, String textureNameOn) {

		this.textureNameShadow = textureNameShadow;
		this.textureNameOn = textureNameOn;
	}


	public void setProgress(float progress) {

		this.progress = progress;
	}


	public float getProgress() {

		return progress;
	}


	@Override
	protected PC_Vec2I calculateMinSize() {

		return calculatePrefSize();
	}


	@Override
	protected PC_Vec2I calculateMaxSize() {

		return calculatePrefSize();
	}


	@Override
	protected PC_Vec2I calculatePrefSize() {

		return getTextureDefaultSize(textureNameShadow);
	}


	@Override
	protected void paint(PC_RectI scissor, double scale, int displayHeight, float timeStamp) {

		drawTexture(textureNameShadow, 0, 0, rect.width, rect.height);
		PC_GresTexture texture = PC_Gres.getGresTexture(textureNameOn);
		int state = enabled && parentEnabled ? mouseDown ? 2 : mouseOver ? 1 : 0 : 3;
		if (texture != null) {
			PC_RectI frame = texture.getFrame();
			if (frame.x > 0) {
				texture.drawBasic(0, 0, (int) (rect.width * progress / 100.0), rect.height, 0, 0, state);
			} else if (frame.y > 0) {
				texture.drawBasic(0, 0, rect.width, (int) (rect.height * progress / 100.0), 0, 0, state);
			} else if (frame.width > 0) {
				int prog = (int) (rect.width * progress / 100.0 + 0.5);
				texture.drawBasic(rect.width - prog, 0, prog, rect.height, 1.0f - prog / (float) frame.width, 0, state);
			} else if (frame.height > 0) {
				int prog = (int) (rect.height * progress / 100.0 + 0.5);
				texture.drawBasic(0, rect.height - prog, rect.width, prog, 0, 1.0f - prog / (float) frame.height, state);
			}
		}
	}

}
