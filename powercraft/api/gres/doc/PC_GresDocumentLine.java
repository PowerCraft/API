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
		String hLine = "";
		List<BlockHighlight> blockHighlight = null;
		if(prev!=null&&prev.endsWithBlockHightlight!=null){
			blockHighlight = prev.endsWithBlockHightlight;
		}
		if(blockHighlight!=null){
			hLine += blockHighlight.get(0).getHighlightingString();
		}else{
			blockHighlight = new ArrayList<BlockHighlight>();
		}
		String word = null;
		PC_GresHighlighting blockHighlighting = highlighting;
		for(int i=0; i<line.length();){
			if(!blockHighlight.isEmpty()){
				IMultiplePossibilities o = blockHighlight.get(0).getEscapeString();
				if(o!=null){
					int length = o.comesNowIn(line, i);
					if(length>0){
						length++;
						hLine += line.substring(i, i+length);
						i += length;
						continue;
					}
				}
				IMultiplePossibilities s = blockHighlight.get(0).getEndString();
				if(s!=null){
					int length = s.comesNowIn(line, i);
					if(length>0){
						hLine += line.substring(i, i+length);
						i += length;
						blockHighlight.remove(0);
						hLine += PC_Formatter.reset();
						if(blockHighlight.isEmpty()){
							blockHighlighting = highlighting;
						}else{
							blockHighlighting = blockHighlight.get(0).getHighlighting();
							hLine += blockHighlight.get(0).getHighlightingString();
						}
						continue;
					}
				}
			}
			if(blockHighlighting!=null){
				int maxLength = 0;
				Highlight bestHighlight = null;
				for(Highlight highlight:blockHighlighting.getSpecialHighlights()){
					IMultiplePossibilities mp = highlight.getHighlightStrings();
					if(mp!=null){
						int length = mp.comesNowIn(line, i);
						if(length>maxLength){
							maxLength = length;
							bestHighlight = highlight;
						}
					}
				}
				if(maxLength==0){
					for(Highlight highlight:blockHighlighting.getOperatorHighlights()){
						IMultiplePossibilities mp = highlight.getHighlightStrings();
						if(mp!=null){
							int length = mp.comesNowIn(line, i);
							if(length>maxLength){
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
					hLine += blockHighlighting.getWordHighlighted(word, reset) + bestHighlight.getHighlightingString() + line.substring(i, i+maxLength);
					if(bestHighlight instanceof BlockHighlight){
						blockHighlight.add(0, (BlockHighlight)bestHighlight);
						blockHighlighting = ((BlockHighlight)bestHighlight).getHighlighting();
					}else{
						hLine += reset;
					}
					word = null;
					i += maxLength;
					continue;
				}else{
					char c = line.charAt(i);
					if(c==' ' || c=='\t' || c=='\r' || c=='\n'){
						hLine += blockHighlighting.getWordHighlighted(word, reset);
						word = null;
						hLine += c;
					}else{
						if(word==null){
							word = "";
						}
						word += c;
					}
				}
			}else{
				hLine += line.charAt(i);
			}
			i++;
		}
		hLine += highlighting.getWordHighlighted(word, "");
		line = hLine;
		word = null;
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
