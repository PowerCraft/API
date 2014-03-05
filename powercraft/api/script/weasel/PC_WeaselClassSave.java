package powercraft.api.script.weasel;

import java.util.HashMap;

import powercraft.api.PC_INBT;



public interface PC_WeaselClassSave extends PC_INBT {
	
	public PC_WeaselSourceClass addClass(String name);
	
	public void removeClass(String name);
	
	public PC_WeaselSourceClass getClass(String name);
	
	public void compileMarked();

	public HashMap<String, ? extends PC_WeaselSourceClass> getSources();
	
}
