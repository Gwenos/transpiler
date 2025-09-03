package lexicalAnalysis;

public enum TokenType {
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
