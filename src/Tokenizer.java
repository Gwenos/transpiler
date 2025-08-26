import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

	private final String regex_python_keywords = "(if|elif|else|for|print|def|or|and|not|while|return)\\b";
	private final String regex_python_identifiers = "[a-zA-Z_][a-zA-Z0-9_]*";
	private final String regex_python_literals = "\\d+(\\.\\d+)?|\"(\\\\.|[^\"\\\\])*\"|'(\\\\.|[^\"\\\\])*'";
	private final String regex_python_boolean_literals = "(True|False|None)";
	private final String regex_python_operators = "[+\\-/*%]";
	private final String getRegex_python_compare_operators = "(==|!=|<|<=|>|>=)";

	private final Error error = new Error();

	//méthode util
	public int getIndentLevel(String line){
		int indent = 0;
		while(line.startsWith("    ")){
			indent++;
			line = line.substring(4);
		}
		return indent;
	}

	public List<Token> tokenize(String content, String separator){
		int nb_ligne = 1;
		int currentIndentLevel = 0;
		List<Token> tokens = new ArrayList<>();

		for (String line : content.split(separator)) {
			int pos = 0;

			//commentaire
			if(line.trim().startsWith("#")) continue;


			//Indentation
			int indentLevel = getIndentLevel(line);
			if(indentLevel > currentIndentLevel){
				tokens.add(new Token(TokenType.INDENT, "    ", line, nb_ligne));
				currentIndentLevel = indentLevel;
			}else if(indentLevel < currentIndentLevel){
				while (indentLevel < currentIndentLevel){
					tokens.add(new Token(TokenType.DEDENT, "", line, nb_ligne));
					currentIndentLevel--;
				}
				if(indentLevel != currentIndentLevel){
					Error.lexicalError(line, "indentation error", nb_ligne);
					throw new RuntimeException("Indentation error");
				}
			}


			//tokenization
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
					tokens.add(new Token(TokenType.LITERAL, literal.toString(), line, nb_ligne));

				//litéraux string
				}else if (c_str.matches("[\"']")) {
					StringBuilder literal = new StringBuilder();
					while (pos < line.length() && !literal.toString().matches(regex_python_literals)) {
						literal.append(line.charAt(pos));
						pos++;
					}
					tokens.add(new Token(TokenType.LITERAL, literal.toString(), line, nb_ligne));

				//identifiers
				}else if(c_str.matches(regex_python_identifiers)){
					StringBuilder identifier = new StringBuilder();
					while(pos < line.length() && (identifier.toString() + line.charAt(pos)).matches(regex_python_identifiers)){
						identifier.append(line.charAt(pos));
						pos++;
					}
					// mot clés
					tokens.add( identifier.toString().matches(regex_python_keywords) ?
							new Token(TokenType.KEYWORD, identifier.toString(), line, nb_ligne) :

							(identifier.toString().matches(regex_python_boolean_literals) ?
							new Token(TokenType.BOOLEAN, identifier.toString(), line, nb_ligne):
							new Token(TokenType.IDENTIFIER, identifier.toString(), line, nb_ligne))
					);

				//Opérateurs
				}else if(c_str.matches(regex_python_operators)) {
					tokens.add(new Token(TokenType.OPERATOR, c_str, line, nb_ligne));
					pos++;

				//opérateurs de comparaison
				}else if(c_str.matches("[=!<>]")){
					String potential_operator = c_str + line.charAt(pos+1) ;
					if (potential_operator.matches(getRegex_python_compare_operators)){
						pos++;
						tokens.add(new Token(TokenType.COMPARE_OPERATOR, potential_operator, line, nb_ligne));
					}else {
						if(c_str.equals("=")){
							tokens.add(new Token(TokenType.ASSIGN, c_str, line, nb_ligne));
						}else{
							tokens.add(new Token(TokenType.COMPARE_OPERATOR, c_str, line, nb_ligne));
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
				} else if (c_str.equals(":")) {
					tokens.add(new Token(TokenType.COLON, c_str, line, nb_ligne));
					pos++;
				}else if (c_str.equals(",")) {
					tokens.add(new Token(TokenType.COMMA, c_str, line, nb_ligne));
					pos++;
				}else if(Character.isWhitespace(c)){//ignoré
					pos++;
				}else{
					error.lexicalError(line, String.valueOf(c), nb_ligne);
					pos++;
				}

			}
			tokens.add(new Token(TokenType.NEWLINE, separator, line, nb_ligne));
			// Fin de lignes
			nb_ligne++;
		}
		tokens.add(new Token(TokenType.EOF, "", "", nb_ligne));
		return tokens;

	}
}
