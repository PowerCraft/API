package powercraft.api.gres;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;

import org.lwjgl.input.Keyboard;

import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.autoadd.PC_AutoAdd;
import powercraft.api.gres.autoadd.PC_AutoHint;
import powercraft.api.gres.autoadd.PC_StringAdd;
import powercraft.api.gres.doc.PC_GresDocument;
import powercraft.api.gres.doc.PC_GresDocumentLine;
import powercraft.api.gres.doc.PC_GresHighlighting;
import powercraft.api.gres.events.PC_GresMouseWheelEvent;
import powercraft.api.gres.font.PC_FontRenderer;
import powercraft.api.gres.font.PC_FontTexture;
import powercraft.api.gres.font.PC_Fonts;
import powercraft.api.script.miniscript.PC_Miniscript;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_GresMultilineHighlightingTextEdit extends PC_GresComponent {

	private static final String scrollH = "ScrollH", scrollHFrame = "ScrollHFrame", scrollV = "ScrollV", scrollVFrame = "ScrollVFrame";
	
	private static final float SCALE = 1.0f/4.0f;
	
	protected static final String textureName = "TextEdit";
	protected static final String textureName2 = "TextEditSelect";
	
	private PC_GresDocument document;
	private PC_GresHighlighting highlighting;
	private int maxLineWidth;
	private PC_Vec2I mouseSelectStart = new PC_Vec2I(0,0);
	private PC_Vec2I mouseSelectEnd = new PC_Vec2I(0,0);
	private int vScrollSize = 0, hScrollSize = 0;
	private float vScrollPos = 0, hScrollPos = 0;
	private PC_Vec2I scroll = new PC_Vec2I(0, 0);
	private int cursorCounter;
	private PC_FontTexture fontTexture;
	private PC_AutoAdd autoAdd;
	private PC_AutoHint autoHint;
	
	private static PC_Vec2I lastMousePosition = new PC_Vec2I(0, 0);
	private static int overBar=-1;
	private static int selectBar=-1;
	
	public PC_GresMultilineHighlightingTextEdit(){
		highlighting = PC_Miniscript.Highlighting.MINISCRIPT;
		autoAdd = PC_Miniscript.Highlighting.MINISCRIPT_AUTOADD;
		document = new PC_GresDocument("", PC_Miniscript.Highlighting.MINISCRIPT);
		fontTexture = PC_Fonts.create(PC_FontRenderer.getFont("Consolas", 0, 24), null);
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
		int maxSizeX = maxLineWidth;
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
		int maxSizeX = maxLineWidth;
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
		String text = line.getText();
		PC_Vec2I size = PC_FontRenderer.getStringSize(text.isEmpty()?" ":text, fontTexture, SCALE);
		if(size.x>maxLineWidth)
			maxLineWidth = size.x;
		if(selected[0].y<=lineNum && lineNum<=selected[1].y && (selected[0].y == selected[1].y?selected[0].x != selected[1].x:true)){
			int startX = 0;
			int endX;
			if(selected[0].y == lineNum){
				startX = PC_FontRenderer.getStringSize(text.substring(0, selected[0].x), fontTexture, SCALE).x;
			}
			if(selected[1].y == lineNum){
				endX = PC_FontRenderer.getStringSize(text.substring(0, selected[1].x), fontTexture, SCALE).x;
			}else{
				endX = size.x;
			}
			drawTexture(textureName2, startX + x, y, endX-startX, size.y);
		}
		PC_FontRenderer.drawString(line.getHighlightedString(), x, y, fontTexture, SCALE);
		if (focus && cursorCounter / 6 % 2 == 0 && mouseSelectEnd.y==lineNum) {
			PC_GresRenderer.drawVerticalLine(x+PC_FontRenderer.getStringSize(text
					.substring(0, mouseSelectEnd.x), fontTexture, SCALE).x, y,
					y + size.y - 1, fontColors[0]|0xff000000);
		}
		return size.y;
	}
	
	protected void addKey(char c) {
		if(!mouseSelectStart.equals(mouseSelectEnd)){
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
		setSelected(addStruct.toAdd, addStruct.cursorPos);
	}

	private void deleteSelected() {
		document.remove(mouseSelectStart, mouseSelectEnd);
		setToStartSelect();
		moveViewToSelect();
	}

	private void key_backspace() {
		if (mouseSelectStart != mouseSelectEnd) {
			deleteSelected();
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
		document.remove(mouseSelectStart, mouseSelectEnd);
		setToStartSelect();
		moveViewToSelect();
	}

	private void key_delete() {
		if (mouseSelectStart != mouseSelectEnd) {
			deleteSelected();
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
		document.remove(mouseSelectStart, mouseSelectEnd);
		setToStartSelect();
		moveViewToSelect();
	}
	
	private String getSelect(){
		return document.getText(mouseSelectStart, mouseSelectEnd);
	}
	
	private void setSelected(String stri, int pos) {
		if(!mouseSelectStart.equals(mouseSelectEnd)){
			document.remove(mouseSelectStart, mouseSelectEnd);
		}
		setToStartSelect();
		document.add(mouseSelectStart, stri);
		mouseSelectEnd = new PC_Vec2I(mouseSelectStart.x+(pos==-1?stri.length():pos), mouseSelectStart.y);
		while(mouseSelectEnd.x>document.getLine(mouseSelectEnd.y).getText().length()){
			mouseSelectEnd.x -= document.getLine(mouseSelectEnd.y).getText().length()+1;
			mouseSelectEnd.y++;
			if(mouseSelectEnd.y>=document.getLines()){
				mouseSelectEnd = mouseSelectStart;
				moveViewToSelect();
				return;
			}
		}
		mouseSelectStart = mouseSelectEnd;
		moveViewToSelect();
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
		int h = PC_FontRenderer.getStringSize(line.getHighlightedString(), fontTexture, SCALE).y;
		while(h<mouse.y-1){
			line = line.next;
			if(line==null)
				break;
			y++;
			h += PC_FontRenderer.getStringSize(line.getHighlightedString(), fontTexture, SCALE).y;
		}
		int x = getPositionFromString(new PC_Vec2I((mouse.x+scroll.x-2), y));
		return new PC_Vec2I(x, y);
	}
	
	private int getPixelPositionFromString(PC_Vec2I pos){
		PC_GresDocumentLine line = document.getLine(pos.y);
		return PC_FontRenderer.getStringSize(line.getText().substring(0, pos.x), fontTexture, SCALE).x;
	}
	
	private int getPositionFromString(PC_Vec2I pos){
		if(pos.y>=document.getLines()){
			pos.y=document.getLines()-1;
		}
		PC_GresDocumentLine line = document.getLine(pos.y);
		String text = line.getText();
		int last = 0;
		for(int i=1; i<=text.length(); i++){
			int l = PC_FontRenderer.getStringSize(text.substring(0, i), fontTexture, SCALE).x;
			if(l>pos.x){
				if((l+last)/2<pos.x){
					return i;
				}
				return i-1;
			}
			last = l;
		}
		return text.length();
	}
	
	@Override
	protected boolean handleKeyTyped(char key, int keyCode) {
		super.handleKeyTyped(key, keyCode);
		cursorCounter = 0;
		switch (key) {
		case 3:
			GuiScreen.setClipboardString(getSelect());
			break;
		case 22:
			setSelected(GuiScreen.getClipboardString(), -1);
			break;
		case 24:
			GuiScreen.setClipboardString(getSelect());
			deleteSelected();
			break;
		default:
			switch (keyCode) {
			case Keyboard.KEY_RETURN:
				addKey('\n');
				return true;
			case Keyboard.KEY_BACK:
				key_backspace();
				return true;
			case Keyboard.KEY_HOME:
				mouseSelectEnd = mouseSelectStart = new PC_Vec2I();
				moveViewToSelect();
				return true;
			case Keyboard.KEY_END:
				mouseSelectEnd = mouseSelectStart = document.getLastPos();
				moveViewToSelect();
				return true;
			case Keyboard.KEY_DELETE:
				key_delete();
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
				moveViewToSelect();
				return true;
			default:
				if (ChatAllowedCharacters.isAllowedCharacter(key)) {
					addKey(key);
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
		int x = PC_FontRenderer.getStringSize(text.isEmpty()?" ":text, fontTexture, SCALE).x;
		if(x>maxLineWidth)
			maxLineWidth = x;
		int shouldX = PC_FontRenderer.getStringSize(document.getLine(mouseSelectEnd.y).getText().substring(0, mouseSelectEnd.x), fontTexture, SCALE).x;
		if(scroll.x>shouldX){
			scroll.x = shouldX;
		}else{
			int i=1;
			text = line.getText();
			if(PC_FontRenderer.getStringSize(text.substring(0, mouseSelectEnd.x), fontTexture, SCALE).x>scroll.x + rect.width-d2-4){
				while(PC_FontRenderer.getStringSize(text.substring(mouseSelectEnd.x-i, mouseSelectEnd.x), fontTexture, SCALE).x<rect.width-d2-4){
					i++;
					if(i>mouseSelectEnd.x){
						break;
					}
				}
				scroll.x = PC_FontRenderer.getStringSize(text.substring(0, mouseSelectEnd.x-i+1), fontTexture, SCALE).x;
			}
		}
		if(scroll.y>shouldY){
			scroll.y = shouldY;
		}else{
			text = line.getHighlightedString();
			int h = PC_FontRenderer.getStringSize(text.isEmpty()?" ":text, fontTexture, SCALE).y;
			while(h<rect.height-d1-2){
				line = line.prev;
				shouldY--;
				if(line==null)
					break;
				text = line.getHighlightedString();
				h += PC_FontRenderer.getStringSize(text.isEmpty()?" ":text, fontTexture, SCALE).y;
			}
			shouldY++;
			if(scroll.y<shouldY)
				scroll.y = shouldY;
		}
		calcScrollPosition();
	}
	
	@Override
	protected void handleMouseWheel(PC_GresMouseWheelEvent event) {
		scroll.y -= event.getWheel();
		calcScrollPosition();
		event.consume();
	}
	
	@Override
	protected boolean handleMouseMove(PC_Vec2I mouse, int buttons) {
		super.handleMouseMove(mouse, buttons);
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
	protected boolean handleMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton) {
		super.handleMouseButtonDown(mouse, buttons, eventButton);
		if(mouseDown){
			lastMousePosition.setTo(mouse);
			selectBar = mouseOverBar(mouse);
		}
		if(mouseOver){
			overBar = mouseOverBar(mouse);
		}
		if(selectBar!=-1 || overBar!=-1)
			return true;
		mouseSelectEnd = getMousePositionInString(mouse);
		if(!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)))
			mouseSelectStart = mouseSelectEnd;
		cursorCounter = 0;
		return true;
	}

	@Override
	protected void handleMouseLeave(PC_Vec2I mouse, int buttons) {
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
		document = new PC_GresDocument(text, highlighting);
	}

	@Override
	public String getText() {
		return document.getWholeText();
	}
	
}
