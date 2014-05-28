package powercraft.api.gres.nodesys;

import org.lwjgl.input.Keyboard;

import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.history.PC_GresHistory;
import powercraft.api.nodesys.PC_NodeGridBase;


public class PC_GresNodesysNodeGroup extends PC_GresNodesysNode {

	private PC_GresNodesysGroup group;
	
	public PC_GresNodesysNodeGroup(String name, PC_NodeGridBase base) {
		super(name);
		setButtonName("NodesysGroup");
		group = new PC_GresNodesysGroup(base);
		this.group.addUser(this);
	}

	@Override
	protected void buttonPressed() {
		PC_GresComponent c = this;
		while(!(c instanceof PC_GresNodesysGridView)){
			if(c==null)
				return;
			c = c.getParent();
		}
		PC_GresNodesysGrid grid = this.group.getGrid();
		((PC_GresNodesysGridView)c).add(grid);
		grid.moveToTop();
		if(!grid.selected.isEmpty()){
			grid.selected.get(grid.selected.size()-1).takeFocus();
		}
	}

	@Override
	protected boolean handleKeyTyped(char key, int keyCode, boolean repeat, PC_GresHistory history) {
		if(keyCode==Keyboard.KEY_TAB && !(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))){
			buttonPressed();
			return true;
		}
		return super.handleKeyTyped(key, keyCode, repeat, history);
	}

	private int where = -1;
	
	public void onPinAdded(boolean left, boolean isInput, int color, int compGroup, String text, int index) {
		PC_GresNodesysEntry entry = new PC_GresNodesysEntry(text);
		entry.add(new PC_GresNodesysConnection(isInput, left, color, compGroup));
		if(left){
			add(entry);
		}else{
			this.where = index+1;
			add(entry);
			this.where = -1;
		}
	}
	
	@Override
	protected void addChild(PC_GresComponent component){
		this.children.add(component);
		if(this.where==-1){
			this.layoutChildOrder.add(component);
		}else{
			this.layoutChildOrder.add(this.where, component);
		}
	}
	
	@Override
	protected void giveChildFocus(PC_GresComponent component){
		if(component.hasFocusOrChild()){
			component.takeFocus();
		}
	}
	
}
