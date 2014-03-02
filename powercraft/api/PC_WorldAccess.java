package powercraft.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.World;
import powercraft.api.recipes.PC_Recipes;
import powercraft.api.reflect.PC_Reflection;

public class PC_WorldAccess implements IWorldAccess {

	private World world;
	
	PC_WorldAccess(World world){
		this.world = world;
	}
	
	@Override
	public void markBlockForUpdate(int x, int y, int z) {
		Class<?> caller = PC_Reflection.getCallerClass(2);
		if(caller==ItemBlock.class || caller==World.class){
			PC_Recipes.tryToDo3DRecipeAuto(this.world, x, y, z);
		}
	}

	@Override
	public void markBlockForRenderUpdate(int x, int y, int z) {/**/}

	@Override
	public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {/**/}

	@Override
	public void playSound(String sound, double x, double y, double z, float var8, float var9) {/**/}

	@Override
	public void playSoundToNearExcept(EntityPlayer player, String sound, double x, double y, double z, float var9, float var10) {/**/}

	@Override
	public void spawnParticle(String particle, double x, double y, double z, double velX, double velY, double velZ) {/**/}

	@Override
	public void onEntityCreate(Entity entity) {/**/}

	@Override
	public void onEntityDestroy(Entity entity) {/**/}

	@Override
	public void playRecord(String sound, int x, int y, int z) {/**/}

	@Override
	public void broadcastSound(int var1, int var2, int var3, int var4, int var5) {/**/}

	@Override
	public void playAuxSFX(EntityPlayer player, int var2, int var3, int var4, int var5, int var6) {/**/}

	@Override
	public void destroyBlockPartially(int var1, int var2, int var3, int var4, int var5) {/**/}

	@Override
	public void onStaticEntitiesChanged() {/**/}

}
