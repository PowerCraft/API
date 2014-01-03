package powercraft.api.utils;

/**
 * @author James
 * Not used for ANYTHING
 */
@SuppressWarnings("javadoc")
public class QrsqrtContainer {
	public native double Qrsqrt(double number);
	public static double Qrsqrtj(double number){
		System.loadLibrary("C_Q_rsqrt");
		return new QrsqrtContainer().Qrsqrt(number);
	}
}
