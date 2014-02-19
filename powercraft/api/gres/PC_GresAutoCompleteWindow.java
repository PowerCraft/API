package powercraft.api.gres;

import org.lwjgl.input.Keyboard;

import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresAlign.Fill;
import powercraft.api.gres.autoadd.PC_AutoCompleteDisplay;
import powercraft.api.gres.autoadd.PC_StringListPart;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresFocusLostEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.layout.PC_GresLayoutVertical;

public class PC_GresAutoCompleteWindow extends PC_GresFrame{

	public static PC_GresAutoCompleteWindow frame;
	
	public static void makeCompleteWindow(PC_GresGuiHandler guiHandler, PC_GresMultilineHighlightingTextEdit textEdit, PC_AutoCompleteDisplay display){
		if(display.display){
			if(frame==null){
				frame = new PC_GresAutoCompleteWindow(textEdit);
				frame.setMinSize(new PC_Vec2I(100, 100));
				frame.setSize(new PC_Vec2I(100, 100));
				frame.setLayout(new PC_GresLayoutVertical());
				frame.scrollArea  = new PC_GresScrollArea();
				frame.scrollArea.setFill(Fill.BOTH);
				frame.scrollArea.setMinSize(new PC_Vec2I(98, 98));
				frame.scrollArea.setSize(new PC_Vec2I(98, 98));
				frame.scrollArea.getContainer().setLayout(new PC_GresLayoutVertical());
				frame.add(frame.scrollArea);
				frame.addEventListener(frame.listener = new Listener());
				guiHandler.add(frame);
			}
			if(frame.textEdit != textEdit){
				frame.textEdit = textEdit;
			}
			PC_GresContainer container = frame.scrollArea.getContainer();
			container.removeAll();
			container.setSize(new PC_Vec2I(0, 0));
			int spl = display.done.lastIndexOf('.')+1;
			String last = "";
			for(PC_StringListPart part:display.parts){
				for(String s:part){
					String t = s.substring(spl);
					int p = t.indexOf('.');
					if(p!=-1){
						t = t.substring(0, p+1);
						if(t.equals(last))
							continue;
					}
					last = t;
					PC_GresButton b = new PC_GresButton(t);
					b.addEventListener(frame.listener);
					b.setFill(Fill.BOTH);
					container.add(b);
				}
			}
			frame.done = display.done.length()-spl;
			frame.takeFocus();
			textEdit.focus = true;
			frame.notifyChange();
		}else if(frame!=null){
			frame.getGuiHandler().remove(frame);
			textEdit.takeFocus();
			frame = null;
		}
	}
		
	private PC_GresMultilineHighlightingTextEdit textEdit;
	private PC_GresScrollArea scrollArea;
	private int done;
	private Listener listener;
	
	private PC_GresAutoCompleteWindow(PC_GresMultilineHighlightingTextEdit textEdit){
		this.textEdit = textEdit;
	}
	
	@Override
	public void putInRect(int x, int y, int width, int height) {
		setLocation(textEdit.getRealLocation().add(textEdit.getCursorLowerPosition()));
	}
	
	@Override
	protected void onScaleChanged(int newScale) {
		textEdit.onScaleChanged(newScale);
		setLocation(textEdit.getRealLocation().add(textEdit.getCursorLowerPosition()));
		super.onScaleChanged(newScale);
	}
	
	private void makeAdd(){
		if(scrollArea.getContainer().children.isEmpty())
			return;
		String text = scrollArea.getContainer().children.get(0).getText();
		makeAdd(text);
	}
	
	private void makeAdd(String text){
		textEdit.autoComplete(text.substring(done));
		if(!text.endsWith("."))
			close();
	}
	
	@Override
	protected boolean handleKeyTyped(char key, int keyCode) {
		switch (keyCode) {
		case Keyboard.KEY_ESCAPE:
			close();
			break;
		case Keyboard.KEY_RETURN:
			makeAdd();
			break;
		case Keyboard.KEY_SPACE:
			makeAdd();
			return textEdit.onKeyTyped(key, keyCode);
		default:
			return textEdit.onKeyTyped(key, keyCode);
		}
		return true;
	}

	private void close(){
		closeNF();
		textEdit.takeFocus();
	}
	
	private void closeNF(){
		frame = null;
		getGuiHandler().remove(this);
	}
	
	private static class Listener implements PC_IGresEventListener{

		@Override
		public void onEvent(PC_GresEvent event) {
			if(frame==null)
				return;
			if(event instanceof PC_GresFocusLostEvent){
				PC_GresFocusLostEvent le = (PC_GresFocusLostEvent)event;
				if(!isParentOf(frame, le.getNewFocusedComponent())){
					if(le.getNewFocusedComponent()!=frame.textEdit)
						frame.textEdit.focus = false;
					frame.closeNF();
				}
			}else if(event instanceof PC_GresMouseButtonEvent){
				PC_GresMouseButtonEvent ev = (PC_GresMouseButtonEvent) event;
				if(ev.getEvent()==PC_GresMouseButtonEvent.Event.CLICK){
					if(ev.getComponent() instanceof PC_GresButton){
						frame.makeAdd(ev.getComponent().getText());
					}
				}
			}
		}
		
		private boolean isParentOf(PC_GresContainer c, PC_GresComponent com){
			while(c!=com){
				if(com==null)
					return false;
				com = com.getParent();
			}
			return true;
		}
		
	}
	
}
