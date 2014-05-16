package powercraft.api;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.management.InstanceAlreadyExistsException;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;
import powercraft.api.block.PC_AbstractBlockBase;
import powercraft.api.block.PC_Block;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.building.PC_Build.ItemStackSpawn;
import powercraft.api.reflect.PC_Reflection;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class PC_Utils {

	private static PC_Utils INSTANCE;

	public static final int BLOCK_NOTIFY = 1, BLOCK_UPDATE = 2, BLOCK_ONLY_SERVERSIDE = 4;

	PC_Utils() throws InstanceAlreadyExistsException {
		if (INSTANCE != null) {
			throw new InstanceAlreadyExistsException();
		}
		INSTANCE = this;
	}

	public static <T> T as(Object obj, Class<T> c) {
		if (obj != null && c.isAssignableFrom(obj.getClass())) {
			return c.cast(obj);
		}
		return null;
	}

	public static TileEntity getTileEntity(IBlockAccess world, int x, int y, int z) {
		return world.getTileEntity(x, y, z);
	}

	public static <T> T getTileEntity(IBlockAccess world, int x, int y, int z, Class<T> c) {
		return as(world.getTileEntity(x, y, z), c);
	}

	public static TileEntity getTileEntity(IBlockAccess world, PC_Vec3I pos) {
		return world.getTileEntity(pos.x, pos.y, pos.z);
	}

	public static <T> T getTileEntity(IBlockAccess world, PC_Vec3I pos, Class<T> c) {
		return getTileEntity(world, pos.x, pos.y, pos.z, c);
	}
	
	public static Block getBlock(IBlockAccess world, int x, int y, int z) {
		return world.getBlock(x, y, z);
	}

	public static <T> T getBlock(IBlockAccess world, int x, int y, int z, Class<T> c) {
		return as(world.getBlock(x, y, z), c);
	}

	public static Block getBlock(IBlockAccess world, PC_Vec3I pos) {
		return getBlock(world, pos.x, pos.y, pos.z);
	}

	public static <T> T getBlock(IBlockAccess world, PC_Vec3I pos, Class<T> c) {
		return getBlock(world, pos.x, pos.y, pos.z, c);
	}

	public static Block getBlock(String name) {
		return getBlock(name, Block.class);
	}

	public static String getBlockSID(Block block) {
		return Block.blockRegistry.getNameForObject(block);
	}
	
	public static <T> T getBlock(String modId, String name, Class<T> c) {
		return as(Block.blockRegistry.getObject(modId + ":" + name), c);
	}

	public static <T> T getBlock(String name, Class<T> c) {
		return as(Block.blockRegistry.getObject(name), c);
	}

	public static boolean setBlock(World world, int x, int y, int z, Block block, int metadata, int flag) {
		return world.setBlock(x, y, z, block, metadata, flag);
	}

	public static boolean setBlock(World world, int x, int y, int z, Block block, int metadata) {
		return setBlock(world, x, y, z, block, metadata, BLOCK_NOTIFY | BLOCK_UPDATE);
	}

	public static boolean setBlock(World world, int x, int y, int z, Block block) {
		return setBlock(world, x, y, z, block, 0);
	}

	public static boolean setBlock(World world, PC_Vec3I pos, Block block, int metadata, int flag) {
		return setBlock(world, pos.x, pos.y, pos.z, block, metadata, flag);
	}

	public static boolean setBlock(World world, PC_Vec3I pos, Block block, int metadata) {
		return setBlock(world, pos, block, metadata, BLOCK_NOTIFY | BLOCK_UPDATE);
	}

	public static boolean setBlock(World world, PC_Vec3I pos, Block block) {
		return setBlock(world, pos, block, 0);
	}

	public static boolean setAir(World world, int x, int y, int z) {
		return setBlock(world, x, y, z, Blocks.air);
	}

	public static boolean setAir(World world, PC_Vec3I pos) {
		return setBlock(world, pos, Blocks.air);
	}

	public static int getMetadata(IBlockAccess world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}

	public static int getMetadata(IBlockAccess world, PC_Vec3I pos) {
		return getMetadata(world, pos.x, pos.y, pos.z);
	}

	public static boolean setMetadata(World world, int x, int y, int z, int metadata) {
		return setMetadata(world, x, y, z, metadata, BLOCK_NOTIFY | BLOCK_UPDATE);
	}

	public static boolean setMetadata(World world, int x, int y, int z, int metadata, int flag) {
		return world.setBlockMetadataWithNotify(x, y, z, metadata, flag);
	}

	public static boolean setMetadata(World world, PC_Vec3I pos, int metadata) {
		return setMetadata(world, pos.x, pos.y, pos.z, metadata, BLOCK_NOTIFY | BLOCK_UPDATE);
	}

	public static boolean setMetadata(World world, PC_Vec3I pos, int metadata, int flag) {
		return setMetadata(world, pos.x, pos.y, pos.z, metadata, flag);
	}

	public static Item getItem(ItemStack itemStack) {
		return itemStack.getItem();
	}

	public static <T> T getItem(ItemStack itemStack, Class<T> c) {
		return as(getItem(itemStack), c);
	}

	public static Item getItem(String name) {
		return getItem(name, Item.class);
	}
	
	public static Item getItemForBlock(Block block) {
		return Item.getItemFromBlock(block);
	}

	public static <T> T getItem(String modId, String name, Class<T> c) {
		return as(Item.itemRegistry.getObject(modId + ":" + name), c);
	}

	public static <T> T getItem(String name, Class<T> c) {
		return as(Item.itemRegistry.getObject(name), c);
	}

	public static PC_Direction getSidePosition(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return getSidePosition(world, x, y, z, PC_Direction.fromForgeDirection(side));
	}

	public static PC_Direction getSidePosition(IBlockAccess world, int x, int y, int z, int side) {
		return getSidePosition(world, x, y, z, PC_Direction.fromSide(side));
	}

	public static PC_Direction getSidePosition(IBlockAccess world, int x, int y, int z, PC_Direction side) {
		Block block = getBlock(world, x, y, z);
		if (block instanceof PC_AbstractBlockBase) {
			if (((PC_AbstractBlockBase) block).canRotate(world, x, y, z)) {
				PC_3DRotation rotation = ((PC_AbstractBlockBase) block).getRotation(world, x, y, z);
				if (rotation != null) {
					return rotation.getSidePosition(side);
				}
				return PC_Direction.UNKNOWN;
			}
		}
		return side;
	}

	public static PC_Direction getSidePositionInv(IBlockAccess world, int x, int y, int z, int side) {
		return getSidePositionInv(world, x, y, z, PC_Direction.fromSide(side));
	}

	public static PC_Direction getSidePositionInv(IBlockAccess world, int x, int y, int z, PC_Direction side) {
		Block block = getBlock(world, x, y, z);
		if (block instanceof PC_AbstractBlockBase) {
			if (((PC_AbstractBlockBase) block).canRotate(world, x, y, z)) {
				PC_3DRotation rotation = ((PC_AbstractBlockBase) block).getRotation(world, x, y, z);
				if (rotation != null) {
					return rotation.getSidePositionInv(side);
				}
				return PC_Direction.UNKNOWN;
			}
		}
		return side;
	}

	public static int getRotation(Entity entity) {
		return PC_MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
	}

	public static int getRotationMetadata(int metadata, Entity entity) {
		return (getRotation(entity) << 2) | (metadata & 3);
	}

	public static AxisAlignedBB rotateAABB(IBlockAccess world, int x, int y, int z, AxisAlignedBB box) {
		Block block = getBlock(world, x, y, z);
		if (block instanceof PC_AbstractBlockBase) {
			if (((PC_AbstractBlockBase) block).canRotate(world, x, y, z)) {
				PC_3DRotation rotation = ((PC_AbstractBlockBase) block).getRotation(world, x, y, z);
				if (rotation != null) {
					return rotation.rotateBox(box);
				}
			}
		}
		return box;
	}

	public static boolean rotateBlock(World world, int x, int y, int z, PC_Direction side) {
		Block block = getBlock(world, x, y, z);
		if (block instanceof PC_Block) {
			if (((PC_AbstractBlockBase) block).canRotate(world, x, y, z)) {
				if (block instanceof PC_Block) {
					int metadata = getMetadata(world, x, y, z);
					int rotation = (metadata >> 2) & 0x3;
					if (side == PC_Direction.UP) {
						rotation++;
						if (rotation > 3)
							rotation = 0;
					} else if (side == PC_Direction.DOWN) {
						rotation--;
						if (rotation < 0)
							rotation = 3;
					} else {
						return false;
					}
					setMetadata(world, x, y, z, rotation << 2 | (metadata & 3));
				} else if (block instanceof PC_BlockTileEntity) {
					PC_TileEntity te = getTileEntity(world, x, y, z, PC_TileEntity.class);
					if (te != null) {
						PC_3DRotation rotation = te.get3DRotation();
						if (rotation != null) {
							rotation = rotation.rotateAround(side);
							if (rotation != null) {
								return te.set3DRotation(rotation);
							}
						}
					}
				}
			}
		}
		return false;
	}

	public static ForgeDirection[] getValidRotations(World world, int x, int y, int z) {
		Block block = getBlock(world, x, y, z);
		if (block instanceof PC_Block) {
			if (((PC_AbstractBlockBase) block).canRotate(world, x, y, z)) {
				if (block instanceof PC_Block) {
					return new ForgeDirection[] { ForgeDirection.UP, ForgeDirection.DOWN };
				} else if (block instanceof PC_BlockTileEntity) {
					PC_TileEntity te = getTileEntity(world, x, y, z, PC_TileEntity.class);
					if (te != null) {
						PC_3DRotation rotation = te.get3DRotation();
						if (rotation != null) {
							return rotation.getValidRotations();
						}
					}
				}
			}
		}
		return null;
	}

	public static boolean canPlaceEntityOnSide(World world, int x, int y, int z, PC_Direction side, Block block,
			Entity entity, ItemStack itemStack) {
		Block block1 = PC_Utils.getBlock(world, x, y, z);
		AxisAlignedBB box = null;
		if (block instanceof PC_AbstractBlockBase && entity != null) {
			if (((PC_AbstractBlockBase) block).canRotate()) {
				box = ((PC_AbstractBlockBase) block).getMainCollisionBoundingBoxPre(world, x, y, z);
				if (box != null) {
					if (block instanceof PC_Block) {
						int md = getRotationMetadata(0, entity) >> 2;
						PC_3DRotation rotation = new PC_3DRotationY(md);
						box = rotation.rotateBox(box);
					} else if (block instanceof PC_BlockTileEntity) {
						PC_3DRotation rotation = new PC_3DRotationFull(side, entity);
						box = rotation.rotateBox(box);
					}
					box.offset(x, y, z);
				}
			}
		} else {
			box = block.getCollisionBoundingBoxFromPool(world, x, y, z);
		}
		return box != null && !world.checkNoEntityCollision(box, entity) ? false
				: (block1.getMaterial() == Material.circuits && block == Blocks.anvil ? true : block1.isReplaceable(
						world, x, y, z) && block.canReplace(world, x, y, z, side.ordinal(), itemStack));
	}

	public static void spawnItem(World world, double x, double y, double z, ItemStack itemStack) {
		if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops") && itemStack != null) {
			spawnItemChecked(world, x, y, z, itemStack);
		}
	}

	public static void spawnItem(World world, PC_Vec3 pos, ItemStack itemStack) {
		spawnItem(world, pos.x, pos.y, pos.z, itemStack);
	}

	public static void spawnItems(World world, double x, double y, double z, List<ItemStack> itemStacks) {
		if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops") && itemStacks != null) {
			for (ItemStack itemStack : itemStacks) {
				if (itemStack != null) {
					spawnItemChecked(world, x, y, z, itemStack);
				}
			}
		}
	}

	public static void spawnItems(World world, PC_Vec3 pos, List<ItemStack> itemStack) {
		spawnItems(world, pos.x, pos.y, pos.z, itemStack);
	}

	public static void spawnItems(World world, List<ItemStackSpawn> itemStacks) {
		if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops") && itemStacks != null) {
			for (ItemStackSpawn itemStack : itemStacks) {
				if (itemStack != null) {
					spawnItemChecked(world, itemStack.pos.x, itemStack.pos.y, itemStack.pos.z, itemStack.itemStack);
				}
			}
		}
	}

	private static void spawnItemChecked(World world, double x, double y, double z, ItemStack itemStack) {
		float f = 0.7F;
		double d0 = (world.rand.nextFloat() * f) + (1.0F - f) * 0.5;
		double d1 = (world.rand.nextFloat() * f) + (1.0F - f) * 0.5;
		double d2 = (world.rand.nextFloat() * f) + (1.0F - f) * 0.5;
		EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, itemStack);
		entityitem.delayBeforeCanPickup = 10;
		world.spawnEntityInWorld(entityitem);
	}
	
	public static void spawnEntity(World world, Entity entity) {
		if (!world.isRemote) {
			world.spawnEntityInWorld(entity);
		}
	}

	public static File getPowerCraftFile(String directory, String f) {
		File file = INSTANCE.iGetPowerCraftFile();
		if (!file.exists())
			file.mkdir();
		if (directory != null) {
			file = new File(file, directory);
			if (!file.exists())
				file.mkdir();
		}
		if(f==null)
			return file;
		return new File(file, f);
	}

	public static MinecraftServer mcs() {
		return MinecraftServer.getServer();
	}

	public static int getRedstoneValue(World world, int x, int y, int z) {
		return world.getStrongestIndirectPower(x, y, z);
	}

	public static GameType getGameTypeFor(EntityPlayer player) {
		return INSTANCE.iGetGameTypeFor(player);
	}

	public static boolean isCreative(EntityPlayer entityPlayer) {
		return getGameTypeFor(entityPlayer).isCreative();
	}

	public static void notifyBlockOfNeighborChange(World world, int x, int y, int z, Block neightbor) {
		Block block = getBlock(world, x, y, z);
		if (block != null) {
			block.onNeighborBlockChange(world, x, y, z, neightbor);
		}
	}
	
	public static void notifyBlockChange(World world, int x, int y, int z, Block block) {
		world.notifyBlockChange(x, y, z, block);
	}

	@SuppressWarnings("static-method")
	File iGetPowerCraftFile() {
		return mcs().getFile("PowerCraft");
	}

	@SuppressWarnings("static-method")
	GameType iGetGameTypeFor(EntityPlayer player) {

		return ((EntityPlayerMP) player).theItemInWorldManager.getGameType();
	}

	public static ResourceLocation getResourceLocation(PC_Module module, String file) {

		return new ResourceLocation(module.getMetadata().modId.toLowerCase(), file);
	}

	public static boolean isOP(EntityPlayer player) {
		return isOP(getUsername(player));
	}

	public static String getUsername(EntityPlayer player) {
		return player.getGameProfile().getName();
	}

	public static boolean isOP(String username) {
		return mcs().getConfigurationManager().getOps().contains(username);
	}

	public static PC_Side getSide() {
		return INSTANCE.iGetSide();
	}

	@SuppressWarnings("static-method")
	PC_Side iGetSide() {
		return PC_Side.SERVER;
	}

	static void markThreadAsServer() {
		INSTANCE.iMarkThreadAsServer();
	}

	void iMarkThreadAsServer() {
		//
	}

	public static boolean isServer() {
		return getSide() == PC_Side.SERVER;
	}

	public static boolean isClient() {
		return getSide() == PC_Side.CLIENT;
	}

	public static PC_Module getActiveModule() {
		ModContainer container = Loader.instance().activeModContainer();
		Object mod = container.getMod();
		if (mod instanceof PC_Module) {
			return (PC_Module) mod;
		}
		return null;
	}

	public static ModContainer getActiveMod() {
		return Loader.instance().activeModContainer();
	}

	public static PC_Direction getEntityMovement2D(Entity entity) {
		double mx = entity.motionX;
		double mz = entity.motionZ;
		if (Math.abs(mx) > Math.abs(mz)) {
			if (mx > 0) {
				return PC_Direction.EAST;
			}
			return PC_Direction.WEST;
		}
		if (mz > 0) {
			return PC_Direction.SOUTH;
		}
		if (mz == 0) {
			if (entity instanceof EntityLivingBase) {
				return PC_Direction.fromRotationY(getRotation(entity)).getOpposite();
			}
		}
		return PC_Direction.NORTH;
	}

	public static NBTTagCompound getNBTTagOf(Object obj) {
		NBTTagCompound tag;
		if (obj instanceof Entity) {
			tag = ((Entity) obj).getEntityData();
		} else if (obj instanceof ItemStack) {
			tag = ((ItemStack) obj).getTagCompound();
		} else {
			return null;
		}
		if (tag == null || !tag.hasKey("PowerCraft"))
			return null;
		return tag.getCompoundTag("PowerCraft");
	}

	public static NBTTagCompound getWritableNBTTagOf(Object obj) {
		NBTTagCompound tag;
		if (obj instanceof Entity) {
			tag = ((Entity) obj).getEntityData();
		} else if (obj instanceof ItemStack) {
			tag = ((ItemStack) obj).getTagCompound();
			if (tag == null) {
				((ItemStack) obj).setTagCompound(tag = new NBTTagCompound());
			}
		} else {
			return null;
		}
		if (tag.hasKey("PowerCraft")) {
			return tag.getCompoundTag("PowerCraft");
		}
		NBTTagCompound pctag = new NBTTagCompound();
		tag.setTag("PowerCraft", pctag);
		return pctag;
	}

	public static EntityPlayer getClientPlayer() {
		return INSTANCE.iGetClientPlayer();
	}

	@SuppressWarnings("static-method")
	EntityPlayer iGetClientPlayer() {
		return null;
	}

	private static MessageDigest digest;

	static {
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getMD5(String s) {
		return new String(digest.digest(s.getBytes()));
	}

	@SuppressWarnings("unused")
	public static int getSideRotation(IBlockAccess world, int x, int y, int z, PC_Direction side, int faceSide) {
		notImplementedYet("getSideRotation");
		// TODO Auto-generated method stub
		return 0;
	}

	public static int getBurnTime(ItemStack itemStack) {
		return TileEntityFurnace.getItemBurnTime(itemStack);
	}

	public static ItemStack getSmeltingResult(ItemStack itemStack) {
		if (itemStack == null)
			return null;
		ItemStack smelted = FurnaceRecipes.smelting().getSmeltingResult(itemStack);
		if (smelted == null)
			return null;
		return smelted.copy();
	}

	public static Entity getEntity(World world, int entityID) {
		return world.getEntityByID(entityID);
	}

	public static <T> T getEntity(World world, int entityID, Class<T> c) {
		return as(getEntity(world, entityID), c);
	}

	public static PC_Vec4I averageVec4I(PC_Vec4I... vecs) {
		PC_Vec4I allInOne = PC_Vec4I.sum(vecs);
		int notNullNumber = 0;
		for (PC_Vec4I vec : vecs) {
			if (vec != null)
				if (!vec.isZero())
					notNullNumber++;
		}
		return allInOne.divide(notNullNumber).roundToInt();
	}

	public static boolean isEntityFX(Entity entity) {
		return INSTANCE.iIsEntityFX(entity);
	}

	@SuppressWarnings({ "static-method", "unused" })
	boolean iIsEntityFX(Entity entity) {
		return false;
	}

	public static void deleteDirectoryOrFile(File file) {
		if (file.isDirectory()) {
			for (File c : file.listFiles()) {
				deleteDirectoryOrFile(c);
			}
		}
		file.delete();
	}

	public static int getDimensionID(World world) {
		return world.provider.dimensionId;
	}

	public static int getTemperature(World world, int x, int y, int z) {
		return PC_BlockTemperatures.getTemperature(world, x, y, z);
	}

	public static BiomeGenBase getBiome(World world, int x, int z) {
		return world.getBiomeGenForCoords(x, z);
	}

	public static void setArrayContentsToNull(Object[] array) {
		Arrays.fill(array, null);
	}

	public static boolean isBlockSideSolid(IBlockAccess world, int x, int y, int z, PC_Direction dir){
		Block block = PC_Utils.getBlock(world, x, y, z);
		return block.isSideSolid(world, x, y, z, dir.toForgeDirection());
	}

	public static void removeBlock(World world, int x, int y, int z) {
		Block block = PC_Utils.getBlock(world, x, y, z);
		block.dropBlockAsItem(world, x, y, z, getMetadata(world, x, y, z), 0);
        setAir(world, x, y, z);
	}

	public static void rotateAABB(AxisAlignedBB box, PC_Direction dir) {
		double tmp;
		switch(dir){
		case UP:
			tmp = box.maxZ;
			box.maxZ = box.maxX;
			box.maxX = 1-box.minZ;
			box.minZ = box.minX;
			box.minX = 1-tmp;
			break;
		default:
			notImplementedYet("AABB rotations around other than UP");
			break;
		}
	}
	
	private static List<String> messages = new ArrayList<String>();
	
	public static void notImplementedYet(String what){
		if(!messages.contains(what)){
			messages.add(what);
			PC_Logger.severe("%s not implemented yet", what);
		}
	}

	public static void staticClassConstructor() {
		Class<?> caller = PC_Reflection.getCallerClass();
		throw new InstantiationError(caller+" is a static class, therefore there can't be an instance");
	}

	public static CreativeTabs[] getCreativeTabsFor(CreativeTabs creativeTab, PC_Module module) {
		List<CreativeTabs> creativeTabList = new ArrayList<CreativeTabs>();
		creativeTabList.add(creativeTab);
		if(!creativeTabList.contains(module.getCreativeTab()))
			creativeTabList.add(module.getCreativeTab());
		if(!creativeTabList.contains(PC_Api.INSTANCE.getCreativeTab()))
			creativeTabList.add(PC_Api.INSTANCE.getCreativeTab());
		return creativeTabList.toArray(new CreativeTabs[creativeTabList.size()]);
	}

	public static int getColorFor(int index) {
		return ItemDye.field_150922_c[index];
	}

	public static int countBits(int mask) {
		int bits = 0;
		for(int i=0; i<32; i++){
			if((mask & 1<<i)!=0){
				bits++;
			}
		}
		return bits;
	}

	public static boolean isBlockReplaceable(World world, int x, int y, int z) {
		Block block = getBlock(world, x, y, z);
		if(block.isReplaceable(world, x, y, z))
			return true;
		return block==Blocks.snow_layer||block == Blocks.vine || block == Blocks.tallgrass || block == Blocks.deadbush;
	}

	public static PC_Vec3 getLookDir(EntityPlayer player) {
		float pitch = (float) (player.rotationPitch*Math.PI/180);
		double y = -PC_MathHelper.sin(pitch);
		double o = PC_MathHelper.cos(pitch);
		float yaw = (float) (player.rotationYaw*Math.PI/180);
		double x = PC_MathHelper.sin(-yaw)*o;
		double z = PC_MathHelper.cos(-yaw)*o;
		PC_Vec3 lookDir = new PC_Vec3(x, y, z);
		return lookDir;
	}
	
}
