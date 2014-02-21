package powercraft.api.gres;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import powercraft.api.PC_Api;
import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_Logger;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec2I;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.network.packet.PC_PacketOpenGresHandler;
import powercraft.api.network.packet.PC_PacketOpenGresItem;
import powercraft.api.network.packet.PC_PacketOpenGresTileEntity;
import powercraft.api.reflect.PC_Security;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class PC_Gres {
	
	private static TreeMap<String, PC_IGresGuiOpenHandler> guiOpenHandlers = new TreeMap<String, PC_IGresGuiOpenHandler>();
	@SideOnly(Side.CLIENT)
	private static TreeMap<String, PC_GresTexture> textures = new TreeMap<String, PC_GresTexture>();
	@SideOnly(Side.CLIENT)
	private static boolean loaded;

	public static void register(){
		PC_Security.allowedCaller("PC_Gres.register()", PC_Api.class);
		PC_PacketHandler.registerPacket(PC_PacketOpenGresHandler.class);
		PC_PacketHandler.registerPacket(PC_PacketOpenGresItem.class);
		PC_PacketHandler.registerPacket(PC_PacketOpenGresTileEntity.class);
	}
	
	@SideOnly(Side.CLIENT)
	public static void openClientGui(EntityPlayer player, PC_TileEntity tileEntity, int windowId, NBTTagCompound nbtTagCompound) {

		if (tileEntity instanceof PC_IGresGuiOpenHandler) {
			PC_IGresGui gui = ((PC_IGresGuiOpenHandler) tileEntity).openClientGui(player, nbtTagCompound);
			openClientGui(player, gui, windowId);
		}
	}


	@SideOnly(Side.CLIENT)
	public static void openClientGui(EntityPlayer player, String guiOpenHandlerName, int windowId, NBTTagCompound nbtTagCompound) {

		PC_IGresGuiOpenHandler guiOpenHandler = guiOpenHandlers.get(guiOpenHandlerName);
		if (guiOpenHandler != null) {
			PC_IGresGui gui = guiOpenHandler.openClientGui(player, nbtTagCompound);
			openClientGui(player, gui, windowId);
		}
	}


	@SideOnly(Side.CLIENT)
	public static void openClientGui(EntityPlayer player, Item item, int windowId, NBTTagCompound nbtTagCompound) {

		if (item instanceof PC_IGresGuiOpenHandler) {
			PC_IGresGui gui = ((PC_IGresGuiOpenHandler) item).openClientGui(player, nbtTagCompound);
			openClientGui(player, gui, windowId);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void openClientGui(EntityPlayer player, PC_IGresGui gui, int windowId) {
		
		if (gui != null) {
			loadTextures();
			Minecraft mc = PC_ClientUtils.mc();
			mc.displayGuiScreen(new PC_GresGuiScreen(gui));
			if (windowId != -1 && gui instanceof PC_GresBaseWithInventory) {
				player.openContainer = (PC_GresBaseWithInventory)gui;
				player.openContainer.windowId = windowId;
			}
		}
		
	}

	public static void openGui(EntityPlayer player, PC_TileEntity tileEntity) {

		if (player instanceof EntityPlayerMP && tileEntity instanceof PC_IGresGuiOpenHandler) {
			EntityPlayerMP playerMP = (EntityPlayerMP) player;
			PC_GresBaseWithInventory container = ((PC_IGresGuiOpenHandler) tileEntity).openServerGui(player);
			NBTTagCompound sendToClient = ((PC_IGresGuiOpenHandler) tileEntity).sendOnGuiOpenToClient(player);
			if(sendToClient==null){
				sendToClient = new NBTTagCompound();
			}
			long session = tileEntity.getNewSession(player);
			if (container != null) {
				playerMP.getNextWindowId();
				playerMP.closeContainer();
				int windowId = playerMP.currentWindowId;
				PC_PacketHandler.sendTo(new PC_PacketOpenGresTileEntity(tileEntity, windowId, session, sendToClient), (EntityPlayerMP)player);
				player.openContainer = container;
				player.openContainer.windowId = windowId;
				player.openContainer.addCraftingToCrafters(playerMP);
			} else {
				PC_PacketHandler.sendTo(new PC_PacketOpenGresTileEntity(tileEntity, -1, session, sendToClient), (EntityPlayerMP)player);
			}
		}
	}


	public static void openGui(EntityPlayer player, String guiOpenHandlerName) {

		if (player instanceof EntityPlayerMP) {
			PC_IGresGuiOpenHandler guiOpenHandler = guiOpenHandlers.get(guiOpenHandlerName);
			if (guiOpenHandler != null) {
				EntityPlayerMP playerMP = (EntityPlayerMP) player;
				PC_GresBaseWithInventory container = guiOpenHandler.openServerGui(player);
				NBTTagCompound sendToClient = guiOpenHandler.sendOnGuiOpenToClient(player);
				if(sendToClient==null){
					sendToClient = new NBTTagCompound();
				}
				if (container != null) {
					playerMP.getNextWindowId();
					playerMP.closeContainer();
					int windowId = playerMP.currentWindowId;
					PC_PacketHandler.sendTo(new PC_PacketOpenGresHandler(guiOpenHandlerName, windowId, sendToClient), (EntityPlayerMP)player);
					player.openContainer = container;
					player.openContainer.windowId = windowId;
					player.openContainer.addCraftingToCrafters(playerMP);
				} else {
					PC_PacketHandler.sendTo(new PC_PacketOpenGresHandler(guiOpenHandlerName, -1, sendToClient), (EntityPlayerMP)player);
				}
			}
		}
	}
	
	public static void openGui(EntityPlayer player, Item item) {

		if (player instanceof EntityPlayerMP && item instanceof PC_IGresGuiOpenHandler) {
			EntityPlayerMP playerMP = (EntityPlayerMP) player;
			PC_GresBaseWithInventory container = ((PC_IGresGuiOpenHandler) item).openServerGui(player);
			NBTTagCompound sendToClient = ((PC_IGresGuiOpenHandler) item).sendOnGuiOpenToClient(player);
			if(sendToClient==null){
				sendToClient = new NBTTagCompound();
			}
			if (container != null) {
				playerMP.getNextWindowId();
				playerMP.closeContainer();
				int windowId = playerMP.currentWindowId;
				PC_PacketHandler.sendTo(new PC_PacketOpenGresItem(item, windowId, sendToClient), (EntityPlayerMP)player);
				player.openContainer = container;
				player.openContainer.windowId = windowId;
				player.openContainer.addCraftingToCrafters(playerMP);
			} else {
				PC_PacketHandler.sendTo(new PC_PacketOpenGresItem(item, -1, sendToClient), (EntityPlayerMP)player);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void loadTextures() {
		if(loaded)
			return;
		loaded=true;
		final String states[] = { "loc_active", "loc_mouseOver", "loc_mouseDown", "loc_disabled" };
		IResourceManager resourceManager = PC_ClientUtils.mc().getResourceManager();
		try {
			IResource resource = resourceManager.getResource(PC_Utils.getResourceLocation(PC_Api.INSTANCE, "textures/gui/GuiDesk.xml"));
			InputStream inputStream = resource.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String page = "";
			String line;
			while ((line = reader.readLine()) != null) {
				page += line + "\n";
			}
			reader.close();
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new ByteArrayInputStream(page.getBytes("UTF-8")));
			doc.getDocumentElement().normalize();
			NodeList textureNodes = ((Element)doc.getChildNodes().item(0)).getChildNodes();
			for (int i = 0; i < textureNodes.getLength(); i++) {
				Node texureNode = textureNodes.item(i);
				if (texureNode.getNodeName().equals("Texture")) {
					Element texureElement = (Element) texureNode;
					String textureName = texureElement.getAttribute("textureName");
					ResourceLocation resourceLocation = PC_Utils.getResourceLocation(PC_Api.INSTANCE, "textures/gui/" + textureName);
					NodeList subTextureNodes = texureNode.getChildNodes();
					for (int j = 0; j < subTextureNodes.getLength(); j++) {
						Node subTextureNode = subTextureNodes.item(j);
						if (subTextureNode.getNodeName().equals("Subtexture")) {
							Element subTextureElement = (Element) subTextureNode;
							String name = subTextureElement.getAttribute("name");
							PC_Vec2I[] others = new PC_Vec2I[states.length];
							for (int k = 0; k < others.length; k++) {
								try {
									others[k] = new PC_Vec2I(subTextureElement.getAttribute(states[k]));
								} catch (NumberFormatException e) {
									e.printStackTrace();
									if (k == 0) {
										PC_Logger.severe("Can't find activeLocation in xml");
									} else {
										PC_Logger.warning("Can't find a specific atribute in xml");
										others[k] = others[0];
									}
								}
							}
							PC_Vec2I size = null;
							try {
								size = new PC_Vec2I(subTextureElement.getAttribute("size"));
							} catch (NumberFormatException e) {
								e.printStackTrace();
								PC_Logger.severe("Can't find size in xml");
							}
							PC_RectI frame = null;
							try {
								frame = new PC_RectI(subTextureElement.getAttribute("frame"));
							} catch (NumberFormatException e) {
								e.printStackTrace();
								PC_Logger.severe("Can't find frame in xml");
							}
							if (name != null && size != null && frame != null && others[0] != null) {
								textures.put(name, new PC_GresTexture(resourceLocation, size, frame, others));
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	@SideOnly(Side.CLIENT)
	public static PC_GresTexture getGresTexture(String name) {

		return textures.get(name);
	}
	
	@SideOnly(Side.CLIENT)
	public static PC_IGresGui getCurrentClientGui() {
		Minecraft mc = PC_ClientUtils.mc();
		if(mc.currentScreen instanceof PC_GresGuiScreen){
			return ((PC_GresGuiScreen)mc.currentScreen).getCurrentClientGui();
		}
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	public static <T> T getCurrentClientGui(Class<T> c) {
		Minecraft mc = PC_ClientUtils.mc();
		if(mc.currentScreen instanceof PC_GresGuiScreen){
			PC_IGresGui gresGui = ((PC_GresGuiScreen)mc.currentScreen).getCurrentClientGui();
			if(c.isAssignableFrom(gresGui.getClass())){
				return c.cast(gresGui);
			}
		}
		return null;
	}

}
