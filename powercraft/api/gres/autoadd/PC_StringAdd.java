package powercraft.api.gres.autoadd;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.doc.PC_GresDocument;
import powercraft.api.gres.doc.PC_GresDocumentLine;

@SideOnly(Side.CLIENT)
public class PC_StringAdd {

	public PC_GresComponent component;
	public PC_GresDocument document;
	public PC_GresDocumentLine documentLine;
	public int pos;
	public String toAdd;
	public int cursorPos;
	
}
