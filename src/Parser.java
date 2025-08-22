import java.util.ArrayList;
import java.util.List;

public class Parser {

	private List<Token> tokens;
	private int indexCurrentToken = 0;

	//==========Méthode outils==========
	public Token getCurrentToken(){
		return tokens.get(indexCurrentToken);
	}
	public void consumeToken(TokenType type){
		if(getCurrentToken().type == type) indexCurrentToken++;
		else throw new RuntimeException("Unexpected token: " + getCurrentToken());
	}
	private Token lookAhead(int cmb) {
		return tokens.get(indexCurrentToken+cmb);
	}
	private boolean eof(){
		return getCurrentToken().type == TokenType.EOF;
	}

	//==========Méthode de parse

	public List<Node> parse(List<Token> tokens) {
		this.tokens = tokens;
		return parseProgram();
	}

	//	PROGRAM -> (STATEMENT NEWLINE*)* EOF
	public List<Node> parseProgram() {
		List<Node> statements = new ArrayList<>();
		while(!eof()){
			Node statement = parseStatement();
			consumeToken(TokenType.NEWLINE);
			statements.add(statement);
		}
		return statements;
	}

	//	STATEMENT	->	ASSIGNMENT
	//					| EXPRESSION
	//                  | IF_STATEMENT
	//                  | WHILE_STATEMENT
	//                  | DEF_STATEMENT
	//                  | RETURN_STATEMENT
	public Node parseStatement() {
		//Assignement
		if (getCurrentToken().type == TokenType.IDENTIFIER) {
			if (lookAhead(1).type == TokenType.OPERATOR && lookAhead(1).value.equals("=")) {
				return parseAssignement();
			} else {
				return parseExpression();
			}
		} else if (getCurrentToken().type == TokenType.KEYWORD) {
			switch (getCurrentToken().value) {
				case "if":
					return ifStatement();
				case "while":
					return whileStatement();
				case "def":
					return defStatement();
				case "return":
					return returnStatement();
			}
		} else if (getCurrentToken().type != TokenType.NEWLINE) {
			throw new RuntimeException("Erreur parseStatement: " + getCurrentToken());
		}
		return null;
	}

	//	IF_STATEMENT  -> 'if' EXPRESSION ':' BLOCK ('elif' EXPRESSION ':' BLOCK)* ('else' ':' BLOCK)?
	public Node ifStatement(){
		return null;
	}
	public Node whileStatement(){
		return null;
	}
	public Node defStatement(){
		return null;
	}
	public Node returnStatement(){
		return null;
	}

	//	ASSIGNMENT	->	IDENTIFIER	'='	EXPRESSION
	public Node parseAssignement() {
		String identifier = getCurrentToken().value;
		consumeToken(TokenType.IDENTIFIER);
		consumeToken(TokenType.OPERATOR);//	String operator = "=";
		Node expression = parseExpression();
		return new AssignmentNode(identifier, expression);
	}

	//	EXPRESSION	->	TERM (('+' | '-') TERM)*
	public Node parseExpression() {
		Node left = parseTerm();
		while(getCurrentToken().type == TokenType.OPERATOR && getCurrentToken().value.matches("[+-]")) {
			String operator = getCurrentToken().value;
			consumeToken(TokenType.OPERATOR);//=
			Node right = parseTerm();
			left = new OperatorNode(left, operator, right);
		}
		return left;
	}

	//	TERM	->	FACTOR (('*' | '/' | '%') FACTOR)*
	public Node parseTerm() {
		Node left = parseFactor();
		while(getCurrentToken().type == TokenType.OPERATOR && getCurrentToken().value.matches("[*/%]")) {
			String operator = getCurrentToken().value;
			consumeToken(TokenType.OPERATOR);
			Node right = parseFactor();
			left = new OperatorNode(left, operator, right);
		}
		return left;
	}

	//	FACTOR	->	NUMBER
	//              | STRING
	//              | IDENTIFIER
	//              | '(' EXPRESSION ')'
	//				| FUNCTION_CALL
	public Node parseFactor() {
		Token currentToken = getCurrentToken();
		if(getCurrentToken().type == TokenType.LITERAL) {
			consumeToken(TokenType.LITERAL);
			return new LiteralNode(currentToken.value);
		} else if (getCurrentToken().type == TokenType.IDENTIFIER) {
			consumeToken(TokenType.IDENTIFIER);
			return new IdentifierNode(currentToken.value);
		} //else if (getCurrentToken().type == TokenType.LPAREN) {
//			consumeToken(TokenType.LPAREN);
//			Node expression = parseExpression();
//			consumeToken(TokenType.RPAREN);
//			return new ExpressionNode();
//		}
		return null;
	}

}
