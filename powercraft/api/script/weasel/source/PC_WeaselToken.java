package powercraft.api.script.weasel.source;

import java.util.List;


public class PC_WeaselToken{

	public PC_WeaselTokenKind kind;
	public PC_WeaselLineDesk lineDesk;
	public PC_WeaselConstantValue param;
	public List<PC_WeaselComment> comments;
	public boolean space;
	
	public PC_WeaselToken(PC_WeaselTokenKind kind, PC_WeaselLineDesk lineDesk, List<PC_WeaselComment> comments, boolean space){
		this.kind = kind;
		this.lineDesk = lineDesk;
		this.comments = comments;
		this.space = space;
	}

	public PC_WeaselToken(PC_WeaselTokenKind kind, PC_WeaselLineDesk lineDesk, List<PC_WeaselComment> comments, PC_WeaselConstantValue param, boolean space) {
		this.kind = kind;
		this.lineDesk = lineDesk;
		this.comments = comments;
		this.param = param;
		this.space = space;
	}
	
	public String getDesk(){
		String name = this.kind.getName();
		if(name!=null)
			return "'"+name+"'";
		if(this.kind == PC_WeaselTokenKind.STRINGLITERAL){
			return "\""+this.param+"\"";
		}else if(this.kind == PC_WeaselTokenKind.CHARLITERAL){
			return "'"+this.param+"'";
		}
		return this.param.toString();
	}
	
	@Override
	public String toString(){
		String name = this.kind.getName();
		if(name!=null)
			return name;
		if(this.kind == PC_WeaselTokenKind.STRINGLITERAL){
			return "\""+this.param+"\"";
		}else if(this.kind == PC_WeaselTokenKind.CHARLITERAL){
			return "'"+this.param+"'";
		}
		return this.param.toString();
	}

}
