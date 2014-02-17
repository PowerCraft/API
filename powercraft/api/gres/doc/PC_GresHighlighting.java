package powercraft.api.gres.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_GresHighlighting {
	
	private final List<Highlight> specialHighlights = new ArrayList<Highlight>();
	private final List<Highlight> operatorHightlights = new ArrayList<Highlight>();
	private final List<Highlight> wordHighlights = new ArrayList<Highlight>();
	
	public void addBlockHighlight(IMultiplePossibilities start, IMultiplePossibilities escape, IMultiplePossibilities end, boolean isMultiline, String formatting){
		specialHighlights.add(new BlockHighlight(start, escape, end, isMultiline, formatting));
	}
	
	public void addBlockHighlight(IMultiplePossibilities start, IMultiplePossibilities escape, IMultiplePossibilities end, String formatting){
		specialHighlights.add(new BlockHighlight(start, escape, end, formatting));
	}
	
	public void addBlockHighlight(IMultiplePossibilities start, IMultiplePossibilities escape, IMultiplePossibilities end, boolean isMultiline, String formatting, PC_GresHighlighting highlighting){
		specialHighlights.add(new BlockHighlight(start, escape, end, isMultiline, formatting, highlighting));
	}
	
	public void addOperatorHighlight(IMultiplePossibilities operators, String formatting){
		operatorHightlights.add(new Highlight(operators, formatting));
	}
	
	public void addWordHighlight(IMultiplePossibilities words, String formatting){
		wordHighlights.add(new Highlight(words, formatting));
	}
	
	public void addSpecialHighlight(IMultiplePossibilities words, String formatting){
		specialHighlights.add(new Highlight(words, formatting));
	}

	public List<Highlight> getOperatorHighlights() {
		return operatorHightlights;
	}
	
	public List<Highlight> getSpecialHighlights() {
		return specialHighlights;
	}
	
	public String getWordHighlighted(String word, String reset) {
		if(word==null)
			return "";
		for(Highlight wordHighlight:wordHighlights){
			IMultiplePossibilities mp = wordHighlight.getHighlightStrings();
			if(mp!=null && word.length() == mp.comesNowIn(word, 0)){
				return wordHighlight.getHighlightingString()+word+reset;
			}
		}
		return word;
	}
	
	public static class BlockHighlight extends Highlight{

		private final IMultiplePossibilities escape;
		private final IMultiplePossibilities end;
		private final boolean isMultiline;
		private final PC_GresHighlighting highlighting;
		
		public BlockHighlight(IMultiplePossibilities start, IMultiplePossibilities escape, IMultiplePossibilities end, String formatting) {
			super(start, formatting);
			this.escape = escape;
			this.end = end;
			this.isMultiline = false;
			this.highlighting = null;
		}
		
		public BlockHighlight(IMultiplePossibilities start, IMultiplePossibilities escape, IMultiplePossibilities end, boolean isMultiline, String formatting) {
			super(start, formatting);
			this.escape = escape;
			this.end = end;
			this.isMultiline = isMultiline;
			this.highlighting = null;
		}
		
		public BlockHighlight(IMultiplePossibilities start, IMultiplePossibilities escape, IMultiplePossibilities end, boolean isMultiline, String formatting, PC_GresHighlighting highlighting) {
			super(start, formatting);
			this.escape = escape;
			this.end = end;
			this.isMultiline = isMultiline;
			this.highlighting = highlighting;
		}

		public IMultiplePossibilities getEscapeString() {
			return escape;
		}

		public IMultiplePossibilities getEndString() {
			return end;
		}

		public boolean isMultiline() {
			return isMultiline;
		}

		public PC_GresHighlighting getHighlighting() {
			return highlighting;
		}
		
	}
	
	public static class Highlight{

		private final IMultiplePossibilities operator;
		private final String formatting;
		
		public Highlight(IMultiplePossibilities operator, String formatting) {
			this.operator = operator;
			this.formatting = formatting;
		}

		public String getHighlightingString() {
			return formatting;
		}

		public IMultiplePossibilities getHighlightStrings() {
			return operator;
		}
		
	}
	
	public static interface IMultiplePossibilities{

		public int comesNowIn(String line, int i);
		
	}
	
	public static class MultipleStringPossibilities implements IMultiplePossibilities{
		
		private final String[] possibilities;
		private final boolean caseSentive;
		
		public MultipleStringPossibilities(String string, boolean caseSentive) {
			if(string==null){
				possibilities = new String[0];
			}else{
				possibilities = string.replaceAll("\\s\\s", " ").split("\\s");
				if(!caseSentive){
					for(int i=0; i<possibilities.length; i++){
						possibilities[i] = possibilities[i].toLowerCase();
					}
				}
			}
			this.caseSentive = caseSentive;
		}
		
		public MultipleStringPossibilities(String[] possibilities, boolean caseSentive) {
			this.possibilities = possibilities;
			if(!caseSentive){
				for(int i=0; i<possibilities.length; i++){
					possibilities[i] = possibilities[i].toLowerCase();
				}
			}
			this.caseSentive = caseSentive;
		}

		@Override
		public int comesNowIn(String line, int i) {
			int l = 0;
			if(!caseSentive){
				line = line.toLowerCase();
			}
			for(String possibility:possibilities){
				if(possibility.length()>l && line.startsWith(possibility, i)){
					l = possibility.length();
				}
			}
			return l;
		}
		
	}
	
	public static class MultipleRegexPossibilities implements IMultiplePossibilities{
		
		private final Pattern regex;
		
		public MultipleRegexPossibilities(String regex) {
			this.regex = Pattern.compile(regex);
		}

		@Override
		public int comesNowIn(String line, int i) {
			Matcher match = regex.matcher(line);
			if(match.find(i) && match.start()==i)
				return match.end()-i;
			return 0;
		}
		
	}
	
	public static IMultiplePossibilities msp(boolean caseSentive, String...possibilities){
		return new MultipleStringPossibilities(possibilities, caseSentive);
	}
	
}
