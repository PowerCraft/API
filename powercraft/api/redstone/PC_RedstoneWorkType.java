package powercraft.api.redstone;

public enum PC_RedstoneWorkType {

	EVER, ON_ON, ON_OFF, ON_HI_FLANK, ON_LOW_FLANK, ON_FLANK;
	
	public static PC_RedstoneWorkType NEVER = null;
	public static PC_RedstoneWorkType[] ALL = {NEVER, EVER, ON_ON, ON_OFF, ON_HI_FLANK, ON_LOW_FLANK, ON_FLANK};
	
}
