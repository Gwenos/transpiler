package syntacticAnalysis;

import lexicalAnalysis.Token;
import lexicalAnalysis.TokenType;
import syntacticAnalysis.node.*;
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
			Error.syntacticError(current.line, current + " expected, got " + type + " instead", current.numLine);
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
		Node extend = new EmptyNode();
		if(getCurrentToken().type == TokenType.KEYWORD){
			extend = extend();
		}
		consumeToken(TokenType.LBRACE);
		List<Node> classMembers = new ArrayList<>();
		while (getCurrentToken().type != TokenType.RBRACE) {
			classMembers.add(classMember());
		}
		return new ClassDeclarationNode(new EmptyNode(), className, extend, classMembers);
	}

	// EXTENDS -> 'extends' IDENTIFIER
	public Node extend() {
		consumeToken(TokenType.KEYWORD);
		return new IdentifierNode(getCurrentToken().value);
	}

	// CLASS_MEMBER -> METHOD_DECLARATION | FIELD_DECLARATION
	// METHOD_DECLARATION -> MODIFIERS? TYPE IDENTIFIER '(' PARAM* ')' BLOCK
	// FIELD_DECLARATION -> MODIFIERS? TYPE IDENTIFIER ('=' EXPRESSION)? ';'
	public Node classMember() {
		Node modifiers = new EmptyNode();
		if (getCurrentToken().type==TokenType.KEYWORD) {
			modifiers = modifiers();
		}
		Node type = type();
		String identifier = getCurrentToken().value;
		consumeToken(TokenType.IDENTIFIER);
		if(getCurrentToken().type==TokenType.LPAREN){ // METHOD_DECLARATION
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
			return new MethodDeclarationNode(modifiers, type, identifier, params, block);

		}else{ // FIELD_DECLARATION
			Node expression = new EmptyNode();
			if (getCurrentToken().type==TokenType.ASSIGN){
				consumeToken(TokenType.ASSIGN);
				expression = expression();
			}
			consumeToken(TokenType.SEMICOLON);
			return new FieldDeclaration(modifiers, type, identifier, expression);
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
			Error.syntacticError(current.line, current.toString(), current.numLine);
			throw new RuntimeException("Statement error: " + getCurrentToken());
		}
		return null;
	}

	// IF_STATEMENT -> 'if' '(' BOOL_EXPR ')' (BLOCK | STATEMENT)
	// 					('else if' '(' BOOL_EXPR ')' (BLOCK | STATEMENT))*
	//					('else' (BLOCK | STATEMENT))?
	public Node ifStatement(){
		List<IfNode> orElse = new ArrayList<>();

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
				orElse.add(new IfNode(elifBoolExpr, elifBlock, new ArrayList<>()));
			}else{
				consumeToken(TokenType.KEYWORD);
				Node elseBlock;
				if(getCurrentToken().type == TokenType.LBRACE) elseBlock = block();
				else elseBlock = statement();

				orElse.add(new IfNode(new EmptyNode(), elseBlock, new ArrayList<>()));
			}
		}
		return new IfNode(ifBoolExpr, ifBlock, orElse);
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

	// MODIFIERS -> ACCESS_MODIFIER? OTHER_MODIFIER*
	Node modifiers(){
		StringBuilder str = new StringBuilder();
		if (getCurrentToken().value.matches("(public|private)")) {
			str.append(getCurrentToken().value);
			consumeToken(TokenType.KEYWORD);
		}
		while (getCurrentToken().type==TokenType.KEYWORD){
			str.append(" ").append(getCurrentToken().value);
			consumeToken(TokenType.KEYWORD);
		}
		return new IdentifierNode(str.toString());
	}

	// PARAM -> TYPE IDENTIFIER
	public Node param(){
		Node type = type();
		Node identifier = new IdentifierNode(getCurrentToken().value);
		Node parameter = new ParameterNode(type, identifier);
		consumeToken(TokenType.IDENTIFIER);
		return parameter;
	}

	// TYPE -> TYPE ('[' ']')*
	public Node type(){
		StringBuilder sb = new StringBuilder(getCurrentToken().value);
		consumeToken(TokenType.TYPE);
		while (getCurrentToken().type==TokenType.LBRACKET) {
			sb.append("[]");
			consumeToken(TokenType.LBRACKET);
			consumeToken(TokenType.RBRACKET);
		}
		return new IdentifierNode(sb.toString());
	}

	// RETURN_STATEMENT -> 'return' EXPRESSION
	public Node returnStatement(){
		consumeToken(TokenType.KEYWORD);
		return new ReturnNode(expression());
	}

	//	ASSIGNMENT -> TYPE IDENTIFIER '=' EXPRESSION
	public Node assignment() {
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
					return methodCall();
				}else{
					consumeToken(TokenType.IDENTIFIER);//IDENTIFIER
					return new IdentifierNode(currentToken.value);
				}
			}
			case TokenType.LPAREN : {//'(' EXPRESSION ')'
				consumeToken(TokenType.LPAREN);
				return expression();
			}
//			case TokenType.BOOLEAN : {
//				System.out.println("ui");
//			}
			default : {
				Error.syntacticError(currentToken.line, currentToken.toString(), currentToken.numLine);
				throw new RuntimeException(currentToken.toString());
//				return new EmptyNode();
			}
		}
	}

	//	FUNCTION_CALL -> IDENTIFIER '(' ARGS* ')'
	public Node methodCall(){
		String identifier = getCurrentToken().value;
		consumeToken(TokenType.IDENTIFIER);
		consumeToken(TokenType.LPAREN);
		List<Node> args = new ArrayList<>();
		if(getCurrentToken().type == TokenType.RPAREN) {
			consumeToken(TokenType.RPAREN);
			return new FonctionCallNode(identifier, args);
		}else {
			if(getCurrentToken().type != TokenType.RPAREN) {
				args.add(arg());
				while (getCurrentToken().type==TokenType.COMMA) {
					consumeToken(TokenType.COMMA);
					args.add(arg());
				}
			}

			consumeToken(TokenType.RPAREN);
			return new FonctionCallNode(identifier, args);
		}
	}

	//	ARGS	->	EXPRESSION
	public Node arg(){
		return expression();
	}
}