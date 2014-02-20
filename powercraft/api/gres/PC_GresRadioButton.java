package powercraft.api.gres;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.history.PC_GresHistory;

@SideOnly(Side.CLIENT)
public class PC_GresRadioButton extends PC_GresComponent {
	
	private static final String textureName[] = {"RadioButton", "RadioButtonChecked"};
	
	private List<PC_GresRadioButton> group;
	private boolean state;
	
	public PC_GresRadioButton(String title){
		setText(title);
		setGroup(null);
	}
	
	public PC_GresRadioButton(String title, PC_GresRadioButton other){
		setText(title);
		setGroup(other);
	}
	
	public void setGroup(PC_GresRadioButton other){
		if(group!=null){
			group.remove(this);
		}
		if(other==null){
			group = new ArrayList<PC_GresRadioButton>();
		}else{
			group = other.group;
		}
		group.add(this);
		state = false;
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		PC_Vec2I tm = getTextureMinSize(textureName[state?1:0]);
		return new PC_Vec2I(tm.x+fontRenderer.getStringWidth(text)+(text!=null&&!text.isEmpty()?1:0), tm.y);
	}

	@Override
	protected PC_Vec2I calculateMaxSize() {
		PC_Vec2I tm = getTextureMinSize(textureName[state?1:0]);
		return new PC_Vec2I(tm.x+fontRenderer.getStringWidth(text)+(text!=null&&!text.isEmpty()?1:0), fontRenderer.FONT_HEIGHT);
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
	protected boolean handleMouseButtonClick(PC_Vec2I mouse, int buttons, int eventButton, PC_GresHistory history) {
		if(!state){
			for(PC_GresRadioButton rb:group){
				rb.state = false;
			}
			state = true;
		}
		notifyChange();
		return true;
	}

}
