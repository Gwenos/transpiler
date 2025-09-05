package lexicalanalysis;

import util.ANSI;

public class Token {

	public TokenType type;
	public String value;
	public String line;
	public int numLine;

	public Token(TokenType type, String value, String line, int numLine){
		this.type = type;
		this.value = value;
		this.line = line;
		this.numLine = numLine;
	}

	public String toString(){
		return type + "(" + ANSI.CYAN + value + ANSI.RESET + ")";
	}
}
