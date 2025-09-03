enum TokenType {
	KEYWORD,
	IDENTIFIER,
	LITERAL,
	BOOLEAN,
	TYPE,

	ASSIGN,	//=
	OPERATOR,	//(+-/*%)
	COMPARE_OPERATOR,	// == != < <= > >=
	LOGICAL_OPERATOR,	// && || !

	LPAREN,	//(
	RPAREN,	//)
	LBRACE, //{
	RBRACE, //}
	LBRACKET, //[
	RBRACKET, //]
	COLON,	//:
	COMMA,	//,
	SEMICOLON, //;

	EOF
}

public class Token {

	public TokenType type;
	public String value;
	public String line;
	public int nb_ligne;

	public Token(TokenType type, String value, String line, int nb_ligne){
		this.type = type;
		this.value = value;
		this.line = line;
		this.nb_ligne = nb_ligne;
	}

	public String toString(){
		return type + "(" + ANSI.CYAN + value + ANSI.RESET + ")";
	}
}
