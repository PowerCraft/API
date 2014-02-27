package powercraft.api.grid;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Utils;

public class PC_GridHelper {

	public static <G extends PC_Grid<G, T, N, E>, T extends PC_IGridTile<G, T, N, E>, N extends PC_Node<G, T, N, E>, E extends PC_Edge<G, T, N, E>> void remove(T tile){
		tile.getGrid().removeTile(tile);
	}
	
	public static <G extends PC_Grid<G, T, N, E>, T extends PC_IGridTile<G, T, N, E>, N extends PC_Node<G, T, N, E>, E extends PC_Edge<G, T, N, E>> void connect(T connection1, T connection2){
		connection1.getGrid().makeConnection(connection1, connection2);
	}
	
	public static <T extends PC_IGridTile<?, T, ?, ?>> T getGridTile(World world, int x, int y, int z, PC_Direction side, Class<T> tileClass){
		TileEntity tileEntity = PC_Utils.getTileEntity(world, x, y, z);
		if(tileEntity instanceof PC_IGridSided){
			return ((PC_IGridSided)tileEntity).getTile(side, tileClass);
		}else if(tileEntity!=null && tileClass.isAssignableFrom(tileEntity.getClass())){
			return tileClass.cast(tileEntity);
		}
		return null;
	}
	
	public static <G extends PC_Grid<G, T, N, E>, T extends PC_IGridTile<G, T, N, E>, N extends PC_Node<G, T, N, E>, E extends PC_Edge<G, T, N, E>> boolean hasGrid(World world, int x, int y, int z, PC_Direction side, Class<T> tileClass){
		TileEntity tileEntity = PC_Utils.getTileEntity(world, x, y, z);
		if(tileEntity instanceof PC_IGridSided){
			return ((PC_IGridSided)tileEntity).getTile(side, tileClass)!=null;
		}else if(tileEntity!=null && tileClass.isAssignableFrom(tileEntity.getClass())){
			return true;
		}
		return false;
	}
	
	public static <G extends PC_Grid<G, T, N, E>, T extends PC_IGridTile<G, T, N, E>, N extends PC_Node<G, T, N, E>, E extends PC_Edge<G, T, N, E>> void getGridIfNull(World world, int x, int y, int z, int sides, T thisTile, PC_IGridFactory<G, T, N, E> factory, Class<T> tileClass){
		if(world!=null && !world.isRemote && thisTile.getGrid()==null){
			boolean hasGrid = false;
			for(PC_Direction dir:PC_Direction.VALID_DIRECTIONS){
				if((sides&1)!=0){
					T tile = getGridTile(world, x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ, dir.getOpposite(), tileClass);
					if(tile!=null && tile.getGrid()!=null){
						if(hasGrid){
							connect(tile, thisTile);
						}else{
							tile.getGrid().addTile(thisTile, tile);
							hasGrid = true;
						}
					}
				}
				sides>>>=1;
			}
			if(!hasGrid){
				factory.make(thisTile);
			}
		}
	}
	
	public static <G extends PC_Grid<G, T, N, E>, T extends PC_IGridTile<G, T, N, E>, N extends PC_Node<G, T, N, E>, E extends PC_Edge<G, T, N, E>> void removeFromGrid(World world, T thisTile) {
		if(world!=null && !world.isRemote && thisTile.getGrid()!=null){
			remove(thisTile);
		}
	}
	
}
