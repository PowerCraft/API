package powercraft.api.gres;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;

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
	private PC_Vec2I mouseSelectStart = new PC_Vec2I(0,0);
	private PC_Vec2I mouseSelectEnd = new PC_Vec2I(0,0);
	private int vScrollSize = 0, hScrollSize = 0;
	private float vScrollPos = 0, hScrollPos = 0;
	private PC_Vec2I scroll = new PC_Vec2I(0, 0);
	private int cursorCounter;
	private PC_FontTexture fontTexture;
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
		document = new PC_GresDocument(text, highlighting, docHandler = new DocHandler(), autoComplete==null?null:autoComplete.getInfoCollector());
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

	@Override
	protected void paint(PC_RectI scissor, double scale, int displayHeight, float timeStamp) {
		
		if(docHandler.searchNewLengst){
			docHandler.searchNewLengst = false;
			docHandler.maxLineLength = -1;
		}
		
		if(!mouseDown){
			calcScrollPosition();
		}
		
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int d2 = getTextureDefaultSize(scrollVFrame).x;
		
		drawTexture(scrollVFrame, rect.width-d2, 0, d2, rect.height-d1, getStateForBar(1));
		drawTexture(scrollV, rect.width-d2+1, (int)vScrollPos+1, d2-2, vScrollSize-1, getStateForBar(1));
		
		drawTexture(scrollHFrame, 0, rect.height-d1, rect.width-d2, d1, getStateForBar(0));
		drawTexture(scrollH, (int)hScrollPos+1, rect.height-d1+1, hScrollSize-1, d1-2, getStateForBar(0));
		
		drawTexture(textureName, 0, 0, rect.width-d2+1, rect.height-d1+1);
		
		PC_Vec2I offset = getRealLocation();
		setDrawRect(scissor, new PC_RectI(2+offset.x, 2+offset.y, rect.width - 3 - d2, rect.height - 3 - d1), scale, displayHeight);
		
		PC_GresDocumentLine line = document.getLine(scroll.y);
		int lineNum = scroll.y;
		int y = 2;
		while(line!=null && y<rect.height - 2 - d1){
			y += drawLine(lineNum, line, 2-scroll.x, y);
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
		return enabled && parentEnabled ? mouseDown && selectBar==bar ? 2 : mouseOver && overBar==bar ? 1 : 0 : 3;
	}
	
	private void calcScrollPosition() {
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int d2 = getTextureDefaultSize(scrollVFrame).x;
		int sizeX = rect.width - d2;
		int maxSizeX = docHandler.maxLineLength/scale;
		int sizeOutOfFrame = maxSizeX - sizeX + 5;
		if (sizeOutOfFrame < 0) {
			sizeOutOfFrame = 0;
		}
		float prozent = maxSizeX > 0 ? ((float) sizeOutOfFrame / maxSizeX) : 0;
		hScrollPos = (sizeOutOfFrame > 0 ? (float) scroll.x / sizeOutOfFrame : 0) * prozent * sizeX;
		hScrollSize = (int) ((1 - prozent) * sizeX + 0.5);

		int sizeY = rect.height - d1;
		int lines = document.getLines();
		if(lines>1){
			prozent = 1.0f/(lines-1);
			vScrollPos = scroll.y * prozent * sizeY * 0.9f;
			vScrollSize = (int) (0.1f * sizeY + 0.5);
		}else{
			vScrollPos = 0;
			vScrollSize = sizeY;
		}
		updateScrollPosition();
	}
	
	private void updateScrollPosition() {
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int d2 = getTextureDefaultSize(scrollVFrame).x;
		int sizeX = rect.width - d2;
		int maxSizeX = docHandler.maxLineLength/scale;
		int sizeOutOfFrame = maxSizeX - sizeX + 5;
		if (sizeOutOfFrame < 0) {
			sizeOutOfFrame = 0;
		}
		float prozent = maxSizeX > 0 ? ((float) sizeOutOfFrame / (maxSizeX)) : 0;
		if (hScrollPos < 0) {
			hScrollPos = 0;
		}
		if (hScrollPos > sizeX - hScrollSize) {
			hScrollPos = sizeX - hScrollSize;
		}
		scroll.x = (int) (hScrollPos / prozent / sizeX * sizeOutOfFrame + 0.5);

		int sizeY = rect.height - d1;
		int lines = document.getLines();
		if(lines>1){
			prozent = 1.0f / (lines - 1);
			if (vScrollPos < 0) {
				vScrollPos = 0;
			}
			if (vScrollPos > 0.9f * sizeY) {
				vScrollPos = 0.9f * sizeY;
			}
			scroll.y = (int) (vScrollPos / prozent / sizeY / 0.9f + 0.5);
		}else{
			vScrollPos = 0;
			scroll.y = 0;
		}
	}
	
	private PC_Vec2I[] sort(PC_Vec2I start, PC_Vec2I end){
		if(start.y<end.y || (start.y==end.y && start.x<end.x))
			return new PC_Vec2I[]{start, end};
		return new PC_Vec2I[]{end, start};
	}
	
	private int drawLine(int lineNum, PC_GresDocumentLine line, int x, int y){
		PC_Vec2I[] selected = sort(mouseSelectStart, mouseSelectEnd);
		String text = line.getHighlightedString();
		PC_Vec2I size = ((LineInfo)line.renderInfo).size;
		if(size.x>docHandler.maxLineLength){
			docHandler.maxLineLength = size.x;
			docHandler.longestLine = line;
		}
		size = new PC_Vec2I(size);
		size.x /= scale;
		size.y /= scale;
		if(selected[0].y<=lineNum && lineNum<=selected[1].y && (selected[0].y == selected[1].y?selected[0].x != selected[1].x:true)){
			int startX = 0;
			int endX;
			if(selected[0].y == lineNum){
				startX = PC_FontRenderer.getStringSize(PC_Formatter.substring(text, 0, selected[0].x), fontTexture, 1.0f/scale).x;
			}
			if(selected[1].y == lineNum){
				endX = PC_FontRenderer.getStringSize(PC_Formatter.substring(text, 0, selected[1].x), fontTexture, 1.0f/scale).x;
			}else{
				endX = size.x;
			}
			drawTexture(textureName2, startX + x, y, endX-startX, size.y);
		}
		PC_FontRenderer.drawString(text, x, y, fontTexture, 1.0f/scale);
		if (focus && cursorCounter / 6 % 2 == 0 && mouseSelectEnd.y==lineNum) {
			PC_GresRenderer.drawVerticalLine(x+PC_FontRenderer.getStringSize(
					PC_Formatter.substring(text, 0, mouseSelectEnd.x), fontTexture, 1.0f/scale).x, y,
					y + size.y - 1, fontColors[0]|0xff000000);
		}
		return size.y;
	}
	
	protected void addKey(char c, PC_GresHistory history) {
		String removed = "";
		if(!mouseSelectStart.equals(mouseSelectEnd)){
			removed = document.getText(mouseSelectStart, mouseSelectEnd);
			document.remove(mouseSelectStart, mouseSelectEnd);
		}
		setToStartSelect();
		PC_StringAdd addStruct = new PC_StringAdd();
		addStruct.component = this;
		addStruct.toAdd = ""+c;
		addStruct.cursorPos = -1;
		addStruct.document = document;
		addStruct.documentLine = document.getLine(mouseSelectStart.y);
		addStruct.pos = mouseSelectStart.x;
		if(autoAdd!=null){
			autoAdd.onCharAdded(addStruct);
		}
		setSelected(addStruct.toAdd, null, false, false);
		if(history!=null){
			history.addHistoryEntry(new HistoryEntry(this, mouseSelectStart, mouseSelectEnd, removed, false, true, true));
			newStart = false;
		}
		if(addStruct.cursorPos!=-1){
			mouseSelectEnd = new PC_Vec2I(mouseSelectStart.x+addStruct.cursorPos, mouseSelectStart.y);
			while(mouseSelectEnd.x>document.getLine(mouseSelectEnd.y).getText().length()){
				mouseSelectEnd.x -= document.getLine(mouseSelectEnd.y).getText().length()+1;
				mouseSelectEnd.y++;
				if(mouseSelectEnd.y>=document.getLines()){
					mouseSelectEnd = mouseSelectStart;
					moveViewToSelect();
					break;
				}
			}
		}
		mouseSelectStart = mouseSelectEnd;
		moveViewToSelect();
		if(autoComplete!=null){
			autoComplete.onStringAdded(this, document, document.getLine(mouseSelectStart.y), addStruct.toAdd, mouseSelectEnd.x, disp);
			showCompleteWindow(history);
		}
	}

	private void showCompleteWindow(PC_GresHistory history){
		PC_GresAutoCompleteWindow.makeCompleteWindow(getGuiHandler(), this, disp, history);
	}
	
	private void deleteSelected(PC_GresHistory history) {
		String removed = document.getText(mouseSelectStart, mouseSelectEnd);
		document.remove(mouseSelectStart, mouseSelectEnd);
		setToStartSelect();
		if(history!=null){
			history.addHistoryEntry(new HistoryEntry(this, mouseSelectStart, mouseSelectStart, removed, true, false, false));
			newStart = false;
		}
		moveViewToSelect();
	}

	private void key_backspace(PC_GresHistory history) {
		if (mouseSelectStart != mouseSelectEnd) {
			deleteSelected(history);
			return;
		}
		mouseSelectStart = new PC_Vec2I(mouseSelectStart.x-1, mouseSelectStart.y);
		if(mouseSelectStart.x<0){
			mouseSelectStart.y--;
			if(mouseSelectStart.y<0){
				mouseSelectStart = mouseSelectEnd;
				moveViewToSelect();
				return;
			}
			mouseSelectStart.x = document.getLine(mouseSelectStart.y).getText().length();
		}
		String removed = document.getText(mouseSelectStart, mouseSelectEnd);
		document.remove(mouseSelectStart, mouseSelectEnd);
		setToStartSelect();
		if(history!=null){
			history.addHistoryEntry(new HistoryEntry(this, mouseSelectStart, mouseSelectStart, removed, newStart, false, true));
			newStart = false;
		}
		moveViewToSelect();
	}

	private void key_delete(PC_GresHistory history) {
		if (mouseSelectStart != mouseSelectEnd) {
			deleteSelected(history);
			return;
		}
		mouseSelectEnd = new PC_Vec2I(mouseSelectStart.x+1, mouseSelectStart.y);
		if(mouseSelectEnd.x>document.getLine(mouseSelectEnd.y).getText().length()){
			mouseSelectEnd.y++;
			if(mouseSelectEnd.y>=document.getLines()){
				mouseSelectEnd = mouseSelectStart;
				moveViewToSelect();
				return;
			}
			mouseSelectEnd.x = 0;
		}
		String removed = document.getText(mouseSelectStart, mouseSelectEnd);
		document.remove(mouseSelectStart, mouseSelectEnd);
		setToStartSelect();
		if(history!=null){
			history.addHistoryEntry(new HistoryEntry(this, mouseSelectStart, mouseSelectStart, removed, newStart, false, true));
			newStart = false;
		}
		moveViewToSelect();
	}
	
	private String getSelect(){
		return document.getText(mouseSelectStart, mouseSelectEnd);
	}
	
	private void setSelected(String stri, PC_GresHistory history, boolean block, boolean startToEnd) {
		String removed = "";
		if(!mouseSelectStart.equals(mouseSelectEnd)){
			removed = document.getText(mouseSelectStart, mouseSelectEnd);
			document.remove(mouseSelectStart, mouseSelectEnd);
		}
		setToStartSelect();
		document.add(mouseSelectStart, stri);
		mouseSelectEnd = new PC_Vec2I(mouseSelectStart.x+stri.length(), mouseSelectStart.y);
		while(mouseSelectEnd.x>document.getLine(mouseSelectEnd.y).getText().length()){
			mouseSelectEnd.x -= document.getLine(mouseSelectEnd.y).getText().length()+1;
			mouseSelectEnd.y++;
			if(mouseSelectEnd.y>=document.getLines()){
				mouseSelectEnd = mouseSelectStart;
				break;
			}
		}
		if(history!=null){
			history.addHistoryEntry(new HistoryEntry(this, mouseSelectStart, mouseSelectEnd, removed, block || newStart, true, !block));
			newStart = false;
		}
		if(startToEnd){
			mouseSelectStart = mouseSelectEnd;
			moveViewToSelect();
		}
	}
	
	private void setToStartSelect(){
		if(mouseSelectStart.y>mouseSelectEnd.y || (mouseSelectStart.y==mouseSelectEnd.y && mouseSelectStart.x>mouseSelectEnd.x)){
			mouseSelectStart = mouseSelectEnd;
		}else{
			mouseSelectEnd = mouseSelectStart;
		}
	}
	
	private PC_Vec2I getMousePositionInString(PC_Vec2I mouse){
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int d2 = getTextureDefaultSize(scrollVFrame).x;
		PC_Vec2I oldMouse = mouse;
		mouse = new PC_Vec2I(mouse);
		if(mouse.x<2){
			mouse.x = 2;
			scroll.x-=4;
		}else if(mouse.x>rect.width-d2-4){
			mouse.x = rect.width-d2-4;
			scroll.x+=4;
		}
		if(mouse.y<2){
			mouse.y = 2;
			scroll.y--;
		}else if(mouse.y>rect.height-d1-4){
			mouse.y = rect.height-d1-4;
			scroll.y++;
		}
		if(!mouse.equals(oldMouse)){
			calcScrollPosition();
		}
		int y = scroll.y;
		PC_GresDocumentLine line = document.getLine(y);
		int h = ((LineInfo)line.renderInfo).size.y/scale;
		while(h<mouse.y-1){
			line = line.next;
			if(line==null)
				break;
			y++;
			h += ((LineInfo)line.renderInfo).size.y/scale;
		}
		int x = getPositionFromString(new PC_Vec2I((mouse.x+scroll.x-2), y));
		return new PC_Vec2I(x, y);
	}
	
	private int getPixelPositionFromString(PC_Vec2I pos){
		PC_GresDocumentLine line = document.getLine(pos.y);
		return PC_FontRenderer.getStringSize(PC_Formatter.substring(line.getHighlightedString(), 0, pos.x), fontTexture, 1.0f/scale).x;
	}
	
	private int getPositionFromString(PC_Vec2I pos){
		if(pos.y>=document.getLines()){
			pos.y=document.getLines()-1;
		}
		PC_GresDocumentLine line = document.getLine(pos.y);
		String text = line.getHighlightedString();
		int length = PC_Formatter.removeFormatting(text).length();
		int last = 0;
		for(int i=1; i<=length; i++){
			int l = PC_FontRenderer.getStringSize(PC_Formatter.substring(text, 0, i), fontTexture, 1.0f/scale).x;
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
		PC_GresDocumentLine line = document.getLine(scroll.y);
		for(int i=scroll.y; i<=mouseSelectEnd.y; i++){
			y += ((LineInfo)line.renderInfo).size.y;
			line = line.next;
		}
		return new PC_Vec2I(getPixelPositionFromString(mouseSelectEnd), 2+y/scale);
	}
	
	public void autoComplete(String with, PC_GresHistory history){
		setSelected(with, history, false, true);
		if(with.endsWith(".") && autoComplete!=null){
			autoComplete.onStringAdded(this, document, document.getLine(mouseSelectStart.y), with, mouseSelectEnd.x, disp);
			showCompleteWindow(history);
		}
	}
	
	@Override
	protected boolean handleKeyTyped(char key, int keyCode, PC_GresHistory history) {
		super.handleKeyTyped(key, keyCode, history);
		cursorCounter = 0;
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
				mouseSelectEnd = mouseSelectStart = new PC_Vec2I();
				newStart = true;
				moveViewToSelect();
				return true;
			case Keyboard.KEY_END:
				mouseSelectEnd = mouseSelectStart = document.getLastPos();
				newStart = true;
				moveViewToSelect();
				return true;
			case Keyboard.KEY_DELETE:
				key_delete(history);
				return true;
			case Keyboard.KEY_LEFT:
				if (mouseSelectEnd.y > 0 || (mouseSelectEnd.y == 0 && mouseSelectEnd.x > 0)) {
					mouseSelectEnd = new PC_Vec2I(mouseSelectEnd.x-1, mouseSelectEnd.y);
					if(mouseSelectEnd.x<0){
						mouseSelectEnd.y--;
						mouseSelectEnd.x = document.getLine(mouseSelectEnd.y).getText().length();
					}
					if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard
							.isKeyDown(Keyboard.KEY_LSHIFT))) {
						mouseSelectStart = mouseSelectEnd;
					}

				}
				newStart = true;
				moveViewToSelect();
				return true;
			case Keyboard.KEY_RIGHT:
				PC_Vec2I prev = mouseSelectEnd;
				mouseSelectEnd = new PC_Vec2I(mouseSelectEnd.x+1, mouseSelectEnd.y);
				if(mouseSelectEnd.x>document.getLine(mouseSelectEnd.y).getText().length()){
					mouseSelectEnd.y++;
					if(mouseSelectEnd.y>=document.getLines()){
						mouseSelectEnd = prev;
						moveViewToSelect();
						return true;
					}
					mouseSelectEnd.x = 0;
				}
				if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard
						.isKeyDown(Keyboard.KEY_LSHIFT))) {
					mouseSelectStart = mouseSelectEnd;
				}
				newStart = true;
				moveViewToSelect();
				return true;
			case Keyboard.KEY_UP:
				if(mouseSelectEnd.y>0){
					int pos = getPixelPositionFromString(mouseSelectEnd);
					mouseSelectEnd = new PC_Vec2I(mouseSelectEnd.x, mouseSelectEnd.y-1);
					mouseSelectEnd.x = getPositionFromString(new PC_Vec2I(pos, mouseSelectEnd.y));
					if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard
							.isKeyDown(Keyboard.KEY_LSHIFT))) {
						mouseSelectStart = mouseSelectEnd;
					}
				}
				newStart = true;
				moveViewToSelect();
				return true;
			case Keyboard.KEY_DOWN:
				if(mouseSelectEnd.y<document.getLines()-1){
					int pos = getPixelPositionFromString(mouseSelectEnd);
					mouseSelectEnd = new PC_Vec2I(mouseSelectEnd.x, mouseSelectEnd.y+1);
					mouseSelectEnd.x = getPositionFromString(new PC_Vec2I(pos, mouseSelectEnd.y));
					if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard
							.isKeyDown(Keyboard.KEY_LSHIFT))) {
						mouseSelectStart = mouseSelectEnd;
					}
				}
				newStart = true;
				moveViewToSelect();
				return true;
			case Keyboard.KEY_SPACE:
				if(Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
					if(autoComplete!=null){
						autoComplete.makeComplete(this, document, document.getLine(mouseSelectEnd.y), mouseSelectEnd.x, disp);
						showCompleteWindow(history);
					}
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

	private void moveViewToSelect(){
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int d2 = getTextureDefaultSize(scrollVFrame).x;
		int shouldY = mouseSelectEnd.y;
		PC_GresDocumentLine line = document.getLine(shouldY);
		String text = line.getHighlightedString();
		int x = ((LineInfo)line.renderInfo).size.x;
		if(x>docHandler.maxLineLength){
			docHandler.maxLineLength = x;
			docHandler.longestLine = line;
		}
		int shouldX = PC_FontRenderer.getStringSize(PC_Formatter.substring(document.getLine(mouseSelectEnd.y).getHighlightedString(), 0, mouseSelectEnd.x), fontTexture, 1.0f/scale).x;
		if(scroll.x>shouldX){
			scroll.x = shouldX;
		}else{
			int i=1;
			if(PC_FontRenderer.getStringSize(PC_Formatter.substring(text, 0, mouseSelectEnd.x), fontTexture, 1.0f/scale).x>scroll.x + rect.width-d2-4){
				while(PC_FontRenderer.getStringSize(PC_Formatter.substring(text, mouseSelectEnd.x-i, mouseSelectEnd.x), fontTexture, 1.0f/scale).x<rect.width-d2-4){
					i++;
					if(i>mouseSelectEnd.x){
						break;
					}
				}
				scroll.x = PC_FontRenderer.getStringSize(PC_Formatter.substring(text, 0, mouseSelectEnd.x-i+1), fontTexture, 1.0f/scale).x;
			}
		}
		if(scroll.y>shouldY){
			scroll.y = shouldY;
		}else{
			int h = ((LineInfo)line.renderInfo).size.y/scale;
			while(h<rect.height-d1-2){
				line = line.prev;
				shouldY--;
				if(line==null)
					break;
				h += ((LineInfo)line.renderInfo).size.y/scale;
			}
			shouldY++;
			if(scroll.y<shouldY)
				scroll.y = shouldY;
		}
		calcScrollPosition();
	}
	
	@Override
	protected void handleMouseWheel(PC_GresMouseWheelEvent event, PC_GresHistory history) {
		scroll.y -= event.getWheel();
		calcScrollPosition();
		event.consume();
	}
	
	@Override
	protected boolean handleMouseMove(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		super.handleMouseMove(mouse, buttons, history);
		if(mouseDown){
			if(selectBar==0){
				hScrollPos += mouse.x - lastMousePosition.x;
				updateScrollPosition();
				lastMousePosition.setTo(mouse);
			}else if(selectBar==1){
				vScrollPos += mouse.y - lastMousePosition.y;
				updateScrollPosition();
				lastMousePosition.setTo(mouse);
			}else{
				newStart = true;
				mouseSelectEnd = getMousePositionInString(mouse);
				cursorCounter = 0;
			}
		}
		if(mouseOver){
			overBar = mouseOverBar(mouse);
		}
		return true;
	}

	@Override
	protected boolean handleMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton, PC_GresHistory history) {
		super.handleMouseButtonDown(mouse, buttons, eventButton, history);
		if(mouseDown){
			lastMousePosition.setTo(mouse);
			selectBar = mouseOverBar(mouse);
		}
		if(mouseOver){
			overBar = mouseOverBar(mouse);
		}
		if(selectBar!=-1 || overBar!=-1)
			return true;
		newStart = true;
		mouseSelectEnd = getMousePositionInString(mouse);
		if(!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)))
			mouseSelectStart = mouseSelectEnd;
		cursorCounter = 0;
		return true;
	}

	@Override
	protected void handleMouseLeave(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		mouseOver = false;
	}
	
	private int mouseOverBar(PC_Vec2I mouse){
		int d1 = getTextureDefaultSize(scrollHFrame).y;
		int d2 = getTextureDefaultSize(scrollVFrame).x;
		if(new PC_RectI(rect.width-d2+1, (int)vScrollPos+1, d2-2, vScrollSize-1).contains(mouse)){
			return 1;
		}
		if(new PC_RectI((int)hScrollPos+1, rect.height-d1+1, hScrollSize-1, d1-2).contains(mouse)){
			return 0;
		}
		return -1;
	}
	
	@Override
	protected void onTick() {
		if (focus) {
			cursorCounter++;
		} else {
			cursorCounter = 0;
		}
	}
	
	@Override
	public void setText(String text) {
		document = new PC_GresDocument(text, highlighting, docHandler = new DocHandler(), autoComplete==null?null:autoComplete.getInfoCollector());
	}

	@Override
	public String getText() {
		return document.getWholeText();
	}
	
	@Override
	protected void onScaleChanged(int newScale) {
		scale = newScale;
	}

	private class DocHandler implements PC_GresDocRenderHandler{

		private PC_GresDocumentLine longestLine;
		private int maxLineLength = -1;
		private boolean searchNewLengst = false;
		private int docLength;
		
		@Override
		public void onLineChange(PC_GresDocumentLine line) {
			if(longestLine==line){
				longestLine = null;
				searchNewLengst = true;
			}
			PC_Vec2I size = ((LineInfo)line.renderInfo).size;
			docLength -= size.y;
		}

		@Override
		public void onLineChanged(PC_GresDocumentLine line) {
			String text = line.getHighlightedString();
			PC_Vec2I size;
			if(text.isEmpty()){
				size = PC_FontRenderer.getCharSize(' ', fontTexture, 1.0f);
				size.x = 0;
			}else{
				size = PC_FontRenderer.getStringSize(text, fontTexture, 1.0f);
			}
			docLength += size.y;
			if(size.x>maxLineLength){
				maxLineLength = size.x;
				longestLine = line;
				searchNewLengst = false;
			}
			LineInfo li = new LineInfo();
			li.size = size;
			line.renderInfo = li;
		}
		
	}
	
	private static class LineInfo{
		
		private PC_Vec2I size;
		
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
			textEdit.mouseSelectStart = startPoint;
			textEdit.mouseSelectEnd = endPoint;
			String ns = textEdit.getSelect();
			textEdit.setSelected(s, null, true, false);
			startPoint = textEdit.mouseSelectStart;
			endPoint = textEdit.mouseSelectEnd;
			textEdit.mouseSelectStart = textEdit.mouseSelectEnd;
			s = ns;
		}

		@Override
		public void undoAction() {
			textEdit.mouseSelectStart = startPoint;
			textEdit.mouseSelectEnd = endPoint;
			String ns = textEdit.getSelect();
			textEdit.setSelected(s, null, true, false);
			startPoint = textEdit.mouseSelectStart;
			endPoint = textEdit.mouseSelectEnd;
			textEdit.mouseSelectStart = textEdit.mouseSelectEnd;
			s = ns;
		}

		@Override
		public boolean tryToMerge(PC_IGresHistoryEntry historyEntry) {
			if(historyEntry instanceof HistoryEntry){
				HistoryEntry he = (HistoryEntry)historyEntry;
				if(he.textEdit == textEdit && !he.newStart && he.add == add && mergeable && he.mergeable){
					if(add){
						if(he.startPoint.equals(endPoint)){
							endPoint = he.endPoint;
						}else if(he.endPoint.equals(startPoint)){
							startPoint = he.startPoint;
						}else{
							return false;
						}
					}else{
						if(he.startPoint.y < startPoint.y || (he.startPoint.y == startPoint.y && he.startPoint.x < startPoint.x)){
							startPoint = he.startPoint;
							endPoint = he.startPoint;
							s = he.s + s;
						}else{
							s += he.s;
						}
					}
					return true;
				}
			}
			return false;
		}
		
	}

	public void setErrors(List<Diagnostic<? extends Void>> diagnostics) {
		for(Diagnostic<? extends Void> diagnostic:diagnostics){
			long line = diagnostic.getLineNumber()-1;
			String message = diagnostic.getMessage(Locale.US);
			long startPos = diagnostic.getStartPosition();
			long endPos = diagnostic.getEndPosition();
			if(startPos==Diagnostic.NOPOS || endPos==Diagnostic.NOPOS){
				int x = document.getLine((int)line).getText().length();
				document.addError(new PC_Vec2I(0, (int)line), new PC_Vec2I(x, (int)line), message);
			}else{
				document.addError(document.getPosFrom(startPos), document.getPosFrom(endPos), message);
			}
		}
	}
	
	public void removeErrors() {
		document.removeErrors();
	}
	
	private PC_Vec2I getMousePositionInStringAsCharPos(PC_Vec2I mouse){
		int y = scroll.y;
		PC_GresDocumentLine line = document.getLine(y);
		int h = ((LineInfo)line.renderInfo).size.y/scale;
		while(h<mouse.y-1){
			line = line.next;
			if(line==null)
				return null;
			y++;
			h += ((LineInfo)line.renderInfo).size.y/scale;
		}
		int x = getPositionFromStringAsCharPos(new PC_Vec2I((mouse.x+scroll.x-2), y));
		if(x==-1)
			return null;
		return new PC_Vec2I(x, y);
	}
	
	private int getPositionFromStringAsCharPos(PC_Vec2I pos){
		if(pos.y>=document.getLines()){
			pos.y=document.getLines()-1;
		}
		PC_GresDocumentLine line = document.getLine(pos.y);
		String text = line.getHighlightedString();
		int length = PC_Formatter.removeFormatting(text).length();
		for(int i=1; i<=length; i++){
			int l = PC_FontRenderer.getStringSize(PC_Formatter.substring(text, 0, i), fontTexture, 1.0f/scale).x;
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
		PC_GresDocumentLine line = document.getLine(pos.y);
		if(line.errors==null)
			return null;
		String s = line.errors[pos.x];
		if(s==null)
			return null;
		List<String> list = new ArrayList<String>();
		String sl[] = s.split("\n");
		for(String ss:sl){
			ss = ss.trim();
			if(!ss.isEmpty()){
				list.add(ss);
			}
		}
		return list;
	}
	
}
