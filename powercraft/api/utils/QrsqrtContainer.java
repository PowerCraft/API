package powercraft.api.utils;

/**
 * @author James
 * Not used for ANYTHING
 */
public class QrsqrtContainer {
	/**
	 * @param number The number to get the square root of
	 * @return The square root of number
	 */
	public native double Qrsqrt(double number);
	/**
	 * @param number The number to get the square root of
	 * @return The square root of number
	 */
	public static double Qrsqrtj(double number){
		System.loadLibrary("C_Q_rsqrt");
		return new QrsqrtContainer().Qrsqrt(number);
	}
}
