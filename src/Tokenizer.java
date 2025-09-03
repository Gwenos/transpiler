import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

	private final String regex_keywords = "(public|private|class|static|final|if|else|while|return|int|double)\\b";
	private final String regex_identifiers = "[a-zA-Z_][a-zA-Z0-9_]*";
	private final String regex_literals = "\\d+(\\.\\d+)?|\"(\\\\.|[^\"\\\\])*\"|'(\\\\.|[^\"\\\\])*'";
	private final String regex_boolean_literals = "(True|False|None)";
	private final String regex_operators = "[+\\-/*%]";
	private final String regex_compare_operators = "(==|!=|<|<=|>|>=)";
	private final String regex_logical_operators = "&&|\\|\\||!";
	private final String regex_type = "\\b(?:int|double|boolean|void|[A-Z][a-zA-Z0-9_]*)\\b";

	private boolean ignore = false;

	public List<Token> tokenize(String content, String separator){
		int nb_ligne = 1;
		List<Token> tokens = new ArrayList<>();

		for (String line : content.split(separator)) {
			int pos = 0;
			line = line.trim();

			//commentaire
			if(line.startsWith("//")) continue;

			//tokenization
			while(pos < line.length()){
				char c = line.charAt(pos);
				String c_str = String.valueOf(c);

				// literaux numérique
				if(Character.isDigit(c)) {
					StringBuilder literal = new StringBuilder();
					while (pos < line.length() && (Character.isDigit(line.charAt(pos)) || line.charAt(pos) == '.')) {
						literal.append(line.charAt(pos));
						pos++;
					}
					tokens.add(new Token(TokenType.LITERAL, literal.toString(), line, nb_ligne));

				//litéraux string
				}else if (c_str.matches("[\"']")) {
					StringBuilder literal = new StringBuilder();
					while (pos < line.length() && !literal.toString().matches(regex_literals)) {
						literal.append(line.charAt(pos));
						pos++;
					}
					tokens.add(new Token(TokenType.LITERAL, literal.toString(), line, nb_ligne));

				//identifiers
				}else if(c_str.matches(regex_identifiers)){
					StringBuilder identifier = new StringBuilder();
					while(pos < line.length() && (identifier.toString() + line.charAt(pos)).matches(regex_identifiers)){
						identifier.append(line.charAt(pos));
						pos++;
					}
					// mot clés
					tokens.add( identifier.toString().matches(regex_type) ?
							new Token(TokenType.TYPE, identifier.toString(), line, nb_ligne) :
							identifier.toString().matches(regex_boolean_literals) ?
								new Token(TokenType.BOOLEAN, identifier.toString(), line, nb_ligne):
								identifier.toString().matches(regex_keywords) ?
									new Token(TokenType.KEYWORD, identifier.toString(), line, nb_ligne):
									new Token(TokenType.IDENTIFIER, identifier.toString(), line, nb_ligne)
					);

				//Opérateurs
				}else if(c_str.matches(regex_operators)) {
					tokens.add(new Token(TokenType.OPERATOR, c_str, line, nb_ligne));
					pos++;

				//opérateurs de comparaison
				}else if(c_str.matches("[=!<>&|]")){
					String potential_operator = c_str + line.charAt(pos+1) ;
					if (potential_operator.matches(regex_logical_operators)){
						pos++;
						tokens.add(new Token(TokenType.LOGICAL_OPERATOR, potential_operator, line, nb_ligne));
					}else if(potential_operator.matches(regex_compare_operators)){
						pos++;
						tokens.add(new Token(TokenType.COMPARE_OPERATOR, potential_operator, line, nb_ligne));
					}else {
						if(c_str.equals("=")){
							tokens.add(new Token(TokenType.ASSIGN, c_str, line, nb_ligne));
						}else{
							if(c_str.matches(regex_compare_operators)) tokens.add(new Token(TokenType.COMPARE_OPERATOR, c_str, line, nb_ligne));
							else tokens.add(new Token(TokenType.LOGICAL_OPERATOR, c_str, line, nb_ligne));
						}
					}
					pos++;

				// Ponctuations
				} else if (c_str.equals("(")) {
					tokens.add(new Token(TokenType.LPAREN, c_str, line, nb_ligne));
					pos++;

				} else if (c_str.equals(")")) {
					tokens.add(new Token(TokenType.RPAREN, c_str, line, nb_ligne));
					pos++;
				} else if (c_str.equals("{")) {
					tokens.add(new Token(TokenType.LBRACE, c_str, line, nb_ligne));
					pos++;
				} else if (c_str.equals("}")) {
					tokens.add(new Token(TokenType.RBRACE, c_str, line, nb_ligne));
					pos++;
				} else if (c_str.equals("[")) {
					tokens.add(new Token(TokenType.LBRACKET, c_str, line, nb_ligne));
					pos++;
				} else if (c_str.equals("]")) {
					tokens.add(new Token(TokenType.RBRACKET, c_str, line, nb_ligne));
					pos++;
				} else if (c_str.equals(":")) {
					tokens.add(new Token(TokenType.COLON, c_str, line, nb_ligne));
					pos++;
				}else if (c_str.equals(",")) {
					tokens.add(new Token(TokenType.COMMA, c_str, line, nb_ligne));
					pos++;
				}else if (c_str.equals(";")) {
					tokens.add(new Token(TokenType.SEMICOLON, c_str, line, nb_ligne));
					pos++;
				}else if(Character.isWhitespace(c)){//ignoré
					pos++;
				}else{
					Error.lexicalError(line, String.valueOf(c), nb_ligne);
					pos++;
				}

			}
			nb_ligne++;
		}
		tokens.add(new Token(TokenType.EOF, "", "", nb_ligne));
		return tokens;

	}
}
