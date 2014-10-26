package powercraft.api.block;

import java.util.Arrays;
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
	protected HashMap<String, Integer> getConsts(){
		return null;
	}
	
	@SuppressWarnings("static-method")
	protected HashMap<String, Integer> getPointers(){
		return null;
	}
	
	@SuppressWarnings("static-method")
	protected String[] getEntryVectors(){
		return null;
	}
	
	protected int entryIndex(String name){
		String[] vec = getEntryVectors();
		if(vec==null)
			return 0;
		int x = Arrays.asList(vec).indexOf(name);
		return x==-1?0:x;
	}

	public void setSource(String source){
		this.diagnostic = null;
		this.script = null;
		this.e = null;
		if(source==null || source.trim().isEmpty()){
			this.source = null;
			return;
		}
		if(this.source!=source)
			markDirty();
		this.source = source;
		if(this.worldObj==null?PC_Utils.isClient():this.worldObj.isRemote)
			return;
		this.diagnostic = new DiagnosticCollector<Void>();
		int entryVectorCount = getEntryVectors()==null?0:getEntryVectors().length;
		try{
			HashMap<String, Integer> hm = new HashMap<String, Integer>();
			hm.putAll(getConsts());
			hm.putAll(getPointers());
			this.script = PC_Miniscript.compile(source, this.diagnostic, hm, entryVectorCount);
		}catch(ScriptException e) {
			this.e = e;
		}
		if(this.diagnostic.getDiagnostics().isEmpty()){
			this.diagnostic = null;
		}
	}
	
	public String getSource(){
		return this.source;
	}
	
	protected int[] getExt(){
		return this.ext;
	}

	protected void invoke(int entryVector){
		if(this.e!=null || this.script==null || isClient())
			return;
		markDirty();
		try{
			PC_Miniscript.invoke(this.script, this.ext, entryVector);
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
