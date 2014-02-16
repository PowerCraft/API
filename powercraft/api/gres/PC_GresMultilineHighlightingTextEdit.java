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

@SideOnly(Side.CLIENT)
public class PC_GresMultilineHighlightingTextEdit extends PC_GresComponent {

	protected static final String textureName = "TextEdit";
	protected static final String textureName2 = "TextEditSelect";
	
	private PC_GresDocument document;
	private int firstVisibleLine;
	private PC_GresDocumentLine firstVisible;
	private PC_GresHighlighting highlighting;
	private int maxLineWidth;
	private PC_Vec2I mouseSelectStart;
	private PC_Vec2I mouseSelectEnd;
	private PC_Vec2I scroll;
	private int cursorCounter;
	
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
		boolean oldFlag = fontRenderer.getUnicodeFlag();
		fontRenderer.setUnicodeFlag(true);
		
		drawTexture(textureName, 0, 0, rect.width, rect.height);
		
		PC_Vec2I offset = getRealLocation();
		setDrawRect(scissor, new PC_RectI(2+offset.x, 6+offset.y, rect.width - 4, rect.height - 12), scale, displayHeight);
		
		PC_GresDocumentLine line = firstVisible;
		int lineNum = firstVisibleLine;
		int y = 6;
		while(line!=null){
			drawLine(lineNum, line, 0, y);
			lineNum++;
			line = line.next;
		}
		
		
		if(scissor==null){
			setDrawRect(scissor, new PC_RectI(-1, -1, -1, -1), scale, displayHeight);
		}else{
			setDrawRect(scissor, scissor, scale, displayHeight);
		}
		
		fontRenderer.setUnicodeFlag(oldFlag);
	}

	private PC_Vec2I[] sort(PC_Vec2I start, PC_Vec2I end){
		if(start.y<end.y || (start.y==end.y && start.x<end.x))
			return new PC_Vec2I[]{start, end};
		return new PC_Vec2I[]{end, start};
	}
	
	private void drawLine(int lineNum, PC_GresDocumentLine line, int x, int y){
		PC_Vec2I[] selected = sort(mouseSelectStart, mouseSelectEnd);
		String text = line.getText();
		if(selected[0].y<=lineNum && lineNum<=selected[1].y){
			int startX = 0;
			int endX;
			if(selected[0].y == lineNum){
				startX = fontRenderer.getStringWidth(text.substring(selected[0].x));
			}
			if(selected[1].y == lineNum){
				endX = fontRenderer.getStringWidth(text.substring(selected[1].x));
			}else{
				endX = fontRenderer.getStringWidth(text);
			}
			drawTexture(textureName2, startX + x + 2, 1, endX-startX, rect.height+1);
		}
		drawString(line.getHighlightedString(), x, y, false);
		if (focus && cursorCounter / 6 % 2 == 0 && mouseSelectEnd.y==lineNum) {
			PC_GresRenderer.drawVerticalLine(fontRenderer.getStringWidth(text
					.substring(0, mouseSelectEnd.x)) + 2, 6,
					6 + fontRenderer.FONT_HEIGHT, fontColors[0]|0xff000000);
		}
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
			if(mouseSelectStart.y<1){
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
			if(mouseSelectEnd.y>document.getLines()){
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
		return fontRenderer.getStringWidth(line.getText().substring(0, pos.x));
	}
	
	private int getPositionFromString(PC_Vec2I pos){
		PC_GresDocumentLine line = document.getLine(pos.y);
		String text = line.getText();
		int last = 0;
		boolean oldFlag = fontRenderer.getUnicodeFlag();
		fontRenderer.setUnicodeFlag(true);
		for(int i=1; i<text.length(); i++){
			int l = fontRenderer.getStringWidth(text.substring(0, i));
			if(l<pos.x){
				fontRenderer.setUnicodeFlag(oldFlag);
				if((l+last)/2<pos.x){
					return last;
				}
				return l;
			}
			last = l;
		}
		fontRenderer.setUnicodeFlag(oldFlag);
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
					mouseSelectEnd.x--;
					if(mouseSelectEnd.x<0){
						mouseSelectEnd.y--;
						mouseSelectStart.x = document.getLine(mouseSelectStart.y).getText().length();
					}
					if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard
							.isKeyDown(Keyboard.KEY_LSHIFT))) {
						mouseSelectStart = mouseSelectEnd;
					}

				}
				return true;
			case Keyboard.KEY_RIGHT:
				mouseSelectEnd = new PC_Vec2I(mouseSelectStart.x+1, mouseSelectStart.y);
				if(mouseSelectEnd.x>document.getLine(mouseSelectEnd.y).getText().length()){
					mouseSelectEnd.y++;
					if(mouseSelectEnd.y>document.getLines()){
						mouseSelectEnd = mouseSelectStart;
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
				if(mouseSelectEnd.y>1){
					int pos = getPixelPositionFromString(mouseSelectEnd);
					mouseSelectEnd.y--;
					mouseSelectEnd.x = getPositionFromString(new PC_Vec2I(pos, mouseSelectEnd.y));
					if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard
							.isKeyDown(Keyboard.KEY_LSHIFT))) {
						mouseSelectStart = mouseSelectEnd;
					}
				}
				return true;
			case Keyboard.KEY_DOWN:
				if(mouseSelectEnd.y<document.getLines()){
					int pos = getPixelPositionFromString(mouseSelectEnd);
					mouseSelectEnd.y++;
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
	protected void handleMouseLeave(PC_Vec2I mouse, int buttons) {
		// TODO Auto-generated method stub
		super.handleMouseLeave(mouse, buttons);
	}

	@Override
	protected boolean handleMouseButtonClick(PC_Vec2I mouse, int buttons, int eventButton) {
		// TODO Auto-generated method stub
		return super.handleMouseButtonClick(mouse, buttons, eventButton);
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
	}

	@Override
	public String getText() {
		return document.getWholeText();
	}
	
}
