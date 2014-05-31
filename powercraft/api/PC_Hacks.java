package powercraft.api;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
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
				int s1 = gui.mc.fontRenderer.getStringWidth(line1);
				int s2 = gui.mc.fontRenderer.getStringWidth(line2);
	            int s = Math.max(s1, s2);
	            int v1 = (gui.width - s) / 2;
	            int v2 = v1 + s;
	            PC_Fields.Client.GuiMainMenu_notificationLine2Width.setValue(gui, Integer.valueOf(s2));
	            PC_Fields.Client.GuiMainMenu_notificationLine1Width.setValue(gui, Integer.valueOf(s1));
	            PC_Fields.Client.GuiMainMenu_notificationLeft.setValue(gui, Integer.valueOf(v1));
	            PC_Fields.Client.GuiMainMenu_notificationRight.setValue(gui, Integer.valueOf(v2));
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void hackGui(GuiScreen gui) {
		if(gui!=null && gui.getClass()==GuiMainMenu.class){
			PC_UpdateInfo ui = PC_UpdateChecker.getUpdateInfo();
			if(ui!=null){
				PC_Version v = PC_Api.INSTANCE.getVersion();
				PC_VersionInfo nv = ui.getNewestVersion("Api", true);
				if(nv!=null && nv.getVersion().compareTo(v)>0){
					hackInfo(gui, "PowerCraft out of date", "Your:"+v+", newest:"+nv.getVersion(), nv.getDownloadLink());
				}
			}
		}
	}
	
}
