package powercraft.api.gres.doc;

import java.util.ArrayList;
import java.util.List;

import powercraft.api.gres.doc.PC_GresHighlighting.BlockHighlight;
import powercraft.api.gres.doc.PC_GresHighlighting.Highlight;
import powercraft.api.gres.doc.PC_GresHighlighting.IMultiplePossibilities;
import powercraft.api.gres.font.PC_Formatter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_GresDocumentLine {

	public int indent;
	public List<BlockHighlight> endsWithBlockHightlight;
	public String line;
	public PC_GresDocumentLine next;
	public PC_GresDocumentLine prev;
	
	public PC_GresDocumentLine(String text) {
		line = text;
	}

	public void resetHighlighting(){
		line = getText();
	}
	
	public boolean recalcHighlighting(PC_GresHighlighting highlighting){
		resetHighlighting();
		String oldLine = line;
		line = "";
		Object lastInfo = null;
		List<BlockHighlight> blockHighlight = null;
		if(prev!=null&&prev.endsWithBlockHightlight!=null){
			blockHighlight = prev.endsWithBlockHightlight;
		}
		if(blockHighlight!=null){
			line += blockHighlight.get(0).getHighlightingString();
		}else{
			blockHighlight = new ArrayList<BlockHighlight>();
		}
		int wordStart = 0;
		int wordLength = 0;
		PC_GresHighlighting blockHighlighting = highlighting;
		for(int i=0; i<oldLine.length();){
			if(!blockHighlight.isEmpty()){
				IMultiplePossibilities o = blockHighlight.get(0).getEscapeString();
				if(o!=null){
					int length = o.comesNowIn(oldLine, i, lastInfo);
					if(length>0){
						length++;
						line += oldLine.substring(i, i+length);
						i += length;
						lastInfo = o.getInfo();
						continue;
					}
				}
				IMultiplePossibilities s = blockHighlight.get(0).getEndString();
				if(s!=null){
					int length = s.comesNowIn(oldLine, i, lastInfo);
					if(length>0){
						line += oldLine.substring(i, i+length);
						i += length;
						blockHighlight.remove(0);
						line += PC_Formatter.reset();
						if(blockHighlight.isEmpty()){
							blockHighlighting = highlighting;
						}else{
							blockHighlighting = blockHighlight.get(0).getHighlighting();
							line += blockHighlight.get(0).getHighlightingString();
						}
						lastInfo = s.getInfo();
						continue;
					}
				}
			}
			if(blockHighlighting!=null){
				int maxLength = 0;
				Highlight bestHighlight = null;
				IMultiplePossibilities bestMP = null;
				for(Highlight highlight:blockHighlighting.getSpecialHighlights()){
					IMultiplePossibilities mp = highlight.getHighlightStrings();
					if(mp!=null){
						int length = mp.comesNowIn(oldLine, i, lastInfo);
						if(length>maxLength){
							bestMP = mp;
							maxLength = length;
							bestHighlight = highlight;
						}
					}
				}
				if(maxLength==0){
					for(Highlight highlight:blockHighlighting.getOperatorHighlights()){
						IMultiplePossibilities mp = highlight.getHighlightStrings();
						if(mp!=null){
							int length = mp.comesNowIn(oldLine, i, lastInfo);
							if(length>maxLength){
								bestMP = mp;
								maxLength = length;
								bestHighlight = highlight;
							}
						}
					}
				}
				String reset = PC_Formatter.reset();
				if(!blockHighlight.isEmpty()){
					reset += blockHighlight.get(0).getHighlightingString();
				}
				if(maxLength>0 && bestHighlight!=null){
					lastInfo = makeWordHighlighted(oldLine, wordStart, wordLength, reset, blockHighlighting, lastInfo);
					line += bestHighlight.getHighlightingString() + oldLine.substring(i, i+maxLength);
					if(bestHighlight instanceof BlockHighlight){
						blockHighlight.add(0, (BlockHighlight)bestHighlight);
						blockHighlighting = ((BlockHighlight)bestHighlight).getHighlighting();
					}else{
						line += reset;
					}
					wordLength = 0;
					i += maxLength;
					lastInfo = bestMP.getInfo();
					continue;
				}else{
					char c = oldLine.charAt(i);
					if(c==' ' || c=='\t' || c=='\r' || c=='\n'){
						lastInfo = makeWordHighlighted(oldLine, wordStart, wordLength, reset, blockHighlighting, lastInfo);
						wordLength = 0;
						line += c;
					}else{
						if(wordLength==0){
							wordStart = i;
						}
						wordLength++;
					}
				}
			}else{
				line += oldLine.charAt(i);
			}
			i++;
		}
		makeWordHighlighted(oldLine, wordStart, wordLength, "", blockHighlighting, lastInfo);
		wordLength = 0;
		endsWithBlockHightlight = null;
		for(int i=0; i<blockHighlight.size(); i++){
			if(!blockHighlight.get(i).isMultiline()){
				while(blockHighlight.size()>i)
					blockHighlight.remove(i);
				break;
			}
		}
		if(endsWithBlockHightlight==null?blockHighlight.isEmpty():endsWithBlockHightlight.equals(blockHighlight))
			return false;
		endsWithBlockHightlight = null;
		if(!blockHighlight.isEmpty())
			endsWithBlockHightlight = blockHighlight;
		return true;
	}
	
	private Object makeWordHighlighted(String test, int start, int length, String reset, PC_GresHighlighting highlighting, Object info){
		if(length==0)
			return info;
		for(Highlight highlight:highlighting.getWordHighlighteds()){
			IMultiplePossibilities mp = highlight.getHighlightStrings();
			if(mp!=null){
				int l = mp.comesNowIn(test, start, info);
				if(l==length){
					line += highlight.getHighlightingString()+test.substring(start, start+length)+reset;
					return mp.getInfo();
				}
			}
		}
		line += test.substring(start, start+length);
		return null;
	}
	
	public String getText() {
		return PC_Formatter.removeFormatting(line);
	}

	public void setText(String text) {
		line = text;
	}

	public String getHighlightedString() {
		return line;
	}
	
}
