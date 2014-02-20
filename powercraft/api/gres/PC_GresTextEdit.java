package powercraft.api.gres;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.history.PC_GresHistory;

@SideOnly(Side.CLIENT)
public class PC_GresTextEdit extends PC_GresComponent {

	protected static final String textureName = "TextEdit";
	protected static final String textureName2 = "TextEditSelect";

	public enum PC_GresInputType {
		/** accept all characters */
		TEXT,
		/** accept signed number */
		INT,
		/** accept unsigned number */
		UNSIGNED_INT,
		/** accept signed number with dot */
		SIGNED_FLOAT,
		/** accept unsigned number with dot */
		UNSIGNED_FLOAT,
		/** Disable user input */
		NONE,
		/** [a-zA-Z_][a-zA-Z0-9_] */
		IDENTIFIER;
	}

	private int maxChars;
	private int scroll;
	private int mouseSelectStart = 0;
	private int mouseSelectEnd = 0;
	private PC_GresInputType type;
	private int cursorCounter;

	public PC_GresTextEdit(String text, int chars) {
		this(text, chars, PC_GresInputType.TEXT);
	}

	public PC_GresTextEdit(String text, int chars, PC_GresInputType type) {
		setText(text);
		this.type = type;
		maxChars = chars;
	}

	@Override
	protected PC_Vec2I calculateMinSize() {
		return new PC_Vec2I(maxChars * 8 + 4, fontRenderer.FONT_HEIGHT + 12);
	}

	@Override
	protected PC_Vec2I calculateMaxSize() {
		return new PC_Vec2I(-1, fontRenderer.FONT_HEIGHT + 12);
	}

	@Override
	protected PC_Vec2I calculatePrefSize() {
		return new PC_Vec2I(maxChars * 8 + 4, fontRenderer.FONT_HEIGHT + 12);
	}

	@Override
	protected void paint(PC_RectI scissor, double scale, int displayHeight,
			float timeStamp) {
		drawTexture(textureName, 0, 0, rect.width, rect.height);
		PC_Vec2I offset = getRealLocation();
		setDrawRect(scissor,
				new PC_RectI(2+offset.x, 6+offset.y, rect.width - 4, rect.height - 12), scale,
				displayHeight);

		if (focus && mouseSelectStart != mouseSelectEnd) {
			int s = mouseSelectStart;
			int e = mouseSelectEnd;
			if (s > e) {
				e = mouseSelectStart;
				s = mouseSelectEnd;
			}
			drawTexture(textureName2,
					fontRenderer.getStringWidth(text.substring(0, s)) - scroll
							+ 2, 1,
					fontRenderer.getStringWidth(text.substring(s, e)),
					rect.height+1);
		}

		drawString(text, 2 - scroll, 6, false);

		if (focus && cursorCounter / 6 % 2 == 0) {
			PC_GresRenderer.drawVerticalLine(fontRenderer.getStringWidth(text
					.substring(0, mouseSelectEnd)) + 2, 6,
					6 + fontRenderer.FONT_HEIGHT, fontColors[0]|0xff000000);
		}

		if(scissor==null){
			setDrawRect(scissor, new PC_RectI(-1, -1, -1, -1), scale, displayHeight);
		}else{
			setDrawRect(scissor, scissor, scale, displayHeight);
		}
	}

	private int getMousePositionInString(int x) {
		int charSize;
		x -= 2 + scroll;
		for (int i = 0; i < text.length(); i++) {
			charSize = fontRenderer.getCharWidth(text.charAt(i));
			if (x - charSize / 2 < 0) {
				return i;
			}
			x -= charSize;
		}
		return text.length();
	}

