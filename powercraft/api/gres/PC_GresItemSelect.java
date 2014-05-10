package powercraft.api.gres;

import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;

import powercraft.api.PC_Rect;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.history.PC_GresHistory;


public class PC_GresItemSelect extends PC_GresComponent {
	
	private ItemStack itemStack;
	
	private PC_GresItemSelect(){
		
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		return new PC_Vec2I(16, 16);
	}
	
	@Override
	protected PC_Vec2I calculateMaxSize() {
		return new PC_Vec2I(16, 16);
	}
	
	@Override
	protected PC_Vec2I calculatePrefSize() {
		return new PC_Vec2I(16, 16);
	}
	
	@Override
	protected void paint(PC_Rect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
		PC_GresRenderer.drawEasyItemStack(0, 0, this.itemStack, null);
	}

	@Override
	protected boolean handleKeyTyped(char key, int keyCode, boolean repeat, PC_GresHistory history) {
		if(keyCode==Keyboard.KEY_DELETE){
			this.itemStack = null;
		}else if(keyCode==Keyboard.KEY_S){
			select();
		}else if(keyCode==Keyboard.KEY_E){
			edit();
		}
		return super.handleKeyTyped(key, keyCode, repeat, history);
	}

	@Override
	protected boolean handleMouseButtonClick(PC_Vec2I mouse, int buttons, int eventButton, PC_GresHistory history) {
		if(eventButton==0){
			select();
		}else if(eventButton==1){
			edit();
		}
		return super.handleMouseButtonClick(mouse, buttons, eventButton, history);
	}
	
	private void select(){
		
	}
	
	private void edit(){
		
	}
	
}
