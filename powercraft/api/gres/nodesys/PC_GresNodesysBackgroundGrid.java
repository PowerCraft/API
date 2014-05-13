package powercraft.api.gres.nodesys;

import java.util.ListIterator;

import powercraft.api.PC_Rect;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresContainer;


public class PC_GresNodesysBackgroundGrid extends PC_GresContainer implements PC_IGresNodesysBackgroundDraw {
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		return new PC_Vec2I(1000, 1000);
	}
	
	@Override
	protected PC_Vec2I calculateMaxSize() {
		return new PC_Vec2I(1000, 1000);
	}
	
	@Override
	protected PC_Vec2I calculatePrefSize() {
		return new PC_Vec2I(1000, 1000);
	}
	
	@Override
	protected void paint(PC_Rect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
		//
	}

	@Override
	public void moveToTop() {
		super.moveToBottom();
	}

	@Override
	public void drawBackground() {
		ListIterator<PC_GresComponent> li = this.children.listIterator(this.children.size());
		while(li.hasPrevious()){
			PC_GresComponent c = li.previous();
			if(c instanceof PC_IGresNodesysBackgroundDraw){
	    		((PC_IGresNodesysBackgroundDraw)c).drawBackground();
	    	}
		}
	}
	
}
