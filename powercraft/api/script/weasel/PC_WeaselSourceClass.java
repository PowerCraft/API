package powercraft.api.script.weasel;

import java.util.List;

import javax.tools.Diagnostic;


public interface PC_WeaselSourceClass {
	
	public void setSource(String source);

	public String getSource();
	
	public List<Diagnostic<String>> getDiagnostics();
	
}
