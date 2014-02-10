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
		return displayObject;
	}
	
	public void setBackground(PC_GresDisplayObject background){
		this.background = background;
	}
	
	public PC_GresDisplayObject getBackground(){
		return background;
	}
	
	public void setFrame(PC_RectI frame){
		this.frame.setTo(frame);
		notifyChange();
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		return displayObject==null?new PC_Vec2I(0, 0):displayObject.getMinSize().add(frame.getLocation()).add(frame.getSize());
	}

	@Override
	protected PC_Vec2I calculateMaxSize() {
		return new PC_Vec2I(-1, -1);
	}

	@Override
	protected PC_Vec2I calculatePrefSize() {
		return (displayObject==null?new PC_Vec2I(16, 16):displayObject.getPrefSize()).add(frame.getLocation()).add(frame.getSize());
	}

	@Override
	protected void paint(PC_RectI scissor, double scale, int displayHeight, float timeStamp) {
		if(background!=null)
			background.draw(0, 0, rect.width, rect.height);
		if(displayObject!=null)
			displayObject.draw(frame.x, frame.y, rect.width-frame.x-frame.width, rect.height-frame.y-frame.height);
	}

}
