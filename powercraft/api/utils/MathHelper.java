package powercraft.api.utils;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * @author Mojang AB with edits by James
 * Math stuff
 * 
 * Its just minecraft's mathhelper class for the most part
 */
@SuppressWarnings("javadoc")
public class MathHelper {

	public static double Q_rsqrt(double number){
		double x = number;
		double xhalf = 0.5d*x;
	    long i = Double.doubleToLongBits(x);
	    i = 0x5fe6ec85e7de30daL - (i>>1);
	    x = Double.longBitsToDouble(i);
	    for(int it = 0; it < 4; it++){
	    	x = x*(1.5d - xhalf*x*x);
	    }
	    x *= number;
	    return x;
	}
	
    /**
     * A table of sin values computed from 0 (inclusive) to 2*pi (exclusive), with steps of 2*PI / 65536.
     */
    private static float[] SIN_TABLE = new float[65536];

    /**
     * sin looked up in a table
     */
    public static final float sin(float par0)
    {
        return SIN_TABLE[(int)(par0 * 10430.378F) & 65535];
    }

    /**
     * cos looked up in the sin table with the appropriate offset
     */
    public static final float cos(float par0)
    {
        return SIN_TABLE[(int)(par0 * 10430.378F + 16384.0F) & 65535];
    }

    public static final float sqrt_float(float par0)
    {
        return (float)Math.sqrt(par0);
    }

    public static final float sqrt_double(double par0)
    {
        return (float)Math.sqrt(par0);
    }

    /**
     * Returns the greatest integer less than or equal to the float argument
     */
    public static int floor_float(float par0)
    {
        int i = (int)par0;
        return par0 < i ? i - 1 : i;
    }

    @SideOnly(Side.CLIENT)

    /**
     * returns par0 cast as an int, and no greater than Integer.MAX_VALUE-1024
     */
    public static int truncateDoubleToInt(double par0)
    {
        return (int)(par0 + 1024.0D) - 1024;
    }

    /**
     * Returns the greatest integer less than or equal to the double argument
     */
    public static int floor_double(double par0)
    {
        int i = (int)par0;
        return par0 < i ? i - 1 : i;
    }

    /**
     * Long version of floor_double
     */
    public static long floor_double_long(double par0)
    {
        long i = (long)par0;
        return par0 < i ? i - 1L : i;
    }

    public static float abs(float par0)
    {
        return par0 >= 0.0F ? par0 : -par0;
    }

    /**
     * Returns the unsigned value of an int.
     */
    public static int abs_int(int par0)
    {
        return par0 >= 0 ? par0 : -par0;
    }

    public static int ceiling_float_int(float par0)
    {
        int i = (int)par0;
        return par0 > i ? i + 1 : i;
    }

    public static int ceiling_double_int(double par0)
    {
        int i = (int)par0;
        return par0 > i ? i + 1 : i;
    }

    /**
     * Returns the value of the first parameter, clamped to be within the lower and upper limits given by the second and
     * third parameters.
     */
    public static int clamp_int(int par0, int par1, int par2)
    {
        return par0 < par1 ? par1 : (par0 > par2 ? par2 : par0);
    }

    /**
     * Returns the value of the first parameter, clamped to be within the lower and upper limits given by the second and
     * third parameters
     */
    public static float clamp_float(float par0, float par1, float par2)
    {
        return par0 < par1 ? par1 : (par0 > par2 ? par2 : par0);
    }

    /**
     * Maximum of the absolute value of two numbers.
     */
    public static double abs_max(double par0, double par2)
    {
        if (par0 < 0.0D)
        {
            par0 = -par0;
        }

        if (par2 < 0.0D)
        {
            par2 = -par2;
        }

        return par0 > par2 ? par0 : par2;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Buckets an integer with specifed bucket sizes.  Args: i, bucketSize
     */
    public static int bucketInt(int par0, int par1)
    {
        return par0 < 0 ? -((-par0 - 1) / par1) - 1 : par0 / par1;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Tests if a string is null or of length zero
     */
    public static boolean stringNullOrLengthZero(String par0Str)
    {
        return par0Str == null || par0Str.length() == 0;
    }

    public static int getRandomIntegerInRange(Random par0Random, int par1, int par2)
    {
        return par1 >= par2 ? par1 : par0Random.nextInt(par2 - par1 + 1) + par1;
    }

    public static double getRandomDoubleInRange(Random par0Random, double par1, double par3)
    {
        return par1 >= par3 ? par1 : par0Random.nextDouble() * (par3 - par1) + par1;
    }

    public static double average(long[] par0ArrayOfLong)
    {
        long i = 0L;
        long[] along1 = par0ArrayOfLong;
        int j = par0ArrayOfLong.length;

        for (int k = 0; k < j; ++k)
        {
            long l = along1[k];
            i += l;
        }

        return (double)i / (double)par0ArrayOfLong.length;
    }

    /**
     * the angle is reduced to an angle between -180 and +180 by mod, and a 360 check
     */
    public static float wrapAngleTo180_float(float par0)
    {
        par0 %= 360.0F;

        if (par0 >= 180.0F)
        {
            par0 -= 360.0F;
        }

        if (par0 < -180.0F)
        {
            par0 += 360.0F;
        }

        return par0;
    }

    /**
     * the angle is reduced to an angle between -180 and +180 by mod, and a 360 check
     */
    public static double wrapAngleTo180_double(double par0)
    {
        par0 %= 360.0D;

        if (par0 >= 180.0D)
        {
            par0 -= 360.0D;
        }

        if (par0 < -180.0D)
        {
            par0 += 360.0D;
        }

        return par0;
    }

    /**
     * parses the string as integer or returns the second parameter if it fails
     */
    public static int parseIntWithDefault(String par0Str, int par1)
    {
        int j = par1;

        try
        {
            j = Integer.parseInt(par0Str);
        }
        catch (Throwable throwable)
        {
            //
        }

        return j;
    }

    /**
     * parses the string as integer or returns the second parameter if it fails. this value is capped to par2
     */
    public static int parseIntWithDefaultAndMax(String par0Str, int par1, int par2)
    {
        int k = par1;

        try
        {
            k = Integer.parseInt(par0Str);
        }
        catch (Throwable throwable)
        {
            //
        }

        if (k < par2)
        {
            k = par2;
        }

        return k;
    }

    /**
     * parses the string as double or returns the second parameter if it fails.
     */
    public static double parseDoubleWithDefault(String par0Str, double par1)
    {
        double d1 = par1;

        try
        {
            d1 = Double.parseDouble(par0Str);
        }
        catch (Throwable throwable)
        {
            //
        }

        return d1;
    }

    public static double func_82713_a(String par0Str, double par1, double par3)
    {
        double d2 = par1;

        try
        {
            d2 = Double.parseDouble(par0Str);
        }
        catch (Throwable throwable)
        {
            //
        }

        if (d2 < par3)
        {
            d2 = par3;
        }

        return d2;
    }

    static
    {
        for (int i = 0; i < 65536; ++i)
        {
            SIN_TABLE[i] = (float)Math.sin(i * Math.PI * 2.0D / 65536.0D);
        }
    }
}
	
