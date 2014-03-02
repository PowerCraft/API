package powercraft.api.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec3I;

public class PC_3DRecipe {
	
	private PC_Vec3I size;
	private PlaceData[][][] array;
	private PC_I3DRecipeHandler obj;
	private boolean doMirrow;
	private List<Block> blocks = new ArrayList<Block>();
	
	private static class PlaceData{
		
		boolean reverse;
		BlockData[] blockDatas;
		
		PlaceData(boolean reverse, BlockData[] blockDatas) {
			this.reverse = reverse;
			this.blockDatas = blockDatas;
		}
		
	}
	
	private static class BlockData{
		
		Block block;
		int meta;
		
		BlockData(Block block, int meta) {
			this.block = block;
			this.meta = meta;
		}
		
	}
	
	public PC_3DRecipe(PC_I3DRecipeHandler obj, Object...o){
		this.obj = obj;
		this.size = new PC_Vec3I();
		int i=0;
		List<String[]> layer = new ArrayList<String[]>();
		HashMap<Character, BlockData[]> map = new HashMap<Character, BlockData[]>();
		
		while(o[i] instanceof String[]){
			layer.add((String[])o[i]);
			i++;
		}
		
		while(i<o.length && o[i] instanceof Character){
			Character c = (Character) o[i];
			i++;
			List<BlockData> list = new ArrayList<BlockData>();
			while(i<o.length && (o[i] instanceof Block || o[i]==null)){
				Block b = (Block)o[i];
				if(!this.blocks.contains(b)){
					this.blocks.add(b);
				}
				i++;
				int meta = -1;
				if(i<o.length && o[i] instanceof Integer){
					meta = ((Integer)o[i]).intValue();
					i++;
				}
				list.add(new BlockData(b, meta));
			}
			map.put(c, list.toArray(new BlockData[list.size()]));
		}
		
		this.size.y = layer.size();
		for(int y=0; y<this.size.y; y++){
			String[] strMap = layer.get(y);
			if(this.size.z < strMap.length){
				this.size.z = strMap.length;
			}
			for(int z=0; z<strMap.length; z++){
				String line = strMap[z];
				line = line.replaceAll("!", "");
				if(this.size.x < line.length()){
					this.size.x = line.length();
				}
			}
		}
		
		this.array = new PlaceData[this.size.x][this.size.y][this.size.z];
		
		for(int y=0; y<this.size.y; y++){
			String[] strMap = layer.get(y);
			for(int z=0; z<strMap.length; z++){
				String line = strMap[z];
				int xp = 0;
				for(int x=0; x+xp<line.length() && x<this.size.x; x++){
					char c = line.charAt(x+xp);
					boolean reverse = false;
					if(c=='!'){
						reverse = true;
						xp++;
						c = line.charAt(x+xp);
					}
					PlaceData s = new PlaceData(reverse, map.get(Character.valueOf(c)));
					if(s.blockDatas==null && !this.blocks.contains(Blocks.air)){
						this.blocks.add(Blocks.air);
					}
					this.array[x][y][z] = s;
				}
			}
		}
		
		this.doMirrow = true;
		
		for(int y=0; y<this.size.y && this.doMirrow; y++){
			int maxX = (this.size.x)/2;
			for(int z=0; z<this.size.z && this.doMirrow; z++){
				for(int x=0; x<=maxX && this.doMirrow; x++){
					if(this.array[x][y][z] != this.array[this.size.x - 1 - x][y][z]){
						this.doMirrow = false;
					}
				}
			}
			int maxZ = (this.size.z)/2;
			for(int x=0; x<this.size.x && this.doMirrow; x++){
				for(int z=0; z<=maxZ && this.doMirrow; z++){
					if(this.array[x][y][z] != this.array[x][y][this.size.z - 1 - z]){
						this.doMirrow = false;
					}
				}
			}
		}
		
	}
	
	public boolean getStructRotation(World world, PC_Vec3I pos, PC_Direction dir){
		for(int x=0; x<this.size.x; x++){
			for(int y=0; y<this.size.y; y++){
				for(int z=0; z<this.size.z; z++){
					int xx = x, zz = z;
					if(dir==PC_Direction.WEST || dir==PC_Direction.NORTH){
						xx=-x;
					}
					if(dir==PC_Direction.WEST || dir==PC_Direction.SOUTH){
						zz=-z;
					}
					if(dir.offsetX==0){
						int tmp = xx;
						xx = zz;
						zz = tmp;
					}
					PlaceData ok = this.array[x][y][z];
					if(ok!=null){
						PC_Vec3I p = pos.offset(xx, this.size.y-y-1, zz);
						Block block = PC_Utils.getBlock(world, p);
						int md = PC_Utils.getMetadata(world, p);
						boolean isOk = false;
						if(ok.blockDatas!=null){
							isOk = true;
							for(BlockData s:ok.blockDatas){
								if(s.block==block && (s.meta==-1 || md==s.meta)){
									isOk = false;
									break;
								}
							}
						}
						if(ok.reverse != isOk){
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	private PC_Direction DIRS[] = {PC_Direction.NORTH, PC_Direction.EAST, PC_Direction.SOUTH, PC_Direction.WEST};
	
	public StructStart getStructStart(World world, PC_Vec3I pos){
		if(!contains(PC_Utils.getBlock(world, pos))){
			return null;
		}
		for(PC_Direction dir:this.DIRS){
			for(int x=0; x<this.size.x; x++){
				for(int y=0; y>-this.size.y; y--){
					for(int z=0; z<this.size.z; z++){
						int xx = x, zz = z;
						if(dir==PC_Direction.EAST || dir==PC_Direction.SOUTH){
							xx=-x;
						}
						if(dir==PC_Direction.EAST || dir==PC_Direction.NORTH){
							zz=-z;
						}
						if(dir.offsetX==0){
							int tmp = xx;
							xx = zz;
							zz = tmp;
						}
						PC_Vec3I p = pos.offset(xx, y, zz);
						if(getStructRotation(world, p, dir)){
							return new StructStart(p, dir);
						}
					}	
				}
			}
			if(this.doMirrow)
				return null;
		}
		return null;
	}
	
	public boolean isStruct(World world, PC_Vec3I pos){
		StructStart structStart = getStructStart(world, pos);
		if(structStart==null)
			return false;
		if(this.obj==null)
			return true;
		return this.obj.foundStructAt(world, structStart);
	}
	
	public boolean contains(Block block){
		return this.blocks.contains(block);
	}
	
	public static class StructStart{
		
		public PC_Vec3I pos;
		public PC_Direction dir;
		
		StructStart(PC_Vec3I pos, PC_Direction dir) {
			this.pos = pos;
			this.dir = dir;
		}
	}
	
}
