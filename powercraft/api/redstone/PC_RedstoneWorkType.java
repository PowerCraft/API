package powercraft.api.redstone;

public enum PC_RedstoneWorkType {

	ALWAYS, ON_ON, ON_OFF, ON_HI_FLANK, ON_LOW_FLANK, ON_FLANK;
	
	public static final PC_RedstoneWorkType NEVER = null;
	public static final PC_RedstoneWorkType[] ALL = {NEVER, ALWAYS, ON_ON, ON_OFF, ON_HI_FLANK, ON_LOW_FLANK, ON_FLANK};
	
}
