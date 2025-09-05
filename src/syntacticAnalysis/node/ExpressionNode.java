package syntacticAnalysis.node;

public abstract class ExpressionNode extends Node {
	public String type;

	public ExpressionNode(String type) {
		this.type = type;
	}
}
