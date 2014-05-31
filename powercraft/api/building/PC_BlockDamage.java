package powercraft.api.building;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_Field.Flag;
import powercraft.api.PC_NBTTagHandler;
import powercraft.api.PC_Side;
import powercraft.api.PC_TickHandler;
import powercraft.api.PC_Utils;
import powercraft.api.PC_TickHandler.PC_ITickHandler;
import powercraft.api.PC_Vec4I;
import powercraft.api.PC_WorldSaveData;
import powercraft.api.building.PC_Build.ItemStackSpawn;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.reflect.PC_Reflection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class PC_BlockDamage extends PC_WorldSaveData implements PC_ITickHandler {
	
	private static final String NAME = "powercraft-blockdamage";
	
	private List<PC_Vec4I> updated = new ArrayList<PC_Vec4I>(); 
	private HashMap<PC_Vec4I, float[]> damages = new HashMap<PC_Vec4I, float[]>(); 
	private static PC_BlockDamage INSTANCE;
	
	private static PC_BlockDamage getInstance(){
		if(INSTANCE==null){
			INSTANCE = loadOrCreate(NAME, PC_BlockDamage.class);
		}
		return INSTANCE;
	}
	
	public PC_BlockDamage(String name){
		super(name);
		PC_TickHandler.registerTickHandler(this);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		PC_NBTTagHandler.loadMapFromNBT(nbtTagCompound, "damages", this.damages, PC_Vec4I.class, float[].class, Flag.SAVE);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		PC_NBTTagHandler.saveMapToNBT(nbtTagCompound, "damages", this.damages, Flag.SAVE);
	}
	
	@Override
	public void cleanup() {
		INSTANCE = null;
		PC_TickHandler.removeTickHander(this);
	}

	// 0-10, on 10 destroy
	public static boolean damageBlock(World world, int x, int y, int z, float amount){
		if(world.isRemote)
			return false;
		PC_Vec4I v4 = new PC_Vec4I(x, y, z, world.provider.dimensionId);
		getInstance();
		float[] damage = INSTANCE.damages.get(v4);
		int pd = -1;
		if(damage==null){
			damage = new float[2];
			PC_Harvest harvest = PC_Build.getHarvest(world, x, y, z, -1);
			if(harvest==null){
				damage[1] = 1;
			}else{
				damage[1] = harvest.digTimeMultiply;
			}
			INSTANCE.damages.put(v4, damage);
		}else{
			pd = (int)damage[0];
		}
		damage[0] += amount/damage[1];
		INSTANCE.markDirty();
		int npd = (int)damage[0];
		if(damage[0]>=10){
			INSTANCE.updated.remove(v4);
			INSTANCE.damages.remove(v4);
			INSTANCE.markDirty();
			PC_PacketHandler.sendToAllAround(new PC_PacketBlockBreaking(x, y, z, -1), v4.w, x, y, z, 32);
			PC_Harvest harvest = PC_Build.getHarvest(world, x, y, z, -1);
			List<ItemStackSpawn> list = PC_Build.harvestWithDropPos(world, harvest, 0);
			PC_Utils.spawnItems(world, list);
			return true;
		}
		if(pd!=npd)
			PC_PacketHandler.sendToAllAround(new PC_PacketBlockBreaking(x, y, z, npd), v4.w, x, y, z, 32);
		if(!INSTANCE.updated.contains(v4))
			INSTANCE.updated.add(v4);
		return false;
	}
	
	@Override
	public void onStartTick(PC_Side side) {
		if(side==PC_Side.SERVER)
			this.updated.clear();
	}

	@Override
	public void onEndTick(PC_Side side) {
		if(side==PC_Side.SERVER){
			Iterator<PC_Vec4I> i = this.damages.keySet().iterator();
			while(i.hasNext()){
				if(!this.updated.contains(i.next()))
					i.remove();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
	static void setClientDamage(int x, int y, int z, int damage){
		Map<Number, DestroyBlockProgress> damagedBlocks = PC_Reflection.getValue(RenderGlobal.class, PC_ClientUtils.mc().renderGlobal, 29, Map.class);
		int cloudTickCounter = PC_Reflection.getValue(RenderGlobal.class, PC_ClientUtils.mc().renderGlobal, 19, int.class).intValue();
		PosDamage pd = new PosDamage(x, y, z);
		if(damage==-1){
			damagedBlocks.remove(pd);
		}else{
			DestroyBlockProgress destroyBlockProgress = damagedBlocks.get(pd);
			if(destroyBlockProgress==null){
				destroyBlockProgress = new DestroyBlockProgress(0, x, y, z);
				damagedBlocks.put(pd, destroyBlockProgress);
			}
			destroyBlockProgress.setPartialBlockDamage(damage);
			destroyBlockProgress.setCloudUpdateTick(cloudTickCounter);
		}
	}
	
	@SideOnly(Side.CLIENT)
	private static class PosDamage extends Number{

		private static final long serialVersionUID = -3611831358033254957L;
		
		private int x;
		private int y;
		private int z;
		
		public PosDamage(int x, int y, int z){
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		@Override
		public double doubleValue() {
			return -1;
		}

		@Override
		public float floatValue() {
			return -1;
		}

		@Override
		public int intValue() {
			return -1;
		}

		@Override
		public long longValue() {
			return -1;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + this.x;
			result = prime * result + this.y;
			result = prime * result + this.z;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			PosDamage other = (PosDamage) obj;
			if (this.x != other.x) return false;
			if (this.y != other.y) return false;
			if (this.z != other.z) return false;
			return true;
		}
		
	}
	
}
