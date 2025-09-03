import javax.lang.model.element.TypeElement;
import java.beans.Expression;
import java.util.ArrayList;
import java.util.List;

public class Parser {

	private List<Token> tokens;
	private int indexCurrentToken = 0;

	private SymbolTable symbolTable = new SymbolTable();

	//==========Méthode utiles==========
	public Token getCurrentToken(){
		return tokens.get(indexCurrentToken);
	}
	public void consumeToken(TokenType type){
		if(getCurrentToken().type == type) {
			indexCurrentToken++;
		}else{
			Token current = getCurrentToken();
			Error.lexicalError(current.line, type + " consommé au lieu de "+current, current.nb_ligne);
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
		return program();
	}

	// PROGRAM -> STATEMENT*
	public List<Node> program() {
		List<Node> statements = new ArrayList<>();
		while(!eof()){
			Node statement = statement();
			statements.add(statement);
		}
		return statements;
	}

	// STATEMENT -> ASSIGNMENT ';'
	// 				| EXPRESSION ';'
	//				| IF_STATEMENT
	//              | WHILE_STATEMENT
	//              | DEF_STATEMENT
	//              | RETURN_STATEMENT ';'
	public Node statement() {
		// ASSIGNEMENT
		if (getCurrentToken().type == TokenType.TYPE) {
			Node assignement = assignement();
			consumeToken(TokenType.SEMICOLON);
			return assignement;

		// EXPRESSION
		}else if(getCurrentToken().type == TokenType.IDENTIFIER || getCurrentToken().type == TokenType.BOOLEAN || getCurrentToken().type == TokenType.LPAREN){
			Node expression = expression();
			consumeToken(TokenType.SEMICOLON);
			return expression;

		} else if (getCurrentToken().type == TokenType.KEYWORD) {
			switch (getCurrentToken().value) {
				case "if":
					return ifStatement();
				case "while":
					return whileStatement();
				case "return":
					Node returnStatement = returnStatement();
					consumeToken(TokenType.SEMICOLON);
					return returnStatement;
				default:
					return defStatement();
			}
		} else {
			Token current = getCurrentToken();
			Error.lexicalError(current.line, current.toString(), current.nb_ligne);
			throw new RuntimeException("Erreur Statement: " + getCurrentToken());
		}
	}

	// IF_STATEMENT -> 'if' '(' BOOL_EXPR ')' (BLOCK | STATEMENT)
	// 					('else if' '(' BOOL_EXPR ')' (BLOCK | STATEMENT))*
	//					('else' (BLOCK | STATEMENT))?
	public Node ifStatement(){
		List<IfNode> orElses = new ArrayList<>();

		consumeToken(TokenType.KEYWORD);
		consumeToken(TokenType.LPAREN);
		Node ifBoolExpr = booleanExpression();
		consumeToken(TokenType.RPAREN);
		Node ifBlock;
		if(getCurrentToken().type == TokenType.LBRACE) ifBlock = block();
		else ifBlock = statement();

		while (getCurrentToken().value.equals("else")){
			if(lookAhead(1).value.equals("if")) {
				consumeToken(TokenType.KEYWORD);
				consumeToken(TokenType.KEYWORD);
				consumeToken(TokenType.LPAREN);
				Node elifBoolExpr = booleanExpression();
				consumeToken(TokenType.RPAREN);
				Node elifBlock;
				if(getCurrentToken().type == TokenType.LBRACE) elifBlock = block();
				else elifBlock = statement();
				orElses.add(new IfNode(elifBoolExpr, elifBlock, new ArrayList<>()));
			}else{
				consumeToken(TokenType.KEYWORD);
				Node elseBlock;
				if(getCurrentToken().type == TokenType.LBRACE) elseBlock = block();
				else elseBlock = statement();

				orElses.add(new IfNode(new LiteralNode(""), elseBlock, new ArrayList<>()));
			}
		}
		return new IfNode(ifBoolExpr, ifBlock, orElses);
	}

	// WHILE_STATEMENT -> 'while' '(' BOOL_EXPR ')' (BLOCK | STATEMENT)
	public Node whileStatement(){
		consumeToken(TokenType.KEYWORD);
		consumeToken(TokenType.LPAREN);
		Node boolExpr = booleanExpression();
		consumeToken(TokenType.RPAREN);
		Node block;
		if(getCurrentToken().type == TokenType.LBRACE) block = block();
		else block = statement();
		return new WhileNode(boolExpr, block);
	}

	// DEF_STATEMENT -> MODIFIERS? TYPE IDENTIFIER '(' PARAMS? ')' BLOCK
	public Node defStatement(){
		Node modifiers = new LiteralNode("");
		if(getCurrentToken().type==TokenType.KEYWORD){
			modifiers = modifiers();
		}
		consumeToken(TokenType.TYPE);
		String identifier = getCurrentToken().value;
		consumeToken(TokenType.IDENTIFIER);
		consumeToken(TokenType.LPAREN);
		Node params = new LiteralNode("");
		if(getCurrentToken().type==TokenType.TYPE) {
			params = params();
		}
		consumeToken(TokenType.RPAREN);
		Node block = block();
		return new DefNode(modifiers, identifier, params, block);
	}
	// MODIFIERS -> ACCESS_MODIFIER? OTHER_MODIFIER*
	Node modifiers(){
		List<Node> nodes = new ArrayList<>();
		if (getCurrentToken().value.matches("(public|private)")) {
			Node access = new LiteralNode(getCurrentToken().value);
			consumeToken(TokenType.KEYWORD);
			nodes.add(access);
		}
		while (getCurrentToken().type==TokenType.KEYWORD){
			Node other = new LiteralNode(getCurrentToken().value);
			consumeToken(TokenType.KEYWORD);
			nodes.add(other);
		}
		return new ConcatNode(nodes);
	}

	// PARAMS -> TYPE IDENTIFIER (',' TYPE IDENTIFIER)*
	public Node params(){
		Node type = type();
		Node identifier = new LiteralNode(getCurrentToken().value);
		consumeToken(TokenType.IDENTIFIER);
		Node left = new ConcatNode(new Node[]{type, identifier});
		while (getCurrentToken().type==TokenType.COMMA) {
			consumeToken(TokenType.COMMA);
			Node type2 = type();
			consumeToken(TokenType.TYPE);
			Node identifier2 = new LiteralNode(getCurrentToken().value);
			consumeToken(TokenType.IDENTIFIER);
			Node right = new ConcatNode(new Node[]{type2, identifier2});
			left = new OperatorNode(left, ",", right);
		}
		return left;
	}

	// TYPE -> TYPE ('[' ']')*
	public Node type(){
		List<Node> nodes = new ArrayList<>();
		nodes.add(new LiteralNode(getCurrentToken().value));
		consumeToken(TokenType.TYPE);
		while (getCurrentToken().type==TokenType.LBRACKET) {
			nodes.add(new LiteralNode("[]"));
			consumeToken(TokenType.LBRACKET);
			consumeToken(TokenType.RBRACKET);
		}
		return new ConcatNode(nodes);
	}

	// RETURN_STATEMENT -> 'return' EXPRESSION
	public Node returnStatement(){
		consumeToken(TokenType.KEYWORD);
		return new ReturnNode(expression());
	}

	//	ASSIGNMENT -> TYPE IDENTIFIER '=' EXPRESSION
	public Node assignement() {
		Node type = type();
		String identifier = getCurrentToken().value;
		consumeToken(TokenType.IDENTIFIER);
		consumeToken(TokenType.ASSIGN);
		Node expression = expression();
		return new AssignmentNode(type.toString(), identifier, expression);
	}

	//	EXPRESSION	->	TERM (('+' | '-') TERM)*
	public Node expression() {
		Node left = Term();
		while(getCurrentToken().type == TokenType.OPERATOR && getCurrentToken().value.matches("[+-]")) {
			String operator = getCurrentToken().value;
			consumeToken(TokenType.OPERATOR);
			Node right = Term();
			left = new OperatorNode(left, operator, right);
		}
		return left;
	}

	//	BOOL_EXPR -> OR_EXPR
	public Node booleanExpression(){
		return orExpression();
	}

	//	OR_EXPR -> AND_EXPR ('||' AND_EXPR)*
	public Node orExpression(){
		Node left = andExpression();
		while(getCurrentToken().value.equals("||")) {
			consumeToken(TokenType.LOGICAL_OPERATOR);
			Node right = andExpression();
			left = new OperatorNode(left, "||", right);
		}
		return left;
	}

	//	AND_EXPR -> NOT_EXPR ('and' NOT_EXPR)*
	public Node andExpression(){
		Node left = notExpression();
		while(getCurrentToken().value.equals("&&")) {
			consumeToken(TokenType.LOGICAL_OPERATOR);
			Node right = notExpression();
			left = new OperatorNode(left, "&&", right);
		}
		return left;
	}

	//	NOT_EXPR -> 'not' NOT_EXPR | COMPARE
	public Node notExpression(){
		if(getCurrentToken().value.equals("!")) {
			consumeToken(TokenType.LOGICAL_OPERATOR);
			return new NotNode(notExpression());
		}
		return compare();
	}

	//	COMPARE -> EXPRESSION (COMPARE_OP EXPRESSION)?
	public Node compare(){
		Node left = expression();
		if(getCurrentToken().type == TokenType.COMPARE_OPERATOR) {
			String operator = getCurrentToken().value;
			consumeToken(TokenType.COMPARE_OPERATOR);
			Node right = expression();
			left = new OperatorNode(left, operator, right);
		}
		return left;
	}

	// BLOCK -> '{' STATEMENT* '}'

	public Node block(){
		List<Node> statements = new ArrayList<>();
		consumeToken(TokenType.LBRACE);
		while (getCurrentToken().type != TokenType.RBRACE) {
			statements.add(statement());
		}
		consumeToken(TokenType.RBRACE);
		return new BlockNode(statements);
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

// FACTOR -> LITERAL
//          | IDENTIFIER
//          | BOOLEAN
//          | '(' EXPRESSION ')'
//			| FUNCTION_CALL
	public Node Factor() {
		Token currentToken = getCurrentToken();

		switch (currentToken.type) {
			case TokenType.LITERAL : {// LITERAL
				consumeToken(TokenType.LITERAL);
				return new LiteralNode(currentToken.value);
			}
			case TokenType.IDENTIFIER : {
				if(lookAhead(1).type == TokenType.LPAREN) {//FUNCTION_CALL
					return fonctionCall();
				}else{
					consumeToken(TokenType.IDENTIFIER);//IDENTIFIER
					return new LiteralNode(currentToken.value);
				}
			}
			case TokenType.LPAREN : {//'(' EXPRESSION ')'
				consumeToken(TokenType.LPAREN);
				return expression();
			}
			default : {
				Error.lexicalError(currentToken.line, currentToken.toString(), currentToken.nb_ligne);
				return new LiteralNode("");
			}
		}
	}

	//	FUNCTION_CALL -> IDENTIFIER '(' ARGS? ')'
	public Node fonctionCall(){
		String identifier = getCurrentToken().value;
		consumeToken(TokenType.IDENTIFIER);
		consumeToken(TokenType.LPAREN);
		if(getCurrentToken().type == TokenType.RPAREN) {
			consumeToken(TokenType.RPAREN);
			return new FonctionCallNode(identifier, new LiteralNode(""));
		}else {
			Node args = args();
			consumeToken(TokenType.RPAREN);
			return new FonctionCallNode(identifier, args);
		}
	}

	//	ARGS	->	EXPRESSION (',' EXPRESSION)*
	public Node args(){
		Node left = expression();
		while (getCurrentToken().type == TokenType.COMMA){
			consumeToken(TokenType.COMMA);
			Node right = expression();
			left = new ArgumentNode(left, right);
		}
		return left;
	}
}