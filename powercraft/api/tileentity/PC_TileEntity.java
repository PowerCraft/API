package powercraft.api.tileentity;

import powercraft.api.world.Location;
import powercraft.api.world.WorldUtils;

/**
 * @author James
 * The powercraft tileentity
 */
public class PC_TileEntity extends net.minecraft.tileentity.TileEntity {
	
	/**
	 * @return The world ID
	 */
	public short getWorldID(){
		// TODO you can get the worldid a different and a lot more stable way by using the
		// dimension id.
		return WorldUtils.getWorldIDByName(this.worldObj.getWorldInfo().getWorldName());
	}
	
	/**
	 * @return The world name
	 */
	public String getWorldName(){
		return this.worldObj.getWorldInfo().getWorldName();
	}
	
	/**
	 * @return The location of the tileentity
	 */
	public Location getBlockLocation(){
		return new Location(this.xCoord, this.yCoord, this.zCoord);
	}
	
	/**
	 * @return The block ID, metadata
	 */
	public short[] getBlockIDWithMeta(){
		return new short[]{ (short) this.blockType.blockID, (short) this.blockMetadata };
	}
}
