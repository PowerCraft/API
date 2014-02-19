package powercraft.api.gres.autoadd;

import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.doc.PC_GresDocInfoCollector;
import powercraft.api.gres.doc.PC_GresDocument;
import powercraft.api.gres.doc.PC_GresDocumentLine;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface PC_AutoComplete {
	
	public void onStringAdded(PC_GresComponent component, PC_GresDocument document, PC_GresDocumentLine line, String toAdd, int x, PC_AutoCompleteDisplay info);

	public void makeComplete(PC_GresComponent component, PC_GresDocument document, PC_GresDocumentLine line, int x, PC_AutoCompleteDisplay info);

	public PC_GresDocInfoCollector getInfoCollector();

}
