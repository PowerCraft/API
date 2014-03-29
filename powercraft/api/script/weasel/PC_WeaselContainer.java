package powercraft.api.script.weasel;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.tools.Diagnostic;

import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.PC_INBT;



public interface PC_WeaselContainer extends PC_INBT {
	
	public PC_WeaselSourceClass addClass(String name);
	
	public void removeClass(String name);
	
	public PC_WeaselSourceClass getClass(String name);
	
	public boolean compileMarked(String[] staticIndirectImports, String[] indirectImports);

	public HashMap<String, ? extends PC_WeaselSourceClass> getSources();
	
	public List<Diagnostic<String>> getDiagnostics();
	
	public void saveDiagnosticsToNBT(NBTTagCompound tagCompound);
	
	public void run(int numInstructions, int numBlocks);
	
	public void callMain(String className, String methodName, Object...params) throws NoSuchMethodException;
	
	public void onEvent(PC_IWeaselEvent event);
	
	public void registerNativeClass(Class<?> c);
	
	public void setErrorOutput(PrintStream errorStream);
	
	public void setHandler(Object handler);
	
	public Map<Object, Object> createInstance(String className);
	
}
