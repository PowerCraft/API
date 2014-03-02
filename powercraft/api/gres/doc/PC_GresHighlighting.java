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
		this.specialHighlights.add(new BlockHighlight(start, escape, end, isMultiline, formatting));
	}
	
	public void addBlockHighlight(IMultiplePossibilities start, IMultiplePossibilities escape, IMultiplePossibilities end, String formatting){
		this.specialHighlights.add(new BlockHighlight(start, escape, end, formatting));
	}
	
	public void addBlockHighlight(IMultiplePossibilities start, IMultiplePossibilities escape, IMultiplePossibilities end, boolean isMultiline, String formatting, PC_GresHighlighting highlighting){
		this.specialHighlights.add(new BlockHighlight(start, escape, end, isMultiline, formatting, highlighting));
	}
	
	public void addOperatorHighlight(IMultiplePossibilities operators, String formatting){
		this.operatorHightlights.add(new Highlight(operators, formatting));
	}
	
	public void addWordHighlight(IMultiplePossibilities words, String formatting){
		this.wordHighlights.add(new Highlight(words, formatting));
	}
	
	public void addSpecialHighlight(IMultiplePossibilities words, String formatting){
		this.specialHighlights.add(new Highlight(words, formatting));
	}

	public List<Highlight> getOperatorHighlights() {
		return this.operatorHightlights;
	}
	
	public List<Highlight> getSpecialHighlights() {
		return this.specialHighlights;
	}
	
	public List<Highlight> getWordHighlighteds() {
		return this.wordHighlights;
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
			return this.escape;
		}

		public IMultiplePossibilities getEndString() {
			return this.end;
		}

		public boolean isMultiline() {
			return this.isMultiline;
		}

		public PC_GresHighlighting getHighlighting() {
			return this.highlighting;
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
			return this.formatting;
		}

		public IMultiplePossibilities getHighlightStrings() {
			return this.operator;
		}
		
	}
	
	public static interface IMultiplePossibilities{

		public int comesNowIn(String line, int i, Object lastInfo);

		public Object getInfo();
		
	}
	
	public static class MultipleStringPossibilities implements IMultiplePossibilities{
		
		private final String[] possibilities;
		private final boolean caseSentive;
		
		public MultipleStringPossibilities(String string, boolean caseSentive) {
			if(string==null){
				this.possibilities = new String[0];
			}else{
				this.possibilities = string.replaceAll("\\s\\s", " ").split("\\s");
				if(!caseSentive){
					for(int i=0; i<this.possibilities.length; i++){
						this.possibilities[i] = this.possibilities[i].toLowerCase();
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
		public int comesNowIn(String line, int i, Object info) {
			int l = 0;
			String lin = line;
			if(!this.caseSentive){
				lin = lin.toLowerCase();
			}
			for(String possibility:this.possibilities){
				if(possibility.length()>l && lin.startsWith(possibility, i)){
					l = possibility.length();
				}
			}
			return l;
		}

		@Override
		public Object getInfo() {
			return null;
		}
		
	}
	
	public static class MultipleRegexPossibilities implements IMultiplePossibilities{
		
		private final Pattern regex;
		
		public MultipleRegexPossibilities(String regex) {
			this.regex = Pattern.compile(regex);
		}

		@Override
		public int comesNowIn(String line, int i, Object info) {
			Matcher match = this.regex.matcher(line);
			if(match.find(i) && match.start()==i)
				return match.end()-i;
			return 0;
		}
		
		@Override
		public Object getInfo() {
			return null;
		}
		
	}
	
	public static IMultiplePossibilities msp(boolean caseSentive, String...possibilities){
		return new MultipleStringPossibilities(possibilities, caseSentive);
	}
	
}
