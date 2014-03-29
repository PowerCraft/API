package powercraft.api.script.weasel.source;

import java.util.HashMap;

public enum PC_WeaselTokenKind {

	EOF, UNKNOWN, ERROR, 
	IDENT, 
	FLOATLITERAL, DOUBLELITERAL, LONGLITERAL, INTLITERAL, CHARLITERAL, STRINGLITERAL, 
	TRUE("true"), FALSE("false"), NULL("null"), 
	BOOL("bool", "boolean"), BYTE("byte"), CHAR("char"), SHORT("short"), INT("int"), LONG("long"), FLOAT("float"), DOUBLE("double"), VOID("void"),
	CLASS("class"), ENUM("enum"), INTERFACE("interface"), ANNOTATION("@interface"),
	PACKAGE("package"), IMPORT("import"),
	PUBLIC("public"), PRIVATE("private"), PROTECTED("protected"), FINAL("final"), ABSTRACT("abstract"), NATIVE("native"), STATIC("static"),
	THIS("this"), SUPER("super"),
	FOR("for"), WHILE("while"), DO("do"), IF("if"), ELSE("else"), 
	SWITCH("switch"), CASE("case"), DEFAULT("default"), 
	RETURN("return"), BREAK("break"), CONTINUE("continue"), THROW("throw"), TRY("try"), CATCH("catch"), FINALLY("finally"),
	ADD('+'), SUB('-'), MUL('*'), DIV('/'), MOD('%'), 
	NOT('!'), BNOT('~'), 
	AND('&'), OR('|'), XOR('^'), 
	OAND("and"), OOR("or"), OXOR("xor"),
	OBAND("bitand"), OBOR("bitor"),
	OMOD("mod"), ONOT("not"), OBNOT("bitnot"), OPOW("pow"),
	EQUAL('='), GREATER('>'), SMALLER('<'), INSTANCEOF("instanceof"),
	QUESTIONMARK('?'), COLON(':'),
	ELEMENT('.'), AT('@'),
	COMMA(','), LBRAKET('{'), RBRAKET('}'), LINDEX('['), RINDEX(']'), LGROUP('('), RGROUP(')'),
	SEMICOLON(';'), 
	EXTENDS("extends"), IMPLEMENTS("implements"), THROWS("throws"), NEW("new"), SYNCHRONIZED("synchronized"),
	ASM("asm"), ASSERT("assert");
	
	
	public final String name;
	public final String[] otherNames;
	
	PC_WeaselTokenKind(){
		this.name = null;
		this.otherNames = null;
	}
	
	PC_WeaselTokenKind(String keyword){
		Statics.keywords.put(keyword, this);
		this.name = keyword;
		this.otherNames = null;
	}
	
	PC_WeaselTokenKind(String keyword, String...otherNames){
		Statics.keywords.put(keyword, this);
		for(String s:otherNames){
			Statics.keywords.put(s, this);
		}
		this.name = keyword;
		this.otherNames = otherNames;
	}
	
	PC_WeaselTokenKind(char charToken){
		Statics.charTokens.put(Character.valueOf(charToken), this);
		this.name = ""+charToken;
		this.otherNames = null;
	}
	
	public String getName() {
		if(this.name==null){
			if(this==EOF){
				return "EOF";
			}else if(this==UNKNOWN){
				return "UNKNOWN";
			}else if(this==ERROR){
				return "ERROR";
			}
		}
		return this.name;
	}

	public static PC_WeaselTokenKind getKeyword(String name){
		return Statics.keywords.get(name);
	}
	
	public static PC_WeaselTokenKind getCharToken(char charToken) {
		return Statics.charTokens.get(Character.valueOf(charToken));
	}
	
	private static class Statics{
		static HashMap<String, PC_WeaselTokenKind> keywords = new HashMap<String, PC_WeaselTokenKind>();
		static HashMap<Character, PC_WeaselTokenKind> charTokens = new HashMap<Character, PC_WeaselTokenKind>();
	}

	
	
}
