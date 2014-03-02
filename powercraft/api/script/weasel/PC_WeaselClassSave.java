package powercraft.api.script.weasel;



public interface PC_WeaselClassSave {
	
	public PC_WeaselSourceClass addClass(String name);
	
	public void removeClass(String name);
	
	public PC_WeaselSourceClass getClass(String name);
	
	public void compileMarked();
	
}
