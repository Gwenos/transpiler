package syntacticanalysis.node;

public class OperatorNode extends TypedNode {
	public final TypedNode left;
	public final String operator;
	public final TypedNode right;
	public OperatorNode(int numLine, TypedNode left, String operator, TypedNode right){
		super(numLine,"Operator");
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	public String toString(){return "(" + left + " " + operator + " " + right + ")";}
}
