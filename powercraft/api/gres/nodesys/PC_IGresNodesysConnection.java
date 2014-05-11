package powercraft.api.gres.nodesys;


public interface PC_IGresNodesysConnection {
	
	public int getCompGroup();
	
	public boolean canConnectWith(boolean isInput);
	
	public PC_GresNodesysNode getNode();
	
	public boolean isInput();
	
}
