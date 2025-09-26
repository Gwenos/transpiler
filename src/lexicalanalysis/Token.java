package lexicalanalysis;

import util.ANSI;

public class Token {

	public TokenType type;
	public String value;
	public int numLine;

	public Token(TokenType type, String value, int numLine){
		this.type = type;
		this.value = value;
		this.numLine = numLine;
	}

	public String toString(){
		return type + "(" + ANSI.CYAN + value + ANSI.RESET + ")";
	}
}
