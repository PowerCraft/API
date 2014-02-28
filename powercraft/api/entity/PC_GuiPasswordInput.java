package powercraft.api.entity;

import org.lwjgl.input.Keyboard;

import powercraft.api.gres.PC_GresButton;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresLabel;
import powercraft.api.gres.PC_GresTextEdit;
import powercraft.api.gres.PC_GresTextEdit.PC_GresInputType;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresKeyEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent.Event;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.network.packet.PC_PacketPasswordReply2;

final class PC_GuiPasswordInput implements PC_IGresGui, PC_IGresEventListener {

	private PC_Entity entity;
	private PC_GresTextEdit password;
	private PC_GresLabel status;
	private PC_GresButton ok;
	private PC_GresButton cancel;
	
	PC_GuiPasswordInput(PC_Entity entity) {
		this.entity = entity;
	}

	@Override
	public void initGui(PC_GresGuiHandler gui) {
		PC_GresWindow window = new PC_GresWindow("Password Input");
		window.setLayout(new PC_GresLayoutVertical());
		password = new PC_GresTextEdit("", 20, PC_GresInputType.PASSWORD);
		window.add(password);
		password.addEventListener(this);
		status = new PC_GresLabel("Type Password");
		window.add(status);
		ok = new PC_GresButton("OK");
		window.add(ok);
		ok.addEventListener(this);
		cancel = new PC_GresButton("Cancel");
		window.add(cancel);
		cancel.addEventListener(this);
		gui.add(window);
		gui.addEventListener(this);
	}

	@Override
	public void onEvent(PC_GresEvent event) {
		PC_GresComponent component = event.getComponent();
		if(event instanceof PC_GresMouseButtonEvent){
			PC_GresMouseButtonEvent bEvent = (PC_GresMouseButtonEvent)event;
			if(bEvent.getEvent()==Event.CLICK){
				if(component==ok){
					send();
				}else if(component==cancel){
					component.getGuiHandler().close();
				}
			}
		}else if(event instanceof PC_GresKeyEvent){
			PC_GresKeyEvent kEvent = (PC_GresKeyEvent)event;
			if(kEvent.getKeyCode()==Keyboard.KEY_RETURN){
				send();
			}else if(kEvent.getKeyCode()==Keyboard.KEY_ESCAPE){
				component.getGuiHandler().close();
			}
		}
	}

	private void send(){
		status.setText("Sending ...");
		ok.setEnabled(false);
		PC_PacketHandler.sendToServer(new PC_PacketPasswordReply2(entity, password.getText()));
	}

	void wrongPassword(PC_Entity entity) {
		if(this.entity==entity){
			status.setText("Failed, wrong password");
			ok.setEnabled(true);
		}
	}
	
}
