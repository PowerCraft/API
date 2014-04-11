package powercraft.api;


/**
 * 
 * NOT FUTURE!!!, used for byref function parameters
 * 
 * @author Rapus
 *
 * @param <T>
 */
public class PC_ParameterReturn<T> {

	private boolean done;
	private T storage;
	
	public PC_ParameterReturn(){
		this.storage = null;
	}
	
	public PC_ParameterReturn(T storage){
		this.storage = storage;
	}
	
	public void set(T obj){
		this.done = true;
		this.storage = obj;
	}
	
	public T get(){
		return this.storage;
	}
	
	public boolean isDone(){
		return this.done;
	}

}
