package powercraft.api.script.weasel.source;

import powercraft.api.gres.doc.PC_GresDocumentLine;

public class PC_WeaselLineDesk {

	public static final PC_WeaselLineDesk NULL = new PC_WeaselLineDesk(null, 0, null, 0);
	
	public PC_GresDocumentLine startLine;
	public int startLinePos;
	public PC_GresDocumentLine endLine;
	public int endLinePos;
	
	public PC_WeaselLineDesk(PC_GresDocumentLine startLine, int startLinePos, PC_GresDocumentLine endLine, int endLinePos){
		this.startLine = startLine;
		this.startLinePos = startLinePos;
		this.endLine = endLine;
		this.endLinePos = endLinePos;
	}

	public PC_WeaselLineDesk(PC_WeaselLineDesk lineDesk) {
		this.startLine = lineDesk.startLine;
		this.startLinePos = lineDesk.startLinePos;
		this.endLine = lineDesk.endLine;
		this.endLinePos = lineDesk.endLinePos;
	}

	@Override
	public String toString() {
		return "line:" + this.startLine + ":" + this.startLinePos + "=>" + this.endLine + ":" + this.endLinePos;
	}

	public boolean isBetween(PC_GresDocumentLine line, int pos) {
		if(this.startLine==line){
			if(this.startLinePos>pos)
				return false;
		}else{
			PC_GresDocumentLine l = line.next;
			while(l!=null){
				if(l==this.startLine)
					return false;
				l = l.next;
			}
		}if(this.endLine==line){
			if(this.endLinePos<=pos)
				return false;
		}else{
			PC_GresDocumentLine l = line.prev;
			while(l!=null){
				if(l==this.endLine)
					return false;
				l = l.prev;
			}
		}
		return true;
	}

	public boolean isAfter(PC_GresDocumentLine line, int pos) {
		if(this.endLine==line){
			return this.endLinePos<=pos;
		}
		PC_GresDocumentLine l = line.next;
		while(l!=null){
			if(l==this.endLine)
				return false;
			l = l.next;
		}
		return true;
	}
	
}
