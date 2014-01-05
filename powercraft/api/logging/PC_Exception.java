package powercraft.api.logging;

/**
 * @author James
 * The exeption that is thrown by PC
 */
public class PC_Exception extends RuntimeException{

	String issue;
	
	/**
	 * The serial version UID for the excpetion (not sure what for)
	 */
	private static final long serialVersionUID = 8163311288878326493L;

	@SuppressWarnings("javadoc")
	public PC_Exception(String issue){
		this.issue = issue;
		if(issue == null || issue == ""){
			try {
				throw new PC_Exception("Custom thrown exception has no value, throwing exception!");
			} catch (PC_Exception e) {
				e.printStackTrace();
				Logger.log(LogLevel.severe, e.getMessage());
			}
		}
	}
	
	@Override
	public String getMessage(){
		return "Custom thrown exception in PowerCraft: " + this.issue;
	}
}
