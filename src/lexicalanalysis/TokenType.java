package lexicalanalysis;

public enum TokenType {
	KEYWORD,
	IDENTIFIER,
	TYPE,
	STRING_LITERAL,
	INT_LITERAL,
	DOUBLE_LITERAL,
	FLOAT_LITERAL,
	BOOLEAN_LITERAL,

	ASSIGN,	//=
	OPERATOR,	//(+-/*%)
	COMPARE_OPERATOR,	// == != < <= > >=
	LOGICAL_OPERATOR,	// && || !

	LPAREN,	RPAREN,	// ()
	LBRACE, RBRACE, // {}
	LBRACKET, RBRACKET, // []
	COLON,	//:
	COMMA,	//,
	SEMICOLON, //;

	EOF
}
