package powercraft.api.gres.doc;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.EnumChatFormatting;
import powercraft.api.gres.doc.PC_GresHighlighting.BlockHighlight;
import powercraft.api.gres.doc.PC_GresHighlighting.MultiplePossibilities;
import powercraft.api.gres.doc.PC_GresHighlighting.OperatorHighlight;

@SideOnly(Side.CLIENT)
public class PC_GresDocumentLine {

	public int indent;
	public BlockHighlight endsWithBlockHightlight;
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
		BlockHighlight blockHighlight = null;
		if(prev!=null&&prev.endsWithBlockHightlight!=null){
			blockHighlight = prev.endsWithBlockHightlight;
		}
		if(blockHighlight!=null){
			hLine += blockHighlight.getHighlightingString();
		}
		String word = null;
		for(int i=0; i<line.length(); i++){
			if(blockHighlight!=null){
				MultiplePossibilities o = blockHighlight.getEscapeString();
				int length = o.comesNowIn(line, i);
				if(length>0){
					length++;
					hLine += line.substring(i, i+length);
					i += length;
				}
				MultiplePossibilities s = blockHighlight.getEndString();
				length = s.comesNowIn(line, i);
				if(length>0){
					hLine += line.substring(i, i+length);
					i += length;
					hLine += EnumChatFormatting.RESET;
					blockHighlight = null;
				}
			}else{
				int maxLength = 0;
				for(BlockHighlight highlight:highlighting.getBlockHighlights()){
					MultiplePossibilities mp = highlight.getStartString();
					int length = mp.comesNowIn(line, i);
					if(length>maxLength){
						maxLength = length;
						blockHighlight = highlight;
					}
				}
				if(maxLength>0){
					hLine += highlighting.getWordHighlighted(word) + blockHighlight.getHighlightingString() + line.substring(i, i+maxLength);
					word = null;
					i += maxLength;
				}else{
					OperatorHighlight operatorHighlight = null;
					for(OperatorHighlight highlight:highlighting.getOperatorHighlights()){
						MultiplePossibilities mp = highlight.getOperatorStrings();
						int length = mp.comesNowIn(line, i);
						if(length>maxLength){
							maxLength = length;
							operatorHighlight = highlight;
						}
					}
					if(maxLength>0){
						hLine += highlighting.getWordHighlighted(word) +  operatorHighlight.getHighlightingString() + line.substring(i, i+maxLength) + EnumChatFormatting.RESET;
						word = null;
						i += maxLength;
					}else{
						char c = line.charAt(i);
						if(c==' ' || c=='\t' || c=='\r' || c=='\n'){
							hLine += highlighting.getWordHighlighted(word);
							word = null;
							hLine += c;
						}else{
							if(word==null){
								word = "";;
							}
							word += c;
						}
					}
				}
			}
		}
		hLine += highlighting.getWordHighlighted(word);
		line = hLine;
		word = null;
		if(endsWithBlockHightlight==blockHighlight)
			return false;
		endsWithBlockHightlight = null;
		if(blockHighlight!=null && blockHighlight.isMultiline())
			endsWithBlockHightlight = blockHighlight;
		return true;
	}

	public String getText() {
		return EnumChatFormatting.getTextWithoutFormattingCodes(line);
	}

	public void setText(String text) {
		line = text;
	}

	public String getHighlightedString() {
		return line;
	}
	
}
