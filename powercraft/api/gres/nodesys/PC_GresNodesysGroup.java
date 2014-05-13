package powercraft.api.gres.nodesys;

import java.util.ArrayList;
import java.util.List;


public class PC_GresNodesysGroup {
	
	private PC_GresNodesysGrid grid = new PC_GresNodesysGrid();
	
	private List<PC_GresNodesysNodeGroup> users = new ArrayList<PC_GresNodesysNodeGroup>();
	
	private List<Pin> inputs = new ArrayList<Pin>();
	
	private List<Pin> outputs = new ArrayList<Pin>();
	
	public PC_GresNodesysGroup(){
		PC_GresNodesysNode node = new PC_GresNodesysNode("Group In");
		PC_GresNodesysEntry entry = new PC_GresNodesysEntry("");
		entry.add(new PC_GresNodesysConnectionEmpty(false, this));
		node.add(entry);
		this.grid.add(node);
		node = new PC_GresNodesysNode("Group Out");
		entry = new PC_GresNodesysEntry("");
		entry.add(new PC_GresNodesysConnectionEmpty(true, this));
		node.add(entry);
		this.grid.add(node);
	}

	public PC_GresNodesysGrid getGrid() {
		return this.grid;
	}

	public void addUser(PC_GresNodesysNodeGroup user) {
		this.users.add(user);
	}

	public void addPin(boolean left, boolean isInput, int color, int compGroup, String text) {
		int index;
		if(left){
			index = this.inputs.size();
			this.inputs.add(new Pin(isInput, color, compGroup, text));
		}else{
			index = this.outputs.size();
			this.outputs.add(new Pin(isInput, color, compGroup, text));
		}
		for(PC_GresNodesysNodeGroup user:this.users){
			user.onPinAdded(left, isInput, color, compGroup, text, index);
		}
	}
	
	private static class Pin{

		boolean isInput;
		int color;
		int compGroup;
		String text;
		
		public Pin(boolean isInput, int color, int compGroup, String text) {
			this.isInput = isInput;
			this.color = color;
			this.compGroup = compGroup;
			this.text = text;
		}
		
	}
	
}
