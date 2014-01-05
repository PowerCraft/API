package powercraft.api.logging;

/**
 * @author James
 * The exeption that is thrown by PC
 */
 // TODO change to RuntimeException, Exception can only be thrown in methods with throws PC_Exception
public class PC_Exception extends Exception{

	String issue;
	
	/**
	 * The serial version UID for the excpetion (not sure what for)
	 */
	private static final long serialVersionUID = 8163311288878326493L;

	@SuppressWarnings("javadoc")
	public PC_Exception(String issue){
		this.issue = issue;
		// Why throw a exception and catch it then? Why not only write into the Logger?
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
