package powercraft.api.redstone;

import powercraft.api.PC_Direction;
import net.minecraft.world.World;

public interface PC_RedstoneConnectable {

	public boolean canRedstoneConnectTo(World world, int x, int y, int z, PC_Direction side, int faceSide);
	
	public int getRedstonePower(World world, int x, int y, int z, PC_Direction side, int faceSide);

	public void setRedstonePower(World world, int x, int y, int z, PC_Direction side, int faceSide, int value);
	
}
