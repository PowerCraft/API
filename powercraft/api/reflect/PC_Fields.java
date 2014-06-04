package powercraft.api.reflect;

import java.util.Map;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.IIcon;
import net.minecraft.util.IntHashMap;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraftforge.oredict.ShapedOreRecipe;
import powercraft.api.PC_Utils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public final class PC_Fields {
	
	private PC_Fields(){
		PC_Utils.staticClassConstructor();
	}
	public static final int INDEX_NetHandlerPlayServer_field_147372_n = 13;
	public static final int INDEX_EntityLivingBase_recentlyHit = 31;
	public static final int INDEX_EntityXPOrb_closestPlayer = 5;
	public static final int INDEX_EntityXPOrb_xpTargetColor = 6;
	public static final int INDEX_RenderGlobal_cloudTickCounter = 19;
	public static final int INDEX_RenderGlobal_damagedBlocks = 29;
	public static final int INDEX_RenderGlobal_destroyBlockIcons = 31;
	public static final int INDEX_PlayerControllerMP_isHittingBlock = 9;
	public static final int INDEX_PlayerControllerMP_currentGameType = 10;
	public static final int INDEX_GuiMainMenu_splashText = 4;
	public static final int INDEX_GuiMainMenu_notificationLine1 = 12;
	public static final int INDEX_GuiMainMenu_notificationLine2 = 13;
	public static final int INDEX_GuiMainMenu_notificationLink = 14;
	public static final int INDEX_ShapedOreRecipe_width = 4;
	
	public static final PC_ReflectionField<NetHandlerPlayServer, IntHashMap> NetHandlerPlayServer_field_147372_n = new PC_ReflectionField<NetHandlerPlayServer, IntHashMap>(NetHandlerPlayServer.class, INDEX_NetHandlerPlayServer_field_147372_n, IntHashMap.class);
	public static final PC_ReflectionField<EntityLivingBase, Integer> EntityLivingBase_recentlyHit = new PC_ReflectionField<EntityLivingBase, Integer>(EntityLivingBase.class, INDEX_EntityLivingBase_recentlyHit, int.class);
	public static final PC_ReflectionField<EntityXPOrb, EntityPlayer> EntityXPOrb_closestPlayer = new PC_ReflectionField<EntityXPOrb, EntityPlayer>(EntityXPOrb.class, INDEX_EntityXPOrb_closestPlayer, EntityPlayer.class);
	public static final PC_ReflectionField<EntityXPOrb, Integer> EntityXPOrb_xpTargetColor = new PC_ReflectionField<EntityXPOrb, Integer>(EntityXPOrb.class, INDEX_EntityXPOrb_xpTargetColor, int.class);
	public static final PC_ReflectionField<ShapedOreRecipe, Integer> ShapedOreRecipe_width = new PC_ReflectionField<ShapedOreRecipe, Integer>(ShapedOreRecipe.class, INDEX_ShapedOreRecipe_width, int.class);
	
	
	@SideOnly(Side.CLIENT)
	public static final class Client{
		public static final PC_ReflectionField<RenderGlobal, Integer> RenderGlobal_cloudTickCounter = new PC_ReflectionField<RenderGlobal, Integer>(RenderGlobal.class, INDEX_RenderGlobal_cloudTickCounter, int.class);
		@SuppressWarnings("rawtypes")
		public static final PC_ReflectionField<RenderGlobal, Map> RenderGlobal_damagedBlocks = new PC_ReflectionField<RenderGlobal, Map>(RenderGlobal.class, INDEX_RenderGlobal_damagedBlocks, Map.class);
		public static final PC_ReflectionField<RenderGlobal, IIcon[]> RenderGlobal_destroyBlockIcons = new PC_ReflectionField<RenderGlobal, IIcon[]>(RenderGlobal.class, INDEX_RenderGlobal_destroyBlockIcons, IIcon[].class);
		public static final PC_ReflectionField<PlayerControllerMP, Boolean> PlayerControllerMP_isHittingBlock = new PC_ReflectionField<PlayerControllerMP, Boolean>(PlayerControllerMP.class, INDEX_PlayerControllerMP_isHittingBlock, boolean.class);
		public static final PC_ReflectionField<PlayerControllerMP, GameType> PlayerControllerMP_currentGameType = new PC_ReflectionField<PlayerControllerMP, GameType>(PlayerControllerMP.class, INDEX_PlayerControllerMP_currentGameType, GameType.class);
		public static final PC_ReflectionField<GuiMainMenu, String> GuiMainMenu_splashText = new PC_ReflectionField<GuiMainMenu, String>(GuiMainMenu.class, INDEX_GuiMainMenu_splashText, String.class);
		public static final PC_ReflectionField<GuiMainMenu, String> GuiMainMenu_notificationLine1 = new PC_ReflectionField<GuiMainMenu, String>(GuiMainMenu.class, INDEX_GuiMainMenu_notificationLine1, String.class);
		public static final PC_ReflectionField<GuiMainMenu, String> GuiMainMenu_notificationLine2 = new PC_ReflectionField<GuiMainMenu, String>(GuiMainMenu.class, INDEX_GuiMainMenu_notificationLine2, String.class);
		public static final PC_ReflectionField<GuiMainMenu, String> GuiMainMenu_notificationLink = new PC_ReflectionField<GuiMainMenu, String>(GuiMainMenu.class, INDEX_GuiMainMenu_notificationLink, String.class);
	}
	
}
