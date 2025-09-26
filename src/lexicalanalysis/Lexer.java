package lexicalanalysis;

import java.util.ArrayList;
import java.util.List;
import util.Error;

public class Lexer {

	public List<Token> tokenize(String content){

		final String KEYWORDS = "(public|private|class|extends|static|final|if|else|while|return)";
		final String TYPE = "int|double|float|boolean|void|[A-Z][a-zA-Z0-9_]*";
		final String STRING = "\"(\\\\.|[^\"\\\\])*\"|'(\\\\.|[^\"\\\\])*'";
		final String COMPARE_OPERATORS = "(==|!=|<|<=|>|>=)";
		final String IDENTIFIER = "[a-zA-Z_][a-zA-Z0-9_]*";
		final String LOGICAL_OPERATORS = "&&|\\|\\||!";
		final String DIGIT = "[0-9]+";
		final String NUMBER = DIGIT+"(."+DIGIT+ ")?([fF])?";
		final String LITERAL = NUMBER+"|"+STRING;
		final String BOOLEAN = "(true|false)";
		final String OPERATORS = "[+\\-/*%]";

		int numLine = 0;
		List<Token> tokens = new ArrayList<>();

		for (String line : content.split("\n")) {
			line = line.trim();
			numLine++;
			int pos = 0;

			//commentaire
			if(line.startsWith("//")) continue;

			//tokenization
			while(pos < line.length()){
				char c = line.charAt(pos);
				String c_str = String.valueOf(c);

				// numerical literal
				if(Character.isDigit(c)) {
					StringBuilder literal = new StringBuilder();
					while (pos < line.length() && (	String.valueOf(line.charAt(pos)).matches(NUMBER) ||
							( line.charAt(pos) == '.' || (line.charAt(pos)=='f' || line.charAt(pos)=='F')))) {
//						pos++;
						literal.append(line.charAt(pos));
						pos++;
					}
					if (literal.toString().contains("f") || literal.toString().contains("F")) tokens.add(new Token(TokenType.FLOAT_LITERAL, literal.toString(), numLine));
					else if (literal.toString().contains(".")) tokens.add(new Token(TokenType.DOUBLE_LITERAL, literal.toString(), numLine));
					else tokens.add(new Token(TokenType.INT_LITERAL, literal.toString(), numLine));

				//string literal
				}else if (c_str.matches("[\"']")) {
					StringBuilder literal = new StringBuilder();
					while (pos < line.length() && !literal.toString().matches(LITERAL)) {
						literal.append(line.charAt(pos));
						pos++;
					}
					tokens.add(new Token(TokenType.STRING_LITERAL, literal.toString(), numLine));

				//identifiers
				}else if(c_str.matches(IDENTIFIER)){
					StringBuilder identifier = new StringBuilder();
					while(pos < line.length() && (identifier.toString() + line.charAt(pos)).matches(IDENTIFIER)){
						identifier.append(line.charAt(pos));
						pos++;
					}
					// mot clés
					tokens.add( identifier.toString().matches(TYPE) ?
							new Token(TokenType.TYPE, identifier.toString(), numLine) :
							identifier.toString().matches(BOOLEAN) ?
								new Token(TokenType.BOOLEAN_LITERAL, identifier.toString(), numLine):
								identifier.toString().matches(KEYWORDS) ?
									new Token(TokenType.KEYWORD, identifier.toString(), numLine):
									new Token(TokenType.IDENTIFIER, identifier.toString(), numLine)
					);

				//Operators
				}else if(c_str.matches(OPERATORS)) {
					tokens.add(new Token(TokenType.OPERATOR, c_str, numLine));
					pos++;

				//Compare operator
				}else if(c_str.matches("[=!<>&|]")) {
					String potential_operator = c_str + line.charAt(pos + 1);

					if (potential_operator.matches(LOGICAL_OPERATORS)) {
						pos++;
						tokens.add(new Token(TokenType.LOGICAL_OPERATOR, potential_operator, numLine));
					} else if (potential_operator.matches(COMPARE_OPERATORS)) {
						pos++;
						tokens.add(new Token(TokenType.COMPARE_OPERATOR, potential_operator, numLine));
					} else if (c_str.equals("=")) {
						tokens.add(new Token(TokenType.ASSIGN, c_str, numLine));
					} else if (c_str.matches(COMPARE_OPERATORS)){
						tokens.add(new Token(TokenType.COMPARE_OPERATOR, c_str, numLine));
					}else {
						tokens.add(new Token(TokenType.LOGICAL_OPERATOR, c_str, numLine));
					}
					pos++;

				// Punctuations
				} else if (c_str.equals("(")) {
					tokens.add(new Token(TokenType.LPAREN, c_str, numLine));
					pos++;
				} else if (c_str.equals(")")) {
					tokens.add(new Token(TokenType.RPAREN, c_str, numLine));
					pos++;
				} else if (c_str.equals("{")) {
					tokens.add(new Token(TokenType.LBRACE, c_str, numLine));
					pos++;
				} else if (c_str.equals("}")) {
					tokens.add(new Token(TokenType.RBRACE, c_str, numLine));
					pos++;
				} else if (c_str.equals("[")) {
					tokens.add(new Token(TokenType.LBRACKET, c_str, numLine));
					pos++;
				} else if (c_str.equals("]")) {
					tokens.add(new Token(TokenType.RBRACKET, c_str, numLine));
					pos++;
				} else if (c_str.equals(":")) {
					tokens.add(new Token(TokenType.COLON, c_str, numLine));
					pos++;
				}else if (c_str.equals(",")) {
					tokens.add(new Token(TokenType.COMMA, c_str, numLine));
					pos++;
				}else if (c_str.equals(";")) {
					tokens.add(new Token(TokenType.SEMICOLON, c_str, numLine));
					pos++;
				}else if(Character.isWhitespace(c)){//ignoré
					pos++;
				}else{
					Error.lexicalError(numLine, String.valueOf(c));
					pos++;
				}
			}
		}
		tokens.add(new Token(TokenType.EOF, "", numLine));
		return tokens;
	}
}
