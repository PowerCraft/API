package powercraft.api.gres;

import org.lwjgl.input.Mouse;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_Vec2I;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

@SideOnly(Side.CLIENT)
class PC_GresGuiScreen extends GuiScreen {

	private PC_GresGuiHandler guiHandler;
	private int buttons;
	
	protected PC_GresGuiScreen(PC_IGresGui client) {
		guiHandler = new PC_GresGuiHandler(client);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float timeStamp){
		guiHandler.eventDrawScreen(new PC_Vec2I(mouseX, mouseY), timeStamp);
	}
	
	@Override
	public void handleMouseInput() {
		int x = Mouse.getEventX() * width / mc.displayWidth;
		int y = height - Mouse.getEventY() * height / mc.displayHeight - 1;
		PC_Vec2I mouse = new PC_Vec2I(x, y);
		int eventButton = Mouse.getEventButton();
		int eventWheel = Mouse.getEventDWheel();
		if (Mouse.getEventDX() != 0 || Mouse.getEventDY() != 0) {
			eventMouseMove(mouse, buttons);
		}
		if (eventButton != -1) {
			if (Mouse.getEventButtonState()) {
				buttons |= 1 << eventButton;
				eventMouseButtonDown(mouse, buttons, eventButton);
			} else {
				buttons &= ~(1 << eventButton);
				eventMouseButtonUp(mouse, buttons, eventButton);
			}
		}
		if (eventWheel != 0) {
			eventMouseWheel(mouse, buttons, eventWheel>0?1:-1);
		}
	}
	
	private void eventMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton) {
		guiHandler.eventMouseButtonDown(mouse, buttons, eventButton);
	}


	private void eventMouseButtonUp(PC_Vec2I mouse, int buttons, int eventButton) {
		guiHandler.eventMouseButtonUp(mouse, buttons, eventButton);
	}


	private void eventMouseMove(PC_Vec2I mouse, int buttons) {
		guiHandler.eventMouseMove(mouse, buttons);
	}


	private void eventMouseWheel(PC_Vec2I mouse, int buttons, int wheel) {
		guiHandler.eventMouseWheel(mouse, buttons, wheel);
	}
	
	@Override
	protected void keyTyped(char key, int keyCode){
		guiHandler.eventKeyTyped(key, keyCode);
	}
	
	@Override
	public void updateScreen() {
		guiHandler.eventUpdateScreen();
	}

	@Override
	public void onGuiClosed() {
		guiHandler.eventGuiClosed();
	}

	@Override
	public void setWorldAndResolution(Minecraft minecraft, int width, int height) {
		super.setWorldAndResolution(minecraft, width, height);
		guiHandler.eventInitGui(width, height);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	public PC_IGresGui getCurrentClientGui() {
		return guiHandler.getClient();
	}
	
}
