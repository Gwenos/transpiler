package semanticAnalysis;

import syntacticAnalysis.node.IdentifierNode;
import syntacticAnalysis.node.NotNode;
import syntacticAnalysis.node.OperatorNode;
import syntacticAnalysis.node.WhileNode;
import syntacticAnalysis.node.FonctionCallNode;
import syntacticAnalysis.node.IfNode;
import syntacticAnalysis.node.AssignmentNode;
import syntacticAnalysis.node.ParameterNode;
import syntacticAnalysis.node.LiteralNode;
import syntacticAnalysis.node.ReturnNode;
import syntacticAnalysis.node.BlockNode;
import syntacticAnalysis.node.EmptyNode;
import syntacticAnalysis.node.MethodDeclarationNode;
import syntacticAnalysis.node.FieldDeclaration;
import syntacticAnalysis.node.ClassDeclarationNode;
import syntacticAnalysis.node.Node;

import java.util.List;

public class Analyser {

	private List<Node> nodes;
	private SymbolTable symbolTable = new SymbolTable();
	private String currentMethodType;

	public void analyse(Node node){


		switch (node){

			case ClassDeclarationNode cdn :
				symbolTable.declare(cdn.className, "class");
				for(Node member :cdn.classMembers){
					analyse(member);
				}
				break;

			case FieldDeclaration fd:
				symbolTable.declare(fd.fieldName, fd.type.toString());
				break;

			case MethodDeclarationNode mdn :
				symbolTable.declare(mdn.methodName, mdn.type.toString());
				symbolTable.enterScope();
				currentMethodType = mdn.type.toString();
				for(Node param : mdn.params){
					analyse(param);
				}
				analyse(mdn.block);
				break;

			case ParameterNode pn :
				symbolTable.declare(pn.identifier.toString(), pn.type.toString());
				break;

			case AssignmentNode an :
				symbolTable.declare(an.identifier, an.type);
				break;

			case IfNode in :
				//est ce que la condition est booléenn
				analyse(in.boolExpr);
				analyse(in.block);
				for (Node n : in.orElse){
					analyse(n);
				}
				break;

			case BlockNode bn :
				for(Node statement : bn.statements){
					analyse(statement);
				}
				break;

			case ReturnNode rn :
				//verifier le type de reour pour qu'il coincide avec le type de la méthode
				analyse(rn.expression);
				symbolTable.exitScope();
				break;

			case FonctionCallNode fcn :
				symbolTable.lookup(fcn.identifier);
				for(Node n : fcn.args){
					analyse(n);
				}
				break;

			case WhileNode wn :
				//verifier si boolexpr est boolén
				analyse(wn.bool_expr);
				analyse(wn.block);
				break;

			case OperatorNode on :
				analyse(on.left);
				analyse(on.right);
				break;

			case NotNode nn :
				//verifeir boolen
				analyse(nn.expression);
				break;

			case IdentifierNode in :
				break;

			case LiteralNode ln :
				break;

			case EmptyNode en:
				break;

			default :
				throw new IllegalStateException("Unexpected value: " + node.getClass());
		}
	}
}
