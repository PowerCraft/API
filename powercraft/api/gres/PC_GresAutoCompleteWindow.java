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
import powercraft.api.gres.events.PC_GresKeyEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent;
import powercraft.api.gres.events.PC_GresTooltipGetEvent;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.history.PC_GresHistory;
import powercraft.api.gres.layout.PC_GresLayoutVertical;

public class PC_GresAutoCompleteWindow extends PC_GresNeedFocusFrame{

	public static PC_GresAutoCompleteWindow completeWindow;
	
	public static void makeCompleteWindow(PC_GresGuiHandler guiHandler, PC_GresMultilineHighlightingTextEdit textEdit, PC_AutoCompleteDisplay display, PC_GresHistory history){
		if(display.display){
			int num = 0;
			for(PC_StringListPart part:display.parts){
				num += part.size();
			}
			display.display = num!=0;
		}
		if(display.display){
			if(completeWindow==null){
				int i = 0;
				for(PC_StringListPart part:display.parts){
					int s = part.size();
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
				completeWindow = new PC_GresAutoCompleteWindow(textEdit);
				completeWindow.setMinSize(new PC_Vec2I(120, 102));
				completeWindow.setSize(new PC_Vec2I(120, 102));
				completeWindow.setLayout(new PC_GresLayoutVertical());
				guiHandler.add(completeWindow);
			}
			if(completeWindow.textEdit != textEdit){
				completeWindow.textEdit = textEdit;
			}
			//int spl = display.done.lastIndexOf('.')+1;
			//String last = "";
			List<PC_StringWithInfo> withInfos = new ArrayList<PC_StringWithInfo>();
			for(PC_StringListPart part:display.parts){
				for(PC_StringWithInfo s:part){
					/*String t = s.getString().substring(spl);
					int p = t.indexOf('.');
					if(p!=-1){
						t = t.substring(0, p+1);
						if(t.equals(last))
							continue;
					}
					last = t;
					withInfos.add(new PC_StringWithInfo(last, s.getTooltip(), s.getInfo()));*/
					withInfos.add(new PC_StringWithInfo(s.getString(), s.getTooltip(), s.getInfo()));
				}
			}
			completeWindow.setStringWithInfo(withInfos);
			//completeWindow.done = display.done.length()-spl;
			completeWindow.done = display.done.length();
			completeWindow.takeFocus();
			textEdit.focus = true;
			completeWindow.notifyChange();
		}else if(completeWindow!=null){
			completeWindow.getGuiHandler().remove(completeWindow);
			textEdit.takeFocus();
			completeWindow = null;
		}
	}
		
	PC_GresMultilineHighlightingTextEdit textEdit;
	PC_GresListBox listBox;
	List<PC_StringWithInfo> withInfos;
	private int done;
	private Listener listener;
	int lastTickMove;
	PC_GresInfoWindow infoWindow;
	PC_GresScrollArea sa;
	int selected;
	
	private PC_GresAutoCompleteWindow(PC_GresMultilineHighlightingTextEdit textEdit){
		this.textEdit = textEdit;
		this.listener = new Listener(this);
	}
	
	public void setStringWithInfo(List<PC_StringWithInfo> withInfos){
		this.withInfos = withInfos;
		removeAll();
		List<String> elements = new ArrayList<String>();
		for(PC_StringWithInfo withInfo:withInfos){
			elements.add(withInfo.getString());
		}
		
		this.listBox = new PC_GresListBox(elements);
		this.listBox.addEventListener(this.listener);
		this.listBox.setSelected(0);
		this.listBox.setFill(Fill.BOTH);
		add(this.listBox);
	}
	
	@Override
	public void putInRect(int x, int y, int width, int height) {
		setLocation(new PC_Vec2I(this.textEdit.getRealLocation()).add(this.textEdit.getCursorLowerPosition()));
		if(this.infoWindow!=null){
			this.infoWindow.setRealPos(new PC_Vec2I(getRealLocation()).add(this.rect.width, 0));
		}
	}
	
	@Override
	protected void onScaleChanged(int newScale) {
		this.textEdit.onScaleChanged(newScale);
		setLocation(new PC_Vec2I(this.textEdit.getRealLocation()).add(this.textEdit.getCursorLowerPosition()));
		if(this.infoWindow!=null){
			this.infoWindow.setRealPos(new PC_Vec2I(getRealLocation()).add(this.rect.width, 0));
		}
		super.onScaleChanged(newScale);
	}
	
	@SuppressWarnings("hiding") 
	void makeAdd(PC_GresHistory history){
		String text = this.listBox.getSelected();
		if(text==null){
			text = this.listBox.getElement(0);
		}
		if(text!=null){
			makeAdd(text, history);
		}
	}
	
	@SuppressWarnings("hiding")
	private void makeAdd(String text, PC_GresHistory history){
		this.textEdit.autoComplete(text.substring(this.done), history);
		if(!text.endsWith("."))
			closeAndTakeFocus();
	}
	
	@Override
	protected boolean handleKeyTyped(char key, int keyCode, boolean repeat, PC_GresHistory history) {
		switch (keyCode) {
		case Keyboard.KEY_ESCAPE:
			closeAndTakeFocus();
			break;
		case Keyboard.KEY_RETURN:
			makeAdd(history);
			break;
		case Keyboard.KEY_SPACE:
			makeAdd(history);
			return this.textEdit.onKeyTyped(key, keyCode, repeat, history);
		case Keyboard.KEY_DOWN:
		case Keyboard.KEY_UP:
			return this.listBox.onKeyTyped(key, keyCode, repeat, history);
		default:
			return this.textEdit.onKeyTyped(key, keyCode, repeat, history);
		}
		return true;
	}

	private void closeAndTakeFocus(){
		close();
		this.textEdit.takeFocus();
	}
	
	@Override
	public void close(){
		completeWindow = null;
		super.close();
	}
	
	@Override
	protected void onTick() {
		super.onTick();
		int select = this.listBox.getSelection();
		if(select==-1)
			return;
		String[] info = this.withInfos.get(select).getInfo();
		if(this.infoWindow==null || this.infoWindow.getParent()==null){
			if(this.infoWindow!=null){
				removeOtherAllowed(this.infoWindow);
			}
			this.sa = null;
			this.infoWindow=null;
			if(info!=null){
				this.lastTickMove++;
				if(this.lastTickMove>=20){
					this.infoWindow = new PC_GresInfoWindow(new PC_Vec2I(getRealLocation()).add(this.rect.width, 0));
					this.sa = new PC_GresScrollArea();
					this.sa.setFill(Fill.BOTH);
					this.sa.setMinSize(new PC_Vec2I(100, 100));
					this.infoWindow.add(this.sa);
					this.sa.getContainer().setLayout(new PC_GresLayoutVertical());
					addLabel(this.sa.getContainer(), info);
					this.infoWindow.setMinSize(new PC_Vec2I(120, this.rect.height));
					this.infoWindow.setSize(new PC_Vec2I(120, this.rect.height));
					this.infoWindow.setLayout(new PC_GresLayoutVertical());
					getGuiHandler().add(this.infoWindow);
					this.infoWindow.addOtherAllowed(this);
					addOtherAllowed(this.infoWindow);
					this.infoWindow.moveToTop();
					this.lastTickMove = -1;
				}
			}else{
				this.lastTickMove=0;
			}
		}else if(info==null){
			this.infoWindow.close();
			removeOtherAllowed(this.infoWindow);
			this.infoWindow=null;
			this.sa = null;
			this.lastTickMove = 0;
		}
	}

	void updateInfo(){
		if(this.infoWindow!=null){
			int select = this.listBox.getSelection();
			if(select==-1)
				return;
			this.selected = select;
			String info[] = this.withInfos.get(this.selected).getInfo();
			if(info==null){
				this.infoWindow.close();
				removeOtherAllowed(this.infoWindow);
				this.infoWindow=null;
				this.sa = null;
				this.lastTickMove = 0;
			}else{
				this.sa.getContainer().removeAll();
				this.sa.getContainer().setSize(new PC_Vec2I(0, 0));
				addLabel(this.sa.getContainer(), info);
			}
		}
	}
	
	private static void addLabel(PC_GresContainer container, String[] s){
		for(String ss:s){
			PC_GresLabel l = new PC_GresLabel(ss);
			l.setFill(Fill.HORIZONTAL);
			l.setAlignH(PC_GresAlign.H.LEFT);
			container.add(l);
		}
	}
	
	private static class Listener implements PC_IGresEventListener{

		@SuppressWarnings("hiding")
		private PC_GresAutoCompleteWindow completeWindow;
		
		Listener(PC_GresAutoCompleteWindow completeWindow) {
			this.completeWindow = completeWindow;
		}

		@Override
		public void onEvent(PC_GresEvent event) {
			if(event instanceof PC_GresMouseButtonEvent){
				PC_GresMouseButtonEvent ev = (PC_GresMouseButtonEvent) event;
				if(ev.getEvent()==PC_GresMouseButtonEvent.Event.DOWN && ev.isDoubleClick() && ev.getComponent()==this.completeWindow.listBox){
					this.completeWindow.makeAdd(ev.getHistory());
				}else if(this.completeWindow.listBox.getSelection()!=this.completeWindow.selected){
					this.completeWindow.updateInfo();
				}
			}else if(event instanceof PC_GresTooltipGetEvent){
				PC_GresTooltipGetEvent tge = (PC_GresTooltipGetEvent) event;
				if(tge.getComponent()==this.completeWindow.listBox){
					int selection = this.completeWindow.listBox.getMouseOver();
					if(selection!=-1){
						String info = this.completeWindow.withInfos.get(selection).getTooltip();
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
			}else if(event instanceof PC_GresKeyEvent){
				PC_GresKeyEvent ke = (PC_GresKeyEvent)event;
				ke.getComponent().handleKeyTyped(ke.getKey(), ke.getKeyCode(), ke.isRepeatEvents(), ke.getHistory());
				ke.consume();
				this.completeWindow.lastTickMove = 0;
				if(this.completeWindow.listBox.getSelection()!=this.completeWindow.selected){
					this.completeWindow.updateInfo();
				}
			}
		}
		
	}
	
}
