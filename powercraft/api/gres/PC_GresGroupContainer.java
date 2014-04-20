package powercraft.api.gres;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;

@SideOnly(Side.CLIENT)
public class PC_GresGroupContainer extends PC_GresContainer {

	public PC_GresGroupContainer(){
		
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		return new PC_Vec2I(-1, -1);
	}

	@Override
	protected PC_Vec2I calculateMaxSize() {
		return new PC_Vec2I(-1, -1);
	}

	@Override
	protected PC_Vec2I calculatePrefSize() {
		return new PC_Vec2I(-1, -1);
	}

	@Override
	protected void paint(PC_RectI scissor, double scale, int displayHeight, float timeStamp, float zoom) {
		//
	}

}
