package powercraft.api.grid;

import powercraft.api.PC_Direction;

public interface PC_IGridSided {

	public <T extends PC_IGridTile<?, T, ?, ?>> T getTile(PC_Direction side, Class<T> tileClass);
	
}
