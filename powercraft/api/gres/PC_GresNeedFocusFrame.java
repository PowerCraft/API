package powercraft.api.gres;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import powercraft.api.PC_Vec2I;
import powercraft.api.gres.history.PC_GresHistory;


public class PC_GresNeedFocusFrame extends PC_GresFrame {
	
	private List<PC_GresComponent> allowedOthers = new ArrayList<PC_GresComponent>();
	
	private PC_Vec2I realPos;
	
	public PC_GresNeedFocusFrame(){
		
	}
	
	public PC_GresNeedFocusFrame(PC_Vec2I realPos){
		this.realPos = realPos;
	}
	
	public void setRealPos(PC_Vec2I realPos){
		if(this.realPos==null?realPos!=null:realPos==null?true:!this.realPos.equals(realPos)){
			if(realPos==null){
				this.realPos = null;
			}else{
				if(this.realPos==null){
					this.realPos = new PC_Vec2I();
				}
				this.realPos.setTo(realPos);
			}
			notifyChange();
		}
	}
	
	@Override
	public void add(PC_GresComponent component) {
		super.add(component);
	}


	@Override
	public void putInRect(int x, int y, int width, int height) {
		if(this.realPos==null){
			super.putInRect(x, y, width, height);
		}else{
			setLocation(this.realPos);
		}
	}
	
	@Override
	protected void onScaleChanged(int newScale) {
		if(this.realPos!=null)
			setLocation(this.realPos);
		super.onScaleChanged(newScale);
	}
	
	public void addOtherAllowed(PC_GresComponent component){
		if(!this.allowedOthers.contains(component)){
			this.allowedOthers.add(component);
		}
	}
	
	public void removeOtherAllowed(PC_GresComponent component){
		this.allowedOthers.remove(component);
	}
	
	@SuppressWarnings("hiding")
	public boolean isFocusAllowed(PC_GresComponent focus){
		if(isParentOf(this, focus))
			return true;
		for(PC_GresComponent c:this.allowedOthers){
			if(c==focus){
				return true;
			}else if(c instanceof PC_GresContainer){
				if(isParentOf((PC_GresContainer)c, focus))
					return true;
			}
		}
		return false;
	}
	
	public void close(){
		if(getParent()!=null){
			getParent().removeNoFocus(this);
		}
	}
	
	@Override
	protected void onFocusChaned(PC_GresComponent oldFocus, PC_GresComponent newFocus){
		if(isFocusAllowed(newFocus)){
			super.onFocusChaned(oldFocus, newFocus);
		}else{
			close();
		}
	}
	
	private static boolean isParentOf(PC_GresContainer c, PC_GresComponent com){
		PC_GresComponent comp = com;
		while(c!=comp){
			if(comp==null)
				return false;
			comp = comp.getParent();
		}
		return true;
	}
	
	@Override
	protected void addToBase(PC_GresComponent c){
		addOtherAllowed(c);
		super.addToBase(c);
	}

	@Override
	protected boolean handleKeyTyped(char key, int keyCode, boolean repeat, PC_GresHistory history) {
		if(keyCode==Keyboard.KEY_ESCAPE){
			close();
			return true;
		}
		return false;
	}
	
}
