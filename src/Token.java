enum TokenType {
	KEYWORD,
	IDENTIFIER,
	LITERAL,
	OPERATOR,

	LPAREN,	//(
	RPAREN,	//)
	COLON,	//:

	INDENT,
	DEDENT,

	NEWLINE,
	EOF
}

public class Token {

	public TokenType type;
	public String value;

	public Token(TokenType type, String value){
		this.type = type;
		this.value = value;
	}

	public String toString(){
		return type + ">>" + value + "<<";
	}
}
