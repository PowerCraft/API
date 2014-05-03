package powercraft.api.gres;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_Rect;
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
		if(this.group!=null){
			this.group.remove(this);
		}
		if(other==null){
			this.group = new ArrayList<PC_GresRadioButton>();
		}else{
			this.group = other.group;
		}
		this.group.add(this);
		this.state = false;
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		PC_Vec2I tm = getTextureMinSize(textureName[this.state?1:0]);
		PC_Vec2I size = fontRenderer.getStringSize(this.text);
		return new PC_Vec2I(tm.x+size.x+(this.text!=null&&!this.text.isEmpty()?1:0), tm.y>size.y?tm.y:size.y);
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
	protected boolean handleMouseButtonClick(PC_Vec2I mouse, int buttons, int eventButton, PC_GresHistory history) {
		check();
		return true;
	}
	
	public void check(){
		if(!this.state){
			for(PC_GresRadioButton rb:this.group){
				rb.state = false;
			}
			this.state = true;
		}
		notifyChange();
	}

	public boolean getState(){
		return this.state;
	}
	
}
