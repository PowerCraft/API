package powercraft.api.gres;

import powercraft.api.PC_Rect;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.events.PC_GresMouseWheelEvent;
import powercraft.api.gres.history.PC_GresHistory;

public class PC_GresScrollBar extends PC_GresComponent {

	private static final String scrollV = "ScrollV", scrollVFrame = "ScrollVFrame";
	
	private int scrollSize = 0, scroll=0, maxScrollSize=0;
	private float scrollPos = 0;
	private final PC_Vec2I lastMousePosition = new PC_Vec2I(0, 0);
	
	public PC_GresScrollBar(int maxScrollSize){
		this.maxScrollSize = maxScrollSize;
		calcScrollSize();
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		return getTextureMinSize(scrollVFrame);
	}

	@Override
	protected PC_Vec2I calculateMaxSize() {
		return new PC_Vec2I(-1, -1);
	}

	@Override
	protected PC_Vec2I calculatePrefSize() {
		return getTextureDefaultSize(scrollVFrame);
	}

	@Override
	protected void paint(PC_Rect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
		if(!this.mouseDown){
			updateScrollPosition();
		}
		drawTexture(scrollVFrame, 0, 0, this.rect.width, this.rect.height);
		drawTexture(scrollV, 1, (int)this.scrollPos+1, this.rect.width-2, this.scrollSize-1);
	}

	private void calcScrollSize(){
		int sizeY = this.rect.height - 1;
		int maxSizeY = this.maxScrollSize;
		int sizeOutOfFrame = maxSizeY - sizeY + 6;
		if (sizeOutOfFrame <= 0) {
			this.scrollSize = sizeY;
		}else{
			if(maxSizeY==0){
				this.scrollSize=0;
			}else{
				this.scrollSize=sizeY*sizeY/maxSizeY;
				if(this.scrollSize<10){
					this.scrollSize=10;
				}
			}
		}
	}
	
	public int getScroll(){
		return this.scroll;
	}
	
	public void setScroll(int scroll){
		int sizeY = this.rect.height - 1;
		int maxSizeY = this.maxScrollSize;
		int sizeOutOfFrame = maxSizeY - sizeY + 6;
		if (sizeOutOfFrame < 0) {
			sizeOutOfFrame = 0;
		}
		float prozent = maxSizeY > 0 ? ((float) sizeOutOfFrame / (maxSizeY)) : 0;
		this.scrollPos = scroll*prozent*sizeY/sizeOutOfFrame;
		updateScrollPosition();
	}
	
	public void setMaxScollSize(int maxScrollSize){
		this.maxScrollSize = maxScrollSize;
		calcScrollSize();
		updateScrollPosition();
	}
	
	private void updateScrollPosition() {

		int sizeY = this.rect.height - 1;
		int maxSizeY = this.maxScrollSize;
		int sizeOutOfFrame = maxSizeY - sizeY + 6;
		if (sizeOutOfFrame < 0) {
			sizeOutOfFrame = 0;
		}
		float prozent = maxSizeY > 0 ? ((float) sizeOutOfFrame / (maxSizeY)) : 0;
		if (this.scrollPos < 0) {
			this.scrollPos = 0;
		}
		if (this.scrollPos > sizeY - this.scrollSize) {
			this.scrollPos = sizeY - this.scrollSize;
		}
		this.scroll = (int) (this.scrollPos / prozent / sizeY * sizeOutOfFrame + 0.5);
		
	}

	@Override
	protected boolean handleMouseMove(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		if (this.mouseDown) {
			this.scrollPos += mouse.y - this.lastMousePosition.y;
			updateScrollPosition();
		}
		this.lastMousePosition.setTo(mouse);
		return true;
	}

	@Override
	protected boolean handleMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton, boolean doubleClick, PC_GresHistory history) {
		this.mouseDown = false;
		this.lastMousePosition.setTo(mouse);
		if (eventButton != -1) {
			if (mouse.y - 1 < this.scrollPos) {
				this.scroll -= 5;
				return true;
			}
			if (mouse.y - 1 >= this.scrollPos + this.scrollSize) {
				this.scroll += 5;
				return true;
			}
			this.mouseDown = true;
			return true;
		}
		return false;
	}

	@Override
	protected void handleMouseWheel(PC_GresMouseWheelEvent event, PC_GresHistory history) {
		this.scrollPos -= event.getWheel()*3;
		updateScrollPosition();
	}

	
	
}
