package powercraft.api.block;

import java.util.HashMap;

import javax.script.CompiledScript;
import javax.script.ScriptException;
import javax.tools.DiagnosticCollector;

import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.PC_Utils;
import powercraft.api.script.miniscript.PC_Miniscript;

public abstract class PC_TileEntityScriptable extends PC_TileEntity {

	private int[] ext;
	
	private CompiledScript script;
	
	private String source;
	
	protected DiagnosticCollector<Void> diagnostic;
	
	protected ScriptException e;
	
	public PC_TileEntityScriptable(int extSize){
		ext = new int[extSize];
	}
	
	protected HashMap<String, Integer> getReplacements(){
		return null;
	}
	
	public void setSource(String source){
		diagnostic = null;
		script = null;
		if(source==null || source.trim().isEmpty()){
			this.source = null;
			return;
		}
		this.source = source;
		if(worldObj==null?PC_Utils.isClient():worldObj.isRemote)
			return;
		DiagnosticCollector<Void> diagnostic = new DiagnosticCollector<Void>();
		try{
			script = PC_Miniscript.compile(source, diagnostic, getReplacements());
		}catch(ScriptException e) {
			this.diagnostic = diagnostic;
		}
	}
	
	public String getSource(){
		return source;
	}
	
	protected int[] getExt(){
		return ext;
	}
	
	protected void invoke(){
		if(e!=null || script==null || worldObj.isRemote)
			return;
		try{
			PC_Miniscript.invoke(script, ext);
		}catch(ScriptException e) {
			this.e = e;
			e.printStackTrace();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		ext = nbtTagCompound.getIntArray("ext");
		setSource(nbtTagCompound.getString("source"));
		super.readFromNBT(nbtTagCompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		nbtTagCompound.setIntArray("ext", ext);
		nbtTagCompound.setString("source", source);
		super.writeToNBT(nbtTagCompound);
	}
	
}
