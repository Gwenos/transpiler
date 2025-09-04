package syntacticAnalysis.node;

public class OperatorNode extends Node {
	public final Node left;
	public final String operator;
	public final Node right;
	public OperatorNode(Node left, String operator, Node right){
		this.left = left;
		this.operator = operator;
		this.right = right;
	}
	public String toString(){return "(" + left + " " + operator + " " + right + ")";}
}
