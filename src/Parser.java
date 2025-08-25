import java.beans.Expression;
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
		if(getCurrentToken().type == type) {
			indexCurrentToken++;
		}else{
			Token current = getCurrentToken();
			Error.lexicalError(current.line, type + " consommé au lieu de "+current.toString(), current.nb_ligne);
			throw new RuntimeException(current.toString());
		}
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
		return Program();
	}

	//	PROGRAM -> (STATEMENT NEWLINE)* EOF
	public List<Node> Program() {
		List<Node> statements = new ArrayList<>();
		while(!eof()){
			Node statement = Statement();
			while (getCurrentToken().type == TokenType.NEWLINE) consumeToken(TokenType.NEWLINE);	//lignes vides
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
	public Node Statement() {
		//Assignement
		if (getCurrentToken().type == TokenType.IDENTIFIER || getCurrentToken().type == TokenType.LITERAL) {
			if (lookAhead(1).type == TokenType.ASSIGN) {
				return Assignement();
			} else {
				if(getCurrentToken().type == TokenType.RPAREN) consumeToken(TokenType.RPAREN);	//c.f méthode Factor
				return Expression();
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
			Token current = getCurrentToken();
			Error.lexicalError(current.line, current.toString(), current.nb_ligne);
			throw new RuntimeException("Erreur Statement: " + getCurrentToken());
		}
		return null;
	}

	//	IF_STATEMENT  -> 'if' BOOLEXPR ':' BLOCK ('elif' BOOLEXPR ':' BLOCK)* ('else' ':' BOOLEXPR)?
	public Node ifStatement(){
		consumeToken(TokenType.KEYWORD);
		return new IfNode(BooleanExpression());
	}

	// WHILE_STATEMENT -> 'while' EXPRESSION ':' BLOCK
	public Node whileStatement(){
		consumeToken(TokenType.KEYWORD);
		return new WhileNode(BooleanExpression());
	}
	public Node defStatement(){
		return null;
	}
	public Node returnStatement(){
		return null;
	}

	//	ASSIGNMENT	->	IDENTIFIER	'='	EXPRESSION
	public Node Assignement() {
		String identifier = getCurrentToken().value;
		consumeToken(TokenType.IDENTIFIER);
		consumeToken(TokenType.ASSIGN);//	String operator = "=";
		Node expression = Expression();
		return new AssignmentNode(identifier, expression);
	}

	//	EXPRESSION	->	TERM (('+' | '-') TERM)*
	public Node Expression() {

		Node left = Term();
		while(getCurrentToken().type == TokenType.OPERATOR && getCurrentToken().value.matches("[+-]")) {
			String operator = getCurrentToken().value;
			consumeToken(TokenType.OPERATOR);
			Node right = Term();
			left = new OperatorNode(left, operator, right);
		}
		if(getCurrentToken().type == TokenType.RPAREN) consumeToken(TokenType.RPAREN);	//c.f. méthode Factor
		return left;
	}

	//	BOOL_EXPR -> OR_EXPR
	public Node BooleanExpression(){
		return OrExpression();
	}

	//	OR_EXPR -> AND_EXPR ('or' AND_EXPR)*
	public Node OrExpression(){
		Node left = AndExpression();
		while(getCurrentToken().type == TokenType.KEYWORD && getCurrentToken().value.equals("or")) {
			consumeToken(TokenType.KEYWORD);
			Node right = AndExpression();
			left = new OperatorNode(left, "or", right);
		}
		return left;
	}

	//	AND_EXPR -> NOT_EXPR ('and' NOT_EXPR)*
	public Node AndExpression(){
		Node left = NotExpression();
		while(getCurrentToken().type == TokenType.KEYWORD && getCurrentToken().value.equals("and")) {
			consumeToken(TokenType.KEYWORD);
			Node right = NotExpression();
			left = new OperatorNode(left, "and", right);
		}
		return left;
	}

	//	NOT_EXPR -> 'not' NOT_EXPR | COMPARE
	public Node NotExpression(){
		if(getCurrentToken().type == TokenType.KEYWORD && getCurrentToken().value.equals("not")) {
			consumeToken(TokenType.KEYWORD);
			return new NotNode(NotExpression());
		}else{
			return Compare();
		}
	}

	//	COMPARE -> EXPRESSION (COMPARE_OP EXPRESSION)?
	public Node Compare(){
		Node left = Expression();
		if(getCurrentToken().type == TokenType.COMPARE_OPERATOR) {
			String operator = getCurrentToken().value;
			consumeToken(TokenType.COMPARE_OPERATOR);
			Node right = Expression();
			left = new OperatorNode(left, operator, right);
		}
		if(getCurrentToken().type==TokenType.COLON)consumeToken(TokenType.COLON);
		return left;
	}


	//	TERM	->	FACTOR (('*' | '/' | '%') FACTOR)*
	public Node Term() {
		Node left = Factor();
		while(getCurrentToken().type == TokenType.OPERATOR && getCurrentToken().value.matches("[*/%]")) {
			String operator = getCurrentToken().value;
			consumeToken(TokenType.OPERATOR);
			Node right = Factor();
			left = new OperatorNode(left, operator, right);
		}
		return left;
	}

	//	FACTOR	->	NUMBER
	//              | STRING
	//              | IDENTIFIER
	//              | '(' EXPRESSION ')'
	//				| FUNCTION_CALL
	public Node Factor() {
		Token currentToken = getCurrentToken();

		switch (currentToken.type) {
			case TokenType.LITERAL : {//NUMBER | STRING
				consumeToken(TokenType.LITERAL);
				return new LiteralNode(currentToken.value);
			}
			case TokenType.IDENTIFIER : {
				if(lookAhead(1).type == TokenType.LPAREN) {//FUNCTION_CALL
					return FonctionCall();
				}else{
					consumeToken(TokenType.IDENTIFIER);//IDENTIFIER
					return new IdentifierNode(currentToken.value);
				}
			}
			case TokenType.LPAREN : {//'(' EXPRESSION ')'
				consumeToken(TokenType.LPAREN);
				return Expression();
			}
			default : {
//				Error.lexicalError(currentToken.line, currentToken.toString(), currentToken.nb_ligne);
				return new LiteralNode("");
			}
		}
	}

	//	FUNCTION_CALL	->	IDENTIFIER '(' ARGS? ')'
	public Node FonctionCall(){
		String identifier = getCurrentToken().value;
		consumeToken(TokenType.IDENTIFIER);
		consumeToken(TokenType.LPAREN);
		if(getCurrentToken().type == TokenType.RPAREN) {
			consumeToken(TokenType.RPAREN);
			return new FonctionCallNode(identifier, Args());
		}else {
			Node args = Args();
			return new FonctionCallNode(identifier, args);
		}
	}

	//	ARGS	->	EXPRESSION (',' EXPRESSION)*
	public Node Args(){
		Node left = Expression();
		while (getCurrentToken().type == TokenType.COMMA){
			consumeToken(TokenType.COMMA);
			Node right = Expression();
			left = new ArgumentNode(left, right);
		}
		return left;
	}

}