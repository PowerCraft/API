package powercraft.api.script.weasel.source;

import java.util.ArrayList;
import java.util.List;

import powercraft.api.gres.doc.PC_GresDocumentLine;
import powercraft.api.script.weasel.PC_WeaselHighlighting.LineInfo;


public class PC_WeaselSourceIterator {

	private String lineString;
	private PC_GresDocumentLine line;

	private int pos;
	
	private PC_GresDocumentLine prevline;
	private int prevlinepos = 0;
	private PC_GresDocumentLine startline;
	private int startlinepos = 0;
	private char scannChar;
	private List<PC_WeaselComment> comments = null;
	private PC_WeaselToken next;
	private boolean space;
	
	public PC_WeaselSourceIterator(PC_GresDocumentLine line, int pos){
		this.line = line;
		this.lineString = this.line.getText();
		this.pos = pos;
	}
	
	public int getTypeAtPos(){
		int where = 0;
		if(this.line.prev!=null){
			where = ((LineInfo)this.line.prev.collectorInfo).lineEndsWithComment?1:0;
		}
		String s = this.lineString;
		for(int i=0; i<this.pos; i++){
			char c = s.charAt(i);
			if(where==1){
				if(c=='*'){
					if(i+1>=this.pos)
						return where;
					c = s.charAt(i+1);
					if(c=='/'){
						where=0;
						i++;
					}
				}
			}else if(where==2){
				if(c=='\\'){
					if(++i>=this.pos)
						return where;
					c = s.charAt(i);
				}else if(c=='"'){
					where = 0;
				}
			}else if(where==3){
				if(c=='\\'){
					if(++i>=this.pos)
						return where;
					c = s.charAt(i);
				}else if(c=='\''){
					where = 0;
				}
			}else{
				if(c=='/'){
					if(i+1>=this.pos)
						return where;
					c = s.charAt(i+1);
					if(c=='*'){
						i++;
						where=1;
					}else if(c=='/'){
						return 4;
					}
				}else if(c=='"'){
					where=2;
				}else if(c=='\''){
					where=3;
				}
			}
		}
		return where;
	}
	
	public void gotoInstructionStart(String starts){
		PC_GresDocumentLine l = this.line;
		while(true){
			int where = 0;
			if(this.line.prev!=null){
				where = ((LineInfo)this.line.prev.collectorInfo).lineEndsWithComment?1:0;
			}
			String s = l.getText();
			int p = s.length();
			if(l==this.line && p>this.pos)
				p = this.pos;
			int best = -1;
			for(int i=0; i<p; i++){
				char c = s.charAt(i);
				if(where==1){
					if(c=='*'){
						if(i+1>=p)
							break;
						c = s.charAt(i+1);
						if(c=='/'){
							where=0;
							i++;
						}
					}
				}else if(where==2){
					if(c=='\\'){
						if(++i>=p)
							break;
						c = s.charAt(i);
					}else if(c=='"'){
						where = 0;
					}
				}else if(where==3){
					if(c=='\\'){
						if(++i>=p)
							break;
						c = s.charAt(i);
					}else if(c=='\''){
						where = 0;
					}
				}else{
					if(c=='/'){
						if(i+1>=p)
							break;
						c = s.charAt(i+1);
						if(c=='*'){
							where=1;
							i++;
						}else if(c=='/'){
							break;
						}
					}else if(c=='"'){
						where=2;
					}else if(c=='\''){
						where=3;
					}else if(starts.indexOf(c)!=-1){
						best = i;
					}
				}
			}
			if(best!=-1){
				if(best==s.length()-1){
					this.pos = 0;
					this.line = l.next;
				}else{
					this.pos = best;
					this.line = l;
				}
				this.lineString = this.line.getText();
				break;
			}
			PC_GresDocumentLine pl = l;
			l = l.prev;
			if(l==null){
				this.pos = 0;
				this.line = pl;
				this.lineString = this.line.getText();
				break;
			}
		}
		scannChar();
	}
	
	private static boolean charOkForRadix(int radix, char c){
		if(radix==2){
			return c=='0' || c=='1';
		}else if(radix==8){
			return c>='0' && c<='7';
		}else if(radix==10){
			return c>='0' && c<='9';
		}else if(radix==16){
			return (c>='a' && c<='f') || (c>='A' && c<='F') || (c>='0' && c<='9');
		}
		return false;
	}
	
	private String scannDigit(int radix){
		String number = "";
		while(charOkForRadix(radix, this.scannChar) || this.scannChar=='_'){
			if(this.scannChar!='_'){
				number += this.scannChar;
			}
			scannChar();
		}
		return number;
	}
	
