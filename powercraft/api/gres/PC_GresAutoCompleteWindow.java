package powercraft.api.gres;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresAlign.Fill;
import powercraft.api.gres.autoadd.PC_AutoCompleteDisplay;
import powercraft.api.gres.autoadd.PC_StringListPart;
import powercraft.api.gres.autoadd.PC_StringWithInfo;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresFocusLostEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.history.PC_GresHistory;
import powercraft.api.gres.layout.PC_GresLayoutVertical;

public class PC_GresAutoCompleteWindow extends PC_GresFrame{

	public static PC_GresAutoCompleteWindow frame;
	
	public static void makeCompleteWindow(PC_GresGuiHandler guiHandler, PC_GresMultilineHighlightingTextEdit textEdit, PC_AutoCompleteDisplay display, PC_GresHistory history){
		if(display.display){
			if(frame==null){
				int i = 0;
				int spl = display.done.lastIndexOf('.')+1;
				for(PC_StringListPart part:display.parts){
					int s = part.size();
					if(s>1){
						String start = part.get(0).getString().substring(spl);
						String end = part.get(s-1).getString().substring(spl);
						int p1 = start.indexOf('.');
						int p2 = end.indexOf('.');
						if(p1!=-1 && p2!=-1){
							if(start.substring(0, p1+1).equals(end.substring(0, p2+1)))
								s=1;
						}
					}
					i+=s;
				}
				if(i==1){
					for(PC_StringListPart part:display.parts){
						if(part.size()>0){
							String s = part.get(0).getString().substring(display.done.length());
							int p = s.indexOf('.');
							if(p!=-1){
								s = s.substring(0, p+1);
							}
							textEdit.autoComplete(s, history);
						}
					}
					return;
				}
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
				for(PC_StringWithInfo s:part){
					String t = s.getString().substring(spl);
					int p = t.indexOf('.');
					if(p!=-1){
						t = t.substring(0, p+1);
						if(t.equals(last))
							continue;
					}
					last = t;
					PC_GresButton b = new Button(t, s.getInfo());
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
	
	private void makeAdd(PC_GresHistory history){
		if(scrollArea.getContainer().children.isEmpty())
			return;
		String text = scrollArea.getContainer().children.get(0).getText();
		makeAdd(text, history);
	}
	
	private void makeAdd(String text, PC_GresHistory history){
		textEdit.autoComplete(text.substring(done), history);
		if(!text.endsWith("."))
			close();
	}
	
	@Override
	protected boolean handleKeyTyped(char key, int keyCode, PC_GresHistory history) {
		switch (keyCode) {
		case Keyboard.KEY_ESCAPE:
			close();
			break;
		case Keyboard.KEY_RETURN:
			makeAdd(history);
			break;
		case Keyboard.KEY_SPACE:
			makeAdd(history);
			return textEdit.onKeyTyped(key, keyCode, history);
		default:
			return textEdit.onKeyTyped(key, keyCode, history);
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
						frame.makeAdd(ev.getComponent().getText(), ev.getHistory());
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
	
	private static class Button extends PC_GresButton{

		private String info;
		
		public Button(String title, String info) {
			super(title);
			this.info = info;
		}

		@Override
		protected List<String> getTooltip(PC_Vec2I position) {
			if(info==null)
				return null;
			List<String> list = new ArrayList<String>();
			String sl[] = info.split("\n");
			for(String ss:sl){
				ss = ss.trim();
				if(!ss.isEmpty()){
					list.add(ss);
				}
			}
			return list;
		}
		
	}
	
}
