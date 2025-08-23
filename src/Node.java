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