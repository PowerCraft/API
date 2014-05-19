package powercraft.api;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import powercraft.api.reflect.PC_Reflection;
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
			PC_Reflection.setValue(GuiMainMenu.class, gui, 4, String.class, splash);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void hackInfo(GuiScreen gui, String line1, String line2, String link){
		if(gui!=null && gui.getClass()==GuiMainMenu.class){
			PC_Reflection.setValue(GuiMainMenu.class, gui, 12, String.class, line1);
			PC_Reflection.setValue(GuiMainMenu.class, gui, 13, String.class, line2);
			PC_Reflection.setValue(GuiMainMenu.class, gui, 14, String.class, link);
			if(gui.mc!=null){
				int s1 = gui.mc.fontRenderer.getStringWidth(line1);
				int s2 = gui.mc.fontRenderer.getStringWidth(line2);
	            int s = Math.max(s1, s2);
	            int v1 = (gui.width - s) / 2;
	            int v2 = v1 + s;
	            PC_Reflection.setValue(GuiMainMenu.class, gui, 19, int.class, Integer.valueOf(s2));
	            PC_Reflection.setValue(GuiMainMenu.class, gui, 20, int.class, Integer.valueOf(s1));
	            PC_Reflection.setValue(GuiMainMenu.class, gui, 21, int.class, Integer.valueOf(v1));
				PC_Reflection.setValue(GuiMainMenu.class, gui, 23, int.class, Integer.valueOf(v2));
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
