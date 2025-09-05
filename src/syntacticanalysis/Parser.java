package syntacticanalysis;

import lexicalanalysis.Token;
import lexicalanalysis.TokenType;
import syntacticanalysis.node.*;
import util.Error;

import java.util.ArrayList;
import java.util.List;

public class Parser {

	private List<Token> tokens;
	private int indexCurrentToken = 0;

//	private semanticAnalysis.SymbolTable symbolTable = new semanticAnalysis.SymbolTable();

	//==========Méthode utiles==========
	public Token getCurrentToken(){
		return tokens.get(indexCurrentToken);
	}
	public void consumeToken(TokenType type){
		if(getCurrentToken().type == type) {
			indexCurrentToken++;
		}else{
			Token current = getCurrentToken();
			Error.syntacticError(current.numLine, current + " expected, got " + type + " instead");
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


	// PROGRAM -> CLASS_DECLARATION*
	public List<Node> program() {
		List<Node> classes = new ArrayList<>();
		while(!eof() && getCurrentToken().value.equals("class")){
			classes.add(classDeclaration());
		}
		return classes;
	}

	// CLASS_DECLARATION -> 'class' IDENTIFIER (EXTENDS)? '{' CLASS_MEMBER* '}'
	public Node classDeclaration() {
		consumeToken(TokenType.KEYWORD);
		String className = getCurrentToken().value;
		consumeToken(TokenType.TYPE);
		int numLine = getCurrentToken().numLine;
		Node extend = new EmptyNode(numLine);
		if(getCurrentToken().type == TokenType.KEYWORD){
			extend = extend();
		}
		consumeToken(TokenType.LBRACE);
		List<Node> classMembers = new ArrayList<>();
		while (getCurrentToken().type != TokenType.RBRACE) {
			classMembers.add(classMember());
		}
		return new ClassDeclarationNode(numLine, new EmptyNode(numLine), className, extend, classMembers);
	}

	// EXTENDS -> 'extends' IDENTIFIER
	public Node extend() {
		consumeToken(TokenType.KEYWORD);
		return new IdentifierNode(getCurrentToken().numLine, getCurrentToken().value);
	}

	// CLASS_MEMBER -> METHOD_DECLARATION | FIELD_DECLARATION
	// METHOD_DECLARATION -> MODIFIERS? TYPE IDENTIFIER '(' PARAM* ')' BLOCK
	// FIELD_DECLARATION -> MODIFIERS? TYPE IDENTIFIER ('=' EXPRESSION)? ';'
	public Node classMember() {
		int numLine = getCurrentToken().numLine;
		Node modifiers = new EmptyNode(numLine);
		if (getCurrentToken().type==TokenType.KEYWORD) {
			modifiers = modifiers();
		}
		Node type = type();
		String identifier = getCurrentToken().value;
		consumeToken(TokenType.IDENTIFIER);
		if(getCurrentToken().type==TokenType.LPAREN){ // METHOD_DECLARATION
			numLine = getCurrentToken().numLine;
			consumeToken(TokenType.LPAREN);
			List<Node> params = new ArrayList<>();
			if(getCurrentToken().type != TokenType.RPAREN){
				params.add(param());
				while (getCurrentToken().type == TokenType.COMMA) {
					consumeToken(TokenType.COMMA);
					params.add(param());
				}
			}
			consumeToken(TokenType.RPAREN);
			Node block = block();
			return new MethodDeclarationNode(numLine, modifiers, type, identifier, params, block);

		}else{ // FIELD_DECLARATION
			numLine = getCurrentToken().numLine;
			Node expression = new EmptyNode(numLine);
			if (getCurrentToken().type==TokenType.ASSIGN){
				consumeToken(TokenType.ASSIGN);
				expression = expression();
			}
			consumeToken(TokenType.SEMICOLON);
			return new FieldDeclaration(numLine, modifiers, type, identifier, expression);
		}
	}

	// STATEMENT -> ASSIGNMENT ';'
	// 				| EXPRESSION ';'
	//				| IF_STATEMENT
	//              | WHILE_STATEMENT
	//              | RETURN_STATEMENT ';'
	public Node statement() {
		// ASSIGNMENT
		if (getCurrentToken().type == TokenType.TYPE) {
			Node assignment = assignment();
			consumeToken(TokenType.SEMICOLON);
			return assignment;

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
			}
		} else {
			Token current = getCurrentToken();
			Error.syntacticError(current.numLine, current.toString());
			throw new RuntimeException("Statement error: " + getCurrentToken());
		}
		return null;
	}

	// IF_STATEMENT -> 'if' '(' BOOL_EXPR ')' (BLOCK | STATEMENT)
	// 					('else if' '(' BOOL_EXPR ')' (BLOCK | STATEMENT))*
	//					('else' (BLOCK | STATEMENT))?
	public Node ifStatement(){
		List<IfNode> orElse = new ArrayList<>();
		int numLine = getCurrentToken().numLine;
		consumeToken(TokenType.KEYWORD);
		consumeToken(TokenType.LPAREN);
		ExpressionNode ifBoolExpr = booleanExpression();
		consumeToken(TokenType.RPAREN);
		Node ifBlock;
		if(getCurrentToken().type == TokenType.LBRACE) ifBlock = block();
		else ifBlock = statement();

		while (getCurrentToken().value.equals("else")){
			if(lookAhead(1).value.equals("if")) {
				numLine = getCurrentToken().numLine;
				consumeToken(TokenType.KEYWORD);
				consumeToken(TokenType.KEYWORD);
				consumeToken(TokenType.LPAREN);
				ExpressionNode elifBoolExpr = booleanExpression();
				consumeToken(TokenType.RPAREN);
				Node elifBlock;
				if(getCurrentToken().type == TokenType.LBRACE) elifBlock = block();
				else elifBlock = statement();
				orElse.add(new IfNode(numLine, elifBoolExpr, elifBlock, new ArrayList<>()));
			}else{
				numLine = getCurrentToken().numLine;
				consumeToken(TokenType.KEYWORD);
				Node elseBlock;
				if(getCurrentToken().type == TokenType.LBRACE) elseBlock = block();
				else elseBlock = statement();

				orElse.add(new IfNode(numLine, new LiteralNode(numLine, ""), elseBlock, new ArrayList<>()));
			}
		}
		return new IfNode(numLine, ifBoolExpr, ifBlock, orElse);
	}

	// WHILE_STATEMENT -> 'while' '(' BOOL_EXPR ')' (BLOCK | STATEMENT)
	public Node whileStatement(){
		int numLine = getCurrentToken().numLine;
		consumeToken(TokenType.KEYWORD);
		consumeToken(TokenType.LPAREN);
		ExpressionNode boolExpr = booleanExpression();
		consumeToken(TokenType.RPAREN);
		Node block;
		if(getCurrentToken().type == TokenType.LBRACE) block = block();
		else block = statement();
		return new WhileNode(numLine, boolExpr, block);
	}

	// MODIFIERS -> ACCESS_MODIFIER? OTHER_MODIFIER*
	Node modifiers(){
		StringBuilder str = new StringBuilder();
		int numLine = getCurrentToken().numLine;
		if (getCurrentToken().value.matches("(public|private)")) {
			str.append(getCurrentToken().value);
			consumeToken(TokenType.KEYWORD);
		}
		while (getCurrentToken().type==TokenType.KEYWORD){
			str.append(" ").append(getCurrentToken().value);
			consumeToken(TokenType.KEYWORD);
		}
		return new IdentifierNode(numLine, str.toString());
	}

	// PARAM -> TYPE IDENTIFIER
	public Node param(){
		int numLine = getCurrentToken().numLine;
		Node type = type();
		Node identifier = new IdentifierNode(numLine, getCurrentToken().value);
		Node parameter = new ParameterNode(numLine, type, identifier);
		consumeToken(TokenType.IDENTIFIER);
		return parameter;
	}

	// TYPE -> TYPE ('[' ']')*
	public Node type(){
		StringBuilder sb = new StringBuilder(getCurrentToken().value);
		int numLine = getCurrentToken().numLine;
		consumeToken(TokenType.TYPE);
		while (getCurrentToken().type==TokenType.LBRACKET) {
			sb.append("[]");
			consumeToken(TokenType.LBRACKET);
			consumeToken(TokenType.RBRACKET);
		}
		return new IdentifierNode(numLine, sb.toString());
	}

	// RETURN_STATEMENT -> 'return' EXPRESSION
	public Node returnStatement(){
		consumeToken(TokenType.KEYWORD);
		return new ReturnNode(getCurrentToken().numLine, expression());
	}

	//	ASSIGNMENT -> TYPE IDENTIFIER '=' EXPRESSION
	public Node assignment() {
		int numLine = getCurrentToken().numLine;
		Node type = type();
		String identifier = getCurrentToken().value;
		consumeToken(TokenType.IDENTIFIER);
		consumeToken(TokenType.ASSIGN);
		Node expression = expression();
		return new AssignmentNode(numLine, type.toString(), identifier, expression);
	}

	//	EXPRESSION	->	TERM (('+' | '-') TERM)*
	public ExpressionNode expression() {
		int numLine = getCurrentToken().numLine;
		ExpressionNode left = Term();
		while(getCurrentToken().value.matches("[+-]")) {
			String operator = getCurrentToken().value;
			consumeToken(TokenType.OPERATOR);
			ExpressionNode right = Term();
			left = new OperatorNode(numLine, left, operator, right);
		}
		return left;
	}

	//	BOOL_EXPR -> OR_EXPR
	public ExpressionNode booleanExpression(){
		return orExpression();
	}

	//	OR_EXPR -> AND_EXPR ('||' AND_EXPR)*
	public ExpressionNode orExpression(){
		int numLine = getCurrentToken().numLine;
		ExpressionNode left = andExpression();
		while(getCurrentToken().value.equals("||")) {
			consumeToken(TokenType.LOGICAL_OPERATOR);
			ExpressionNode right = andExpression();
			left = new OperatorNode(numLine, left, "||", right);
		}
		return left;
	}

	//	AND_EXPR -> NOT_EXPR ('and' NOT_EXPR)*
	public ExpressionNode andExpression(){
		int numLine = getCurrentToken().numLine;
		ExpressionNode left = notExpression();
		while(getCurrentToken().value.equals("&&")) {
			consumeToken(TokenType.LOGICAL_OPERATOR);
			ExpressionNode right = notExpression();
			left = new OperatorNode(numLine, left, "&&", right);
		}
		return left;
	}

	//	NOT_EXPR -> 'not' NOT_EXPR | COMPARE
	public ExpressionNode notExpression(){
		int numLine = getCurrentToken().numLine;
		if(getCurrentToken().value.equals("!")) {
			consumeToken(TokenType.LOGICAL_OPERATOR);
			return new NotNode(numLine, notExpression());
		}
		return compare();
	}

	//	COMPARE -> EXPRESSION (COMPARE_OP EXPRESSION)?
	public ExpressionNode compare(){
		int numLine = getCurrentToken().numLine;
		ExpressionNode left = expression();
		if(getCurrentToken().type == TokenType.COMPARE_OPERATOR) {
			String operator = getCurrentToken().value;
			consumeToken(TokenType.COMPARE_OPERATOR);
			ExpressionNode right = expression();
			left = new OperatorNode(numLine, left, operator, right);
		}
		return left;
	}

	// BLOCK -> '{' STATEMENT* '}'

	public Node block(){
		int numLine = getCurrentToken().numLine;
		List<Node> statements = new ArrayList<>();
		consumeToken(TokenType.LBRACE);
		while (getCurrentToken().type != TokenType.RBRACE) {
			statements.add(statement());
		}
		consumeToken(TokenType.RBRACE);
		return new BlockNode(numLine, statements);
	}


	//	TERM	->	FACTOR (('*' | '/' | '%') FACTOR)*
	public ExpressionNode Term() {
		int numLine = getCurrentToken().numLine;
		ExpressionNode left = Factor();
		while(getCurrentToken().type == TokenType.OPERATOR && getCurrentToken().value.matches("[*/%]")) {
			String operator = getCurrentToken().value;
			consumeToken(TokenType.OPERATOR);
			ExpressionNode right = Factor();
			left = new OperatorNode(numLine, left, operator, right);
		}
		return left;
	}

// FACTOR -> LITERAL
//          | IDENTIFIER
//          | BOOLEAN
//          | '(' EXPRESSION ')'
//			| FUNCTION_CALL
	public ExpressionNode Factor() {
		int numLine = getCurrentToken().numLine;
		Token currentToken = getCurrentToken();
		switch (currentToken.type) {
			case TokenType.LITERAL : {// LITERAL
				consumeToken(TokenType.LITERAL);
				return new LiteralNode(numLine, currentToken.value);
			}
			case TokenType.IDENTIFIER : {
				if(lookAhead(1).type == TokenType.LPAREN) {//FUNCTION_CALL
					return methodCall();
				}else{
					consumeToken(TokenType.IDENTIFIER);//IDENTIFIER
					return new IdentifierNode(numLine, currentToken.value);
				}
			}
			case TokenType.LPAREN : {//'(' EXPRESSION ')'
				consumeToken(TokenType.LPAREN);
				return expression();
			}
			case TokenType.BOOLEAN : {
				String bool = getCurrentToken().value;
				consumeToken(TokenType.BOOLEAN);
				return new BooleanNode(numLine, bool);
			}
			default : {
				Error.syntacticError(currentToken.numLine, currentToken.toString());
				throw new RuntimeException(currentToken.toString());
//				return new EmptyNode();
			}
		}
	}

	//	METHOD_CALL -> IDENTIFIER '(' ARGS* ')'
	public ExpressionNode methodCall(){
		int numLine = getCurrentToken().numLine;
		String identifier = getCurrentToken().value;
		consumeToken(TokenType.IDENTIFIER);
		consumeToken(TokenType.LPAREN);
		List<Node> args = new ArrayList<>();
		if(getCurrentToken().type == TokenType.RPAREN) {
			consumeToken(TokenType.RPAREN);
			return new MethodCallNode(numLine, identifier, args);
		}else {
			if(getCurrentToken().type != TokenType.RPAREN) {
				args.add(arg());
				while (getCurrentToken().type==TokenType.COMMA) {
					consumeToken(TokenType.COMMA);
					args.add(arg());
				}
			}

			consumeToken(TokenType.RPAREN);
			return new MethodCallNode(numLine, identifier, args);
		}
	}

	//	ARGS	->	EXPRESSION
	public Node arg(){
		return expression();
	}
}