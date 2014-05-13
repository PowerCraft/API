package powercraft.api.gres.nodesys;

import org.lwjgl.input.Keyboard;

import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.history.PC_GresHistory;


public class PC_GresNodesysNodeGroup extends PC_GresNodesysNode {

	private PC_GresNodesysGrid grid = new PC_GresNodesysGrid();
	
	public PC_GresNodesysNodeGroup(String name) {
		super(name);
		setButtonName("NodesysGroup");
		PC_GresNodesysNode node = new PC_GresNodesysNode("Group In");
		PC_GresNodesysEntry entry = new PC_GresNodesysEntry("");
		entry.add(new PC_GresNodesysConnectionEmpty(false));
		node.add(entry);
		this.grid.add(node);
		node = new PC_GresNodesysNode("Group Out");
		entry = new PC_GresNodesysEntry("");
		entry.add(new PC_GresNodesysConnectionEmpty(true));
		node.add(entry);
		this.grid.add(node);
	}

	@Override
	protected void buttonPressed() {
		PC_GresComponent c = this;
		while(!(c instanceof PC_GresNodesysGridView)){
			if(c==null)
				return;
			c = c.getParent();
		}
		((PC_GresNodesysGridView)c).add(this.grid);
		this.grid.moveToTop();
		if(!this.grid.selected.isEmpty()){
			this.grid.selected.get(this.grid.selected.size()-1).takeFocus();
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
	
	
	
}