	private PC_WeaselToken scannNumber(){
		int radix = 10;
		PC_WeaselTokenKind kind = PC_WeaselTokenKind.INTLITERAL;
		String number = "";
		if(this.scannChar=='0'){
			scannChar();
			if(this.scannChar=='x' || this.scannChar=='X'){
				scannChar();
				radix = 16;
			}else if(this.scannChar=='b' || this.scannChar=='B'){
				scannChar();
				radix = 2;
			}else{
				number = "0";
			}
		}
		number += scannDigit(radix);
		if(this.scannChar=='.' && radix==10){
			kind = PC_WeaselTokenKind.DOUBLELITERAL;
			scannChar();
			boolean nothing = number.isEmpty();
			if(nothing)
				number = "0";
			number += ".";
			String scann = scannDigit(radix);
			if(scann.isEmpty() && nothing){
				scann = "0";
				parserMessage("literal.single_dot");
			}
			number += scann;
		}
		if(radix==10 && (this.scannChar=='e' || this.scannChar=='E')){
			scannChar();
			number += "e";
			if(this.scannChar=='+' || this.scannChar=='-'){
				number += this.scannChar;
				scannChar();
			}
			String scann = scannDigit(radix);
			if(scann.isEmpty()){
				scann = "0";
				parserMessage("literal.empty_exponent");
			}
			number += scann;
		}
		if(this.scannChar=='f' || this.scannChar=='F'){
			kind = PC_WeaselTokenKind.FLOATLITERAL;
			scannChar();
		}else if(this.scannChar=='d' || this.scannChar=='D'){
			kind = PC_WeaselTokenKind.DOUBLELITERAL;
			scannChar();
		}else if(this.scannChar=='l' || this.scannChar=='L'){
			kind = PC_WeaselTokenKind.LONGLITERAL;
			scannChar();
		}
		try{
			if(kind==PC_WeaselTokenKind.INTLITERAL){
				return makeToken(kind, new PC_WeaselConstantValue(Integer.parseInt(number, radix)));
			}else if(kind==PC_WeaselTokenKind.FLOATLITERAL){
				return makeToken(kind, new PC_WeaselConstantValue(Float.parseFloat(number)));
			}else if(kind==PC_WeaselTokenKind.DOUBLELITERAL){
				return makeToken(kind, new PC_WeaselConstantValue(Double.parseDouble(number)));
			}else if(kind==PC_WeaselTokenKind.LONGLITERAL){
				return makeToken(kind, new PC_WeaselConstantValue(Long.parseLong(number, radix)));
			}
		}catch(NumberFormatException e){
			parserMessage("number.format", e.getMessage());
			return makeToken(PC_WeaselTokenKind.INTLITERAL, new PC_WeaselConstantValue(0));
		}
		throw new AssertionError();
	}
	
	private PC_WeaselToken scannIdent(){
		String ident = "";
		while((this.scannChar>='A' && this.scannChar<='Z') || (this.scannChar>='a' && this.scannChar<='z') || this.scannChar=='_' || (this.scannChar>='0' && this.scannChar<='9')){
			ident += this.scannChar;
			scannChar();
		}
		PC_WeaselTokenKind keyword = PC_WeaselTokenKind.getKeyword(ident);
		if(keyword==null)
			return makeToken(PC_WeaselTokenKind.IDENT, new PC_WeaselConstantValue(ident));
		return makeToken(keyword);
	}
	
	private PC_WeaselToken scannString(boolean isChar){
		String string = "";
		char end = isChar?'\'':'"';
		while(this.scannChar!=0 && this.scannChar!=end){
			if(this.scannChar=='\\'){
				scannChar();
				if(this.scannChar=='\\'){
					string += '\\';
				}else if(this.scannChar=='\n'){
					string += '\n';
				}else if(this.scannChar=='\r'){
					string += '\r';
				}else if(this.scannChar=='\t'){
					string += '\t';
				}else if(this.scannChar=='\''){
					string += '\'';
				}else if(this.scannChar=='"'){
					string += '"';
				}
			}else{
				string += this.scannChar;
			}
			scannChar();
		}
		if(this.scannChar!=end){
			parserMessage(isChar?"char.eof":"string.eof");
		}else{
			scannChar();
		}
		if(isChar){
			if(string.length()!=1)
				parserMessage("char.no1length");
			if(string.length()==0){
				return makeToken(isChar?PC_WeaselTokenKind.CHARLITERAL:PC_WeaselTokenKind.STRINGLITERAL, new PC_WeaselConstantValue(0));
			}
			return makeToken(isChar?PC_WeaselTokenKind.CHARLITERAL:PC_WeaselTokenKind.STRINGLITERAL, new PC_WeaselConstantValue(string.charAt(0)));
		}
		return makeToken(PC_WeaselTokenKind.STRINGLITERAL, new PC_WeaselConstantValue(string));
	}
	
