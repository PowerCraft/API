package powercraft.api.gres.layout;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresContainer;

@SideOnly(Side.CLIENT)
public interface PC_IGresLayout {

	public PC_Vec2I getPreferredLayoutSize(PC_GresContainer container);


	public PC_Vec2I getMinimumLayoutSize(PC_GresContainer container);


	public void updateLayout(PC_GresContainer container);

}
