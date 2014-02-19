package powercraft.api.gres.doc;

import powercraft.api.PC_Vec2I;
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
		onLineChanged(firstLine);
		add(new PC_Vec2I(), text);
	}

	private void onLineChange(PC_GresDocumentLine line){
		if(handler!=null){
			handler.onLineChange(line);
		}
		if(infoCollector!=null){
			infoCollector.onLineChange(line);
		}
	}
	
	private void onLineChanged(PC_GresDocumentLine line){
		if(handler!=null){
			handler.onLineChanged(line);
		}
		if(infoCollector!=null){
			infoCollector.onLineChanged(line);
		}
	}
	
	public void remove(PC_Vec2I start, PC_Vec2I end){
		PC_Vec2I[] selects = sort(start, end);
		PC_GresDocumentLine line = getLine(selects[0].y);
		if(selects[0].y == selects[1].y){
			String text = line.getText();
			text = text.substring(0, selects[0].x) + text.substring(selects[1].x);
			onLineChange(line);
			line.setText(text);
			onLineChanged(line);
		}else{
			PC_GresDocumentLine line2 = getLine(selects[1].y);
			String text = line.getText();
			String text2 = line2.getText();
			text = text.substring(0, selects[0].x) + text2.substring(selects[1].x);
			if(handler!=null || infoCollector!=null){
				PC_GresDocumentLine l = line.next;
				while(l!=line2){
					onLineChange(l);
					l = l.next;
				}
				onLineChange(l);
				onLineChange(line);
			}
			line.setText(text);
			onLineChanged(line);
			line2 = line2.next;
			line.next = line2;
			if(line2!=null)
				line2.prev = line;
			lines -= selects[1].y - selects[0].y;
		}
		recalcHighlights(line, 1);
	}
	
	public void add(PC_Vec2I pos, String insert){
		String[] inserts = insert.split("\n", -1);
		PC_GresDocumentLine line = getLine(pos.y);
		String text = line.getText();
		String start = text.substring(0, pos.x);
		String end = text.substring(pos.x);
		if(inserts.length==1){
			onLineChange(line);
			line.setText(start+inserts[0]+end);
			onLineChanged(line);
		}else{
			lines += inserts.length-1;
			onLineChange(line);
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
		}else{
			PC_GresDocumentLine line2 = getLine(selects[1].y);
			String text = line.getText().substring(selects[0].x) + "\n";
			line = line.next;
			while(line!=line2){
				text += line.getText() + "\n";
				line = line.next;
			}
			return text + line2.getText().substring(0, selects[1].x);
		}
	}
	
	public PC_GresDocumentLine getLine(int i){
		if(i>=lines)
			return null;
		PC_GresDocumentLine line = firstLine;
		while(i-->0 && line!=null)
			line = line.next;
		return line;
	}
	
	public int getLines(){
		return lines;
	}
	
	public String getWholeText(){
		PC_GresDocumentLine line = firstLine;
		if(line==null)
			return "";
		String text = line.getText();
		while((line = line.next)!=null){
			text += "\n"+line.getText();
		}
		return text;
	}
	
	public PC_Vec2I getLastPos() {
		return new PC_Vec2I(getLine(lines-1).getText().length(), lines-1);
	}
	
	private PC_Vec2I[] sort(PC_Vec2I start, PC_Vec2I end){
		if(start.y<end.y || (start.y==end.y && start.x<end.x))
			return new PC_Vec2I[]{start, end};
		return new PC_Vec2I[]{end, start};
	}
	
	private void recalcHighlights(PC_GresDocumentLine line, int num){
		if(highlighting!=null){
			boolean again = false;
			while((num>0 || again) && line!=null){
				again = line.recalcHighlighting(highlighting);
				line = line.next;
				num--;
			}
		}
	}
	
	public void setHighlighting(PC_GresHighlighting highlighting){
		if(this.highlighting == highlighting)
			return;
		this.highlighting = highlighting;
		PC_GresDocumentLine line = firstLine;
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
		return highlighting;
	}
	
	public void setRenderHandler(PC_GresDocRenderHandler handler){
		this.handler = handler;
		if(handler!=null){
			PC_GresDocumentLine line = firstLine;
			while(line!=null){
				handler.onLineChanged(line);
				line = line.next;
			}
		}
	}
	
	public PC_GresDocRenderHandler getRenderHandler(){
		return handler;
	}
	
	public void setInfoCollector(PC_GresDocInfoCollector infoCollector){
		this.infoCollector = infoCollector;
		if(handler!=null){
			PC_GresDocumentLine line = firstLine;
			while(line!=null){
				infoCollector.onLineChanged(line);
				line = line.next;
			}
		}
	}
	
	public PC_GresDocInfoCollector getInfoCollector(){
		return infoCollector;
	}
	
}