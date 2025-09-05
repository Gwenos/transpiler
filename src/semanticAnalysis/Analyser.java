package semanticAnalysis;

import syntacticAnalysis.node.*;
import util.Error;

import java.util.List;

public class Analyser {

	private final SymbolTable symbolTable = new SymbolTable();
	private String currentMethodType = null;

	public SymbolTable analyse(List<Node> node){
		for(Node n : node){
			analyse(n);
		}
		return symbolTable;
	}

	public String analyse(Node node){

		switch (node){

			case ClassDeclarationNode cdn :
				symbolTable.declare(cdn.className, "class");
				for(Node n : cdn.classMembers){
					analyse(n);
				}
				break;

			case FieldDeclaration fd:
				symbolTable.declare(fd.fieldName, fd.type.toString());
				break;

			case MethodDeclarationNode mdn :
				symbolTable.declare(mdn.methodName, mdn.type.toString());
				currentMethodType = mdn.type.toString();
				symbolTable.enterScope();
				for(Node n : mdn.params){
					analyse(n);
				}
				analyse(mdn.block);
				symbolTable.exitScope();
				break;

			case ParameterNode pn :
				symbolTable.declare(pn.identifier.toString(), pn.type.toString());
				break;

			case AssignmentNode an :
				boolean exist = symbolTable.exists(an.identifier);
				analyse(an.value);
				String realType = analyse(an.value);
				if(!realType.equals(an.type)){
					Error.semanticError("Type mysmatch '" + an.identifier + "' got " + realType + " instead of " + an.type);
				} else if (!exist) {
					symbolTable.declare(an.identifier, an.type);
				}
				break;

			case IfNode in :
				analyse(in.boolExpr);
//				if(!in.boolExpr.equals("boolean")) Error.semanticError("Expected a boolean expression");
				analyse(in.block);

				for(Node n : in.orElse){
					analyse(n);
				}
				break;

			case BlockNode bn :
				for(Node statement : bn.statements){
					analyse(statement);
				}
				break;

			case ReturnNode rn :
				//verifier le type de retour pour qu'il coincide avec le type de la méthode

				String returnType = analyse(rn.expression);

				if(currentMethodType != null){
					if(!returnType.equals(currentMethodType)){
						Error.semanticError("return " + returnType + " instead of " + currentMethodType);
					}else{
						currentMethodType = null;
					}
				}
				break;

			case FonctionCallNode fcn :
				Symbol s = symbolTable.lookup(fcn.identifier);
				for(Node n : fcn.args){
					analyse(n);
				}
				return s.type;

			case WhileNode wn :
//				if(!wn.bool_expr.type.equals("boolean")) Error.semanticError("Expected a boolean expression");
				analyse(wn.bool_expr);
				analyse(wn.block);
				break;

			case NotNode nn :
				if(!nn.expression.type.equals("boolean")) Error.semanticError("Expected a boolean expression");
				analyse(nn.expression);
				break;

			case OperatorNode on :
				String leftType = analyse(on.left);
				String rightType = analyse(on.right);
				String type = getType(leftType, rightType);
				return type;

			case IdentifierNode in :
				Symbol symbol = symbolTable.lookup(in.value);
				if(symbol != null){
					return symbol.type;
				}
				break;

			case LiteralNode ln:
				return ln.type;

			case BooleanNode _, EmptyNode _ :
				break;

			default :
				throw new IllegalStateException("Unexpected value: " + node.getClass());
		}
		return "";
	}

	public String getType(String type1, String type2){
		if(type1.equals(type2)){
			return type1;
		}
		if((type1.equals("int") && type2.equals("double") || type1.equals("double") && type2.equals("int") )){
			return "double";
		}
		return "unknown type";
	}
}
