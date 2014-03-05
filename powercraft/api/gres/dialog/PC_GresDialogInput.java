package powercraft.api.gres.dialog;

import org.lwjgl.input.Keyboard;

import powercraft.api.gres.PC_GresAlign.Fill;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresTextEdit;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresKeyEventResult;
import powercraft.api.gres.events.PC_IGresEventListener;


public class PC_GresDialogInput extends PC_GresDialogBasic {
	
	private PC_GresTextEdit textEdit;
	
	public PC_GresDialogInput(String titel, String input, String doButtonName) {
		super(titel, doButtonName, new Object[]{input});
	}

	@Override
	protected void init(PC_GresWindow window, Object[] data) {
		this.textEdit = new PC_GresTextEdit((String)data[0], 20);
		this.textEdit.setFill(Fill.HORIZONTAL);
		this.textEdit.addEventListener(this);
		window.add(this.textEdit);
	}
	
	@Override
	protected void doButtonClicked() {
		fireEvent(new EventInput(this, this.textEdit.getText()));
	}
	
	@Override
	public void onEvent(PC_GresEvent event) {
		super.onEvent(event);
		if(event instanceof PC_GresKeyEventResult){
			if(event.getComponent()==this.textEdit){
				if(((PC_GresKeyEventResult) event).getKeyCode()==Keyboard.KEY_RETURN){
					close();
					doButtonClicked();
				}else{
					String input = this.textEdit.getText();
					EventInputChanged ev = new EventInputChanged(this, input);
					fireEvent(ev);
					this.doButton.setEnabled(ev.isEnabled());
				}
			}
		}
	}

	@Override
	public void addEventListener(PC_IGresEventListener eventListener) {
		super.addEventListener(eventListener);
		if(this.textEdit!=null){
			String input = this.textEdit.getText();
			EventInputChanged ev = new EventInputChanged(this, input);
			fireEvent(ev);
			this.doButton.setEnabled(ev.isEnabled());
		}
	}

	public static class EventInput extends PC_GresEvent{

		private String input;
		
		EventInput(PC_GresComponent component, String input) {
			super(component);
			this.input = input;
		}
		
		public String getInput(){
			return this.input;
		}
		
	}
	
	public static class EventInputChanged extends PC_GresEvent{

		private final String input;
		private boolean enabled;
		
		public EventInputChanged(PC_GresComponent component, String input) {
			super(component);
			this.input = input;
		}

		public String getInput() {
			return this.input;
		}
		
		public boolean isEnabled() {
			return this.enabled;
		}
		
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
		
	}
	
}
