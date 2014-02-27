package powercraft.api.gres;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.history.PC_GresHistory;

@SideOnly(Side.CLIENT)
public class PC_GresCheckBox extends PC_GresComponent {

	private static final String textureName[] = {"CheckBox", "CheckBoxChecked"};
	
	private boolean state;
	
	public PC_GresCheckBox(String title){
		setText(title);
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		PC_Vec2I tm = getTextureMinSize(textureName[state?1:0]);
		PC_Vec2I size = fontRenderer.getStringSize(text);
		return new PC_Vec2I(tm.x+size.x+(text!=null&&!text.isEmpty()?1:0), tm.y>size.y?tm.x:size.y);
	}

	@Override
	protected PC_Vec2I calculateMaxSize() {
		PC_Vec2I tm = getTextureMinSize(textureName[state?1:0]);
		PC_Vec2I size = fontRenderer.getStringSize(text);
		return new PC_Vec2I(tm.x+size.x+(text!=null&&!text.isEmpty()?1:0), tm.y>size.y?tm.y:size.y);
	}

	@Override
	protected PC_Vec2I calculatePrefSize() {
		return calculateMaxSize();
	}
	
	@Override
	protected void paint(PC_RectI scissor, double scale, int displayHeight, float timeStamp) {
		PC_Vec2I tm = getTextureMinSize(textureName[state?1:0]);
		drawTexture(textureName[state?1:0], 0, 0, tm.x, tm.y);
		drawString(text, tm.x+1, 0, rect.width - tm.x-1, rect.height, PC_GresAlign.H.CENTER, PC_GresAlign.V.CENTER, false);
	}
	
	@Override
	protected boolean handleMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton, PC_GresHistory history) {
		state=!state;
		super.handleMouseButtonDown(mouse, buttons, eventButton, history);
		notifyChange();
		return true;
	}
	
}
