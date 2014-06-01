package powercraft.api;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import powercraft.api.PC_TickHandler.PC_IRenderTickHandler;
import powercraft.api.reflect.PC_Fields;
import powercraft.api.version.PC_UpdateChecker;
import powercraft.api.version.PC_UpdateInfo;
import powercraft.api.version.PC_Version;
import powercraft.api.version.PC_VersionInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public final class PC_Hacks {
	
	private PC_Hacks(){
		PC_Utils.staticClassConstructor();
	}
	
	@SideOnly(Side.CLIENT)
	public static void hackSplash(GuiScreen gui, String splash){
		if(gui!=null && gui.getClass()==GuiMainMenu.class){
			PC_Fields.Client.GuiMainMenu_splashText.setValue(gui, splash);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void hackInfo(GuiScreen gui, String line1, String line2, String link){
		if(gui!=null && gui.getClass()==GuiMainMenu.class){
			PC_Fields.Client.GuiMainMenu_notificationLine1.setValue(gui, line1);
			PC_Fields.Client.GuiMainMenu_notificationLine2.setValue(gui, line2);
			PC_Fields.Client.GuiMainMenu_notificationLink.setValue(gui, link);
			if(gui.mc!=null){
				gui.initGui();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void hackGui(GuiScreen gui) {
		if(gui!=null && gui.getClass()==GuiMainMenu.class){
			PC_UpdateInfo ui = PC_UpdateChecker.getUpdateInfo();
			if(ui==null){
				ticker = new Ticker();
				PC_TickHandler.registerTickHandler(ticker);
			}else{
				PC_Version v = PC_Api.INSTANCE.getVersion();
				PC_VersionInfo nv = ui.getNewestVersion("Api", true);
				if(nv!=null && nv.getVersion().compareTo(v)>0){
					hackInfo(gui, PC_Lang.translate("PC.out.of.date"), PC_Lang.translate("PC.version.show", v, nv.getVersion()), nv.getDownloadLink());
				}
			}
		}else if(ticker!=null){
			PC_TickHandler.removeTickHander(ticker);
			ticker = null;
		}
	}
	
	@SideOnly(Side.CLIENT)
	static Ticker ticker;
	
	@SideOnly(Side.CLIENT)
	private static final class Ticker implements PC_IRenderTickHandler{

		Ticker() {
		}

		@Override
		public void onStartTick(float renderTickTime) {
			PC_UpdateInfo ui = PC_UpdateChecker.getUpdateInfo();
			if(ui!=null){
				hackGui(PC_ClientUtils.mc().currentScreen);
				PC_TickHandler.removeTickHander(this);
				ticker = null;
			}
		}

		@Override
		public void onEndTick(float renderTickTime) {
			PC_UpdateInfo ui = PC_UpdateChecker.getUpdateInfo();
			if(ui!=null){
				hackGui(PC_ClientUtils.mc().currentScreen);
				PC_TickHandler.removeTickHander(this);
				ticker = null;
			}
		}
		
	}
	
}
