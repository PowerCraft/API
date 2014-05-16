package powercraft.api.nodesys;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;


public class PC_NodeGridBase extends PC_NodeGrid {

	private int version;
	private List<PC_NodeGrid> groupGrids = new ArrayList<PC_NodeGrid>();
	
	public PC_NodeGridBase(){
		version = ((Long) System.currentTimeMillis()).hashCode();
	}
	
	public PC_NodeGridBase(NBTTagCompound nbtTagCompound){
		super(nbtTagCompound);
		this.version = nbtTagCompound.getInteger("version");
		NBTTagList list = nbtTagCompound.getTagList("groups", NBT.TAG_COMPOUND);
		for(int i=0; i<list.tagCount(); i++) {
			groupGrids.add(new PC_NodeGrid(list.getCompoundTagAt(i)));
		}
	}

	public void add(PC_NodeGrid nodeGrid) {
		groupGrids.add(nodeGrid);
	}
	
	public PC_NodeGrid getGrid(String name){
		for(PC_NodeGrid groupGrid:groupGrids){
			if(name.equals(groupGrid.getName())){
				return groupGrid;
			}
		}
		return null;
	}
	
	@Override
	public void saveTo(NBTTagCompound nbtTagCompound){
		super.saveTo(nbtTagCompound);
		nbtTagCompound.setInteger("version", this.version);
		NBTTagList list = new NBTTagList();
		for(int i=0; i<groupGrids.size(); i++) {
			NBTTagCompound compound = new NBTTagCompound();
			groupGrids.get(i).saveTo(compound);
			list.appendTag(compound);
		}
		nbtTagCompound.setTag("groups", list);
	}
	
	@Override
	public int getVersion() {
		return version;
	}
	
}
