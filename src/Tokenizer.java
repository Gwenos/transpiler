import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

	private final String regex_python_keywords = "(if|for|print)\\b";
	private final String regex_python_identifiers = "[a-zA-Z_][a-zA-Z0-9_]*";
	private final String regex_python_literals = "\\d+(\\.\\d+)?|\"(\\\\.|[^\"\\\\])*\"|'(\\\\.|[^\"\\\\])*'|(True|False|None)";
	private final String regex_python_operators = "[+\\-/*%=]";

	private final Error error = new Error();

	public List<Token> tokenize(String content, String separator){
		int nb_ligne = 1;
		List<Token> tokens = new ArrayList<>();

		for (String line : content.split(separator)) {
			int pos = 0;

			if(line.isEmpty()) continue;//lignes vides
			if(line.startsWith("#")) continue;//commentaires

			while(pos < line.length()){
				char c = line.charAt(pos);
				String c_str = String.valueOf(c);

				// literaux numérique
				if(Character.isDigit(c)){
					StringBuilder literal = new StringBuilder();
					while(pos < line.length() && (Character.isDigit(line.charAt(pos)) || line.charAt(pos)=='.') ){
						literal.append(line.charAt(pos));
						pos++;
					}
					tokens.add(new Token(TokenType.LITERAL, literal.toString()));

				//litéraux string
				}else if (c_str.matches("[\"']")) {
					StringBuilder literal = new StringBuilder();
					while(pos < line.length() && !literal.toString().matches(regex_python_literals)){
						literal.append(line.charAt(pos));
						pos++;
					}
					tokens.add(new Token(TokenType.LITERAL, literal.toString()));

				//identifiers
				}else if(c_str.matches(regex_python_identifiers)){
					StringBuilder identifier = new StringBuilder();
					while(pos < line.length() && (identifier.toString() + line.charAt(pos)).matches(regex_python_identifiers)){
						identifier.append(line.charAt(pos));
						pos++;
					}
					// mot clés
					tokens.add( identifier.toString().matches(regex_python_keywords) ?
							new Token(TokenType.KEYWORD, identifier.toString()) :
							new Token(TokenType.IDENTIFIER, identifier.toString())
					);

				//Opérateurs
				}else if(c_str.matches(regex_python_operators)) {
					tokens.add(new Token(TokenType.OPERATOR, c_str));
					pos++;

				} else if (c_str.equals("(")) {
					tokens.add(new Token(TokenType.LPAREN, c_str));
					pos++;

				} else if (c_str.equals(")")) {
					tokens.add(new Token(TokenType.RPAREN, c_str));
					pos++;
				} else if (c_str.equals(":")) {
					tokens.add(new Token(TokenType.COLON, c_str));
					pos++;
				}else if(Character.isWhitespace(c)){//ignoré
					pos++;
				}else{
					error.lexicalError(line, String.valueOf(c), nb_ligne);
					pos++;
				}

			}
			tokens.add(new Token(TokenType.NEWLINE, separator));
			// Fin de lignes
			nb_ligne++;
		}
		tokens.add(new Token(TokenType.EOF, ""));
		return tokens;

	}
}
