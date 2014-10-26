package powercraft.api.gres;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_Rect;
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
		PC_Vec2I tm = getTextureMinSize(textureName[this.state?1:0]);
		PC_Vec2I size = fontRenderer.getStringSize(this.text);
		return new PC_Vec2I(tm.x+size.x+(this.text!=null&&!this.text.isEmpty()?1:0), tm.y>size.y?tm.x:size.y);
	}

	@Override
	protected PC_Vec2I calculateMaxSize() {
		PC_Vec2I tm = getTextureMinSize(textureName[this.state?1:0]);
		PC_Vec2I size = fontRenderer.getStringSize(this.text);
		return new PC_Vec2I(tm.x+size.x+(this.text!=null&&!this.text.isEmpty()?1:0), tm.y>size.y?tm.y:size.y);
	}

	@Override
	protected PC_Vec2I calculatePrefSize() {
		return calculateMaxSize();
	}
	
	@Override
	protected void paint(PC_Rect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
		PC_Vec2I tm = getTextureMinSize(textureName[this.state?1:0]);
		drawTexture(textureName[this.state?1:0], 0, 0, tm.x, tm.y);
		drawString(this.text, tm.x+1, 0, this.rect.width - tm.x-1, this.rect.height, PC_GresAlign.H.CENTER, PC_GresAlign.V.CENTER, false);
	}
	
	@Override
	protected boolean handleMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton, boolean doubleClick, PC_GresHistory history) {
		this.state=!this.state;
		super.handleMouseButtonDown(mouse, buttons, eventButton, doubleClick, history);
		notifyChange();
		return true;
	}

	public void check(boolean state) {
		this.state = state;
	}
	
	public boolean isChecked() {
		return this.state;
	}
	
}