	private void readLineComment(boolean multiline){
		scannChar();
		if(this.comments==null)
			this.comments = new ArrayList<PC_WeaselComment>();
		if(multiline){
			boolean doc = this.scannChar=='*';
			if(doc){
				scannChar();
			}
			if(this.scannChar=='/'){
				this.comments.add(new PC_WeaselComment(PC_WeaselCommentType.MULTILINE, ""));
			}else{
				String comment = "";
				while(this.scannChar!=0){
					if(this.scannChar=='*'){
						scannChar();
						if(this.scannChar=='/')
							break;
						comment += '*';
					}
					comment += this.scannChar;
					scannChar();
				}
				if(this.scannChar!='/'){
					parserMessage("comment.eof");
				}
				this.comments.add(new PC_WeaselComment(doc?PC_WeaselCommentType.DOCMULTILINE:PC_WeaselCommentType.MULTILINE, comment));
			}
		}else{
			String comment = "";
			while(this.scannChar!='\n' && this.scannChar!=0){
				comment += this.scannChar;
				scannChar();
			}
			if(this.scannChar!='\n'){
				parserMessage("comment.eof");
			}
			this.comments.add(new PC_WeaselComment(PC_WeaselCommentType.SINGLELINE, comment));
		}
	}
	
	public PC_WeaselToken readNextToken(){
		if(this.next!=null){
			PC_WeaselToken token = this.next;
			this.next = null;
			return token;
		}
		this.comments = null;
		this.space=false;
		while(this.scannChar!=0){
			setStartLineAndPos();
			if(this.scannChar>='0' && this.scannChar<='9'){
				return scannNumber();
			}else if((this.scannChar>='A' && this.scannChar<='Z') || (this.scannChar>='a' && this.scannChar<='z') || this.scannChar=='_'){
				return scannIdent();
			}else if(this.scannChar=='/'){
				scannChar();
				if(this.scannChar=='/' || this.scannChar=='*'){
					readLineComment(this.scannChar=='*');
				}else{
					return makeToken(PC_WeaselTokenKind.DIV);
				}
			}else if(this.scannChar=='"' || this.scannChar=='\''){
				boolean isChar = this.scannChar=='\'';
				scannChar();
				return scannString(isChar);
			}else if(!(this.scannChar==' ' || this.scannChar=='\t' || this.scannChar=='\n' || this.scannChar=='\r')){
				PC_WeaselTokenKind tokenKind = PC_WeaselTokenKind.getCharToken(this.scannChar);
				if(tokenKind!=null){
					if(tokenKind==PC_WeaselTokenKind.AT){
						PC_GresDocumentLine sl = this.startline;
						int slp = this.startlinepos;
						scannChar();
						this.next = readNextToken();
						if(this.next.kind==PC_WeaselTokenKind.INTERFACE){
							this.next = null;
							tokenKind = PC_WeaselTokenKind.ANNOTATION;
						}
						return new PC_WeaselToken(tokenKind, new PC_WeaselLineDesk(sl, slp, this.prevline, this.prevlinepos), this.comments, this.space);
					}
					scannChar();
					return makeToken(tokenKind);
				}
				parserMessage("unknown.char");
			}
			scannChar();
			this.space = true;
		}
		return makeToken(PC_WeaselTokenKind.EOF);
		
	}
	
	private PC_WeaselToken makeToken(PC_WeaselTokenKind kind){
		return new PC_WeaselToken(kind, new PC_WeaselLineDesk(this.startline, this.startlinepos, this.prevline, this.prevlinepos), this.comments, this.space);
	}
	
	private PC_WeaselToken makeToken(PC_WeaselTokenKind kind, PC_WeaselConstantValue param){
		return new PC_WeaselToken(kind, new PC_WeaselLineDesk(this.startline, this.startlinepos, this.prevline, this.prevlinepos), this.comments, param, this.space);
	}
	
	private void setStartLineAndPos(){
		this.startline = this.line;
		this.startlinepos = this.pos;
	}
	
	private void parserMessage(String key, Object...args){
		parserMessage(key, new PC_WeaselLineDesk(this.startline, this.startlinepos, this.line, this.pos), args);
	}
	
	@SuppressWarnings("unused")
	private void parserMessage(String key, PC_WeaselLineDesk lineDesk, Object...args){
		//
	}
	
	@SuppressWarnings("unused")
	private char preview(int p){
		if(this.lineString.length()>this.pos+p){
			return this.lineString.charAt(this.pos+p);
		}
		int pp = this.pos+p;
		PC_GresDocumentLine l = this.line;
		while(l!=null){
			String text = l.getText();
			if(pp<text.length()){
				return text.charAt(pp);
			}
			pp -= text.length();
			l = l.next;
		}
		return 0;
	}

	private void scannChar(){
		this.prevline = this.line;
		this.prevlinepos = this.pos;
		if(this.lineString.length()<=this.pos && this.line.next!=null){
			this.pos = 0;
			do{
				this.line = this.line.next;
				this.lineString = this.line.getText();
			}while(this.lineString.isEmpty() && this.line!=null);
		}
		if(this.lineString.length()>this.pos){
			this.scannChar = this.lineString.charAt(this.pos++);
		}else{
			this.scannChar = 0;
		}
	}
	
}
