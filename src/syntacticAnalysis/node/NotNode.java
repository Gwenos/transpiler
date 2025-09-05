package syntacticAnalysis.node;

public class NotNode extends ExpressionNode {
	public final ExpressionNode expression;
	public NotNode(ExpressionNode expression){
		super("notNode");
		this.expression = expression;
		this.type = expression.type;
	}
	public String toString(){return "!" + expression;}

}
