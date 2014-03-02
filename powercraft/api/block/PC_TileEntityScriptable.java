package powercraft.api.block;

import java.util.HashMap;

import javax.script.CompiledScript;
import javax.script.ScriptException;
import javax.tools.DiagnosticCollector;

import powercraft.api.PC_Field;
import powercraft.api.PC_Field.Flag;
import powercraft.api.PC_Utils;
import powercraft.api.script.miniscript.PC_Miniscript;

public abstract class PC_TileEntityScriptable extends PC_TileEntity {

	@PC_Field
	private int[] ext;
	
	private CompiledScript script;
	
	@PC_Field
	private String source;
	
	protected DiagnosticCollector<Void> diagnostic;
	
	protected ScriptException e;
	
	public PC_TileEntityScriptable(int extSize){
		this.ext = new int[extSize];
	}
	
	@SuppressWarnings("static-method")
	protected HashMap<String, Integer> getReplacements(){
		return null;
	}
	
	@SuppressWarnings("hiding")
	public void setSource(String source){
		this.diagnostic = null;
		this.script = null;
		if(source==null || source.trim().isEmpty()){
			this.source = null;
			return;
		}
		if(this.source!=source)
			markDirty();
		this.source = source;
		if(this.worldObj==null?PC_Utils.isClient():this.worldObj.isRemote)
			return;
		DiagnosticCollector<Void> diagnostic = new DiagnosticCollector<Void>();
		try{
			this.script = PC_Miniscript.compile(source, diagnostic, getReplacements());
		}catch(ScriptException e) {
			this.diagnostic = diagnostic;
		}
	}
	
	public String getSource(){
		return this.source;
	}
	
	protected int[] getExt(){
		return this.ext;
	}
	
	@SuppressWarnings("hiding")
	protected void invoke(){
		if(this.e!=null || this.script==null || isClient())
			return;
		markDirty();
		try{
			PC_Miniscript.invoke(this.script, this.ext);
		}catch(ScriptException e) {
			this.e = e;
			e.printStackTrace();
		}
	}

	@Override
	public void onLoadedFromNBT(Flag flag){
		setSource(this.source);
	}
	
}
