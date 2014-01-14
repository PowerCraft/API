package powercraft_new.api.tileentity;

import powercraft_new.api.world.Location;
import powercraft_new.api.world.WorldUtils;

/**
 * @author James
 * The powercraft tileentity
 */
public class PC_TileEntity extends net.minecraft.tileentity.TileEntity {
	
	/**
	 * @return The world ID
	 */
	public short getWorldID(){
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