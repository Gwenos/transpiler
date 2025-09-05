package syntacticanalysis.node;

public class OperatorNode extends ExpressionNode {
	public final ExpressionNode left;
	public final String operator;
	public final ExpressionNode right;
	public OperatorNode(int numLine, ExpressionNode left, String operator, ExpressionNode right){
		super(numLine,"Operator");
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	public String toString(){return "(" + left + " " + operator + " " + right + ")";}
}
