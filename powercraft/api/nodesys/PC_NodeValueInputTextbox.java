package powercraft.api.nodesys;


public class PC_NodeValueInputTextbox implements PC_INodeValueInput {
	
	public enum InputType{
		INTEGER,
		UINTEGER,
		FLOAT,
		UFLOAT,
		STRING
	}
	
	private InputType type;
	
	public PC_NodeValueInputTextbox(InputType type){
		this.type = type;
	}
	
	public InputType getInputType(){
		return type;
	}
	
}
