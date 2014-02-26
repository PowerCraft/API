package powercraft.api;

import net.minecraftforge.common.util.ForgeDirection;


public enum PC_Direction {

	/** -Y */
    DOWN(0, -1, 0),

    /** +Y */
    UP(0, 1, 0),

    /** -Z */
    NORTH(0, 0, -1),

    /** +Z */
    SOUTH(0, 0, 1),

    /** -X */
    WEST(-1, 0, 0),

    /** +X */
    EAST(1, 0, 0),

    /**
     * Used only by getOrientation, for invalid inputs
     */
    UNKNOWN(0, 0, 0);

    public final int offsetX;
    public final int offsetY;
    public final int offsetZ;
    public final int flag;
    public static final PC_Direction[] VALID_DIRECTIONS = {DOWN, UP, NORTH, SOUTH, WEST, EAST};
    public static final PC_Direction[] OPPOSITES = {UP, DOWN, SOUTH, NORTH, EAST, WEST, UNKNOWN};
    // Left hand rule rotation matrix for all possible axes of rotation
    public static final PC_Direction[][] ROTATION_MATRIX = {
        {DOWN, UP, WEST, EAST, SOUTH, NORTH, UNKNOWN},
        {DOWN, UP, EAST, WEST, NORTH, SOUTH, UNKNOWN},
    	{EAST, WEST, NORTH, SOUTH, DOWN, UP, UNKNOWN},
    	{WEST, EAST, NORTH, SOUTH, UP, DOWN, UNKNOWN},
    	{NORTH, SOUTH, UP, DOWN, WEST, EAST, UNKNOWN},
    	{SOUTH, NORTH, DOWN, UP, WEST, EAST, UNKNOWN},
    	{DOWN, UP, NORTH, SOUTH, WEST, EAST, UNKNOWN},
    };

    private PC_Direction(int x, int y, int z) {
        offsetX = x;
        offsetY = y;
        offsetZ = z;
        flag = 1 << ordinal();
    }

    public static PC_Direction fromSide(int id){
        if (id >= 0 && id < VALID_DIRECTIONS.length)
        {
            return VALID_DIRECTIONS[id];
        }
        return UNKNOWN;
    }

    public static PC_Direction fromSide(ForgeDirection side) {
		return fromSide(side.ordinal());
	}
    
    public PC_Direction getOpposite(){
        return OPPOSITES[ordinal()];
    }

    public PC_Direction getRotation(PC_Direction axis){
    	return ROTATION_MATRIX[axis.ordinal()][ordinal()];
    }

	public PC_Direction getRotation(PC_Direction axis, int times) {
		times = ((times %4) +4) %4;
		if(times==0)
			return this;
		else if(times==1)
			return getRotation(axis);
		else if(times==2)
			return this==axis||getOpposite()==axis?this:getOpposite();
		else if(times==3)
			return getRotation(axis.getOpposite());
		return UNKNOWN;
	}

	public ForgeDirection toForgeDirection() {
		return ForgeDirection.getOrientation(ordinal());
	}

}
