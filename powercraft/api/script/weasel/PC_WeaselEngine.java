package powercraft.api.script.weasel;

import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptException;

import xscript.runtime.XVirtualMachine;

public class PC_WeaselEngine {

	private Map<String, Map<String, Object>> virtualMachine;
	
	public PC_WeaselEngine(Map<String, Map<String, Object>> virtualMachine){
		this.virtualMachine = virtualMachine;
	}
	
	public void run(int numInstructions, int numBlocks) throws ScriptException{
		((XVirtualMachine)virtualMachine).getThreadProvider().run(numInstructions, numBlocks);
	}
	
	public void callMain(String className, String methodName, Object...params) throws ScriptException{
		try {
			((Invocable)virtualMachine).invokeFunction(className+"."+methodName, params);
		} catch (NoSuchMethodException e) {
			throw new ScriptException(e);
		} 
	}
	
}
