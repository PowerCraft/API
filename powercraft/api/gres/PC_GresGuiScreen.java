package powercraft.api.gres;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import powercraft.api.PC_Vec2I;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class PC_GresGuiScreen extends GuiScreen {

	private static final long DOUBLE_CLICK_DIFF = 250000000L;
	
	private PC_GresGuiHandler guiHandler;
	private int buttons;
	private long lastLeftClick;
	
	protected PC_GresGuiScreen(PC_IGresGui client) {
		this.guiHandler = new PC_GresGuiHandler(client);
		Keyboard.enableRepeatEvents(true);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float timeStamp){
		this.guiHandler.eventDrawScreen(new PC_Vec2I(mouseX, mouseY), timeStamp);
	}
	
	@Override
	public void handleMouseInput() {
		int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
		PC_Vec2I mouse = new PC_Vec2I(x, y);
		int eventButton = Mouse.getEventButton();
		int eventWheel = Mouse.getEventDWheel();
		if (Mouse.getEventDX() != 0 || Mouse.getEventDY() != 0) {
			eventMouseMove(mouse, this.buttons);
		}
		if (eventButton != -1) {
			if (Mouse.getEventButtonState()) {
				boolean doubleClick = false;
				if(eventButton==0){
					long clickTime = Mouse.getEventNanoseconds();
					doubleClick = clickTime - this.lastLeftClick<DOUBLE_CLICK_DIFF;
					this.lastLeftClick = clickTime;
				}
				this.buttons |= 1 << eventButton;
				eventMouseButtonDown(mouse, this.buttons, eventButton, doubleClick);
			} else {
				this.buttons &= ~(1 << eventButton);
				eventMouseButtonUp(mouse, this.buttons, eventButton);
			}
		}
		if (eventWheel != 0) {
			eventMouseWheel(mouse, this.buttons, eventWheel>0?1:-1);
		}
	}

	private void eventMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton, boolean doubleClick) {
		this.guiHandler.eventMouseButtonDown(mouse, buttons, eventButton, doubleClick);
	}

	private void eventMouseButtonUp(PC_Vec2I mouse, int buttons, int eventButton) {
		this.guiHandler.eventMouseButtonUp(mouse, buttons, eventButton);
	}

	private void eventMouseMove(PC_Vec2I mouse, int buttons) {
		this.guiHandler.eventMouseMove(mouse, buttons);
	}

	private void eventMouseWheel(PC_Vec2I mouse, int buttons, int wheel) {
		this.guiHandler.eventMouseWheel(mouse, buttons, wheel);
	}
	
	@Override
	protected void keyTyped(char key, int keyCode){
		this.guiHandler.eventKeyTyped(key, keyCode, Keyboard.isRepeatEvent());
	}
	
	@Override
	public void updateScreen() {
		this.guiHandler.eventUpdateScreen();
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		this.guiHandler.eventGuiClosed();
	}

	@Override
	public void setWorldAndResolution(Minecraft minecraft, int width, int height) {
		super.setWorldAndResolution(minecraft, width, height);
		this.guiHandler.eventInitGui(width, height);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	public PC_IGresGui getCurrentClientGui() {
		return this.guiHandler.getClient();
	}
	
}
