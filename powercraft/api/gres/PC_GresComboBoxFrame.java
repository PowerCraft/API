package powercraft.api.gres;

import powercraft.api.PC_Vec2I;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class PC_GresComboBoxFrame extends PC_GresFrame {

	private PC_GresComboBox comboBox;
	
	protected PC_GresComboBoxFrame(PC_GresComboBox comboBox){
		this.comboBox = comboBox;
	}
	
	@Override
	protected void setParent(PC_GresContainer parent) {
		if(parent instanceof PC_GresGuiHandler)
			super.setParent(parent);
	}

	@Override
	public void putInRect(int x, int y, int width, int height) {
		setLocation(new PC_Vec2I(this.comboBox.getRealLocation()).add(0, (int) (this.comboBox.rect.height*this.comboBox.getRecursiveZoom())));
	}
	
}
