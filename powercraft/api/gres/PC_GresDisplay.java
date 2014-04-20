package powercraft.api.gres;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;

@SideOnly(Side.CLIENT)
public class PC_GresDisplay extends PC_GresComponent {

	private PC_GresDisplayObject displayObject;
	private PC_GresDisplayObject background;
	private final PC_RectI frame = new PC_RectI();
	
	public PC_GresDisplay(PC_GresDisplayObject displayObject){
		this.displayObject = displayObject;
		setDisplayObject(displayObject);
	}
	
	public void setDisplayObject(PC_GresDisplayObject displayObject){
		removeEventListener(this.displayObject);
		addEventListener(displayObject);
		this.displayObject = displayObject;
		notifyChange();
	}
	
	public PC_GresDisplayObject getDisplayObject(){
		return this.displayObject;
	}
	
	public void setBackground(PC_GresDisplayObject background){
		this.background = background;
	}
	
	public PC_GresDisplayObject getBackground(){
		return this.background;
	}
	
	public void setFrame(PC_RectI frame){
		this.frame.setTo(frame);
		notifyChange();
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		return this.displayObject==null?new PC_Vec2I(0, 0):this.displayObject.getMinSize().add(this.frame.getLocation()).add(this.frame.getSize());
	}

	@Override
	protected PC_Vec2I calculateMaxSize() {
		return new PC_Vec2I(-1, -1);
	}

	@Override
	protected PC_Vec2I calculatePrefSize() {
		return (this.displayObject==null?new PC_Vec2I(16, 16):this.displayObject.getPrefSize()).add(this.frame.getLocation()).add(this.frame.getSize());
	}

	@Override
	protected void paint(PC_RectI scissor, double scale, int displayHeight, float timeStamp, float zoom) {
		if(this.background!=null)
			this.background.draw(0, 0, this.rect.width, this.rect.height);
		if(this.displayObject!=null)
			this.displayObject.draw(this.frame.x, this.frame.y, this.rect.width-this.frame.x-this.frame.width, this.rect.height-this.frame.y-this.frame.height);
	}

}
