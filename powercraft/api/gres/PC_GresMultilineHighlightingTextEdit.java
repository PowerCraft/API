package powercraft.api.gres;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.doc.PC_GresDocument;
import powercraft.api.gres.doc.PC_GresDocumentLine;
import powercraft.api.gres.doc.PC_GresHighlighting;
import powercraft.api.gres.events.PC_GresMouseWheelEvent;
import powercraft.api.gres.font.PC_FontRenderer;
import powercraft.api.gres.font.PC_FontTexture;
import powercraft.api.gres.font.PC_Fonts;
import powercraft.api.script.miniscript.PC_Miniscript;

@SideOnly(Side.CLIENT)
public class PC_GresMultilineHighlightingTextEdit extends PC_GresComponent {

	private static final float SCALE = 1.0f/4.0f;
	
	protected static final String textureName = "TextEdit";
	protected static final String textureName2 = "TextEditSelect";
	
	private PC_GresDocument document;
	private int firstVisibleLine;
	private PC_GresDocumentLine firstVisible;
	private PC_GresHighlighting highlighting;
	private int maxLineWidth;
	private PC_Vec2I mouseSelectStart = new PC_Vec2I(0,0);
	private PC_Vec2I mouseSelectEnd = new PC_Vec2I(0,0);
	private PC_Vec2I scroll = new PC_Vec2I();
	private int cursorCounter;
	private PC_FontTexture fontTexture;
	
	public PC_GresMultilineHighlightingTextEdit(){
		highlighting = PC_Miniscript.Highlighting.MINISCRIPT;
		document = new PC_GresDocument("", PC_Miniscript.Highlighting.MINISCRIPT);
		fontTexture = PC_Fonts.create(PC_FontRenderer.getFont("Consolas", 0, 24), null);
		firstVisible = document.getLine(0);
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
		
		drawTexture(textureName, 0, 0, rect.width, rect.height);
		
		PC_Vec2I offset = getRealLocation();
		setDrawRect(scissor, new PC_RectI(2+offset.x, 6+offset.y, rect.width - 4, rect.height - 12), scale, displayHeight);
		
		PC_GresDocumentLine line = firstVisible;
		int lineNum = firstVisibleLine;
		int y = 6;
		while(line!=null){
			y += drawLine(lineNum, line, 2, y);
			lineNum++;
			line = line.next;
		}
		
		if(scissor==null){
			setDrawRect(scissor, new PC_RectI(-1, -1, -1, -1), scale, displayHeight);
		}else{
			setDrawRect(scissor, scissor, scale, displayHeight);
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
		setSelected(""+c);
	}

	private void deleteSelected() {
		document.remove(mouseSelectStart, mouseSelectEnd);
		setToStartSelect();
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
				return;
			}
			mouseSelectStart.x = document.getLine(mouseSelectStart.y).getText().length();
		}
		document.remove(mouseSelectStart, mouseSelectEnd);
		setToStartSelect();
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
				return;
			}
			mouseSelectEnd.x = 0;
		}
		document.remove(mouseSelectStart, mouseSelectEnd);
		setToStartSelect();
	}
	
	private String getSelect(){
		return document.getText(mouseSelectStart, mouseSelectEnd);
	}
	
	private void setSelected(String stri) {
		document.remove(mouseSelectStart, mouseSelectEnd);
		setToStartSelect();
		document.add(mouseSelectStart, stri);
		mouseSelectEnd = new PC_Vec2I(mouseSelectStart.x+1, mouseSelectStart.y);
		if(mouseSelectEnd.x>document.getLine(mouseSelectEnd.y).getText().length()){
			mouseSelectEnd.y++;
			if(mouseSelectEnd.y>=document.getLines()){
				mouseSelectEnd = mouseSelectStart;
				return;
			}
			mouseSelectEnd.x = 0;
		}
		mouseSelectStart = mouseSelectEnd;
	}
	
	private void setToStartSelect(){
		if(mouseSelectStart.y>mouseSelectEnd.y || (mouseSelectStart.y==mouseSelectEnd.y && mouseSelectStart.x>mouseSelectEnd.x)){
			mouseSelectStart = mouseSelectEnd;
		}else{
			mouseSelectEnd = mouseSelectStart;
		}
	}
	
	private PC_Vec2I getMousePositionInString(PC_Vec2I mouse){
		int y = (mouse.y+scroll.y) / fontRenderer.FONT_HEIGHT;
		int x = getPositionFromString(new PC_Vec2I((mouse.x+scroll.x), y));
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
			setSelected(GuiScreen.getClipboardString());
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
				return true;
			case Keyboard.KEY_END:
				mouseSelectEnd = mouseSelectStart = document.getLastPos();
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
				return true;
			case Keyboard.KEY_RIGHT:
				PC_Vec2I prev = mouseSelectEnd;
				mouseSelectEnd = new PC_Vec2I(mouseSelectEnd.x+1, mouseSelectEnd.y);
				if(mouseSelectEnd.x>document.getLine(mouseSelectEnd.y).getText().length()){
					mouseSelectEnd.y++;
					if(mouseSelectEnd.y>=document.getLines()){
						mouseSelectEnd = prev;
						return true;
					}
					mouseSelectEnd.x = 0;
				}
				if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard
						.isKeyDown(Keyboard.KEY_LSHIFT))) {
					mouseSelectStart = mouseSelectEnd;
				}
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

	@Override
	protected void handleMouseWheel(PC_GresMouseWheelEvent event) {
		// TODO Auto-generated method stub
		super.handleMouseWheel(event);
	}
	
	@Override
	protected boolean handleMouseMove(PC_Vec2I mouse, int buttons) {
		super.handleMouseMove(mouse, buttons);
		if (mouseDown) {
			mouseSelectEnd = getMousePositionInString(mouse);
			cursorCounter = 0;
		}
		return true;
	}

	@Override
	protected boolean handleMouseButtonDown(PC_Vec2I mouse, int buttons,
			int eventButton) {
		super.handleMouseButtonDown(mouse, buttons, eventButton);
		mouseSelectStart = getMousePositionInString(mouse);
		mouseSelectEnd = mouseSelectStart;
		cursorCounter = 0;
		return true;
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
		firstVisible = document.getLine(0);
	}

	@Override
	public String getText() {
		return document.getWholeText();
	}
	
}
