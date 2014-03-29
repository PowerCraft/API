package powercraft.api.script.weasel;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.autoadd.PC_AutoCompleteDisplay;
import powercraft.api.gres.doc.PC_GresDocument;
import powercraft.api.gres.doc.PC_GresDocumentLine;

public interface PC_WeaselModule {

	public PC_WeaselContainer createContainer(String deviceName, int memSize);

	@SideOnly(Side.CLIENT)
	public void makeComplete(PC_GresComponent component, PC_GresDocument document, PC_GresDocumentLine line, int x, PC_AutoCompleteDisplay info, PC_WeaselGresEdit weaselGresEdit);

}
