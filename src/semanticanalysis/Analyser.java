package semanticanalysis;

import syntacticanalysis.node.*;
import util.Error;

import java.util.ArrayList;
import java.util.List;

public class Analyser {

	private final SymbolTable SYMBOL_TABLE = new SymbolTable();

	public void analyse(List<Node> nodes){
		for(Node node : nodes){
			analyse(node);
		}
	}

	public String analyse(Node node){

		switch (node){

			case ClassDeclarationNode classDeclaration :
				this.SYMBOL_TABLE.declare(classDeclaration.numLine, classDeclaration.className, "class", new ArrayList<>());
				for(Node n : classDeclaration.classMembers){
					analyse(n);
				}
				return "void";

			case FieldDeclaration fieldDeclaration:
				this.SYMBOL_TABLE.declare(fieldDeclaration.numLine, fieldDeclaration.fieldName, fieldDeclaration.type.toString(), new ArrayList<>());
				return "void";

			case MethodDeclarationNode methodDeclaration :
				List<String> params = new ArrayList<>();
				for(Node n : methodDeclaration.params){
					params.add(analyse(n));
				}
				this.SYMBOL_TABLE.declare(methodDeclaration.numLine, methodDeclaration.methodName, methodDeclaration.type.toString(), params);
				String methodType = methodDeclaration.type.toString();
				this.SYMBOL_TABLE.enterScope();
				String bockType = analyse(methodDeclaration.block);
				if (!methodType.equals(bockType)) {
					Error.semanticError(methodDeclaration.numLine, "Type mismatch, returned "+bockType+" instead of "+methodType);
				}
				this.SYMBOL_TABLE.exitScope();
				return "void";

			case ParameterNode parameter :
				this.SYMBOL_TABLE.declare(parameter.numLine, parameter.identifier.toString(), parameter.type, new ArrayList<>());
				return parameter.type;

			case AssignmentNode assignment :
				String realType = analyse(assignment.value);
				if(!realType.equals(assignment.type)){
					Error.semanticError(assignment.numLine, "Type mismatch '" + assignment.identifier + "' got " + realType + " instead of " + assignment.type);
				}
				this.SYMBOL_TABLE.declare(assignment.numLine, assignment.identifier, assignment.type, new ArrayList<>());
				return "void";

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
				return "void";

			case BlockNode block :
				this.SYMBOL_TABLE.enterScope();
				for(Node statement : block.statements){
					analyse(statement);
				}
				this.SYMBOL_TABLE.exitScope();
				if(block.statements.isEmpty()) return "void";
				else return analyse(block.statements.getLast());

			case ReturnNode returnNode :
				return analyse(returnNode.expression);

			case MethodCallNode methodCall ://bon nombre et bon type des args
				Symbol method = this.SYMBOL_TABLE.lookup(methodCall.numLine, methodCall.identifier);
				List<String> argumentsTypes = new ArrayList<>();
				for(Node arg : methodCall.args){
					argumentsTypes.add(analyse(arg));
				}
				if(method.parametersTypes.size() != argumentsTypes.size()){
					Error.semanticError(methodCall.numLine, "Method "+methodCall.identifier+" expect "+method.parametersTypes.size()+" arguments, "+argumentsTypes.size()+" was given");
				}else if(!method.parametersTypes.equals(argumentsTypes)){
					Error.semanticError(methodCall.numLine, "Method "+methodCall.identifier+" expect "+method.parametersTypes+", "+argumentsTypes+" given");
				}
				return method.type;

			case ArgumentNode argument :
				return analyse(argument.expression) ;

			case WhileNode whileNode :
				if(!analyse(whileNode.bool_expr).equals("boolean")) Error.semanticError(whileNode.numLine, "while expect a boolean expression");
				analyse(whileNode.bool_expr);
				analyse(whileNode.block);
				return "void";

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
				Symbol identifier = this.SYMBOL_TABLE.lookup(identifierNode.numLine, identifierNode.value);
				if(identifier != null){
					return identifier.type;
				}
				return "void";

			case LiteralNode literal:
				return literal.type;

			case EmptyNode _ :
				return "empty";

			default :
				Error.semanticError(node.numLine, "Unexpected Node: "+node.getClass());
				throw new IllegalStateException("Unexpected value: " + node.getClass());
		}
	}

	public String getType(int numLine, String type1, String operator, String type2){
		if(operator.matches("(==|!=|<|<=|>|>=|&&|\\|\\|)")){
			if (!type1.equals(type2)) {
				Error.semanticError(numLine, "Expected a boolean expression");
			}
			return "boolean";
		}
		if(type1.equals(type2)){
			return type1;
		}
		if((type1.equals("int") && type2.equals("double") || type1.equals("double") && type2.equals("int") )){
			return "double";
		}

//		float x = 1.1;

		return "unknown type";
	}
}
