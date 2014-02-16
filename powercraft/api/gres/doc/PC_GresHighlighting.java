package powercraft.api.gres.doc;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.EnumChatFormatting;

@SideOnly(Side.CLIENT)
public class PC_GresHighlighting {

	public static final PC_GresHighlighting MINISCRIPT = new PC_GresHighlighting();
	static{
		MINISCRIPT.addOperatorHighlight("+ - *", "");
		MINISCRIPT.addOperatorHighlight("[ ]", "");
		MINISCRIPT.addOperatorHighlight(",", "");
		MINISCRIPT.addBlockHighlight(";", "", "", false, ""+EnumChatFormatting.GRAY);
		MINISCRIPT.addWordHighlight("", ""+EnumChatFormatting.BOLD+EnumChatFormatting.DARK_PURPLE);
	}
	
	private final List<BlockHighlight> blockHighlights = new ArrayList<BlockHighlight>();
	private final List<OperatorHighlight> operatorHightlights = new ArrayList<OperatorHighlight>();
	private final List<WordHighlight> wordHighlights = new ArrayList<WordHighlight>();
	
	public void addBlockHighlight(String start, String escape, String end, boolean isMultiline, String formatting){
		blockHighlights.add(new BlockHighlight(new MultiplePossibilities(start), new MultiplePossibilities(escape), 
				new MultiplePossibilities(end), isMultiline, formatting));
	}
	
	public void addOperatorHighlight(String operators, String formatting){
		operatorHightlights.add(new OperatorHighlight(new MultiplePossibilities(operators), formatting));
	}
	
	public void addWordHighlight(String operators, String formatting){
		wordHighlights.add(new WordHighlight(new MultiplePossibilities(operators), formatting));
	}
	
	public List<BlockHighlight> getBlockHighlights() {
		return blockHighlights;
	}

	public List<OperatorHighlight> getOperatorHighlights() {
		return operatorHightlights;
	}
	
	public String getWordHighlighted(String word) {
		if(word==null)
			return "";
		for(WordHighlight wordHighlight:wordHighlights){
			MultiplePossibilities mp = wordHighlight.getWordStrings();
			if(word.length() == mp.comesNowIn(word, 0)){
				return wordHighlight.getHighlightingString()+word+EnumChatFormatting.RESET;
			}
		}
		return word;
	}
	
	public static class BlockHighlight{

		private final MultiplePossibilities start;
		private final MultiplePossibilities escape;
		private final MultiplePossibilities end;
		private final boolean isMultiline;
		private final String formatting;
		
		public BlockHighlight(MultiplePossibilities start, MultiplePossibilities escape, MultiplePossibilities end, boolean isMultiline, String formatting) {
			this.start = start;
			this.escape = escape;
			this.end = end;
			this.isMultiline = isMultiline;
			this.formatting = formatting;
		}

		public String getHighlightingString() {
			return formatting;
		}

		public MultiplePossibilities getEscapeString() {
			return escape;
		}

		public MultiplePossibilities getEndString() {
			return end;
		}

		public MultiplePossibilities getStartString() {
			return start;
		}

		public boolean isMultiline() {
			return isMultiline;
		}
		
	}
	
	public static class OperatorHighlight{

		private final MultiplePossibilities operator;
		private final String formatting;
		
		public OperatorHighlight(MultiplePossibilities operator, String formatting) {
			this.operator = operator;
			this.formatting = formatting;
		}

		public String getHighlightingString() {
			return formatting;
		}

		public MultiplePossibilities getOperatorStrings() {
			return operator;
		}
		
	}
	
	public static class WordHighlight{
		
		private final MultiplePossibilities word;
		private final String formatting;
		
		public WordHighlight(MultiplePossibilities word, String formatting) {
			this.word = word;
			this.formatting = formatting;
		}
		
		public String getHighlightingString() {
			return formatting;
		}

		public MultiplePossibilities getWordStrings() {
			return word;
		}
		
	}
	
	public static class MultiplePossibilities{

		private final String[] possibilities;
		
		public MultiplePossibilities(String string) {
			if(string==null){
				possibilities = new String[0];
			}else{
				possibilities = string.replaceAll("\\s\\s", " ").split("\\s");
			}
		}

		public int comesNowIn(String line, int i) {
			int l = 0;
			for(String possibility:possibilities){
				if(possibility.length()>l && line.startsWith(possibility, i)){
					l = possibility.length();
				}
			}
			return l;
		}
		
	}
	
}
