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
	public String toString(){return identifier + " = " + value;}
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

//=======================================TYPE====================================

class LiteralNode extends Node {
	String value;
	LiteralNode(String value) {
		this.value = value;
	}
	public String toString() { return value; }
}

class IdentifierNode extends Node {
	String identifier;
	IdentifierNode(String name) {
		this.identifier = name;
	}
	public String toString() { return identifier; }
}
