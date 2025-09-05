package semanticanalysis;

import syntacticanalysis.node.*;
import util.Error;

import java.beans.Expression;
import java.util.List;

public class Analyser {

	private final SymbolTable symbolTable = new SymbolTable();
	private String currentMethodType = null;

	public SymbolTable analyse(List<Node> nodes){
		for(Node node : nodes){
			analyse(node);
		}
		return symbolTable;
	}

	public String analyse(Node node){

		switch (node){

			case ClassDeclarationNode classDeclaration :
				this.symbolTable.declare(node.numLine, classDeclaration.className, "class");
				for(Node n : classDeclaration.classMembers){
					analyse(n);
				}
				break;

			case FieldDeclaration fieldDeclaration:
				this.symbolTable.declare(node.numLine, fieldDeclaration.fieldName, fieldDeclaration.type.toString());
				break;

			case MethodDeclarationNode methodDeclaration :
				//tu ne compares pas les types attendus vs donnés.
				this.symbolTable.declare(node.numLine, methodDeclaration.methodName, methodDeclaration.type.toString());
				this.currentMethodType = methodDeclaration.type.toString();
				this.symbolTable.enterScope();
				for(Node n : methodDeclaration.params){
					analyse(n);
				}
				analyse(methodDeclaration.block);
				this.symbolTable.exitScope();
				break;

			case ParameterNode parameter :
				this.symbolTable.declare(node.numLine, parameter.identifier.toString(), parameter.type.toString());
				break;

			case AssignmentNode assignement :
				String realType = analyse(assignement.value);
				if(!realType.equals(assignement.type)){
					Error.semanticError(node.numLine, "Type mismatch '" + assignement.identifier + "' got " + realType + " instead of " + assignement.type);
				}
				this.symbolTable.declare(node.numLine, assignement.identifier, assignement.type);
				break;

			case IfNode ifNode :
				if(!analyse(ifNode.boolExpr).equals("empty")) {
					if (!analyse(ifNode.boolExpr).equals("boolean")) {
						Error.semanticError(ifNode.numLine, "if expect a boolean expression");
					}
				}
				analyse(ifNode.block);
				for(Node n : ifNode.orElse){
					analyse(n);
				}
				break;

			case BlockNode block :
				this.symbolTable.enterScope();
				for(Node statement : block.statements){
					analyse(statement);
				}
				this.symbolTable.exitScope();
				break;

			case ReturnNode returnNode :
				String returnType = analyse(returnNode.expression);
				if(this.currentMethodType != null){
					if(!returnType.equals(this.currentMethodType)){
						Error.semanticError(node.numLine, "return " + returnType + " instead of " + this.currentMethodType);
					}else{
						this.currentMethodType = null;
					}
				}
				break;

			case MethodCallNode methodCall :
				Symbol method = this.symbolTable.lookup(node.numLine, methodCall.identifier);
				for(Node arg : methodCall.args){
					analyse(arg);
				}
				return method.type;

			case WhileNode whileNode :
				if(!analyse(whileNode.bool_expr).equals("boolean")) Error.semanticError(whileNode.numLine, "while expect a boolean expression");
				analyse(whileNode.bool_expr);
				analyse(whileNode.block);
				break;

			case NotNode notNode :
				String expressionType = analyse(notNode.expression);
				if(!expressionType.equals("boolean")) {
					Error.semanticError(notNode.numLine, "! expect a boolean expression");
				}
				return "boolean";

			case OperatorNode operatorNode :
				String leftType = analyse(operatorNode.left);
				String operator = operatorNode.operator;
				String rightType = analyse(operatorNode.right);
				return getType(operatorNode.numLine, leftType, operator, rightType);

			case IdentifierNode identifierNode :
				Symbol identifier = this.symbolTable.lookup(node.numLine, identifierNode.value);
				if(identifier != null){
					return identifier.type;
				}
				break;

			case LiteralNode literal:
				return literal.type;

			case BooleanNode _:
				return "boolean";

			case EmptyNode _ :
				return "empty";

			default :
				Error.semanticError(node.numLine, "Unexpected Node: "+node.getClass());
				throw new IllegalStateException("Unexpected value: " + node.getClass());
		}
		return "";
	}

	public String getType(int numLine, String type1, String operator, String type2){
		if(operator.matches("(==|!=|<|<=|>|>=|&&|\\|\\|)")){
			if(type1.equals(type2)){
				return "boolean";
			}else {
				Error.semanticError(numLine, "Expected a boolean expression");
				return "boolean";
			}
		}
		if(type1.equals(type2)){
			return type1;
		}
		if((type1.equals("int") && type2.equals("double") || type1.equals("double") && type2.equals("int") )){
			return "double";
		}

		return "unknown type";
	}
}
