package powercraft.api.gres.doc;

import javax.tools.Diagnostic.Kind;

import powercraft.api.PC_Vec2I;
import powercraft.api.gres.doc.PC_GresDocumentLine.Message;
import powercraft.api.gres.font.PC_Formatter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_GresDocument {

	private PC_GresDocumentLine firstLine = new PC_GresDocumentLine("");
	private int lines = 1;
	private PC_GresHighlighting highlighting;
	private PC_GresDocRenderHandler handler;
	private PC_GresDocInfoCollector infoCollector;
	
	public PC_GresDocument(String text, PC_GresHighlighting highlighting, PC_GresDocRenderHandler handler, PC_GresDocInfoCollector infoCollector) {
		this.highlighting = highlighting;
		this.handler = handler;
		this.infoCollector = infoCollector;
		onLineChanged(this.firstLine);
		add(new PC_Vec2I(), text);
	}

	public void onLineChange(PC_GresDocumentLine line){
		if(this.handler!=null){
			this.handler.onLineChange(line);
		}
		if(this.infoCollector!=null){
			this.infoCollector.onLineChange(line);
		}
	}
	
	public void onLineChanged(PC_GresDocumentLine line){
		if(this.handler!=null){
			this.handler.onLineChanged(line);
		}
		if(this.infoCollector!=null){
			this.infoCollector.onLineChanged(line);
		}
	}
	
	private static Message[] subArray(Message[] array, int start, int end, Message[] array2, int start2, int end2){
		int size = end-start;
		int size2 = end2-start2;
		Message[] result = new Message[size+size2];
		for(int i=0; i<size; i++){
			result[i] = array[i+start];
		}
		for(int i=0; i<size2; i++){
			result[i+size] = array2[i+start2];
		}
		return result;
	}
	
	public void remove(PC_Vec2I start, PC_Vec2I end){
		PC_Vec2I[] selects = sort(start, end);
		PC_GresDocumentLine line = getLine(selects[0].y);
		if(selects[0].y == selects[1].y){
			String text = line.getText();
			int l = text.length();
			text = text.substring(0, selects[0].x) + text.substring(selects[1].x);
			onLineChange(line);
			if(line.errors!=null){
				line.errors = subArray(line.errors, 0, selects[0].x, line.errors, selects[1].x, l);
			}
			line.setText(text);
			onLineChanged(line);
		}else{
			PC_GresDocumentLine line2 = getLine(selects[1].y);
			String text = line.getText();
			String text2 = line2.getText();
			text = text.substring(0, selects[0].x) + text2.substring(selects[1].x);
			if(this.handler!=null || this.infoCollector!=null){
				PC_GresDocumentLine l = line.next;
				while(l!=line2){
					onLineChange(l);
					l = l.next;
				}
				onLineChange(l);
				onLineChange(line);
			}
			if(line.errors!=null && line2.errors!=null){
				line.errors = subArray(line.errors, 0, selects[0].x, line2.errors, selects[1].x, text2.length());
			}else if(line.errors!=null){
				Message[] errors = line.errors;
				line.errors = new Message[text.length()];
				System.arraycopy(errors, 0, line.errors, 0, selects[0].x);
			}else if(line2.errors!=null){
				line.errors = new Message[text.length()];
				System.arraycopy(line2.errors, selects[1].x, line.errors, 0, text2.length()-selects[1].x);
			}
			line.setText(text);
			onLineChanged(line);
			line2 = line2.next;
			line.next = line2;
			if(line2!=null)
				line2.prev = line;
			this.lines -= selects[1].y - selects[0].y;
		}
		recalcHighlights(line, 1);
	}
	
	private static Message[] concat(Message[] array, int start, int end, int s, Message[] array2, int start2, int end2){
		int size = end-start;
		int size2 = end2-start2;
		Message[] result = new Message[size+size2+s];
		for(int i=0; i<size; i++){
			result[i] = array[i+start];
		}
		for(int i=0; i<size2; i++){
			result[i+size+s] = array2[i+start2];
		}
		return result;
	}
	
	public void add(PC_Vec2I pos, String insert){
		String[] inserts = insert.split("\n", -1);
		PC_GresDocumentLine line = getLine(pos.y);
		String text = line.getText();
		String start = text.substring(0, pos.x);
		String end = text.substring(pos.x);
		if(inserts.length==1){
			onLineChange(line);
			if(line.errors!=null){
				line.errors = concat(line.errors, 0, pos.x, inserts[0].length(), line.errors, pos.x, text.length());
			}
			line.setText(start+inserts[0]+end);
			onLineChanged(line);
		}else{
			this.lines += inserts.length-1;
			onLineChange(line);
			Message[] errors = line.errors;
			if(errors!=null){
				line.errors = new Message[pos.x+inserts[0].length()];
				System.arraycopy(errors, 0, line.errors, 0, pos.x);
			}
			line.setText(start+inserts[0]);
			onLineChanged(line);
			PC_GresDocumentLine last = line.next;
			PC_GresDocumentLine actLine = line;
			for(int i=1; i<inserts.length-1; i++){
				PC_GresDocumentLine newLine = new PC_GresDocumentLine(inserts[i]);
				onLineChanged(newLine);
				actLine.next = newLine;
				newLine.prev = actLine;
				actLine = newLine;
			}
			PC_GresDocumentLine newLine = new PC_GresDocumentLine(inserts[inserts.length-1]+end);
			if(errors!=null){
				newLine.errors = new Message[inserts[inserts.length-1].length()+end.length()];
				System.arraycopy(errors, 0, newLine.errors, inserts[inserts.length-1].length(), end.length());
			}
			onLineChanged(newLine);
			actLine.next = newLine;
			newLine.prev = actLine;
			newLine.next = last;
			if(last!=null)
				last.prev = newLine;
		}
		recalcHighlights(line, inserts.length);
	}
	
	public String getText(PC_Vec2I start, PC_Vec2I end){
		PC_Vec2I[] selects = sort(start, end);
		PC_GresDocumentLine line = getLine(selects[0].y);
		if(selects[0].y == selects[1].y){
			String text = line.getText();
			return text.substring(selects[0].x, selects[1].x);
		}
		PC_GresDocumentLine line2 = getLine(selects[1].y);
		String text = line.getText().substring(selects[0].x) + "\n";
		line = line.next;
		while(line!=line2){
			text += line.getText() + "\n";
			line = line.next;
		}
		return text + line2.getText().substring(0, selects[1].x);
	}
	
	public PC_GresDocumentLine getLine(int i){
		if(i>=this.lines)
			return null;
		PC_GresDocumentLine line = this.firstLine;
		int ii = i;
		while(ii-->0 && line!=null)
			line = line.next;
		return line;
	}
	
	public int getLines(){
		return this.lines;
	}
	
	public String getWholeText(){
		PC_GresDocumentLine line = this.firstLine;
		if(line==null)
			return "";
		String text = line.getText();
		while((line = line.next)!=null){
			text += "\n"+line.getText();
		}
		return text;
	}
	
	public PC_Vec2I getLastPos() {
		return new PC_Vec2I(getLine(this.lines-1).getText().length(), this.lines-1);
	}
	
	private static PC_Vec2I[] sort(PC_Vec2I start, PC_Vec2I end){
		if(start.y<end.y || (start.y==end.y && start.x<end.x))
			return new PC_Vec2I[]{start, end};
		return new PC_Vec2I[]{end, start};
	}
	
	public void recalcHighlights(PC_GresDocumentLine line, int num){
		if(this.highlighting!=null){
			PC_GresDocumentLine l = line;
			int n = num;
			boolean again = false;
			while((n>0 || again) && l!=null){
				again = l.recalcHighlighting(this.highlighting);
				l = l.next;
				n--;
			}
		}
	}
	
	public void setHighlighting(PC_GresHighlighting highlighting){
		if(this.highlighting == highlighting)
			return;
		this.highlighting = highlighting;
		PC_GresDocumentLine line = this.firstLine;
		if(highlighting==null){
			while(line!=null){
				line.resetHighlighting();
				line = line.next;
			}
		}else{
			while(line!=null){
				line.recalcHighlighting(highlighting);
				line = line.next;
			}
		}
	}
	
	public PC_GresHighlighting getHighlighting(){
		return this.highlighting;
	}
	
	public void setRenderHandler(PC_GresDocRenderHandler handler){
		this.handler = handler;
		if(handler!=null){
			PC_GresDocumentLine line = this.firstLine;
			while(line!=null){
				handler.onLineChanged(line);
				line = line.next;
			}
		}
	}
	
	public PC_GresDocRenderHandler getRenderHandler(){
		return this.handler;
	}
	
	public void setInfoCollector(PC_GresDocInfoCollector infoCollector){
		this.infoCollector = infoCollector;
		if(this.handler!=null){
			PC_GresDocumentLine line = this.firstLine;
			while(line!=null){
				infoCollector.onLineChanged(line);
				line = line.next;
			}
		}
	}
	
	public PC_GresDocInfoCollector getInfoCollector(){
		return this.infoCollector;
	}

	public void addError(PC_Vec2I start, PC_Vec2I end, Kind kind, String message) {
		PC_Vec2I sorted[] = sort(start, end);
		PC_GresDocumentLine line = getLine(sorted[0].y);
		if(sorted[0].y == sorted[1].y){
			line.addError(sorted[0].x, sorted[1].x, kind, message);
			recalcHighlights(line, 1);
		}else{
			PC_GresDocumentLine sline = line;
			line.addError(sorted[0].x, -1, kind, message);
			line = line.next;
			for(int i=sorted[0].y+1; i<sorted[1].y; i++){
				line.addError(0, -1, kind, message);
				line = line.next;
			}
			line.addError(0, sorted[1].x, kind, message);
			recalcHighlights(sline, sorted[1].y-sorted[0].y+1);
		}
	}

	
	
	public PC_Vec2I getPosFrom(long pos) {
		int lineNum = 0;
		PC_GresDocumentLine line = this.firstLine;
		long p = pos;
		while(line!=null){
			int l = line.getText().length();
			if(l<=p){
				p -= l+1;
			}else{
				return new PC_Vec2I((int)p, lineNum);
			}
			line = line.next;
			lineNum++;
		}
		return null;
	}

	public void removeErrors() {
		PC_GresDocumentLine line = this.firstLine;
		while(line!=null){
			if(line.errors!=null){
				line.errors = null;
				line.line = PC_Formatter.removeErrorFormatting(line.line);
			}
			line = line.next;
		}
	}
	
}
