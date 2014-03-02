package powercraft.api.gres;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;

@SideOnly(Side.CLIENT)
public class PC_GresLabel extends PC_GresComponent {

	public PC_GresLabel(String text) {

		setText(text);
	}


	@Override
	protected PC_Vec2I calculateMinSize() {

		return fontRenderer.getStringSize(this.text);
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
	protected void paint(PC_RectI scissor, double scale, int displayHeight, float timeStamp) {

		drawString(this.text, 0, 0, this.rect.width, this.rect.height, this.alignH, this.alignV, false);
	}

}
