package powercraft.api.logging;

/**
 * @author James
 * The crash report for PC
 */
public class PC_CrashReport extends net.minecraft.crash.CrashReport{

	/**
	 * The normal crash report constructor
	 * @param par1Str The issue description
	 * @param par2Throwable The throwable
	 */
	public PC_CrashReport(String par1Str, Throwable par2Throwable) {
		super("Error in PowerCraft: " + par1Str, par2Throwable);
	}
}
