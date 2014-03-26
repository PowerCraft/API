package powercraft.api;

public class PC_ParameterReturn<T> {
	
	T storage=null;
	
	public void set(T obj){
		storage = obj;
	}
	
	public T get(){
		return storage;
	}
}