	@Override
	protected boolean handleKeyTyped(char key, int keyCode, PC_GresHistory history) {
		super.handleKeyTyped(key, keyCode, history);
		cursorCounter = 0;
		if (type == PC_GresInputType.NONE)
			return true;
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
				return true;
			case Keyboard.KEY_BACK:
				key_backspace();
				return true;
			case Keyboard.KEY_HOME:
				mouseSelectEnd = mouseSelectStart = 0;
				return true;
			case Keyboard.KEY_END:
				mouseSelectEnd = mouseSelectStart = text.length();
				return true;
			case Keyboard.KEY_DELETE:
				key_delete();
				return true;
			case Keyboard.KEY_LEFT:
				if (mouseSelectEnd > 0) {
					mouseSelectEnd -= 1;
					if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard
							.isKeyDown(Keyboard.KEY_LSHIFT))) {
						mouseSelectStart = mouseSelectEnd;
					}

				}
				return true;
			case Keyboard.KEY_RIGHT:
				if (mouseSelectEnd < text.length()) {
					mouseSelectEnd += 1;
					if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard
							.isKeyDown(Keyboard.KEY_LSHIFT))) {
						mouseSelectStart = mouseSelectEnd;
					}
				}
				return true;
			default:
				switch (type) {
				case UNSIGNED_INT:
					if (Character.isDigit(key)) {
						addKey(key);
						return true;
					}
					return false;

				case INT:
					// writing before minus
					if (text.length() > 0 && text.charAt(0) == '-'
							&& mouseSelectStart == 0 && mouseSelectEnd == 0) {
						return true;
					}

					if (Character.isDigit(key)) {
						addKey(key);
						return true;
					} else if ((mouseSelectStart == 0 || mouseSelectEnd == 0)
							&& key == '-') {
						addKey(key);
						return true;
					}
					return false;

				case SIGNED_FLOAT:

					if (key == '.') {
						if (mouseSelectStart == 0 || mouseSelectEnd == 0) {
							return true;
						}
						if (text.length() > 0
								&& (mouseSelectStart == 1 || mouseSelectEnd == 1)
								&& text.charAt(0) == '-') {
							return true;
						}
						if (text.length() > 0 && text.contains(".")) {
							return true;
						}
						addKey(key);
						return true;
					}

					if (text.length() > 0 && text.charAt(0) == '-'
							&& mouseSelectStart == 0 && mouseSelectEnd == 0) {
						return true;
					}

					if (Character.isDigit(key)) {
						addKey(key);
						return true;
					} else if ((mouseSelectStart == 0 || mouseSelectEnd == 0)
							&& key == '-') {
						addKey(key);
						return true;
					}

					return false;

				case UNSIGNED_FLOAT:

					if (key == '.') {
						if (mouseSelectStart == 0 || mouseSelectEnd == 0) {
							return true;
						}
						if (text.length() > 0 && text.contains(".")) {
							return true;
						}
						addKey(key);
						return true;
					}

					if (Character.isDigit(key)) {
						addKey(key);
						return true;
					}

					return false;

				case IDENTIFIER:

					if (Character.isDigit(key)) {
						if (mouseSelectStart == 0 || mouseSelectEnd == 0) {
							return true;
						}
						addKey(key);
						return true;
					}

					if (Character.isLetter(key) || key == '_') {
						addKey(key);
						return true;
					}

					return false;

				case TEXT:
				default:
					if (ChatAllowedCharacters.isAllowedCharacter(key)) {
						addKey(key);
						return true;
					}
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Add a character instead of current selection (or in place of, if start ==
	 * end)
	 * 
	 * @param c
	 *            character
	 */
	protected void addKey(char c) {
		int s = mouseSelectStart, e = mouseSelectEnd;
		if (s > e) {
			e = mouseSelectStart;
			s = mouseSelectEnd;
		}
		try {
			String s1 = text.substring(0, s);
			String s2 = text.substring(e);
			if ((s1 + c + s2).length() > maxChars) {
				return;
			}
			text = s1 + c + s2;
			mouseSelectEnd =  s + 1;
			mouseSelectStart = mouseSelectEnd;
		} catch (StringIndexOutOfBoundsException ss) {
			ss.printStackTrace();
		}
	}

	private void deleteSelected() {
		int s = mouseSelectStart, e = mouseSelectEnd;
		if (s > e) {
			e = mouseSelectStart;
			s = mouseSelectEnd;
		}
		String s1 = text.substring(0, s);
		String s2 = text.substring(e);
		text = s1 + s2;
		mouseSelectEnd = s;
		mouseSelectStart = s;
	}

	private void key_backspace() {
		if (mouseSelectStart != mouseSelectEnd) {
			deleteSelected();
			return;
		}
		if (mouseSelectEnd <= 0) {
			return;
		}
		String s1 = text.substring(0, mouseSelectEnd - 1);
		String s2 = text.substring(mouseSelectEnd);
		text = s1 + s2;
		mouseSelectEnd -= 1;
		mouseSelectStart = mouseSelectEnd;
	}

	private void key_delete() {
		if (mouseSelectStart != mouseSelectEnd) {
			deleteSelected();
			return;
		}
		if (mouseSelectEnd >= text.length()) {
			return;
		}
		String s1 = text.substring(0, mouseSelectEnd);
		String s2 = text.substring(mouseSelectEnd + 1);
		text = s1 + s2;
	}

	private String getSelect() {
		int s = mouseSelectStart, e = mouseSelectEnd;
		if (s > e) {
			e = mouseSelectStart;
			s = mouseSelectEnd;
		}
		return text.substring(s, e);
	}

	/**
	 * Replace selected part of the text
	 * 
	 * @param stri
	 *            replacement
	 */
	private void setSelected(String stri) {
		int s = mouseSelectStart, e = mouseSelectEnd;
		if (s > e) {
			e = mouseSelectStart;
			s = mouseSelectEnd;
		}
		String s1 = text.substring(0, s);
		String s2 = text.substring(e);
		String ss = "";
		switch (type) {
		case UNSIGNED_INT:
			for (int i = 0; i < stri.length(); i++) {
				if (Character.isDigit(Character.valueOf(stri.charAt(i)))) {
					ss += stri.charAt(i);
				}
			}
			break;

		case INT:
			if (text.length() > 0) {
				if (text.charAt(0) == '-') {
					if (mouseSelectStart == 0 && mouseSelectEnd == 0) {
						break;
					}
				}
			}
			for (int i = 0; i < stri.length(); i++) {
				if (i == 0) {
					if (stri.charAt(0) == '-') {
						if (s == 0) {
							ss += stri.charAt(i);
						}
					}
				}
				if (Character.isDigit(Character.valueOf(stri.charAt(i)))) {
					ss += stri.charAt(i);
				}
			}
			break;

		case SIGNED_FLOAT:
			if (text.length() > 0) {
				if (text.charAt(0) == '-') {
					if (mouseSelectStart == 0 && mouseSelectEnd == 0) {
						break;
					}
				}
			}
			for (int i = 0; i < stri.length(); i++) {
				if (i == 0) {
					if (stri.charAt(0) == '-') {
						if (s == 0) {
							ss += stri.charAt(i);
						}
					}
				}
				if (stri.charAt(i) == '.') {
					if (!(s1.contains(".") || s2.contains(".") || ss
							.contains("."))) {
						ss += ".";
					}
				}
				if (Character.isDigit(Character.valueOf(stri.charAt(i)))) {
					ss += stri.charAt(i);
				}
			}
			break;

		case UNSIGNED_FLOAT:
			for (int i = 0; i < stri.length(); i++) {
				if (stri.charAt(i) == '.') {
					if (!(s1.contains(".") || s2.contains(".") || ss
							.contains("."))) {
						ss += ".";
					}
				}
				if (Character.isDigit(Character.valueOf(stri.charAt(i)))) {
					ss += stri.charAt(i);
				}
			}
			break;

		case NONE:
			break;

		default:
			for (int i = 0; i < stri.length(); i++) {
				if (ChatAllowedCharacters.isAllowedCharacter(stri.charAt(i))) {
					ss += stri.charAt(i);
				}
			}
			break;
		}
		if ((s1 + ss + s2).length() > maxChars) {
			return;
		}
		text = s1 + ss + s2;
		mouseSelectEnd = s + ss.length();
		mouseSelectStart = s;
	}

	@Override
	protected boolean handleMouseMove(PC_Vec2I mouse, int buttons, PC_GresHistory history) {
		super.handleMouseMove(mouse, buttons, history);
		if (mouseDown) {
			mouseSelectEnd = getMousePositionInString(mouse.x);
			cursorCounter = 0;
		}
		return true;
	}

	@Override
	protected boolean handleMouseButtonDown(PC_Vec2I mouse, int buttons,
			int eventButton, PC_GresHistory history) {
		super.handleMouseButtonDown(mouse, buttons, eventButton, history);
		mouseSelectStart = getMousePositionInString(mouse.x);
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

}
