package powercraft.api.gres;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;

import org.lwjgl.input.Keyboard;

import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.autoadd.PC_AutoAdd;
import powercraft.api.gres.autoadd.PC_AutoComplete;
import powercraft.api.gres.autoadd.PC_AutoCompleteDisplay;
import powercraft.api.gres.autoadd.PC_StringAdd;
import powercraft.api.gres.doc.PC_GresDocRenderHandler;
import powercraft.api.gres.doc.PC_GresDocument;
import powercraft.api.gres.doc.PC_GresDocumentLine;
import powercraft.api.gres.doc.PC_GresDocumentLine.Message;
import powercraft.api.gres.doc.PC_GresHighlighting;
import powercraft.api.gres.events.PC_GresMouseWheelEvent;
import powercraft.api.gres.font.PC_FontRenderer;
import powercraft.api.gres.font.PC_FontTexture;
import powercraft.api.gres.font.PC_Formatter;
import powercraft.api.gres.history.PC_GresHistory;
import powercraft.api.gres.history.PC_IGresHistoryEntry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_GresMultilineHighlightingTextEdit extends PC_GresComponent {

	private static final String scrollH = "ScrollH", scrollHFrame = "ScrollHFrame", scrollV = "ScrollV", scrollVFrame = "ScrollVFrame";
	
	protected static final String textureName = "TextEdit";
	protected static final String textureName2 = "TextEditSelect";
	
	private PC_GresDocument document;
	private PC_GresHighlighting highlighting;
	PC_Vec2I mouseSelectStart = new PC_Vec2I(0,0);
	PC_Vec2I mouseSelectEnd = new PC_Vec2I(0,0);
	private int vScrollSize = 0, hScrollSize = 0;
	private float vScrollPos = 0, hScrollPos = 0;
	private PC_Vec2I scroll = new PC_Vec2I(0, 0);
	private int cursorCounter;
	PC_FontTexture fontTexture;
	private PC_AutoAdd autoAdd;
	private PC_AutoComplete autoComplete;
	private int scale = 1;
	private DocHandler docHandler;
	private PC_AutoCompleteDisplay disp = new PC_AutoCompleteDisplay();
	private boolean newStart = false;
	
	private static PC_Vec2I lastMousePosition = new PC_Vec2I(0, 0);
	private static int overBar=-1;
	private static int selectBar=-1;
	
	public PC_GresMultilineHighlightingTextEdit(PC_FontTexture fontTexture, PC_GresHighlighting highlighting, PC_AutoAdd autoAdd, PC_AutoComplete autoComplete, String text){
		this.fontTexture = fontTexture;
		this.highlighting = highlighting;
		this.autoAdd = autoAdd;
		this.autoComplete = autoComplete;
		this.document = new PC_GresDocument(text, highlighting, this.docHandler = new DocHandler(), autoComplete==null?null:autoComplete.getInfoCollector());
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		return new PC_Vec2I(300, 200);
	}

	@Override
	protected PC_Vec2I calculateMaxSize() {
		return new PC_Vec2I(-1, -1);
	}

	@Override
	protected PC_Vec2I calculatePrefSize() {
		return new PC_Vec2I(300, 200);
	}

	@SuppressWarnings("hiding")
	@Override
	protected void paint(PC_RectI scissor, double scale, int displayHeight, float timeStamp) {
		
		if(this.docHandler.searchNewLengst){
			this.docHandler.searchNewLengst = false;
			this.docHandler.maxLineLength = -1;
		}
		
		if(!this.mouseDown){
			calcScrollPosition();
		}
		
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int d2 = getTextureDefaultSize(scrollVFrame).x;
		
		drawTexture(scrollVFrame, this.rect.width-d2, 0, d2, this.rect.height-d1, getStateForBar(1));
		drawTexture(scrollV, this.rect.width-d2+1, (int)this.vScrollPos+1, d2-2, this.vScrollSize-1, getStateForBar(1));
		
		drawTexture(scrollHFrame, 0, this.rect.height-d1, this.rect.width-d2, d1, getStateForBar(0));
		drawTexture(scrollH, (int)this.hScrollPos+1, this.rect.height-d1+1, this.hScrollSize-1, d1-2, getStateForBar(0));
		
		drawTexture(textureName, 0, 0, this.rect.width-d2+1, this.rect.height-d1+1);
		
		PC_Vec2I offset = getRealLocation();
		setDrawRect(scissor, new PC_RectI(2+offset.x, 2+offset.y, this.rect.width - 3 - d2, this.rect.height - 3 - d1), scale, displayHeight);
		
		PC_GresDocumentLine line = this.document.getLine(this.scroll.y);
		int lineNum = this.scroll.y;
		int y = 2;
		while(line!=null && y<this.rect.height - 2 - d1){
			y += drawLine(lineNum, line, 2-this.scroll.x, y);
			lineNum++;
			line = line.next;
		}
		
		if(scissor==null){
			setDrawRect(scissor, new PC_RectI(-1, -1, -1, -1), scale, displayHeight);
		}else{
			setDrawRect(scissor, scissor, scale, displayHeight);
		}
		
	}

	private int getStateForBar(int bar){
		return this.enabled && this.parentEnabled ? this.mouseDown && selectBar==bar ? 2 : this.mouseOver && overBar==bar ? 1 : 0 : 3;
	}
	
	private void calcScrollPosition() {
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int d2 = getTextureDefaultSize(scrollVFrame).x;
		int sizeX = this.rect.width - d2;
		int maxSizeX = this.docHandler.maxLineLength/this.scale;
		int sizeOutOfFrame = maxSizeX - sizeX + 5;
		if (sizeOutOfFrame < 0) {
			sizeOutOfFrame = 0;
		}
		float prozent = maxSizeX > 0 ? ((float) sizeOutOfFrame / maxSizeX) : 0;
		this.hScrollPos = (sizeOutOfFrame > 0 ? (float) this.scroll.x / sizeOutOfFrame : 0) * prozent * sizeX;
		this.hScrollSize = (int) ((1 - prozent) * sizeX + 0.5);

		int sizeY = this.rect.height - d1;
		int lines = this.document.getLines();
		if(lines>1){
			prozent = 1.0f/(lines-1);
			this.vScrollPos = this.scroll.y * prozent * sizeY * 0.9f;
			this.vScrollSize = (int) (0.1f * sizeY + 0.5);
		}else{
			this.vScrollPos = 0;
			this.vScrollSize = sizeY;
		}
		updateScrollPosition();
	}
	
	private void updateScrollPosition() {
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int d2 = getTextureDefaultSize(scrollVFrame).x;
		int sizeX = this.rect.width - d2;
		int maxSizeX = this.docHandler.maxLineLength/this.scale;
		int sizeOutOfFrame = maxSizeX - sizeX + 5;
		if (sizeOutOfFrame < 0) {
			sizeOutOfFrame = 0;
		}
		float prozent = maxSizeX > 0 ? ((float) sizeOutOfFrame / (maxSizeX)) : 0;
		if (this.hScrollPos < 0) {
			this.hScrollPos = 0;
		}
		if (this.hScrollPos > sizeX - this.hScrollSize) {
			this.hScrollPos = sizeX - this.hScrollSize;
		}
		this.scroll.x = (int) (this.hScrollPos / prozent / sizeX * sizeOutOfFrame + 0.5);

		int sizeY = this.rect.height - d1;
		int lines = this.document.getLines();
		if(lines>1){
			prozent = 1.0f / (lines - 1);
			if (this.vScrollPos < 0) {
				this.vScrollPos = 0;
			}
			if (this.vScrollPos > 0.9f * sizeY) {
				this.vScrollPos = 0.9f * sizeY;
			}
			this.scroll.y = (int) (this.vScrollPos / prozent / sizeY / 0.9f + 0.5);
		}else{
			this.vScrollPos = 0;
			this.scroll.y = 0;
		}
	}
	
	private static PC_Vec2I[] sort(PC_Vec2I start, PC_Vec2I end){
		if(start.y<end.y || (start.y==end.y && start.x<end.x))
			return new PC_Vec2I[]{start, end};
		return new PC_Vec2I[]{end, start};
	}
	
	@SuppressWarnings("hiding")
	private int drawLine(int lineNum, PC_GresDocumentLine line, int x, int y){
		PC_Vec2I[] selected = sort(this.mouseSelectStart, this.mouseSelectEnd);
		String text = line.getHighlightedString();
		PC_Vec2I size = ((LineInfo)line.renderInfo).size;
		if(size.x>this.docHandler.maxLineLength){
			this.docHandler.maxLineLength = size.x;
			this.docHandler.longestLine = line;
		}
		size = new PC_Vec2I(size);
		size.x /= this.scale;
		size.y /= this.scale;
		if(selected[0].y<=lineNum && lineNum<=selected[1].y && (selected[0].y == selected[1].y?selected[0].x != selected[1].x:true)){
			int startX = 0;
			int endX;
			if(selected[0].y == lineNum){
				startX = PC_FontRenderer.getStringSize(PC_Formatter.substring(text, 0, selected[0].x), this.fontTexture, 1.0f/this.scale).x;
			}
			if(selected[1].y == lineNum){
				endX = PC_FontRenderer.getStringSize(PC_Formatter.substring(text, 0, selected[1].x), this.fontTexture, 1.0f/this.scale).x;
			}else{
				endX = size.x;
			}
			drawTexture(textureName2, startX + x, y, endX-startX, size.y);
		}
		PC_FontRenderer.drawString(text, x, y, this.fontTexture, 1.0f/this.scale);
		if (this.focus && this.cursorCounter / 6 % 2 == 0 && this.mouseSelectEnd.y==lineNum) {
			PC_GresRenderer.drawVerticalLine(x+PC_FontRenderer.getStringSize(
					PC_Formatter.substring(text, 0, this.mouseSelectEnd.x), this.fontTexture, 1.0f/this.scale).x, y,
					y + size.y - 1, this.fontColors[0]|0xff000000);
		}
		return size.y;
	}
	
	protected void addKey(char c, PC_GresHistory history) {
		String removed = "";
		if(!this.mouseSelectStart.equals(this.mouseSelectEnd)){
			removed = this.document.getText(this.mouseSelectStart, this.mouseSelectEnd);
			this.document.remove(this.mouseSelectStart, this.mouseSelectEnd);
		}
		setToStartSelect();
		PC_StringAdd addStruct = new PC_StringAdd();
		addStruct.component = this;
		addStruct.toAdd = ""+c;
		addStruct.cursorPos = -1;
		addStruct.document = this.document;
		addStruct.documentLine = this.document.getLine(this.mouseSelectStart.y);
		addStruct.pos = this.mouseSelectStart.x;
		if(this.autoAdd!=null){
			this.autoAdd.onCharAdded(addStruct);
		}
		setSelected(addStruct.toAdd, null, false, false);
		if(history!=null){
			history.addHistoryEntry(new HistoryEntry(this, this.mouseSelectStart, this.mouseSelectEnd, removed, false, true, true));
			this.newStart = false;
		}
		if(addStruct.cursorPos!=-1){
			this.mouseSelectEnd = new PC_Vec2I(this.mouseSelectStart.x+addStruct.cursorPos, this.mouseSelectStart.y);
			while(this.mouseSelectEnd.x>this.document.getLine(this.mouseSelectEnd.y).getText().length()){
				this.mouseSelectEnd.x -= this.document.getLine(this.mouseSelectEnd.y).getText().length()+1;
				this.mouseSelectEnd.y++;
				if(this.mouseSelectEnd.y>=this.document.getLines()){
					this.mouseSelectEnd = this.mouseSelectStart;
					moveViewToSelect();
					break;
				}
			}
		}
		this.mouseSelectStart = this.mouseSelectEnd;
		moveViewToSelect();
		if(this.autoComplete!=null){
			this.autoComplete.onStringAdded(this, this.document, this.document.getLine(this.mouseSelectStart.y), addStruct.toAdd, this.mouseSelectEnd.x, this.disp);
			showCompleteWindow(history);
		}
	}

	private void showCompleteWindow(PC_GresHistory history){
		PC_GresAutoCompleteWindow.makeCompleteWindow(getGuiHandler(), this, this.disp, history);
	}
	
	private void updateComplete(PC_GresHistory history){
		if(this.autoComplete!=null && this.disp.display){
			this.autoComplete.makeComplete(this, this.document, this.document.getLine(this.mouseSelectEnd.y), this.mouseSelectEnd.x, this.disp);
			showCompleteWindow(history);
		}
	}
	
	private void deleteSelected(PC_GresHistory history) {
		String removed = this.document.getText(this.mouseSelectStart, this.mouseSelectEnd);
		this.document.remove(this.mouseSelectStart, this.mouseSelectEnd);
		setToStartSelect();
		if(history!=null){
			history.addHistoryEntry(new HistoryEntry(this, this.mouseSelectStart, this.mouseSelectStart, removed, true, false, false));
			this.newStart = false;
		}
		moveViewToSelect();
	}

	private void key_backspace(PC_GresHistory history) {
		if (this.mouseSelectStart != this.mouseSelectEnd) {
			deleteSelected(history);
			return;
		}
		this.mouseSelectStart = new PC_Vec2I(this.mouseSelectStart.x-1, this.mouseSelectStart.y);
		if(this.mouseSelectStart.x<0){
			this.mouseSelectStart.y--;
			if(this.mouseSelectStart.y<0){
				this.mouseSelectStart = this.mouseSelectEnd;
				moveViewToSelect();
				return;
			}
			this.mouseSelectStart.x = this.document.getLine(this.mouseSelectStart.y).getText().length();
		}
		String removed = this.document.getText(this.mouseSelectStart, this.mouseSelectEnd);
		this.document.remove(this.mouseSelectStart, this.mouseSelectEnd);
		setToStartSelect();
		if(history!=null){
			history.addHistoryEntry(new HistoryEntry(this, this.mouseSelectStart, this.mouseSelectStart, removed, this.newStart, false, true));
			this.newStart = false;
		}
		moveViewToSelect();
	}

	private void key_delete(PC_GresHistory history) {
		if (this.mouseSelectStart != this.mouseSelectEnd) {
			deleteSelected(history);
			return;
		}
		this.mouseSelectEnd = new PC_Vec2I(this.mouseSelectStart.x+1, this.mouseSelectStart.y);
		if(this.mouseSelectEnd.x>this.document.getLine(this.mouseSelectEnd.y).getText().length()){
			this.mouseSelectEnd.y++;
			if(this.mouseSelectEnd.y>=this.document.getLines()){
				this.mouseSelectEnd = this.mouseSelectStart;
				moveViewToSelect();
				return;
			}
			this.mouseSelectEnd.x = 0;
		}
		String removed = this.document.getText(this.mouseSelectStart, this.mouseSelectEnd);
		this.document.remove(this.mouseSelectStart, this.mouseSelectEnd);
		setToStartSelect();
		if(history!=null){
			history.addHistoryEntry(new HistoryEntry(this, this.mouseSelectStart, this.mouseSelectStart, removed, this.newStart, false, true));
			this.newStart = false;
		}
		moveViewToSelect();
	}
	
	String getSelect(){
		return this.document.getText(this.mouseSelectStart, this.mouseSelectEnd);
	}
	
	void setSelected(String stri, PC_GresHistory history, boolean block, boolean startToEnd) {
		String removed = "";
		if(!this.mouseSelectStart.equals(this.mouseSelectEnd)){
			removed = this.document.getText(this.mouseSelectStart, this.mouseSelectEnd);
			this.document.remove(this.mouseSelectStart, this.mouseSelectEnd);
		}
		setToStartSelect();
		this.document.add(this.mouseSelectStart, stri);
		this.mouseSelectEnd = new PC_Vec2I(this.mouseSelectStart.x+stri.length(), this.mouseSelectStart.y);
		while(this.mouseSelectEnd.x>this.document.getLine(this.mouseSelectEnd.y).getText().length()){
			this.mouseSelectEnd.x -= this.document.getLine(this.mouseSelectEnd.y).getText().length()+1;
			this.mouseSelectEnd.y++;
			if(this.mouseSelectEnd.y>=this.document.getLines()){
				this.mouseSelectEnd = this.mouseSelectStart;
				break;
			}
		}
		if(history!=null){
			history.addHistoryEntry(new HistoryEntry(this, this.mouseSelectStart, this.mouseSelectEnd, removed, block || this.newStart, true, !block));
			this.newStart = false;
		}
		if(startToEnd){
			this.mouseSelectStart = this.mouseSelectEnd;
			moveViewToSelect();
		}
	}
	
	private void setToStartSelect(){
		if(this.mouseSelectStart.y>this.mouseSelectEnd.y || (this.mouseSelectStart.y==this.mouseSelectEnd.y && this.mouseSelectStart.x>this.mouseSelectEnd.x)){
			this.mouseSelectStart = this.mouseSelectEnd;
		}else{
			this.mouseSelectEnd = this.mouseSelectStart;
		}
	}
	
	private PC_Vec2I getMousePositionInString(PC_Vec2I mouse){
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int d2 = getTextureDefaultSize(scrollVFrame).x;
		PC_Vec2I newMouse = new PC_Vec2I(mouse);
		if(newMouse.x<2){
			newMouse.x = 2;
			this.scroll.x-=4;
		}else if(newMouse.x>this.rect.width-d2-4){
			newMouse.x = this.rect.width-d2-4;
			this.scroll.x+=4;
		}
		if(newMouse.y<2){
			newMouse.y = 2;
			this.scroll.y--;
		}else if(newMouse.y>this.rect.height-d1-4){
			newMouse.y = this.rect.height-d1-4;
			this.scroll.y++;
		}
		if(!newMouse.equals(mouse)){
			calcScrollPosition();
		}
		int y = this.scroll.y;
		PC_GresDocumentLine line = this.document.getLine(y);
		int h = ((LineInfo)line.renderInfo).size.y/this.scale;
		while(h<newMouse.y-1){
			line = line.next;
			if(line==null)
				break;
			y++;
			h += ((LineInfo)line.renderInfo).size.y/this.scale;
		}
		int x = getPositionFromString(new PC_Vec2I((newMouse.x+this.scroll.x-2), y));
		return new PC_Vec2I(x, y);
	}
	
	private int getPixelPositionFromString(PC_Vec2I pos){
		PC_GresDocumentLine line = this.document.getLine(pos.y);
		return PC_FontRenderer.getStringSize(PC_Formatter.substring(line.getHighlightedString(), 0, pos.x), this.fontTexture, 1.0f/this.scale).x;
	}
	
	@SuppressWarnings("hiding")
	private int getPositionFromString(PC_Vec2I pos){
		if(pos.y>=this.document.getLines()){
			pos.y=this.document.getLines()-1;
		}
		PC_GresDocumentLine line = this.document.getLine(pos.y);
		String text = line.getHighlightedString();
		int length = PC_Formatter.removeFormatting(text).length();
		int last = 0;
		for(int i=1; i<=length; i++){
			int l = PC_FontRenderer.getStringSize(PC_Formatter.substring(text, 0, i), this.fontTexture, 1.0f/this.scale).x;
			if(l>pos.x){
				if((l+last)/2<pos.x){
					return i;
				}
				return i-1;
			}
			last = l;
		}
		return length;
	}
	
	public PC_Vec2I getCursorLowerPosition(){
		int y = 0;
		PC_GresDocumentLine line = this.document.getLine(this.scroll.y);
		for(int i=this.scroll.y; i<=this.mouseSelectEnd.y; i++){
			y += ((LineInfo)line.renderInfo).size.y;
			line = line.next;
		}
		return new PC_Vec2I(getPixelPositionFromString(this.mouseSelectEnd), 2+y/this.scale);
	}
	
	public void autoComplete(String with, PC_GresHistory history){
		setSelected(with, history, false, true);
		if(with.endsWith(".") && this.autoComplete!=null){
			this.autoComplete.onStringAdded(this, this.document, this.document.getLine(this.mouseSelectStart.y), with, this.mouseSelectEnd.x, this.disp);
			showCompleteWindow(history);
		}
	}
	
	@SuppressWarnings("hiding")
	@Override
	protected boolean handleKeyTyped(char key, int keyCode, boolean repeat, PC_GresHistory history) {
		super.handleKeyTyped(key, keyCode, repeat, history);
		this.cursorCounter = 0;
		switch (key) {
		case 3:
			GuiScreen.setClipboardString(getSelect());
			break;
		case 22:
			setSelected(GuiScreen.getClipboardString(), history, true, true);
			break;
		case 24:
			GuiScreen.setClipboardString(getSelect());
			deleteSelected(history);
			break;
		default:
			switch (keyCode) {
			case Keyboard.KEY_RETURN:
				addKey('\n', history);
				return true;
			case Keyboard.KEY_BACK:
				key_backspace(history);
				return true;
			case Keyboard.KEY_HOME:
				if (Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) || Keyboard
						.isKeyDown(Keyboard.KEY_LCONTROL)) {
					this.mouseSelectEnd = new PC_Vec2I();
				}else{
					int first = 0;
					String text = this.document.getLine(this.mouseSelectEnd.y).getText();
					for(int i=0; i<text.length(); i++){
						char c = text.charAt(i);
						if(c==' ' || c=='\t'){
							first++;
						}else{
							break;
						}
					}
					if(first==text.length() || first==this.mouseSelectEnd.x){
						this.mouseSelectEnd.x = 0;
					}else{
						this.mouseSelectEnd.x = first;
					}
				}
				if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard
						.isKeyDown(Keyboard.KEY_LSHIFT))) {
					this.mouseSelectStart = this.mouseSelectEnd;
				}
				this.newStart = true;
				moveViewToSelect();
				updateComplete(history);
				return true;
			case Keyboard.KEY_END:
				if (Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) || Keyboard
						.isKeyDown(Keyboard.KEY_LCONTROL)) {
					this.mouseSelectEnd = this.document.getLastPos();
				}else{
					int last = 0;
					String text = this.document.getLine(this.mouseSelectEnd.y).getText();
					last = text.length();
					for(int i=last-1; i>=0; i--){
						char c = text.charAt(i);
						if(c==' ' || c=='\t'){
							last--;
						}else{
							break;
						}
					}
					if(last==0 || last==this.mouseSelectEnd.x){
						this.mouseSelectEnd.x = text.length();
					}else{
						this.mouseSelectEnd.x = last;
					}
				}
				if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard
						.isKeyDown(Keyboard.KEY_LSHIFT))) {
					this.mouseSelectStart = this.mouseSelectEnd;
				}
				this.newStart = true;
				moveViewToSelect();
				updateComplete(history);
				return true;
			case Keyboard.KEY_DELETE:
				key_delete(history);
				updateComplete(history);
				return true;
			case Keyboard.KEY_LEFT:
				if (this.mouseSelectEnd.y > 0 || (this.mouseSelectEnd.y == 0 && this.mouseSelectEnd.x > 0)) {
					this.mouseSelectEnd = new PC_Vec2I(this.mouseSelectEnd.x-1, this.mouseSelectEnd.y);
					if(this.mouseSelectEnd.x<0){
						this.mouseSelectEnd.y--;
						this.mouseSelectEnd.x = this.document.getLine(this.mouseSelectEnd.y).getText().length();
					}
					if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard
							.isKeyDown(Keyboard.KEY_LSHIFT))) {
						this.mouseSelectStart = this.mouseSelectEnd;
					}

				}
				this.newStart = true;
				moveViewToSelect();
				updateComplete(history);
				return true;
			case Keyboard.KEY_RIGHT:
				PC_Vec2I prev = this.mouseSelectEnd;
				this.mouseSelectEnd = new PC_Vec2I(this.mouseSelectEnd.x+1, this.mouseSelectEnd.y);
				if(this.mouseSelectEnd.x>this.document.getLine(this.mouseSelectEnd.y).getText().length()){
					this.mouseSelectEnd.y++;
					if(this.mouseSelectEnd.y>=this.document.getLines()){
						this.mouseSelectEnd = prev;
						moveViewToSelect();
						return true;
					}
					this.mouseSelectEnd.x = 0;
				}
				if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard
						.isKeyDown(Keyboard.KEY_LSHIFT))) {
					this.mouseSelectStart = this.mouseSelectEnd;
				}
				this.newStart = true;
				moveViewToSelect();
				updateComplete(history);
				return true;
			case Keyboard.KEY_UP:
				if(this.mouseSelectEnd.y>0){
					int pos = getPixelPositionFromString(this.mouseSelectEnd);
					this.mouseSelectEnd = new PC_Vec2I(this.mouseSelectEnd.x, this.mouseSelectEnd.y-1);
					this.mouseSelectEnd.x = getPositionFromString(new PC_Vec2I(pos, this.mouseSelectEnd.y));
					if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard
							.isKeyDown(Keyboard.KEY_LSHIFT))) {
						this.mouseSelectStart = this.mouseSelectEnd;
					}
				}
				this.newStart = true;
				moveViewToSelect();
				return true;
			case Keyboard.KEY_DOWN:
				if(this.mouseSelectEnd.y<this.document.getLines()-1){
					int pos = getPixelPositionFromString(this.mouseSelectEnd);
					this.mouseSelectEnd = new PC_Vec2I(this.mouseSelectEnd.x, this.mouseSelectEnd.y+1);
					this.mouseSelectEnd.x = getPositionFromString(new PC_Vec2I(pos, this.mouseSelectEnd.y));
					if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard
							.isKeyDown(Keyboard.KEY_LSHIFT))) {
						this.mouseSelectStart = this.mouseSelectEnd;
					}
				}
				this.newStart = true;
				moveViewToSelect();
				return true;
			case Keyboard.KEY_SPACE:
				if(Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
					if(this.autoComplete!=null){
						this.autoComplete.makeComplete(this, this.document, this.document.getLine(this.mouseSelectEnd.y), this.mouseSelectEnd.x, this.disp);
						showCompleteWindow(history);
					}
					break;
				}
				addKey(key, history);
				break;
			case Keyboard.KEY_A:
				if(Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
					this.mouseSelectStart = new PC_Vec2I();
					this.mouseSelectEnd = this.document.getLastPos();
					this.newStart = true;
					break;
				}
				addKey(key, history);
				break;
			case Keyboard.KEY_TAB:
			{				
				int last = this.document.getLine(this.mouseSelectEnd.y).getText().length();
				if(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || this.mouseSelectEnd.y!=this.mouseSelectStart.y || (this.mouseSelectEnd.x==0 && this.mouseSelectStart.x==last && this.mouseSelectStart.x!=0) || (this.mouseSelectEnd.x==last && this.mouseSelectEnd.x!=0 && this.mouseSelectStart.x==0)){
					int sy = this.mouseSelectEnd.y;
					int ey = this.mouseSelectStart.y;
					boolean swap = sy>ey;
					if(swap){
						sy = ey;
						ey = this.mouseSelectEnd.y;
					}
					this.newStart = true;
					String old = "";
					PC_GresDocumentLine line = this.document.getLine(sy);
					PC_GresDocumentLine sline = line;
					PC_GresDocumentLine l = line;
					for(int i=sy; i<ey; i++){
						old += l.getText()+"\n";
						l = l.next;
					}
					old += l.getText();
					if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
						int sj=0;
						int ej=0;
						for(int i=sy; i<=ey; i++){
							String text = line.getText();
							boolean done = false;
							for(int j=0; j<text.length(); j++){
								char c = text.charAt(j);
								if(c=='\t'){
									text = text.substring(0, j)+text.substring(j+1);
									done = true;
									if(i==sy)
										sj = 1;
									if(i==ey)
										ej = 1;
									break;
								}else if(c!=' '){
									break;
								}
							}
							if(!done){
								int j=0;
								while(j<text.length() && j<4 && text.charAt(j)==' '){
									j++;
								}
								text = text.substring(j);
								if(i==sy)
									sj = j;
								if(i==ey)
									ej = j;
							}
							this.document.onLineChange(line);
							line.setText(text);
							this.document.onLineChanged(line);
							line = line.next;
						}
						if(swap){
							this.mouseSelectStart.x -= sj;
							if(this.mouseSelectStart!=this.mouseSelectEnd)
								this.mouseSelectEnd.x -= ej;
						}else{
							this.mouseSelectStart.x -= ej;
							if(this.mouseSelectStart!=this.mouseSelectEnd)
								this.mouseSelectEnd.x -= sj;
						}
						if(this.mouseSelectStart.x<0)
							this.mouseSelectStart.x=0;
						if(this.mouseSelectStart.x<0)
							this.mouseSelectStart.x=0;
					}else{
						for(int i=sy; i<=ey; i++){
							this.document.onLineChange(line);
							line.setText("\t"+line.getText());
							this.document.onLineChanged(line);
							line = line.next;
						}
						if(this.mouseSelectStart.x!=0)
							this.mouseSelectStart.x++;
						if(this.mouseSelectEnd.x!=0)
							this.mouseSelectEnd.x++;
					}
					this.document.recalcHighlights(sline, ey-sy+1);
					history.addHistoryEntry(new HistoryEntry(this, new PC_Vec2I(0, sy), new PC_Vec2I(l.getText().length(), ey), old, true, true, false));
					break;
				}
				addKey(key, history);
				break;
			}
			default:
				if (ChatAllowedCharacters.isAllowedCharacter(key)) {
					addKey(key, history);
					return true;
				}
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("hiding")
	private void moveViewToSelect(){
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int d2 = getTextureDefaultSize(scrollVFrame).x;
		int shouldY = this.mouseSelectEnd.y;
		PC_GresDocumentLine line = this.document.getLine(shouldY);
		String text = line.getHighlightedString();
		int x = ((LineInfo)line.renderInfo).size.x;
		if(x>this.docHandler.maxLineLength){
			this.docHandler.maxLineLength = x;
			this.docHandler.longestLine = line;
		}
		int shouldX = PC_FontRenderer.getStringSize(PC_Formatter.substring(this.document.getLine(this.mouseSelectEnd.y).getHighlightedString(), 0, this.mouseSelectEnd.x), this.fontTexture, 1.0f/this.scale).x;
		if(this.scroll.x>shouldX){
			this.scroll.x = shouldX;
		}else{
			int i=1;
			if(PC_FontRenderer.getStringSize(PC_Formatter.substring(text, 0, this.mouseSelectEnd.x), this.fontTexture, 1.0f/this.scale).x>this.scroll.x + this.rect.width-d2-4){
				while(PC_FontRenderer.getStringSize(PC_Formatter.substring(text, this.mouseSelectEnd.x-i, this.mouseSelectEnd.x), this.fontTexture, 1.0f/this.scale).x<this.rect.width-d2-4){
					i++;
					if(i>this.mouseSelectEnd.x){
						break;
					}
				}
				this.scroll.x = PC_FontRenderer.getStringSize(PC_Formatter.substring(text, 0, this.mouseSelectEnd.x-i+1), this.fontTexture, 1.0f/this.scale).x;
			}
		}
		if(this.scroll.y>shouldY){
			this.scroll.y = shouldY;
		}else{
			int h = ((LineInfo)line.renderInfo).size.y/this.scale;
			while(h<this.rect.height-d1-2){
				line = line.prev;
				shouldY--;
				if(line==null)
					break;
				h += ((LineInfo)line.renderInfo).size.y/this.scale;
			}
			shouldY++;
			if(this.scroll.y<shouldY)
				this.scroll.y = shouldY;
		}
		calcScrollPosition();
	}
	
	@Override
	protected void handleMouseWheel(PC_GresMouseWheelEvent event, PC_GresHistory history) {
		this.scroll.y -= event.getWheel();
		calcScrollPosition();
		event.consume();
	}
	
	@Override
	protected boolean handleMouseMove(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		super.handleMouseMove(mouse, buttons, history);
		if(this.mouseDown){
			if(selectBar==0){
				this.hScrollPos += mouse.x - lastMousePosition.x;
				updateScrollPosition();
				lastMousePosition.setTo(mouse);
			}else if(selectBar==1){
				this.vScrollPos += mouse.y - lastMousePosition.y;
				updateScrollPosition();
				lastMousePosition.setTo(mouse);
			}else{
				this.newStart = true;
				this.mouseSelectEnd = getMousePositionInString(mouse);
				this.cursorCounter = 0;
			}
		}
		if(this.mouseOver){
			overBar = mouseOverBar(mouse);
		}
		return true;
	}

	@Override
	protected boolean handleMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton, boolean doubleClick, PC_GresHistory history) {
		super.handleMouseButtonDown(mouse, buttons, eventButton, doubleClick, history);
		if(this.mouseDown){
			lastMousePosition.setTo(mouse);
			selectBar = mouseOverBar(mouse);
		}
		if(this.mouseOver){
			overBar = mouseOverBar(mouse);
		}
		if(selectBar!=-1 || overBar!=-1)
			return true;
		this.newStart = true;
		this.mouseSelectEnd = getMousePositionInString(mouse);
		if(!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)))
			this.mouseSelectStart = this.mouseSelectEnd;
		this.cursorCounter = 0;
		return true;
	}

	@Override
	protected void handleMouseLeave(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		this.mouseOver = false;
	}
	
	private int mouseOverBar(PC_Vec2I mouse){
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int d2 = getTextureDefaultSize(scrollVFrame).x;
		if(new PC_RectI(this.rect.width-d2+1, (int)this.vScrollPos+1, d2-2, this.vScrollSize-1).contains(mouse)){
			return 1;
		}
		if(new PC_RectI((int)this.hScrollPos+1, this.rect.height-d1+1, this.hScrollSize-1, d1-2).contains(mouse)){
			return 0;
		}
		return -1;
	}
	
	@Override
	protected void onTick() {
		if (this.focus) {
			this.cursorCounter++;
		} else {
			this.cursorCounter = 0;
		}
	}
	
	@Override
	public void setText(String text) {
		this.document = new PC_GresDocument(text, this.highlighting, this.docHandler = new DocHandler(), this.autoComplete==null?null:this.autoComplete.getInfoCollector());
	}

	@Override
	public String getText() {
		return this.document.getWholeText();
	}
	
	@Override
	protected void onScaleChanged(int newScale) {
		this.scale = newScale;
	}

	private class DocHandler implements PC_GresDocRenderHandler{

		PC_GresDocumentLine longestLine;
		int maxLineLength = -1;
		boolean searchNewLengst = false;
		int docLength;
		
		DocHandler() {
			
		}

		@Override
		public void onLineChange(PC_GresDocumentLine line) {
			if(this.longestLine==line){
				this.longestLine = null;
				this.searchNewLengst = true;
			}
			PC_Vec2I size = ((LineInfo)line.renderInfo).size;
			this.docLength -= size.y;
		}

		@SuppressWarnings("hiding")
		@Override
		public void onLineChanged(PC_GresDocumentLine line) {
			String text = line.getHighlightedString();
			PC_Vec2I size;
			if(text.isEmpty()){
				size = PC_FontRenderer.getCharSize(' ', PC_GresMultilineHighlightingTextEdit.this.fontTexture, 1.0f);
				size.x = 0;
			}else{
				size = PC_FontRenderer.getStringSize(text, PC_GresMultilineHighlightingTextEdit.this.fontTexture, 1.0f);
			}
			this.docLength += size.y;
			if(size.x>this.maxLineLength){
				this.maxLineLength = size.x;
				this.longestLine = line;
				this.searchNewLengst = false;
			}
			line.renderInfo = new LineInfo(size);
		}
		
	}
	
	private static class LineInfo{
		
		PC_Vec2I size;
		
		LineInfo(PC_Vec2I size) {
			this.size = size;
		}
		
	}
	
	
	
	private static class HistoryEntry implements PC_IGresHistoryEntry{

		private PC_GresMultilineHighlightingTextEdit textEdit;
		private PC_Vec2I startPoint;
		private PC_Vec2I endPoint;
		private String s;
		private boolean newStart;
		private boolean add;
		private boolean mergeable;
		
		public HistoryEntry(PC_GresMultilineHighlightingTextEdit textEdit, PC_Vec2I startPoint, PC_Vec2I endPoint, String s, boolean newStart, boolean add, boolean mergeable) {
			this.textEdit = textEdit;
			this.startPoint = startPoint;
			this.endPoint = endPoint;
			this.s = s;
			this.newStart = newStart;
			this.add = add;
			this.mergeable = mergeable;
		}

		@Override
		public void doAction() {
			this.textEdit.mouseSelectStart = this.startPoint;
			this.textEdit.mouseSelectEnd = this.endPoint;
			String ns = this.textEdit.getSelect();
			this.textEdit.setSelected(this.s, null, true, false);
			this.startPoint = this.textEdit.mouseSelectStart;
			this.endPoint = this.textEdit.mouseSelectEnd;
			this.textEdit.mouseSelectStart = this.textEdit.mouseSelectEnd;
			this.s = ns;
		}

		@Override
		public void undoAction() {
			this.textEdit.mouseSelectStart = this.startPoint;
			this.textEdit.mouseSelectEnd = this.endPoint;
			String ns = this.textEdit.getSelect();
			this.textEdit.setSelected(this.s, null, true, false);
			this.startPoint = this.textEdit.mouseSelectStart;
			this.endPoint = this.textEdit.mouseSelectEnd;
			this.textEdit.mouseSelectStart = this.textEdit.mouseSelectEnd;
			this.s = ns;
		}

		@Override
		public boolean tryToMerge(PC_IGresHistoryEntry historyEntry) {
			if(historyEntry instanceof HistoryEntry){
				HistoryEntry he = (HistoryEntry)historyEntry;
				if(he.textEdit == this.textEdit && !he.newStart && he.add == this.add && this.mergeable && he.mergeable){
					if(this.add){
						if(he.startPoint.equals(this.endPoint)){
							this.endPoint = he.endPoint;
						}else if(he.endPoint.equals(this.startPoint)){
							this.startPoint = he.startPoint;
						}else{
							return false;
						}
					}else{
						if(he.startPoint.y < this.startPoint.y || (he.startPoint.y == this.startPoint.y && he.startPoint.x < this.startPoint.x)){
							this.startPoint = he.startPoint;
							this.endPoint = he.startPoint;
							this.s = he.s + this.s;
						}else{
							this.s += he.s;
						}
					}
					return true;
				}
			}
			return false;
		}
		
	}

	public void setErrors(List<Diagnostic<?>> diagnostics) {
		for(Diagnostic<?> diagnostic:diagnostics){
			long line = diagnostic.getLineNumber()-1;
			String message = diagnostic.getMessage(Locale.US);
			Kind kind = diagnostic.getKind();
			long startPos = diagnostic.getStartPosition();
			long endPos = diagnostic.getEndPosition();
			if(startPos==Diagnostic.NOPOS || endPos==Diagnostic.NOPOS){
				if(line==-1)
					continue;
				int x = this.document.getLine((int)line).getText().length();
				this.document.addError(new PC_Vec2I(0, (int)line), new PC_Vec2I(x, (int)line), kind, message);
			}else{
				this.document.addError(this.document.getPosFrom(startPos), this.document.getPosFrom(endPos), kind, message);
			}
		}
	}
	
	public void removeErrors() {
		this.document.removeErrors();
	}
	
	private PC_Vec2I getMousePositionInStringAsCharPos(PC_Vec2I mouse){
		int y = this.scroll.y;
		PC_GresDocumentLine line = this.document.getLine(y);
		int h = ((LineInfo)line.renderInfo).size.y/this.scale;
		while(h<mouse.y-1){
			line = line.next;
			if(line==null)
				return null;
			y++;
			h += ((LineInfo)line.renderInfo).size.y/this.scale;
		}
		int x = getPositionFromStringAsCharPos(new PC_Vec2I((mouse.x+this.scroll.x-2), y));
		if(x==-1)
			return null;
		return new PC_Vec2I(x, y);
	}
	
	@SuppressWarnings("hiding")
	private int getPositionFromStringAsCharPos(PC_Vec2I pos){
		if(pos.y>=this.document.getLines()){
			pos.y=this.document.getLines()-1;
		}
		PC_GresDocumentLine line = this.document.getLine(pos.y);
		String text = line.getHighlightedString();
		int length = PC_Formatter.removeFormatting(text).length();
		for(int i=1; i<=length; i++){
			int l = PC_FontRenderer.getStringSize(PC_Formatter.substring(text, 0, i), this.fontTexture, 1.0f/this.scale).x;
			if(l>pos.x){
				return i-1;
			}
		}
		return -1;
	}

	@Override
	protected List<String> getTooltip(PC_Vec2I position) {
		PC_Vec2I pos = getMousePositionInStringAsCharPos(position);
		if(pos==null)
			return null;
		PC_GresDocumentLine line = this.document.getLine(pos.y);
		if(line.errors==null)
			return null;
		Message s = line.errors[pos.x];
		if(s==null)
			return null;
		List<String> list = new ArrayList<String>();
		String sl[] = s.getMessage().split("\n");
		for(String ss:sl){
			ss = ss.trim();
			if(!ss.isEmpty()){
				list.add(ss);
			}
		}
		return list;
	}
	
}
