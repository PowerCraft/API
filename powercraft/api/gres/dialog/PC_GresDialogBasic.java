package powercraft.api.gres.dialog;

import org.lwjgl.input.Keyboard;

import powercraft.api.gres.PC_GresAlign.Fill;
import powercraft.api.gres.PC_GresAlign.H;
import powercraft.api.gres.PC_GresButton;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresDialogBox;
import powercraft.api.gres.PC_GresGroupContainer;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresKeyEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent.Event;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.layout.PC_GresLayoutHorizontal;
import powercraft.api.gres.layout.PC_GresLayoutVertical;


public abstract class PC_GresDialogBasic extends PC_GresDialogBox implements PC_IGresEventListener {
	
	protected final PC_GresButton doButton;
	protected final PC_GresButton cancelButton;
	
	public PC_GresDialogBasic(String titel, String doButtonName, Object[] data){
		setLayout(new PC_GresLayoutVertical());
		addEventListener(this);
		PC_GresWindow window = new PC_GresWindow(titel);
		window.setLayout(new PC_GresLayoutVertical());
		init(window, data);
		PC_GresGroupContainer group = new PC_GresGroupContainer();
		group.setLayout(new PC_GresLayoutHorizontal());
		if(doButtonName!=null){
			this.doButton = new PC_GresButton(doButtonName);
			this.doButton.addEventListener(this);
			group.add(this.doButton);
		}else{
			this.doButton = null;
		}
		this.cancelButton = new PC_GresButton("Cancel");
		this.cancelButton.addEventListener(this);
		group.add(this.cancelButton);
		group.setFill(Fill.HORIZONTAL);
		group.setAlignH(H.RIGHT);
		window.add(group);
		add(window);
	}
	
	protected abstract void init(PC_GresWindow window, Object[] data);
	
	protected abstract void doButtonClicked();
	
	@Override
	public void onEvent(PC_GresEvent event) {
		PC_GresComponent component = event.getComponent();
		if(event instanceof PC_GresKeyEvent){
			PC_GresKeyEvent kEvent = (PC_GresKeyEvent)event;
			if(kEvent.getKeyCode()==Keyboard.KEY_ESCAPE){
				close();
				fireEvent(new EventCancel(this));
				kEvent.consume();
			}
		}else if(event instanceof PC_GresMouseButtonEvent){
			PC_GresMouseButtonEvent mbe = (PC_GresMouseButtonEvent)event;
			if(mbe.getEvent()==Event.CLICK){
				if(component==this.cancelButton){
					close();
					fireEvent(new EventCancel(this));
				}else if(component==this.doButton){
					close();
					doButtonClicked();
				}
			}
		}
	}
	
	public void close(){
		if(getParent()!=null){
			getParent().remove(this);
		}
	}
	
	public static class EventCancel extends PC_GresEvent{

		EventCancel(PC_GresComponent component) {
			super(component);
		}
		
	}
	
}
