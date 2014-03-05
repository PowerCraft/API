package powercraft.api.gres.dialog;

import powercraft.api.gres.PC_GresAlign.Fill;
import powercraft.api.gres.PC_GresAlign.H;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresLabel;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.events.PC_GresEvent;


public class PC_GresDialogYesNo extends PC_GresDialogBasic {
	
	public PC_GresDialogYesNo(String titel, String message, String doButtonName) {
		super(titel, doButtonName, new Object[]{message});
	}

	@Override
	protected void init(PC_GresWindow window, Object[] data) {
		PC_GresLabel label = new PC_GresLabel((String)data[0]);
		label.setFill(Fill.HORIZONTAL);
		label.setAlignH(H.LEFT);
		window.add(label);
	}

	@Override
	protected void doButtonClicked() {
		fireEvent(new EventYes(this));
	}
	
	public static class EventYes extends PC_GresEvent{

		EventYes(PC_GresComponent component) {
			super(component);
		}
		
	}
	
}
