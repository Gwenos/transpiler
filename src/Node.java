import java.util.List;

public abstract class Node {}

//==========================EXPRESSION=================================

class AssignmentNode extends Node{
	private final String identifier;
	private final Node value;
	public AssignmentNode(String identifier, Node value){
		this.identifier = identifier;
		this.value = value;
	}
	public String toString(){return identifier + ANSI.PURPLE + " = " + ANSI.RESET + value;}
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
	public String toString(){return "(" + left + ANSI.PURPLE + " " + operator + " " + ANSI.RESET + right + ")";}
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
	public String toString(){return ANSI.PURPLE + "not " + ANSI.RESET + expression;}
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
		str.append("if " + boolExpr + " :\n    " + block+(orElse.isEmpty()?"":"\n"));
		for (int i = 0; i < orElse.size(); i++) {
			if(i==orElse.size()-1){
				str.append("else :\n    "+orElse.get(i).block);
			}else{
				str.append("elif "+orElse.get(i).boolExpr +" :\n    "+orElse.get(i).block+"\n");
			}
		}
		return str.toString();
	}
}

class WhileNode extends Node{
	private final Node bool_expr;
	WhileNode(Node bool_expr){
		this.bool_expr = bool_expr;
	}
	public String toString(){return "while " + bool_expr + " :";}
}

//=======================================TYPE====================================
//noeud terminaux

class LiteralNode extends Node {
	String value;
	LiteralNode(String value) {
		this.value = value;
	}
	public String toString() { return ANSI.CYAN + value + ANSI.RESET; }
}

class IdentifierNode extends Node {
	String identifier;
	IdentifierNode(String name) {
		this.identifier = name;
	}
	public String toString() { return ANSI.CYAN + identifier + ANSI.RESET; }
}

class FonctionCallNode extends Node {
	String identifier;
	Node args;
	FonctionCallNode(String identifier, Node args) {this.identifier = identifier;this.args = args;}
	public String toString() { return identifier+"("+args+")"; }
}

class BlockNode extends Node{
	List<Node> statements;
	BlockNode(List<Node> statements){
		this.statements = statements;
	}
	public String toString(){
		StringBuilder str = new StringBuilder();
		for(Node statement : statements){
			str.append(statement).append("\n    ");
		}
		return str.toString().substring(0, str.length()-5);
	}
}

class NullNode extends Node{
	public String toString() {
		return "";
	}
	public boolean equals(Node other){
		return other==null;
	}
}