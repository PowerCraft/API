package powercraft.api.script;

import java.util.Locale;


public interface PC_DiagnosticTranslater {

	String translate(String message, String[] args, Locale locale);
	
}
