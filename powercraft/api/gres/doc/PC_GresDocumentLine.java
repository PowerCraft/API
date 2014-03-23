package powercraft.api.gres.doc;

import java.util.ArrayList;
import java.util.List;

import javax.tools.Diagnostic.Kind;

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
	public Object renderInfo;
	public Object collectorInfo;
	public Message[] errors;
	
	public PC_GresDocumentLine(String text) {
		this.line = text;
	}

	public void resetHighlighting(){
		this.line = getText();
	}
	
	public boolean recalcHighlighting(PC_GresHighlighting highlighting){
		resetHighlighting();
		String oldLine = this.line;
		this.line = "";
		Object lastInfo = null;
		List<BlockHighlight> blockHighlight = null;
		PC_GresHighlighting blockHighlighting = highlighting;
		if(this.prev!=null&&this.prev.endsWithBlockHightlight!=null){
			blockHighlight = new ArrayList<BlockHighlight>(this.prev.endsWithBlockHightlight);
		}
		if(blockHighlight!=null){
			this.line += blockHighlight.get(0).getHighlightingString();
			blockHighlighting = blockHighlight.get(0).getHighlighting();
		}else{
			blockHighlight = new ArrayList<BlockHighlight>();
		}
		int wordStart = 0;
		int wordLength = 0;
		for(int i=0; i<oldLine.length();){
			if(!blockHighlight.isEmpty()){
				IMultiplePossibilities o = blockHighlight.get(0).getEscapeString();
				if(o!=null){
					int length = o.comesNowIn(oldLine, i, lastInfo);
					if(length>0){
						length++;
						this.line += oldLine.substring(i, i+length);
						i += length;
						lastInfo = o.getInfo();
						continue;
					}
				}
				IMultiplePossibilities s = blockHighlight.get(0).getEndString();
				if(s!=null){
					int length = s.comesNowIn(oldLine, i, lastInfo);
					if(length>0){
						String reset = PC_Formatter.reset();
						if(!blockHighlight.isEmpty()){
							reset += blockHighlight.get(0).getHighlightingString();
						}
						lastInfo = makeWordHighlighted(oldLine, wordStart, wordLength, reset, blockHighlighting, lastInfo);
						wordLength = 0;
						this.line += oldLine.substring(i, i+length);
						i += length;
						blockHighlight.remove(0);
						this.line += PC_Formatter.reset();
						if(blockHighlight.isEmpty()){
							blockHighlighting = highlighting;
						}else{
							blockHighlighting = blockHighlight.get(0).getHighlighting();
							this.line += blockHighlight.get(0).getHighlightingString();
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
				if(maxLength>0 && bestHighlight!=null && bestMP!=null){
					lastInfo = makeWordHighlighted(oldLine, wordStart, wordLength, reset, blockHighlighting, lastInfo);
					this.line += bestHighlight.getHighlightingString() + oldLine.substring(i, i+maxLength);
					if(bestHighlight instanceof BlockHighlight){
						blockHighlight.add(0, (BlockHighlight)bestHighlight);
						blockHighlighting = ((BlockHighlight)bestHighlight).getHighlighting();
					}else{
						this.line += reset;
					}
					lastInfo = bestMP.getInfo();
					wordLength = 0;
					i += maxLength;
					continue;
				}
				char c = oldLine.charAt(i);
				if(c==' ' || c=='\t' || c=='\r' || c=='\n'){
					lastInfo = makeWordHighlighted(oldLine, wordStart, wordLength, reset, blockHighlighting, lastInfo);
					wordLength = 0;
					this.line += c;
				}else{
					if(wordLength==0){
						wordStart = i;
					}
					wordLength++;
				}
			}else{
				this.line += oldLine.charAt(i);
			}
			i++;
		}
		makeWordHighlighted(oldLine, wordStart, wordLength, "", blockHighlighting, lastInfo);
		wordLength = 0;
		for(int i=0; i<blockHighlight.size(); i++){
			if(!blockHighlight.get(i).isMultiline()){
				while(blockHighlight.size()>i)
					blockHighlight.remove(i);
				break;
			}
		}
		if(this.errors!=null){
			checkErrors();
			if(this.errors!=null){
				int j=0;
				oldLine = this.line;
				this.line = "";
				boolean error = false;
				for(int i=0; i<oldLine.length(); i++){
					char c = oldLine.charAt(i);
					if(c==PC_Formatter.START_SEQ && i + 1 < oldLine.length()){
						this.line += c;
						c = oldLine.charAt(++i);
						int s = i;
						i += PC_Formatter.data[c];
						this.line += c;
						this.line += oldLine.substring(s+1, i+1);
					}else{
						if(error && this.errors[j]==null){
							error = false;
							this.line += PC_Formatter.errorStop();
						}else if(!error && this.errors[j]!=null){
							this.line += PC_Formatter.error(this.errors[j].getRed(), this.errors[j].getGreen(), this.errors[j].getBlue());
							error = true;
						}
						this.line += c;
						j++;
					}
				}
			}
		}
		if(this.endsWithBlockHightlight==null?blockHighlight.isEmpty():this.endsWithBlockHightlight.equals(blockHighlight))
			return false;
		if(blockHighlight.isEmpty()){
			this.endsWithBlockHightlight = null;
		}else{
			this.endsWithBlockHightlight = blockHighlight;
		}
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
					this.line += highlight.getHighlightingString()+test.substring(start, start+length)+reset;
					return mp.getInfo();
				}
			}
		}
		this.line += test.substring(start, start+length);
		return null;
	}
	
	public String getText() {
		return PC_Formatter.removeFormatting(this.line);
	}

	public void setText(String text) {
		this.line = text;
	}

	public String getHighlightedString() {
		return this.line;
	}

	public void checkErrors(){
		if(this.errors!=null){
			for(Message error:this.errors){
				if(error!=null)
					return;
			}
			this.errors = null;
		}
	}
	
	public void addError(int sx, int ex, Kind kind, String message) {
		if(this.errors==null){
			this.errors = new Message[getText().length()];
		}
		int exx = ex;
		if(exx==-1){
			exx = this.errors.length;
		}
		for(int i=sx; i<=exx; i++){
			this.errors[i] = new Message(kind, message);
		}
	}
	
	public static class Message{
		
		Kind kind;
		String message;
		
		public Message(Kind kind, String message) {
			this.kind = kind;
			this.message = message;
		}
		
		public int getRed(){
			return this.kind==Kind.ERROR || this.kind==Kind.WARNING?255:0;
		}
		
		public int getGreen(){
			return this.kind==Kind.WARNING?255:0;
		}
		
		public int getBlue(){
			return this.kind==Kind.NOTE?255:0;
		}

		public String getMessage() {
			return this.message;
		}
	}
	
}
