package powercraft.api.gres.nodesys;

import powercraft.api.PC_Vec2;


public interface PC_IGresNodesysConnection {
	
	public int getCompGroup();
	
	public PC_GresNodesysNode getNode();

	public int getType(boolean fromThis);
	
	public PC_Vec2 getPosOnScreen();
	
	public void removeConnection(PC_IGresNodesysConnection con);
	
	public void addConnection(PC_IGresNodesysConnection con, boolean asInput);
	
}
