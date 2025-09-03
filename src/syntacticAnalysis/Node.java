package syntacticAnalysis;

import util.ANSI;

import java.util.List;

public abstract class Node {
}

class ClassDeclarationNode extends Node {
	private final Node modifiers;
	private final String className;
	private final Node extend;
	private final List<Node> classMembers;
	public ClassDeclarationNode (Node modifiers, String className, Node extend, List<Node> classMembers){
		this.modifiers = modifiers;
		this.className = className;
		this.extend = extend;
		this.classMembers = classMembers;
	}
	public String toString (){
		StringBuilder sb = new StringBuilder();
		sb.append(modifiers).append(" class ").append(className).append(" ").append(extend).append("{\n");
		for (Node member : classMembers){
			sb.append(member).append("\n");
		}
		sb.append("}\n");
		return sb.toString();
	}
}

class MethodDeclarationNode extends Node{
	private final Node modifiers;
	private final Node type;
	private final String methodName;
	private final Node params;
	private final Node block;
	public MethodDeclarationNode (Node modifiers, Node type, String methodName, Node params, Node block){
		this.modifiers = modifiers;
		this.type = type;
		this.methodName = methodName;
		this.params = params;
		this.block = block;
	}
	public String toString (){
		return modifiers + " " + type + " " + methodName + "(" + params + ")" + block;
	}
}

class FieldDeclaration extends Node {
	private final Node modifiers;
	private final Node type;
	private final String methodName;
	private final Node expression;
	public FieldDeclaration (Node modifiers, Node type, String methodName, Node expression){
		this.modifiers = modifiers;
		this.type = type;
		this.methodName = methodName;
		this.expression = expression;
	}
	public String toString (){
		return modifiers + " " + type + " " + methodName + ((expression.toString().isEmpty()) ? "" : " " + expression) + ";";
	}

}

//==========================EXPRESSION=================================

class AssignmentNode extends Node{
	private final String type;
	private final String identifier;
	private final Node value;
	public AssignmentNode(String type, String identifier, Node value){
		this.type = type;
		this.identifier = identifier;
		this.value = value;
	}
	public String toString(){return type + " " + identifier + " = " + value + ";";}
}

class ExpressionNode extends Node{
	private final Node expression;
	public ExpressionNode(Node expression){
		this.expression = expression;
	}
	public String toString(){return expression + ";";}
}

class OperatorNode extends Node{
	private final Node left;
	private final String operator;
	private final Node right;
	public OperatorNode(Node left, String operaor, Node right){
		this.left = left;
		this.operator = operaor;
		this.right = right;
	}
	public String toString(){return "(" + left + " " + operator + " " + right + ")";}
}

class ConcatNode extends Node{
	private final Node[] nodes;
	public ConcatNode(Node[] nodes){
		this.nodes = nodes;
	}
	public ConcatNode(List<Node> nodes){
		this.nodes = new Node[nodes.size()];
		for(int i=0; i<nodes.size(); i++){
			this.nodes[i] = nodes.get(i);
		}
	}
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(Node n : nodes){
			sb.append(n.toString());
			sb.append(" ");
		}
		return sb.toString();
	}
}

class ArgumentNode extends Node{
	private final Node left;
	private final Node right;
	ArgumentNode(Node left, Node right){
		this.left = left;
		this.right = right;
	}
	public String toString(){return left + ANSI.PURPLE + ", " + ANSI.RESET + right;}
}

class NotNode extends Node{
	private final Node expression;
	public NotNode(Node expression){
		this.expression = expression;
	}
	public String toString(){return "!" + expression;}

}

class IfNode extends Node{
	private final Node boolExpr;
	private final Node block;
	private final List<IfNode> orElse;
	IfNode(Node boolExpr, Node block, List<IfNode> orElse){
		this.boolExpr = boolExpr;
		this.block = block;
		this.orElse = orElse;
	}
	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append("if (").append(boolExpr).append(") ").append(block);
		for(IfNode ifnode : orElse){
			if(ifnode.boolExpr.toString().isEmpty()){
				str.append("else ").append(ifnode.block);
			}else {
				str.append("else if(").append(ifnode.boolExpr).append(") ").append(ifnode.block);
			}
		}
//		str.append("if (" + boolExpr + "){\n" + block+(orElse.isEmpty()?"":"\n"));
//		for (int i = 0; i < orElse.size(); i++) {
//			if(i==orElse.size()-1 && orElse.get(i).boolExpr==null){
//				str.append("else {\n"+orElse.get(i).block);
//			}else{
//				str.append("else if (" + orElse.get(i).boolExpr +"){\n"+orElse.get(i).block+"\n");
//			}
//		}
		return str.toString();
	}
}

class WhileNode extends Node{
	private final Node bool_expr;
	private final Node block;
	WhileNode(Node bool_expr, Node block){
		this.bool_expr = bool_expr;
		this.block = block;
	}
	public String toString(){return "while(" + bool_expr + ")" + block;}
}

class DefNode extends Node{
	private final Node modifiers;
	private final String identifier;
	private final Node args;
	private final Node block;
	public DefNode(Node modifiers, String identifier, Node args, Node block){
		this.modifiers = modifiers;
		this.identifier = identifier;
		this.args = args;
		this.block = block;
	}
	public String toString(){
		return modifiers + identifier + "("+args+")" + block;
	}
}

class ReturnNode extends Node{
	private final Node expression;
	public ReturnNode(Node expression){
		this.expression = expression;
	}
	public String toString(){return "return " + expression + ";";}
}

//=======================================TYPE====================================
//noeud terminaux

class LiteralNode extends Node {
	String value;
	LiteralNode(String value) {
		this.value = value;
	}
	public String toString() { return value; }
}

class FonctionCallNode extends Node {
	String identifier;
	Node args;
	FonctionCallNode(String identifier, Node args) {this.identifier = identifier;this.args = args;}
	public String toString() {return identifier+"("+args+")";}
}

class BlockNode extends Node{
	List<Node> statements;
	BlockNode(List<Node> statements){
		this.statements = statements;
	}
	public String toString(){
		StringBuilder str = new StringBuilder("{\n");
		for(Node statement : statements){
			str.append(statement).append("\n");
		}
		str.append("}");
		return str.toString();
	}
}