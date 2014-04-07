package powercraft.api.grid;

import powercraft.api.PC_Direction;

public interface PC_IGridSidedSide {

	public <T extends PC_IGridTile<?, T, ?, ?>> T getTile(PC_Direction dir, PC_Direction dir2, Class<T> tileClass);
	
}
