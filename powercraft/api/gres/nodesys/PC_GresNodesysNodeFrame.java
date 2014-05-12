package powercraft.api.gres.nodesys;

import powercraft.api.PC_RectI;
import powercraft.api.gres.PC_GresComponent;


public class PC_GresNodesysNodeFrame extends PC_GresNodesysNode {

	public PC_GresNodesysNodeFrame() {
		super("Frame", false);
	}

	private boolean updating;
	
	public void makeToRightSize(){
		if(updating){
			return;
		}
		updating = true;
		PC_RectI rect = null;
		for(PC_GresComponent c:this.children){
			if(rect==null){
				rect=c.getRect();
			}else{
				rect = rect.enclosing(c.getRect());
			}
		}
		if(rect==null){
			rect = new PC_RectI(0, 0, 100, 100);
		}else{
			rect.x -= 5;
			rect.y -= 5;
			rect.width += 10;
			rect.height += 10+frame.y+frame.height;
		}
		for(PC_GresComponent c:this.children){
			c.setLocation(c.getLocation().sub(rect.getLocation()));
		}
		setLocation(getLocation().add(rect.getLocation()));
		setSize(rect.getSize());
		updating = false;
	}
	
	@Override
	protected void notifyChange() {
		super.notifyChange();
		makeToRightSize();
	}
	
}
