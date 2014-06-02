package powercraft.api;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import powercraft.api.PC_TickHandler.PC_ITickHandler;
import powercraft.api.reflect.PC_Security;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class PC_Keyboard implements PC_ITickHandler{
	
	static List<PC_KeyHandler> handlers = new ArrayList<PC_KeyHandler>();
	
	private static final PC_Keyboard INSTANCE = new PC_Keyboard();
	
	@SideOnly(Side.CLIENT)
	static void register(){
		PC_Security.allowedCaller("PC_Keyboard.register()", PC_ClientUtils.class);
		FMLCommonHandler.instance().bus().register(INSTANCE);
		PC_TickHandler.registerTickHandler(INSTANCE);
	}
	
	private PC_Keyboard(){
		
	}
	
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unused", "static-method" })
	@SubscribeEvent
	public void onKeyEvent(KeyInputEvent inputEvent){
		int key = Keyboard.getEventKey();
		boolean state = Keyboard.getEventKeyState();
		for(PC_KeyHandler handler:handlers){
			if(handler.getKeyCode()==key){
				handler.onEvent(state);
			}
		}
	}
	
	public static abstract class PC_KeyHandler extends KeyBinding{

		private boolean state;
		
		public PC_KeyHandler(String sKey, int key, String desk) {
			super(sKey, key, desk);
			handlers.add(this);
		}

		@SuppressWarnings("hiding")
		void onEvent(boolean state) {
			if(this.state && state){
				onTick();
			}else if(this.state && !state){
				onRelease();
			}else if(!this.state && state){
				onPressed();
			}
			this.state = state;
		}
		
		@Override
		public boolean isPressed() {
			return this.state;
		}

		public abstract void onTick();
		
		public abstract void onPressed();
		
		public abstract void onRelease();

		void checkUnpressed() {
			if(!getIsKeyPressed() && this.state){
				onRelease();
				this.state = false;
			}
		}
		
	}

	@Override
	public void onStartTick(PC_Side side) {
		//
	}

	@Override
	public void onEndTick(PC_Side side) {
		for(PC_KeyHandler handler:handlers){
			handler.checkUnpressed();
		}
	}
	
}
