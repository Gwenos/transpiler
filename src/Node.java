import java.util.List;

public abstract class Node {}

//==========================EXPRESSION=================================

class AssignmentNode extends Node{
	private String identifier;
	private Node value;
	public AssignmentNode(String identifier, Node value){
		this.identifier = identifier;
		this.value = value;
	}
	public String toString(){return identifier + " = " + value;}
}

class OperatorNode extends Node{
	private Node left;
	private String operator;
	private Node right;
	public OperatorNode(Node left, String operaor, Node right){
		this.left = left;
		this.operator = operaor;
		this.right = right;
	}
	public String toString(){return "(" + left + " " + operator + " " + right + ")";}
}

//EXPRESSION    -> TERM (('+' | '-') TERM)*
class ExpressionNode extends Node{
	private Node term;
	private String operator;
	private List<Node> terms;
	public ExpressionNode(Node term, String operator, List<Node> terms){
		this.term = term;
		this.operator = operator;
		this.terms = terms;
	}
	public String toString(){
		return terms.isEmpty() ? term.toString() : term + " " + operator + " " + terms;
	}
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
