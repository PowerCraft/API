package powercraft.api.nodesys;


public class PC_NodeValueInputDropdown implements PC_INodeValueInput {

	private String[] values;
	
	public PC_NodeValueInputDropdown(String...values){
		this.values = values;
	}
	
	public String[] getValues(){
		return values;
	}
	
}
