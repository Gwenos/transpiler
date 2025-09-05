package syntacticanalysis.node;

public class NotNode extends ExpressionNode {
	public final ExpressionNode expression;
	public NotNode(int numLine, ExpressionNode expression){
		super(numLine, "notNode");
		this.expression = expression;
		this.type = expression.type;
	}
	public String toString(){return "!" + expression;}
}
