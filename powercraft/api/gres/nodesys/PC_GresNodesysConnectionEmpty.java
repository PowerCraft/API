package powercraft.api.gres.nodesys;

import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.history.PC_GresHistory;


public class PC_GresNodesysConnectionEmpty extends PC_GresNodesysConnection {

	private int input = 3;
	
	private PC_GresNodesysGroup group;
	
	public PC_GresNodesysConnectionEmpty(boolean left, PC_GresNodesysGroup group) {
		super(false, left, 0x80FFFFFF, -1);
		this.group = group;
	}
	
	@Override
	protected boolean handleMouseButtonUp(PC_Vec2I mouse, int buttons, int eventButton, PC_GresHistory history) {
		PC_Vec2I pos = mouse.mul(getRecursiveZoom()).add(new PC_Vec2I(getRealLocation()));
		PC_GresComponent c = getGuiHandler().getComponentAtPosition(pos);
		PC_IGresNodesysConnection nc;
		if(!this.connections.isEmpty() && isInput()){
			nc = PC_GresNodesysHelper.getConnection(this.connections.get(0), c);
		}else{
			nc = PC_GresNodesysHelper.getConnection(this, c);
		}
		if(nc==null){
			if(!this.connections.isEmpty() && isInput()){
				removeConnection(this.connections.get(0));
			}
		}else{
			if(this.connections.isEmpty() || !isInput()){
				addConnection(nc, (nc.getType(false)&1)==0);
			}else{
				PC_IGresNodesysConnection to = this.connections.get(0);
				removeConnection(to);
				to.addConnection(nc, false);
			}
		}
		return super.handleMouseButtonUp(mouse, buttons, eventButton, history);
	}
	
	@Override
	public void addConnection(PC_IGresNodesysConnection con, boolean asInput) {
		if(this.connections.contains(con))
			return;
		if(this.input==3){
			int t = con.getType(true);
			if(t!=3){
				PC_GresNodesysEntry entry = new PC_GresNodesysEntry("");
				entry.add(new PC_GresNodesysConnectionEmpty(this.left, this.group));
				getParent().getParent().add(entry);
				this.input = asInput?1:2;
				this.color = con.getColor();
				this.compGroup = con.getCompGroup();
				getParent().setText(con.getName());
				this.group.addPin(!this.left, !asInput, this.color, this.compGroup, getParent().getText());
			}
		}
		if(isInput() && !this.connections.isEmpty()){
			removeConnection(this.connections.get(0));
		}
		this.connections.add(con);
		con.addConnection(this, !asInput);
	}
	
	@Override
	public int getType(boolean fromThis) {
		return this.input;
	}
	
}
