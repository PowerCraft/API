package powercraft.api.tileentity;

import powercraft.api.world.Location;
import powercraft.api.world.WorldUtils;

/**
 * @author James
 * The powercraft tileentity
 */
public class TileEntity extends net.minecraft.tileentity.TileEntity {
	
	/**
	 * @return The world ID
	 */
	public int getWorldID(){
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
	public int[] getBlockIDWithMeta(){
		return new int[]{ this.blockType.blockID, this.blockMetadata };
	}
}
