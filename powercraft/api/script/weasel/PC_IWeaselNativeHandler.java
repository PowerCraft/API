package powercraft.api.script.weasel;

public interface PC_IWeaselNativeHandler {

	public int getTypeUnsafe(int address);

	public boolean isDevicePresent(int address);

	public int getRedstoneValueUnsafe(int address, int side);

	public boolean setRedstoneValueUnsafe(int address, int side, int value);

}
