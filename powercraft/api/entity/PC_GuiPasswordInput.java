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

	private PC_IEntity entity;
	private PC_GresTextEdit password;
	private PC_GresLabel status;
	private PC_GresButton ok;
	private PC_GresButton cancel;
	private PC_GresGuiHandler gui;
	
	PC_GuiPasswordInput(PC_Entity entity) {
		this.entity = entity;
	}

	@Override
	public void initGui(PC_GresGuiHandler gui) {
		this.gui = gui;
		PC_GresWindow window = new PC_GresWindow("Password Input");
		window.setLayout(new PC_GresLayoutVertical());
		this.password = new PC_GresTextEdit("", 20, PC_GresInputType.PASSWORD);
		window.add(this.password);
		this.password.addEventListener(this);
		this.status = new PC_GresLabel("Type Password");
		window.add(this.status);
		this.ok = new PC_GresButton("OK");
		window.add(this.ok);
		this.ok.addEventListener(this);
		this.cancel = new PC_GresButton("Cancel");
		window.add(this.cancel);
		this.cancel.addEventListener(this);
		gui.add(window);
		gui.addEventListener(this);
	}

	@Override
	public void onEvent(PC_GresEvent event) {
		PC_GresComponent component = event.getComponent();
		if(event instanceof PC_GresMouseButtonEvent){
			PC_GresMouseButtonEvent bEvent = (PC_GresMouseButtonEvent)event;
			if(bEvent.getEvent()==Event.CLICK){
				if(component==this.ok){
					send();
				}else if(component==this.cancel){
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
		this.gui.addWorking();
		this.status.setText("Sending ...");
		this.ok.setEnabled(false);
		PC_PacketHandler.sendToServer(new PC_PacketPasswordReply2(this.entity, this.password.getText()));
	}

	void wrongPassword(PC_Entity entity) {
		if(this.entity==entity){
			this.gui.removeWorking();
			this.status.setText("Failed, wrong password");
			this.ok.setEnabled(true);
		}
	}
	
}
