package powercraft.api.gres;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresAlign.Fill;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresFocusLostEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.history.PC_GresHistory;
import powercraft.api.gres.layout.PC_GresLayoutVertical;

@SideOnly(Side.CLIENT)
public class PC_GresComboBox extends PC_GresComponent {

	private static final String textureName = "Button";
	
	PC_GresFrame frame;
	private List<String> items;
	
	public PC_GresComboBox(List<String> items, int select){
		this.items = new ArrayList<String>(items);
		setText(items.get(select));
		notifyChange();
	}
	
	public PC_GresComboBox(){
		this.items = new ArrayList<String>();
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		PC_Vec2I s = getTextureMinSize(textureName);
		PC_Vec2I size = new PC_Vec2I();
		for(String item:this.items){
			PC_Vec2I fsize = fontRenderer.getStringSize(item);
			size = fsize.max(size);
		}
		return new PC_Vec2I(s.x+size.x+20, s.y+size.y);
	}

	@Override
	protected PC_Vec2I calculateMaxSize() {
		return new PC_Vec2I(-1, -1);
	}

	@Override
	protected PC_Vec2I calculatePrefSize() {
		PC_Vec2I s = getTextureMinSize(textureName);
		PC_Vec2I size = new PC_Vec2I();
		for(String item:this.items){
			PC_Vec2I fsize = fontRenderer.getStringSize(item);
			size = fsize.max(size);
		}
		return new PC_Vec2I(s.x+size.x+20, s.y+size.y);
	}

	@Override
	protected void paint(PC_RectI scissor, double scale, int displayHeight, float timeStamp) {
		drawTexture(textureName, 0, 0, this.rect.width, this.rect.height);
		drawString(this.text, 3, this.frame!=null ? 4 : 3, this.rect.width - 6, this.rect.height - 6, PC_GresAlign.H.CENTER, PC_GresAlign.V.CENTER, true);
	}
	
	@Override
	protected boolean handleMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton, boolean doubleClick, PC_GresHistory history) {
		if(this.frame==null){
			this.mouseDown = true;
			this.frame = new PC_GresComboBoxFrame(this);
			this.frame.setLayout(new PC_GresLayoutVertical());
			ComboBoxEventListener cbel = new ComboBoxEventListener();
			PC_GresScrollArea sa;
			this.frame.add(sa = new PC_GresScrollArea());
			sa.setFill(Fill.BOTH);
			sa.addEventListener(cbel);
			sa.setMinSize(new PC_Vec2I(this.rect.width, 50));
			sa.setSize(new PC_Vec2I(this.rect.width, 50));
			PC_GresContainer c = sa.getContainer();
			c.addEventListener(cbel);
			c.setLayout(new PC_GresLayoutVertical());
			for(String item:this.items){
				PC_GresButton b = new PC_GresButton(item);
				b.setFill(Fill.BOTH);
				b.addEventListener(cbel);
				c.add(b);
			}
			this.frame.addEventListener(cbel);
			this.frame.setMinSize(new PC_Vec2I(this.rect.width, 50));
			this.frame.setSize(new PC_Vec2I(this.rect.width, 50));
			getGuiHandler().add(this.frame);
			this.frame.takeFocus();
		}else{
			closeDropDown();
		}
		return true;
	}
	
	@Override
	protected boolean handleMouseButtonUp(PC_Vec2I mouse, int buttons,int eventButton, PC_GresHistory history) {
		return true;
	}
	
	@Override
	protected void handleMouseLeave(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		this.mouseOver = false;
	}

	@Override
	protected void setParent(PC_GresContainer parent) {
		if(this.frame!=null){
			closeDropDown();
		}
		super.setParent(parent);
	}

	void closeDropDown(){
		getGuiHandler().remove(this.frame);
		this.frame = null;
		this.mouseDown = false;
	}
	
	private class ComboBoxEventListener implements PC_IGresEventListener{

		private boolean handleClosing;
		
		ComboBoxEventListener() {
			//
		}

		@Override
		public void onEvent(PC_GresEvent event) {
			if(event instanceof PC_GresFocusLostEvent){
				PC_GresFocusLostEvent le = (PC_GresFocusLostEvent)event;
				if(!isParentOf(PC_GresComboBox.this.frame, le.getNewFocusedComponent()) && le.getNewFocusedComponent()!=PC_GresComboBox.this){
					if(!this.handleClosing){
						this.handleClosing = true;
						closeDropDown();
					}
				}
			}else if(event instanceof PC_GresMouseButtonEvent){
				PC_GresMouseButtonEvent ev = (PC_GresMouseButtonEvent) event;
				if(ev.getEvent()==PC_GresMouseButtonEvent.Event.CLICK){
					if(ev.getComponent() instanceof PC_GresButton){
						setText(ev.getComponent().getText());
						if(!this.handleClosing){
							this.handleClosing = true;
							closeDropDown();
						}
					}
				}
			}
		}
		
		private boolean isParentOf(PC_GresContainer c, PC_GresComponent com){
			PC_GresComponent comp = com;
			while(c!=comp){
				if(comp==null)
					return false;
				comp = comp.getParent();
			}
			return true;
		}
		
	}
	
}
