package powercraft.api.logging;

/**
 * @author James
 * The crash report for PC
 */
public class PC_CrashReport extends net.minecraft.crash.CrashReport{

	@SuppressWarnings("javadoc")
	public PC_CrashReport(String par1Str, Throwable par2Throwable) {
		super("Error in PowerCraft: " + par1Str, par2Throwable);
	}
}
