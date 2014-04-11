package powercraft.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.input.Keyboard;

import powercraft.api.reflect.PC_Security;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class PC_Keyboard {
	
	private static HashMap<Integer, KeyHandlers> handlers = new HashMap<Integer, KeyHandlers>();
	
	private static final PC_Keyboard INSTANCE = new PC_Keyboard();
	
	@SideOnly(Side.CLIENT)
	static void register(){
		PC_Security.allowedCaller("PC_ResourceListener.register()", PC_ClientUtils.class);
		FMLCommonHandler.instance().bus().register(INSTANCE);
	}
	
	private PC_Keyboard(){
		
	}
	
	public static void registerKeyHandler(int key, PC_KeyHandler keyHandler){
		KeyHandlers h = handlers.get(Integer.valueOf(key));
		if(h==null){
			handlers.put(Integer.valueOf(key), h = new KeyHandlers());
		}
		h.addKeyHandler(keyHandler);
	}
	
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unused", "static-method" })
	@SubscribeEvent
	public void onKeyEvent(KeyInputEvent inputEvent){
		int key = Keyboard.getEventKey();
		KeyHandlers handler = PC_Keyboard.handlers.get(Integer.valueOf(key));
		if(handler!=null){
			handler.onEvent();
		}
	}
	
	private static class KeyHandlers{

		private boolean state;
		
		@SuppressWarnings("hiding")
		private List<PC_KeyHandler> handlers = new ArrayList<PC_KeyHandler>();
		
		KeyHandlers() {}

		public void onEvent() {
			boolean b = Keyboard.getEventKeyState();
			if(b && this.state){
				for(PC_KeyHandler handler:this.handlers){
					handler.onTick();
				}
			}else if(b && !this.state){
				this.state = b;
				for(PC_KeyHandler handler:this.handlers){
					handler.onPressed();
				}
			}else if(!b && this.state){
				this.state = b;
				for(PC_KeyHandler handler:this.handlers){
					handler.onReleased();
				}
			}
		}

		public void addKeyHandler(PC_KeyHandler keyHandler) {
			this.handlers.add(keyHandler);
		}
		
	}
	
	public static interface PC_KeyHandler{

		public void onTick();

		public void onReleased();

		public void onPressed();
		
	}
	
}
