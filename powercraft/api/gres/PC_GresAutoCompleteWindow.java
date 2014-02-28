package powercraft.api.gres;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import powercraft.api.PC_Vec2I;
import powercraft.api.gres.autoadd.PC_AutoCompleteDisplay;
import powercraft.api.gres.autoadd.PC_StringListPart;
import powercraft.api.gres.autoadd.PC_StringWithInfo;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresFocusLostEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent;
import powercraft.api.gres.events.PC_GresTooltipGetEvent;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.history.PC_GresHistory;
import powercraft.api.gres.layout.PC_GresLayoutVertical;

public class PC_GresAutoCompleteWindow extends PC_GresFrame{

	public static PC_GresAutoCompleteWindow frame;
	
	public static void makeCompleteWindow(PC_GresGuiHandler guiHandler, PC_GresMultilineHighlightingTextEdit textEdit, PC_AutoCompleteDisplay display, PC_GresHistory history){
		if(display.display){
			int num = 0;
			for(PC_StringListPart part:display.parts){
				num += part.size();
			}
			display.display = num!=0;
		}
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
				frame.addEventListener(frame.listener = new Listener());
				guiHandler.add(frame);
			}
			if(frame.textEdit != textEdit){
				frame.textEdit = textEdit;
			}
			int spl = display.done.lastIndexOf('.')+1;
			String last = "";
			List<PC_StringWithInfo> withInfos = new ArrayList<PC_StringWithInfo>();
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
					withInfos.add(new PC_StringWithInfo(last, s.getTooltip(), s.getInfo()));
				}
			}
			frame.setStringWithInfo(withInfos);
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
	private PC_GresListBox listBox;
	private List<PC_StringWithInfo> withInfos;
	private int done;
	private Listener listener;
	
	private PC_GresAutoCompleteWindow(PC_GresMultilineHighlightingTextEdit textEdit){
		this.textEdit = textEdit;
	}
	
	public void setStringWithInfo(List<PC_StringWithInfo> withInfos){
		this.withInfos = withInfos;
		removeAll();
		List<String> elements = new ArrayList<String>();
		for(PC_StringWithInfo withInfo:withInfos){
			elements.add(withInfo.getString());
		}
		add(listBox = new PC_GresListBox(elements));
		listBox.addEventListener(listener);
		listBox.setSelected(0);
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
		String text = listBox.getSelected();
		if(text==null){
			text = listBox.getElement(0);
		}
		if(text!=null){
			makeAdd(text, history);
		}
	}
	
	private void makeAdd(String text, PC_GresHistory history){
		textEdit.autoComplete(text.substring(done), history);
		if(!text.endsWith("."))
			close();
	}
	
	@Override
	protected boolean handleKeyTyped(char key, int keyCode, boolean repeat, PC_GresHistory history) {
		switch (keyCode) {
		case Keyboard.KEY_ESCAPE:
			close();
			break;
		case Keyboard.KEY_RETURN:
			makeAdd(history);
			break;
		case Keyboard.KEY_SPACE:
			makeAdd(history);
			return textEdit.onKeyTyped(key, keyCode, repeat, history);
		case Keyboard.KEY_DOWN:
		case Keyboard.KEY_UP:
			return listBox.handleKeyTyped(key, keyCode, repeat, history);
		default:
			return textEdit.onKeyTyped(key, keyCode, repeat, history);
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
				if(ev.getEvent()==PC_GresMouseButtonEvent.Event.DOWN && ev.isDoubleClick() && ev.getComponent()==frame.listBox){
					frame.makeAdd(ev.getHistory());
				}
			}else if(event instanceof PC_GresTooltipGetEvent){
				PC_GresTooltipGetEvent tge = (PC_GresTooltipGetEvent) event;
				if(tge.getComponent()==frame.listBox){
					int selection = frame.listBox.getMouseOver();
					if(selection!=-1){
						String info = frame.withInfos.get(selection).getTooltip();
						if(info!=null){
							List<String> tooltip = new ArrayList<String>();
							String sl[] = info.split("\n");
							for(String ss:sl){
								ss = ss.trim();
								if(!ss.isEmpty()){
									tooltip.add(ss);
								}
							}
							tge.setTooltip(tooltip);
							tge.consume();
						}
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
