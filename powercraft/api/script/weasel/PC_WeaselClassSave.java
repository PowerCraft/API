package powercraft.api.script.weasel;

import java.util.HashMap;
import java.util.List;

import javax.tools.Diagnostic;

import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.PC_INBT;



public interface PC_WeaselClassSave extends PC_INBT {
	
	public PC_WeaselSourceClass addClass(String name);
	
	public void removeClass(String name);
	
	public PC_WeaselSourceClass getClass(String name);
	
	public boolean compileMarked(String[] staticIndirectImports, String[] indirectImports);

	public HashMap<String, ? extends PC_WeaselSourceClass> getSources();
	
	public List<Diagnostic<String>> getDiagnostics();
	
	public void saveDiagnosticsToNBT(NBTTagCompound tagCompound);
	
}
